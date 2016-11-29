package org.lyj.ext.db.mongo.utils.aggregation;

import org.bson.Document;
import org.lyj.ext.db.mongo.model.LyjGeoJSON;

/**
 * Documentation: https://docs.mongodb.org/v3.0/reference/operator/aggregation/geoNear/
 * <p>
 * When using $geoNear, consider that:
 * <ol>
 * <li>
 * You can only use $geoNear as the first stage of a pipeline.
 * </li>
 * <li>
 * You must include the distanceField option. The distanceField option specifies the field that will contain the calculated distance.
 * </li>
 * <li>
 * The collection must have a geospatial index.
 * </li>
 * <li>
 * The $geoNear requires that a collection have at most only one 2d index and/or only one 2dsphere index.
 * </li>
 * <li>
 * You do not need to specify which field in the documents hold the coordinate pair or point.
 * Because $geoNear requires that the collection have a single geospatial index, $geoNear implicitly uses the indexed field.
 * </li>
 * <li>
 * If using a 2dsphere index, you must specify spherical: true.
 * </li>
 * <li>
 * You cannot specify a $near predicate in the query field of the $geoNear stage.
 * </li>
 * </ol>
 */
public class LyjGeoNearOptions {


    private static final String SPHERICAL = "spherical";        // boolean - default is true
    private static final String LIMIT = "limit";                // int - default 100
    private static final String MAX_DISTANCE = "maxDistance";   // int - default is 10000 meters
    private static final String QUERY = "query";                // Document
    private static final String DISTANCE_MULTIPLIER = "distanceMultiplier"; // Double
    private static final String UNIQUE_DOCS = "uniqueDocs"; // boolean
    private static final String NEAR = "near"; // GoeJSONPoint
    private static final String DISTANCE_FIELD = "distanceField"; // String
    private static final String INCLUDE_LOCS = "includeLocs"; // String

    private static final boolean DEF_SPHERICAL = true;
    private static final int DEF_LIMIT = 100;
    private static final boolean DEF_UNIQUE_DOCS = true;
    private static final String DEF_DISTANCE_FIELD = "dist";
    private static final String DEF_INCLUDE_LOCS = "loc";
    private static final double DEF_DISTANCE_MULTIPLIER = 3963.2; // hearth radius. Distance is in Km

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Document _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LyjGeoNearOptions() {
        _data = new Document();

        this.setSpherical(DEF_SPHERICAL)
                .setUniqueDocs(DEF_UNIQUE_DOCS)
                .setLimit(DEF_LIMIT)
                .setDistanceField(DEF_DISTANCE_FIELD)
                .setIncludeLocs(DEF_INCLUDE_LOCS)
                .setDistanceMultiplier(DEF_DISTANCE_MULTIPLIER);
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Document asBson() {
        return _data;
    }

    public boolean isSpherical() {
        return _data.getBoolean(SPHERICAL, DEF_SPHERICAL);
    }

    public LyjGeoNearOptions setSpherical(final boolean value) {
        _data.put(SPHERICAL, value);
        return this;
    }

    public boolean isUniqueDocs() {
        return _data.getBoolean(UNIQUE_DOCS, DEF_UNIQUE_DOCS);
    }

    public LyjGeoNearOptions setUniqueDocs(final boolean value) {
        _data.put(UNIQUE_DOCS, value);
        return this;
    }

    public int getLimit() {
        return _data.getInteger(LIMIT, DEF_LIMIT);
    }

    public LyjGeoNearOptions setLimit(final int value) {
        _data.put(LIMIT, value);
        return this;
    }

    public long getMaxDistance() {
        return _data.getInteger(MAX_DISTANCE, -1);
    }

    public LyjGeoNearOptions setMaxDistance(final long value) {
        _data.put(MAX_DISTANCE, value);
        return this;
    }

    public double getDistanceMultiplier() {
        return _data.getDouble(DISTANCE_MULTIPLIER);
    }

    public LyjGeoNearOptions setDistanceMultiplier(final double value) {
        _data.put(DISTANCE_MULTIPLIER, value);
        return this;
    }

    public Document getQuery() {
        if (!_data.containsKey(QUERY)) {
            _data.put(QUERY, new Document());
        }
        return (Document) _data.get(QUERY);
    }

    /**
     * Optional. Limits the results to the documents that match the query.
     * The query syntax is the usual MongoDB read operation query syntax.
     * <p>
     * You cannot specify a $near predicate in the query field of the $geoNear stage.
     *
     * @param value Document
     * @return this
     */
    public LyjGeoNearOptions setQuery(final Document value) {
        _data.put(QUERY, value);
        return this;
    }

    public LyjGeoJSON getNear() {
        if (!_data.containsKey(NEAR)) {
            _data.put(NEAR, LyjGeoJSON.buildPoint(0, 0));
        }
        return (LyjGeoJSON) _data.get(NEAR);
    }

    /**
     * The point for which to find the closest documents.<p>
     * If using a 2dsphere index, you can specify the point as either a GeoJSON point or legacy coordinate pair.
     *
     * @param value GeoJsonPoint
     * @return this
     */
    public LyjGeoNearOptions setNear(final LyjGeoJSON value) {
        _data.put(NEAR, value);
        return this;
    }

    public LyjGeoNearOptions setNear(final double longitude, final double latitude) {
        _data.put(NEAR, LyjGeoJSON.buildPoint(longitude, latitude));
        return this;
    }

    public String getDistanceField() {
        return _data.getString(DISTANCE_FIELD);
    }

    public LyjGeoNearOptions setDistanceField(final String value) {
        _data.put(DISTANCE_FIELD, value);
        return this;
    }

    public String getIncludeLocs() {
        return _data.getString(INCLUDE_LOCS);
    }

    public LyjGeoNearOptions setIncludeLocs(final String value) {
        _data.put(INCLUDE_LOCS, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
