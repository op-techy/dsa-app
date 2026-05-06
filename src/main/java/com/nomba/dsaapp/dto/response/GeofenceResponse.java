package com.nomba.dsaapp.dto.response;
import java.util.UUID;

public record GeofenceResponse(
        UUID id, String name,
        long latitude, long longitude, long radiusInMetres) {}