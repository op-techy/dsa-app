# DSA App

A backend service for tracking **Direct Sales Agents (DSAs)** in the field. It manages geofenced check-ins, GPS location tracking during active sessions, and checkout with distance/time summaries. The service exposes a **gRPC API for check-in** and a **REST API for checkout and geofence queries**, secured with stateless JWT authentication.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Data Model](#data-model)
- [API Reference](#api-reference)
- [Coordinate Encoding](#coordinate-encoding)
- [Error Handling](#error-handling)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)

---

## Overview

A DSA must be within one of their assigned geofences to check in. Once checked in, their device submits GPS points continuously. On checkout, the service computes:

- **Total distance** travelled across all submitted GPS points (Haversine formula)
- **Total session time** in seconds

Each DSA carries a JWT whose subject is their UUID. All endpoints derive identity from this token — there are no separate login endpoints in this service.

---

## Architecture

```
┌──────────────────────────────────────────────────────┐
│                   Clients                            │
│   gRPC client (check-in)   REST client (checkout)   │
└──────────┬───────────────────────────┬───────────────┘
           │                           │
           ▼                           ▼
┌──────────────────┐       ┌───────────────────────────┐
│ CheckInGrpcService│       │ CheckOutController         │
│  (grpc/)         │       │ GeolocationController      │
└────────┬─────────┘       └────────────┬──────────────┘
         │                              │
         ▼                              ▼
┌──────────────────────────────────────────────────────┐
│                  Service Layer                        │
│  CheckInService · CheckOutService · GeolocationService│
│  JwtService                                          │
└──────────────────────────┬───────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────┐
│              Spring Data JPA Repositories            │
│  Dsa · DsaSession · Checkout · LocationDatum         │
│  Geofence · DsaGeofence                              │
└──────────────────────────┬───────────────────────────┘
                           │
                           ▼
                    PostgreSQL 16
                (Flyway-managed schema)
```

**Security:** Every request passes through `JwtAuthFilter`, which validates the bearer token and populates the Spring Security context. The app is fully stateless — no sessions, no CSRF.

**gRPC:** The proto definition lives in `src/main/proto/checkin.proto`. Protobuf sources are generated into `build/generated/` at compile time by the `com.google.protobuf` Gradle plugin.

---

## Data Model

```
dsa ──< dsa_geofence >── geofence
 │
 └──< dsa_session ──── checkout
           │
           └──< location_data
```

| Table | Purpose |
|---|---|
| `dsa` | Agent identity (UUID + name) |
| `geofence` | Named circular boundary — centre point + radius in metres |
| `dsa_geofence` | Which geofences a DSA is permitted to check in from |
| `dsa_session` | An open or closed work session; closed when `checkout_id` is populated |
| `checkout` | End-of-session record: end time, total distance (m), total time (s) |
| `location_data` | Individual GPS breadcrumbs submitted at checkout |

All primary keys are UUIDs. Coordinates are stored as scaled integers (see [Coordinate Encoding](#coordinate-encoding)).

Schema is managed by Flyway — never modify tables directly. Add a new `V{n}__description.sql` migration file instead.

---

## API Reference

### gRPC — Check-In

**Service:** `checkin.CheckInService`  
**Method:** `CheckIn`  
**Default gRPC port:** configured via `grpc.server.port`

```protobuf
message CheckInRequest {
  string bearer_token = 1;  // "Bearer <jwt>"
  int64  timestamp    = 2;  // epoch milliseconds
  int64  latitude     = 3;  // decimal degrees × 1,000,000
  int64  longitude    = 4;  // decimal degrees × 1,000,000
}

message CheckInResponse {
  string session_id = 1;   // UUID of the created session
}
```

**Business rules enforced:**
- DSA must exist (derived from JWT subject)
- No open session already exists for this DSA
- Coordinates must fall within at least one of the DSA's assigned geofences

---

### REST — Checkout

**`POST /api/checkout`**

Headers:
```
Authorization: Bearer <jwt>
```

Request body:
```json
{
  "sessionId": "uuid",
  "timestamp": 1714000000000,
  "latLongs": [
    [6123456, 3456789, 1714000000000],
    [6124000, 3457000, 1714000060000]
  ]
}
```

Each entry in `latLongs` is `[latitude, longitude, timestamp]` — all scaled integers (see below).

Response: `200 OK` (empty body) on success.

**Business rules enforced:**
- Session must exist and belong to the authenticated DSA
- Session must not already be checked out

---

### REST — Get Assigned Geofences

**`GET /api/geolocation`**

Headers:
```
Authorization: Bearer <jwt>
```

Response:
```json
[
  {
    "id": "uuid",
    "name": "Branch A",
    "latitude": 6123456,
    "longitude": 3456789,
    "radiusInMetres": 200
  }
]
```

---

## Coordinate Encoding

All latitude/longitude values — in API requests, responses, and the database — are **decimal degrees multiplied by 1,000,000 and stored as `long`**.

| Real value | Encoded value |
|---|---|
| 6.123456° | `6123456` |
| 3.456789° | `3456789` |

This avoids floating-point precision loss across the wire and in the database. `GeoUtils.haversineDistance()` divides by `1_000_000.0` internally before computing distances.

---

## Error Handling

All errors follow a consistent JSON envelope:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "DSA not found with id: ...",
  "timestamp": "2024-04-25T10:00:00"
}
```

| Exception | HTTP Status |
|---|---|
| `DsaNotFoundException` | 404 |
| `SessionNotFoundException` | 404 |
| `GeofenceViolationException` | 403 |
| `UnauthorizedSessionAccessException` | 403 |
| `SessionAlreadyCheckedOutException` | 409 |
| Unhandled exceptions | 500 |

---

## Prerequisites

- Java 17+
- Docker & Docker Compose (for PostgreSQL)
- Gradle (wrapper included — no local install needed)

---

## Getting Started

```bash
# 1. Clone the repository
git clone https://github.com/op-techy/dsa-app.git
cd dsa-app

# 2. Start PostgreSQL
docker-compose up -d

# 3. Copy environment variables (edit values as needed)
cp .env.example .env   # or ensure .env exists with the values below

# 4. Build and run
./gradlew bootRun
```

The REST server starts on port `8080` by default. The gRPC server port is controlled by `grpc.server.port` in `application.yaml`.

---

## Running Tests

Tests use an in-memory H2 database — no running PostgreSQL instance required.

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.nomba.dsaapp.SomeTest"
```
