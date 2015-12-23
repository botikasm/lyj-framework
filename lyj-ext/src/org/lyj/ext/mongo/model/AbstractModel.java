package org.lyj.ext.mongo.model;

import org.bson.Document;
import org.lyj.commons.cryptograph.GUID;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.RandomUtils;
import org.lyj.ext.mongo.LyjMongo;
import org.lyj.ext.mongo.schema.AbstractSchema;
import org.lyj.ext.mongo.schema.LyjMongoField;
import org.lyj.ext.mongo.schema.LyjMongoSchemas;

/**
 * Base class for Mongo models
 */
public abstract class AbstractModel
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _id_prefix;
    private final AbstractSchema _schema;
    private final Document _document;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractModel(final Class<? extends AbstractSchema> schemaClass) {
        this(schemaClass, new Document(), "");
        this.initDefaults();
    }

    public AbstractModel(final Class<? extends AbstractSchema> schemaClass,
                         final String id_prefix) {
        this(schemaClass, new Document(), id_prefix);
    }

    public AbstractModel(final Class<? extends AbstractSchema> schemaClass, final Document document,
                         final String id_prefix) {
        _schema = this.getSchema(schemaClass);
        _document = document;
        _id_prefix = null != id_prefix ? id_prefix : "";
    }

    @Override
    public String toString() {
        return _document.toJson();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Document document() {
        return _document;
    }

    public void put(final String key, final Object value) {
        _document.put(key, value);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected String buildId() {
        return GUID.create(false, true).toLowerCase();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private AbstractSchema getSchema(final Class<? extends AbstractSchema> schemaClass) {
        AbstractSchema result = LyjMongo.getInstance().getSchemas().get(schemaClass);
        if (null == result) {
            try {
                result = schemaClass.newInstance();
            } catch (Throwable ignored) {
            }
        }
        return result;
    }

    private void initDefaults() {
        if (null != _schema) {
            _document.put("_id", _id_prefix.concat(this.buildId()));
            _document.put("_collection", _schema.getCollectionName());

            final LyjMongoField[] fields = _schema.fields();
            for (final LyjMongoField field : fields) {
                final Object value = field.getDefaultValue();
                if (null != value) {
                    _document.put(field.getName(), value); // 1450804004452
                }
            }
        }
    }

}
