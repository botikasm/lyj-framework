package org.lyj.ext.mongo.utils;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.lyj.commons.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Helper class to build filters for mongo query
 */
public class LyjMongoFilter {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String NE = "$ne";
    private static final String NIN = "$nin";

    private static final String OR = "$or";
    private static final String AND = "$and";

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

    public LyjMongoFilter notEquals(final String fieldName, final Object value) {
        _filter.put(fieldName, new Document(NE, value));
        return this;
    }

    public LyjMongoFilter notIn(final String fieldName, final Collection list) {
        _filter.put(fieldName, new Document(NIN, list));
        return this;
    }

    public LyjMongoFilter or(final Collection<Bson> conditions) {
        _filter.put(OR, conditions);
        return this;
    }

    public LyjMongoFilter or(final Bson... conditions) {
        _filter.put(OR, Arrays.asList(conditions));
        return this;
    }

    public LyjMongoFilter and(final Collection<Bson> conditions) {
        _filter.put(AND, conditions);
        return this;
    }

    public LyjMongoFilter and(final Bson... conditions) {
        _filter.put(AND, Arrays.asList(conditions));
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

}
