package org.lyj.ext.mongo.utils;

import com.sun.xml.internal.bind.v2.model.core.ID;
import org.bson.Document;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.mongo.ILyjMongoConstants;

import java.util.*;

/**
 * Helper class to build filters for mongo query.
 * <p>
 * In Aggregation the sequence order of commands is important.
 * To filter an aggregation remember to declare a "$match" as first command.
 */
public class LyjMongoAggregate
        implements ILyjMongoConstants {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Document> _condition;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private LyjMongoAggregate() {
        _condition = new LinkedHashMap<>();
    }

    @Override
    public String toString() {
        return LyjMongoObjects.toJson(_condition.values());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public List<Document> asBson() {
        return new LinkedList<>(_condition.values());
    }

    public LyjMongoAggregate match(final Document filter) {
        LyjMongoObjects.extend(this.get($MATCH), filter, true);
        return this;
    }

    public LyjMongoAggregate match(final String key, final Object value) {
        this.get($MATCH).put(key, value);
        return this;
    }

    public LyjMongoAggregate group(final String fieldName) {
        this.get($GROUP).put(F_ID, "$".concat(fieldName));
        return this;
    }

    public LyjMongoAggregate group(final Document expression) {
        this.get($GROUP).put(F_ID, expression);
        return this;
    }

    public LyjMongoAggregate avg(final String avgAlias, final String fieldName) {
        this.get($GROUP).put("avg" + StringUtils.capitalize(avgAlias), new Document($AVG, "$".concat(fieldName)));
        return this;
    }

    public LyjMongoAggregate avg(final String avgAlias, final Document expression) {
        this.get($GROUP).put("avg" + avgAlias, new Document($AVG, expression));
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Document get(final String key) {
        if (!_condition.containsKey(key)) {
            _condition.put(key, new Document(key, new Document()));
        }
        return (Document) _condition.get(key).get(key);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static LyjMongoAggregate create() {
        return new LyjMongoAggregate();
    }


}
