package com.nomba.dsaapp.service;

import com.nomba.dsaapp.dto.response.GeofenceResponse;
import com.nomba.dsaapp.entity.Dsa;
import com.nomba.dsaapp.exception.DsaNotFoundException;
import com.nomba.dsaapp.repository.DsaGeofenceRepository;
import com.nomba.dsaapp.repository.DsaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeolocationService {
    private final DsaRepository dsaRepository;
    private final DsaGeofenceRepository dsaGeofenceRepository;
    private final JwtService jwtService;

    /**
     * Retrieves a list of geofences associated with a Direct Sales Agent (DSA) using the provided bearer token.
     * The DSA ID is extracted from the token and used to fetch the corresponding geofences.
     *
     * @param bearerToken The bearer token containing the JWT with the DSA ID as the subject.
     * @return A list of geofences associated with the DSA identified by the token.
     * @throws DsaNotFoundException If no DSA is found with the extracted ID.
     */
    public List<GeofenceResponse> getGeofencesForDsa(String bearerToken) {
        UUID dsaId = jwtService.extractDsaId(bearerToken);

        Dsa dsa = dsaRepository.findById(dsaId)
                .orElseThrow(()-> new DsaNotFoundException(dsaId));

        return dsaGeofenceRepository.findByDsaId(dsa.getId())
                .stream()
                .map(g -> new GeofenceResponse(
                        g.getGeofence().getId(),
                        g.getGeofence().getName(),
                        g.getGeofence().getLatitude(),
                        g.getGeofence().getLongitude(),
                        g.getGeofence().getRadiusInMetres()))
                .toList();
    }
}
