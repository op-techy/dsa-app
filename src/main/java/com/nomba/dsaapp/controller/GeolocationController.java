package com.nomba.dsaapp.controller;

import com.nomba.dsaapp.dto.response.GeofenceResponse;
import com.nomba.dsaapp.service.GeolocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeolocationController {

    private final GeolocationService geolocationService;

    @GetMapping("/geolocation")
    public ResponseEntity<List<GeofenceResponse>> getGeolocation(
            @RequestHeader("Authorization") String bearerToken) {
        return ResponseEntity.ok(geolocationService.getGeofencesForDsa(bearerToken));
    }
}