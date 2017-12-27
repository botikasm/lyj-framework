package org.lyj.ext.db.arango.query;

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
    public static String none(final String collection, final String field, final Object[] values) {
        final StringBuilder query = new StringBuilder();

        query.append("FOR t IN ").append(collection).append(" \n");
        query.append("FILTER ").append(arrayOf(values));
        query.append(" NONE == t.").append(field).append(" \n");
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
