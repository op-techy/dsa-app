package com.nomba.dsaapp.exception;

public class GeofenceViolationException extends RuntimeException {
    public GeofenceViolationException() {
        super("DSA is not within any assigned geofence");
    }
}