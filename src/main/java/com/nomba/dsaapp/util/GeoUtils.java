package com.nomba.dsaapp.util;


public class GeoUtils {

    private GeoUtils() {}  // prevent instantiation

    private static final double SCALE = 1_000_000.0;

    /**
     * Calculates the Haversine distance, which is the great-circle distance between two points
     * on the Earth's surface specified in decimal degrees, using their latitude and longitude.
     * The distance is returned in meters.
     *
     * @param lat1 The latitude of the first point, multiplied by 1,000,000 and specified as a long.
     * @param lon1 The longitude of the first point, multiplied by 1,000,000 and specified as a long.
     * @param lat2 The latitude of the second point, multiplied by 1,000,000 and specified as a long.
     * @param lon2 The longitude of the second point, multiplied by 1,000,000 and specified as a long.
     * @return The great-circle distance between the two points in meters.
     */
    public static double haversineDistance(long lat1, long lon1, long lat2, long lon2) {
        final int R = 6_371_000;
        // Divide by scale factor to recover decimal degrees
        double dLat = Math.toRadians((lat2 - lat1) / SCALE);
        double dLon = Math.toRadians((lon2 - lon1) / SCALE);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1 / SCALE))
                * Math.cos(Math.toRadians(lat2 / SCALE))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }


}