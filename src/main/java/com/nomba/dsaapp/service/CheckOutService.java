package com.nomba.dsaapp.service;

import com.nomba.dsaapp.entity.Checkout;
import com.nomba.dsaapp.entity.DsaSession;
import com.nomba.dsaapp.entity.LocationDatum;
import com.nomba.dsaapp.exception.SessionAlreadyCheckedOutException;
import com.nomba.dsaapp.exception.SessionNotFoundException;
import com.nomba.dsaapp.exception.UnauthorizedSessionAccessException;
import com.nomba.dsaapp.repository.CheckoutRepository;
import com.nomba.dsaapp.repository.DsaSessionRepository;
import com.nomba.dsaapp.repository.LocationDatumRepository;
import com.nomba.dsaapp.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckOutService {
    private final DsaSessionRepository dsaSessionRepository;
    private final CheckoutRepository checkoutRepository;
    private final LocationDatumRepository locationDatumRepository;
    private final JwtService jwtService;

    /**
     * Completes the checkout process for a given session by saving location data,
     * calculating total distance and time, and associating a checkout record with the session.
     *
     * @param sessionId the unique identifier of the session to be checked out
     * @param timestamp the time at which the session is checked out, in milliseconds since epoch
     * @param latLongs a list of geographic coordinate points represented as arrays,
     *                 where each array contains three elements:
     *                 [latitude (scaled), longitude (scaled), recorded timestamp]
     * @throws SessionNotFoundException if no session is found with the provided {@code sessionId}
     * @throws SessionAlreadyCheckedOutException if the session has already been checked out
     */
    public void checkOut(String bearerToken, UUID sessionId, long timestamp, List<long[]> latLongs) {
        UUID dsaId = jwtService.extractDsaId(bearerToken);

        DsaSession session = dsaSessionRepository.findById(sessionId)
                .orElseThrow(()-> new SessionNotFoundException(sessionId));

        if (!session.getDsa().getId().equals(dsaId)) {
            throw new UnauthorizedSessionAccessException(sessionId);
        }

        if(session.getCheckout() != null) {
            throw new SessionAlreadyCheckedOutException(sessionId);
        }

        // Save all location points
        List<LocationDatum> locations = new ArrayList<>();
        for (long[] point : latLongs) {
            LocationDatum ld = new LocationDatum();
            ld.setSession(session);
            ld.setLatitude(point[0]);
            ld.setLongitude(point[1]);
            ld.setTimestamp(point[2]);
            locations.add(ld);
        }
        locationDatumRepository.saveAll(locations);

        // Calculate totals
        long totalDistance = calculateTotalDistance(locations);
        long totalTime = (timestamp - session.getStartTime()) / 1000L;

        // Create checkout
        Checkout checkout = new Checkout();
        checkout.setSession(session);
        checkout.setEndTime(timestamp);
        checkout.setTotalDistanceM(totalDistance);
        checkout.setTotalTimeS(totalTime);
        checkoutRepository.save(checkout);

        // Close the session
        session.setCheckout(checkout);
        dsaSessionRepository.save(session);
    }

    /**
     * Calculates the total distance between consecutive geographic points
     * in a list using the Haversine formula.
     *
     * @param points a list of {@code LocationDatum} objects representing the geographic points,
     *               each containing latitude and longitude coordinates
     * @return the total distance between the points in meters, rounded to the nearest whole number
     */
    private long calculateTotalDistance(List<LocationDatum> points) {
        long total = 0;
        for (int i = 1; i < points.size(); i++) {
            total += Math.round(GeoUtils.haversineDistance(
                    points.get(i - 1).getLatitude(), points.get(i - 1).getLongitude(),
                    points.get(i).getLatitude(), points.get(i).getLongitude()));
        }
        return total;
    }
}
