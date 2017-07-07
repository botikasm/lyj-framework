package org.lyj.ext.db.arango.query;

import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility to build Arango QUERY
 * <p>
 * FOR user1 IN users
 * ...FOR user2 IN users
 * ...FILTER user1 != user2
 * ...LET sumOfAges = user1.age + user2.age
 * ...FILTER sumOfAges < 100
 * ...RETURN {
 * ......pair: [user1.name, user2.name],
 * ......sumOfAges: sumOfAges
 * ...}
 */
public class ArnQuery {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FOR = "FOR %s IN %s";
    private static final String FILTER = "FILTER";
    private static final String SORT = "SORT";
    private static final String RETURN = "RETURN";
    private static final String LET = "LET";

    // geo  https://docs.arangodb.com/3.0/AQL/Functions/Geo.html
    /*
        FOR doc IN NEAR(@@collection, @latitude, @longitude, @limit)
            RETURN doc
     */
    private static final String NEAR = "FOR %s IN NEAR (%s"; // NEAR(coll, latitude, longitude, limit, distanceName) → docArray
    private static final String WITHIN = "FOR %s IN WITHIN (%s"; // WITHIN(coll, latitude, longitude, radius, distanceName) → docArray
    private static final String WITHIN_RECTANGLE = "FOR %s IN WITHIN_RECTANGLE (%s"; // WITHIN_RECTANGLE(coll, latitude1, longitude1, latitude2, longitude2) → docArray
    // geo utility (to use in combination with functions above , because this are not indexed and not optimized)
    private static final String IS_IN_POLYGON = "IS_IN_POLYGON"; // IS_IN_POLYGON(polygon, latitude, longitude) → bool


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final StringBuilder _sb;
    private final Map<String, String> _collections;
    private int _indent;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ArnQuery() {
        _sb = new StringBuilder();
        _collections = new LinkedHashMap<>();
        _indent = 0;
    }

    @Override
    public String toString() {
        return _sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String[] tables() {
        return _collections.keySet().toArray(new String[_collections.size()]);
    }

    // ------------------------------------------------------------------------
    //                      s t a t e m e n t s
    // ------------------------------------------------------------------------

    public ArnQuery FOR(final String collection_name) {
        final String t = this.addCollection(collection_name);
        this.addLine(FormatUtils.format(FOR, t, collection_name));
        return this;
    }

    public ArnQuery FILTER(final String filter) {
        if (StringUtils.hasText(filter)) {
            this.addLine(FILTER).append(" ").append(filter);
        }
        return this;
    }

    public ArnQuery LET(final String name, final String value) {
        if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
            this.addLine(LET).append(" ").append(name).append("=").append(value);
        }
        return this;
    }


    public ArnQuery RETURN(final Object ret) {
        if (null != ret) {
            this.addLine(RETURN).append(" ").append(ret.toString());
        }
        return this;
    }

    // ------------------------------------------------------------------------
    //                      g e o
    // ------------------------------------------------------------------------

    /**
     * FOR doc IN WITHIN (@@collection, 43.9794537, 12.4982482, 10000, "distance_mt")
     * RETURN doc
     */
    public ArnQuery WITHIN(final String collection_name, final String latitude, final String longitude,
                           final String radius_mt) {
        return this.WITHIN(collection_name, latitude, longitude, radius_mt, "distance_mt");
    }

    /**
     * WITHIN(coll, latitude, longitude, radius, distanceName)
     */
    public ArnQuery WITHIN(final String collection_name,
                           final String latitude,
                           final String longitude,
                           final String radius_mt,
                           final String distanceName) {
        if (StringUtils.hasText(collection_name)
                && StringUtils.hasText(latitude)
                && StringUtils.hasText(longitude)
                && StringUtils.hasText(radius_mt)
                && StringUtils.hasText(distanceName)) {

            final String t = this.addCollection(collection_name);
            this.addLine(FormatUtils.format(WITHIN, t, collection_name)).append(",")
                    .append(latitude).append(",")
                    .append(longitude).append(",")
                    .append(radius_mt).append(",")
                    .append(distanceName)
                    .append(")");
            this.addLine(SORT).append(" ").append(distanceName).append(" asc");
        }
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private StringBuilder addLine(final String line) {
        if (StringUtils.hasText(line)) {
            if (_sb.length() > 0) {
                _sb.append("\n");
                if (line.startsWith("FOR")) {
                    _indent++;
                }
            }
            for (int i = 0; i < _indent; i++) {
                _sb.append("\t");
            }

            _sb.append(line);
        }
        return _sb;
    }

    private String addCollection(final String collection_name) {
        final String name = "t" + (_collections.size() + 1);
        _collections.put(name, collection_name);
        return name;
    }

}
