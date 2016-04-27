package org.lyj.ext.mongo.utils.filter;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.mongo.ILyjMongoConstants;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Helper class to build filters for mongo query
 */
public class LyjMongoFilter
        implements ILyjMongoConstants {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Document _filter;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private LyjMongoFilter() {
        _filter = new Document();
    }

    private LyjMongoFilter(final Document filter) {
        _filter = filter;
    }

    private LyjMongoFilter(final String filter) {
        _filter = StringUtils.isJSONObject(filter) ? Document.parse(filter) : new Document();
    }

    @Override
    public String toString() {
        return _filter.toJson();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Document asBson() {
        return _filter;
    }

    public LyjMongoFilter put(final String fieldName, final Object value) {
        _filter.put(fieldName, value);
        return this;
    }

    public LyjMongoFilter like(final String fieldName, final Object value) {
        _filter.put(fieldName, new Document($REGEX, value));
        return this;
    }

    public LyjMongoFilter notEquals(final String fieldName, final Object value) {
        _filter.put(fieldName, new Document($NE, value));
        return this;
    }

    public LyjMongoFilter notIn(final String fieldName, final Collection list) {
        _filter.put(fieldName, new Document($NIN, list));
        return this;
    }

    public LyjMongoFilter in(final String fieldName, final Collection list) {
        _filter.put(fieldName, new Document($IN, list));
        return this;
    }

    public LyjMongoFilter or(final Collection<Bson> conditions) {
        _filter.put($OR, conditions);
        return this;
    }

    public LyjMongoFilter or(final Bson... conditions) {
        _filter.put($OR, Arrays.asList(conditions));
        return this;
    }

    public LyjMongoFilter and(final Collection<Bson> conditions) {
        _filter.put($AND, conditions);
        return this;
    }

    public LyjMongoFilter and(final Bson... conditions) {
        _filter.put($AND, Arrays.asList(conditions));
        return this;
    }



    // ------------------------------------------------------------------------
    //                      G E O
    // ------------------------------------------------------------------------

    /**
     * Queries for location data found within a GeoJSON polygon
     *
     * @param locationField Name of location field
     * @param coordinates   Array of coordinates that delim the polygon
     * @return LyjMongoFilter { "loc" : { "$geoWithin" : { "$geometry" : { "type" : "Polygon", "coordinates" : "[[0,10], [3,6], [4,5]]" } } } }
     */
    @SafeVarargs
    public final LyjMongoFilter geoWithin(final String locationField,
                                          final List<Double>... coordinates) {
        _filter.put(locationField,
                new Document($GEO_WITHIN,
                        new Document($GEOMETRY,
                                new Document(TYPE, POLYGON).append(COORDINATES, Arrays.asList(coordinates))
                        )
                )
        );
        return this;
    }

    public final LyjMongoFilter geoWithinSphereMiles(final String locationField,
                                                     final Double longitude,
                                                     final Double latitude,
                                                     final Double radiusInMiles) {
        return geoWithinSphereRadians(locationField, longitude, latitude, radiusInMiles / EARTH_RADIUS_MILES);
    }

    public final LyjMongoFilter geoWithinSphereKilometers(final String locationField,
                                                          final Double longitude,
                                                          final Double latitude,
                                                          final Double radiusInKilometers) {
        return geoWithinSphereRadians(locationField, longitude, latitude, radiusInKilometers / EARTH_RADIUS_KILOMETERS);
    }

    public final LyjMongoFilter geoWithinSphereRadians(final String locationField,
                                                       final Double longitude,
                                                       final Double latitude,
                                                       final Double radiusInRadians) {
        _filter.put(locationField,
                new Document($GEO_WITHIN,
                        new Document($CENTER_SPHERE,
                                Arrays.asList(Arrays.asList(longitude, latitude), radiusInRadians)
                        )
                )
        );
        return this;
    }

    /**
     * Queries for locations that intersect a specified GeoJSON object.
     * A location intersects the object if the intersection is non-empty.
     * This includes documents that have a shared edge.
     *
     * @param locationField Name of location field
     * @param type          Type of geometry
     * @param coordinates   Array of coordinates that delim the geometry type
     * @return { "loc" : { "$geoIntersects" : { "$geometry" : { "type" : "Polygon", "coordinates" : "[[0,10], [3,6], [4,5]]" } } } }
     */
    @SafeVarargs
    public final LyjMongoFilter geoIntersects(final String locationField,
                                              final String type,
                                              final List<Double>... coordinates) {
        _filter.put(locationField,
                new Document($GEO_INTERSECTS,
                        new Document($GEOMETRY,
                                new Document(TYPE, type).append(COORDINATES, Arrays.asList(coordinates))
                        )
                )
        );
        return this;
    }

    /**
     * Return a query to find the points closest to the defined point and sorts the results by distance.
     *
     * @param locationField Name of location field
     * @param maxDistance   Max Distance in meters
     * @param longitude     Latitude
     * @param latitude      Longitude
     * @return { "loc" : { "$near" : { "$geometry" : { "type" : "Point", "coordinates" : "[0,10]" }, "$maxDistance" : 20 } } }
     */
    public final LyjMongoFilter near(final String locationField,
                                     final Integer maxDistance,
                                     final Double longitude,
                                     final Double latitude) {
        _filter.put(locationField,
                new Document($NEAR,
                        new Document($GEOMETRY,
                                new Document(TYPE, POINT)
                                        .append(COORDINATES, Arrays.asList(longitude, latitude))
                        ).append($MAX_DISTANCE, maxDistance)
                )
        );
        return this;
    }

    /**
     * @param locationField Name of location field
     * @param maxDistance   Max Distance in meters
     * @param minDistance   Min Distance in meters
     * @param longitude     Latitude
     * @param latitude      Longitude
     * @return { "loc" : { "$near" : { "$geometry" : { "type" : "Point", "coordinates" : "[0,10]"}, "$maxDistance" : 20, "$minDistance" : 5 } } }
     */
    public final LyjMongoFilter near(final String locationField,
                                     final Integer maxDistance, final Integer minDistance,
                                     final Double longitude,
                                     final Double latitude) {
        _filter.put(locationField,
                new Document($NEAR,
                        new Document($GEOMETRY,
                                new Document(TYPE, POINT)
                                        .append(COORDINATES, Arrays.asList(longitude, latitude))
                        ).append($MAX_DISTANCE, maxDistance).append($MIN_DISTANCE, minDistance)
                )
        );
        return this;
    }



    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static LyjMongoFilter create() {
        return new LyjMongoFilter();
    }

    public static LyjMongoFilter create(final Document filter) {
        return new LyjMongoFilter(filter);
    }

    public static LyjMongoFilter create(final String filter) {
        return new LyjMongoFilter(filter);
    }

    public static Document in(final Document filter, final String fieldName, final Collection list){
        filter.put(fieldName, new Document($IN, list));
        return filter;
    }

    public static Document nin(final Document filter, final String fieldName, final Collection list){
        filter.put(fieldName, new Document($NIN, list));
        return filter;
    }

}
