package org.lyj.ext.db.arango.query;

import org.lyj.commons.util.StringUtils;

/**
 * Helper class for query building.
 */
public class ArnQueryUtils {

    // ------------------------------------------------------------------------
    //                      A R R A Y
    // ------------------------------------------------------------------------

    /**
     * Sample:
     * FOR t IN sys_teams
     * FILTER ["admin","team"] ANY == t._key
     * RETURN t
     *
     * @param collection Collection NAme
     * @param field      Field Name
     * @param values     Array of String values
     * @return
     */
    public static String any(final String collection, final String field, final Object[] values) {
        final StringBuilder query = new StringBuilder();

        query.append("FOR t IN ").append(collection).append(" \n");
        query.append("FILTER ").append(arrayOf(values));
        query.append(" ANY == t.").append(field).append(" \n");
        query.append("RETURN t");

        return query.toString();
    }

    /**
     * FOR t IN sys_teams
     * FILTER ["admin","team"] ANY == t._key
     * RETURN t
     *
     * @param collection
     * @param field
     * @param values
     * @return
     */
    public static String none(final String collection,
                              final String field,
                              final Object[] values) {
        final StringBuilder query = new StringBuilder();

        query.append("FOR t IN ").append(collection).append(" \n");
        query.append("FILTER ").append(arrayOf(values));
        query.append(" NONE == t.").append(field).append(" \n");
        query.append("RETURN t");

        return query.toString();
    }

    public static String anyIn(final String collection,
                               final String field,
                               final Object[] values) {
        return anyIn(collection, field, values, "", 0, 0);
    }

    public static String anyIn(final String collection,
                               final String field,
                               final Object[] values,
                               final int limit_offset,
                               final int limit_count) {
        return anyIn(collection, field, values, "", limit_offset, limit_count);
    }

    public static String anyIn(final String collection,
                               final String field,
                               final Object[] values,
                               final String more_conditions,
                               final int limit_offset,
                               final int limit_count) {
        final StringBuilder query = new StringBuilder();

        query.append("FOR t IN ").append(collection).append(" \n");
        query.append("FILTER ");
        query.append("(");
        query.append(arrayOf(values));
        query.append(" ANY IN t.").append(field);
        query.append(")");
        if (StringUtils.hasText(more_conditions)) {
            query.append(" ").append(more_conditions);
        }
        query.append(" \n");

        // limit
        if (limit_count > 0) {
            query.append(" ").append("LIMIT ").append(limit_offset).append(", ").append(limit_count).append(" \n");
        }

        query.append("RETURN t");

        return query.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String arrayOf(final Object[] values) {
        final StringBuilder response = new StringBuilder();
        response.append("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                response.append(",");
            }
            final Object value = values[i];
            if (value instanceof String) {
                response.append("\"").append(value).append("\"");
            } else {
                response.append(value);
            }

        }
        response.append("]");
        return response.toString();
    }

}
