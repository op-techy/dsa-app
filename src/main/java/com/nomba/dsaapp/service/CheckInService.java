package com.nomba.dsaapp.service;

import com.nomba.dsaapp.entity.Dsa;
import com.nomba.dsaapp.entity.DsaSession;
import com.nomba.dsaapp.entity.Geofence;
import com.nomba.dsaapp.exception.DsaNotFoundException;
import com.nomba.dsaapp.exception.GeofenceViolationException;
import com.nomba.dsaapp.repository.DsaGeofenceRepository;
import com.nomba.dsaapp.repository.DsaRepository;
import com.nomba.dsaapp.repository.DsaSessionRepository;
import com.nomba.dsaapp.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final DsaRepository dsaRepository;
    private final DsaGeofenceRepository dsaGeofenceRepository;
    private final DsaSessionRepository dsaSessionRepository;
    private final JwtService jwtService;

    /**
     * Initiates a check-in process for a DSA (Direct Sales Agent) based on the provided geolocation
     * and authentication details. Validates the DSA's presence within assigned geofences
     * and creates a session if the check-in is successful.
     *
     * @param bearerToken A bearer token containing the DSA's authentication and identification details.
     * @param timestamp The timestamp at which the check-in is initiated, represented in milliseconds since epoch.
     * @param latitude The latitude of the DSA's current location.
     * @param longitude The longitude of the DSA's current location.
     * @return A UUID representing the newly created DSA session.
     * @throws DsaNotFoundException If no DSA is found with the ID extracted from the bearer token.
     * @throws GeofenceViolationException If the DSA's location does not fall within any of their assigned geofences.
     */
    public UUID checkIn(String bearerToken, long timestamp, long latitude, long longitude) {
        UUID dsaId = jwtService.extractDsaId(bearerToken);

        Dsa dsa = dsaRepository.findById(dsaId)
                .orElseThrow(()-> new DsaNotFoundException(dsaId));

        dsaSessionRepository.findByDsaIdAndCheckoutIsNull(dsa.getId())
                .ifPresent(open -> {
                    throw new IllegalStateException(
                            "DSA already has an open session: " + open.getId() +
                                    ". Please check out before checking in again.");
                });

        boolean withinFence = dsaGeofenceRepository.findByDsaId(dsa.getId())
                .stream()
                .anyMatch(dsaGeofence -> isWithinGeofence(
                        latitude, longitude, dsaGeofence.getGeofence()));

        if(!withinFence) {
            throw new GeofenceViolationException();
        }

        DsaSession session = new DsaSession();
        session.setDsa(dsa);
        session.setStartTime(timestamp);

        return dsaSessionRepository.save(session).getId();
    }

    /**
     * Determines whether the provided latitude and longitude coordinates fall within the boundaries
     * of the specified geofence.
     *
     * @param latitude The latitude of the point to check, in degrees.
     * @param longitude The longitude of the point to check, in degrees.
     * @param fence The geofence against which the point is to be validated.
     * @return {@code true} if the point is within the geofence; {@code false} otherwise.
     */
    private boolean isWithinGeofence(long latitude, long longitude, Geofence fence) {
        double distance = GeoUtils.haversineDistance(latitude, longitude,
                fence.getLatitude(), fence.getLongitude());
        return distance <= fence.getRadiusInMetres();
    }
}
