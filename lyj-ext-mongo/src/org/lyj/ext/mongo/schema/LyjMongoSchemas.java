package org.lyj.ext.mongo.schema;

import org.bson.Document;
import org.lyj.commons.Delegates;

import java.util.*;

/**
 * Schema repository
 */
public final class LyjMongoSchemas {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<Class<? extends AbstractSchema>, AbstractSchema> _schemas;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LyjMongoSchemas() {
        _schemas = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void register(final AbstractSchema schema) {
        _schemas.put(schema.getClass(), schema);
    }

    public void ensureIndexes(final Delegates.SingleResultCallback<Map<String, List<Document>>> callback) {
        final Map<String, List<Document>> report = new LinkedHashMap<>();
        final Collection<AbstractSchema> schemas = _schemas.values();
        for (final AbstractSchema schema : schemas) {
            schema.ensureIndexes((err, indexes) -> {
                if (null == err) {
                    report.put(schema.getDatabaseName().concat(".").concat(schema.getCollectionName()), indexes);
                }
            });
        }
        Delegates.invoke(callback, null, report);
    }

    public AbstractSchema get(final Class<? extends AbstractSchema> schemaClass) {
        return _schemas.get(schemaClass);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
