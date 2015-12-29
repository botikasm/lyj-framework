package org.lyj.ext.mongo;

/**
 * Constants
 */
public interface ILyjMongoConstants {



    public static final String F_ID = "_id";
    public static final String F_COLLECTION = "_collection";

    public static final String $NUMBER_LONG = "$numberLong";

    public static final String NE = "$ne";
    public static final String NIN = "$nin";

    public static final String OR = "$or";
    public static final String AND = "$and";



    public static final String TYPE = "type";
    public static final String COORDINATES = "coordinates";

    // ------------------------------------------------------------------------
    //                      A G G R E G A T E
    // ------------------------------------------------------------------------

    public static final String $MATCH = "$match";
    public static final String $GROUP = "$group";
    public static final String $AVG = "$avg";
    public static final String $SKIP = "$skip";
    public static final String $LIMIT = "$limit";

    // ------------------------------------------------------------------------
    //                      G E O - T Y P E S
    // ------------------------------------------------------------------------

    /**
     * A GeometryCollection
     */
    public static final String GEOMETRY_COLLECTION = "GeometryCollection";

    /**
     * A LineString
     */
    public static final String LINE_STRING = "LineString";

    /**
     * A MultiLineString
     */
    public static final String MULTI_LINE_STRING = "MultiLineString";

    /**
     * A MultiPoint
     */
    public static final String MULTI_POINT = "MultiPoint";

    /**
     * A MultiPolygon
     */
    public static final String MULTI_POLYGON = "MultiPolygon";

    /**
     * A Point
     */
    public static final String POINT = "Point";

    /**
     * A Polygon
     */
    public static final String POLYGON = "Polygon";

    // ------------------------------------------------------------------------
    //                      G E O
    // ------------------------------------------------------------------------

    public static final double EARTH_RADIUS_MILES = 3963.2;
    public static final double EARTH_RADIUS_KILOMETERS = 6378.1;

    public static final String $GEOMETRY = "$geometry";
    public static final String $MAX_DISTANCE = "$maxDistance";
    public static final String $MIN_DISTANCE = "$minDistance";
    public static final String $CENTER_SPHERE = "$centerSphere";

    public static final String $GEO_WITHIN = "$geoWithin";
    public static final String $GEO_INTERSECTS = "$geoIntersects";
    public static final String $NEAR = "$near";

    // ------------------------------------------------------------------------
    //                      G E O  C O M M A N D
    // ------------------------------------------------------------------------

    public static final String GEO_NEAR = "geoNear";
    public static final String NEAR = "near";
    public static final String SPHERICAL = "spherical";
    public static final String LIMIT = "limit";
    public static final String MIN_DISTANCE = "minDistance";
    public static final String MAX_DISTANCE = "maxDistance";
    public static final String QUERY = "query";
    public static final String DISTANCE_MULTIPLIER = "distanceMultiplier";
    public static final String INCLUDE_LOCS = "includeLocs";
    public static final String UNIQUE_DOCS = "uniqueDocs";


}
