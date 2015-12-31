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

    public LyjMongoAggregate group(final String...fieldNames) {
        if(fieldNames.length>0){
            final Document id = new Document();
            for(final String fieldName:fieldNames){
                id.append(fieldName, "$".concat(fieldName));
            }
            this.get($GROUP).put(F_ID, id);
        }

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

    public LyjMongoAggregate groupCount(final String alias) {
        this.get($GROUP).put(alias, new Document($SUM, 1));
        return this;
    }

    public LyjMongoAggregate project(final String alias, final String fieldName) {
        this.get($PROJECT).put(alias, "$".concat(fieldName));
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

    public LyjMongoAggregate max(final String maxAlias, final String fieldName) {
        this.get($GROUP).put("max" + StringUtils.capitalize(maxAlias), new Document($MAX, "$".concat(fieldName)));
        return this;
    }

    public LyjMongoAggregate max(final String maxAlias, final Document expression) {
        this.get($GROUP).put("max" + maxAlias, new Document($MAX, expression));
        return this;
    }

    public LyjMongoAggregate min(final String minAlias, final String fieldName) {
        this.get($GROUP).put("min" + StringUtils.capitalize(minAlias), new Document($MIN, "$".concat(fieldName)));
        return this;
    }

    public LyjMongoAggregate min(final String minAlias, final Document expression) {
        this.get($GROUP).put("min" + minAlias, new Document($MIN, expression));
        return this;
    }

    public LyjMongoAggregate sort(final Document sort) {
        this.get($SORT).putAll(sort);
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
