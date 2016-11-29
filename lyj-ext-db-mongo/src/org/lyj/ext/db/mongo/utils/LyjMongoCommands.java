package org.lyj.ext.db.mongo.utils;

import org.bson.Document;
import org.lyj.ext.db.mongo.ILyjMongoConstants;
import org.lyj.ext.db.mongo.model.LyjGeoJSON;

import java.util.Arrays;

/**
 * Helper for mongo commands
 */
public class LyjMongoCommands
        implements ILyjMongoConstants {

    /**
     * @param collection         Name of collection to query
     * @param longitute          Longitude
     * @param latitude           Latitude
     * @param spherical          Required if using a 2dsphere index. Determines how MongoDB calculates the distance.
     *                           The default value is false.f true, then MongoDB uses spherical geometry to
     *                           calculate distances in meters if the specified (near) point is a GeoJSON point
     *                           and in radians if the specified (near) point is a legacy coordinate pair.
     *                           <p>
     *                           If false, then MongoDB uses 2d planar geometry to calculate distance between points.
     *                           <p>
     *                           If using a 2dsphere index, spherical must be true.
     * @param limit              Optional. The maximum number of documents to return. The default value is 100.
     * @param minDistance        Optional. The minimum distance from the center point that the documents must be.
     *                           MongoDB filters the results to those documents that are at least the specified
     *                           distance from the center point.
     *                           <p>
     *                           Only available for use with 2dsphere index.
     *                           <p>
     *                           Specify the distance in meters for GeoJSON data and in radians for legacy
     *                           coordinate pairs.
     * @param maxDistance        Optional. The maximum distance from the center point that the documents can be.
     *                           MongoDB limits the results to those documents that fall within the specified distance
     *                           from the center point.
     *                           <p>
     *                           Specify the distance in meters for GeoJSON data and in radians for legacy
     *                           coordinate pairs.
     * @param filter             Optional. Limits the results to the documents that match the query.
     *                           The query syntax is the usual MongoDB read operation query syntax.
     *                           <p>
     *                           You cannot specify a $near predicate in the query field of the geoNear command.
     * @param distanceMultiplier Optional. The factor to multiply all distances returned by the query. For example, use
     *                           the distanceMultiplier to convert radians, as returned by a spherical query,
     *                           to kilometers by multiplying by the radius of the Earth.
     * @param includeLocs        Optional. If this is true, the query returns the location of the matching documents
     *                           in the results.
     *                           The default is false. This option is useful when a location field contains multiple
     *                           locations. To specify a field within an embedded document, use dot notation.
     * @return geoNear command
     */
    public static Document geoNear(final String collection, final Double longitute, final Double latitude,
                                   final boolean spherical, final int limit,
                                   final int minDistance, final int maxDistance,
                                   final Document filter,
                                   final double distanceMultiplier, final Boolean includeLocs) {
        final Document result = new Document();

        // collection to query
        result.put(GEO_NEAR, collection);

        // point
        result.put(NEAR, new LyjGeoJSON().setType(LyjGeoJSON.POINT).setCoordinates(Arrays.asList(longitute, latitude)));

        // spherical
        result.put(SPHERICAL, spherical);

        // limit of documents
        if (limit > 0) {
            result.put(LIMIT, limit);
        }

        if (minDistance > 0) {
            result.put(MIN_DISTANCE, minDistance);
        }
        if (maxDistance > 0) {
            result.put(MAX_DISTANCE, maxDistance);
        }

        if (null!=filter && filter.size()>0){
            result.put(QUERY, filter);
        }

        if(distanceMultiplier>0){
            result.put(DISTANCE_MULTIPLIER, distanceMultiplier);
        }

        if (null!=includeLocs){
            result.put(INCLUDE_LOCS, includeLocs);
        }

        return result;
    }

    public static Document geoNearSphere(final String collection, final Double longitute, final Double latitude,
                                         final int limit,
                                         final int minDistance, final int maxDistance,
                                         final Document filter) {
        return geoNear(collection, longitute, latitude, true, limit, minDistance, maxDistance, filter, 0, null);
    }

    public static Document geoNearSphereMiles(final String collection, final Double longitute, final Double latitude,
                                        final int limit,
                                        final int minDistance, final int maxDistance,
                                        final Document filter) {
        return geoNear(collection, longitute, latitude, true, limit, minDistance, maxDistance, filter, EARTH_RADIUS_MILES, null);
    }

    public static Document geoNearSphereKilometers(final String collection, final Double longitute, final Double latitude,
                                             final int limit,
                                             final int minDistance, final int maxDistance,
                                             final Document filter) {
        return geoNear(collection, longitute, latitude, true, limit, minDistance, maxDistance, filter, EARTH_RADIUS_KILOMETERS, null);
    }

}
