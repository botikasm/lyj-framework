package org.lyj.ext.mongo.model;

import org.bson.Document;
import org.lyj.commons.cryptograph.GUID;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.RandomUtils;
import org.lyj.ext.mongo.ILyjMongoConstants;
import org.lyj.ext.mongo.LyjMongo;
import org.lyj.ext.mongo.schema.AbstractSchema;
import org.lyj.ext.mongo.schema.LyjMongoField;
import org.lyj.ext.mongo.utils.LyjMongoObjects;

import java.util.List;

/**
 * Base class for Mongo models
 */
public abstract class AbstractModel
        extends AbstractLogEmitter
        implements ILyjMongoConstants {

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

    public AbstractModel(final Class<? extends AbstractSchema> schemaClass,
                         final Document document) {
        this(schemaClass, document, "");
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

    public Object get(final String key) {
        return null != _document ? _document.get(key) : null;
    }

    public List<String> getArrayOfString(final String key) {
        return LyjMongoObjects.getArrayOfString(_document, key);
    }

    public List<String> getArrayOfString(final String key, final boolean addIfNone) {
        return LyjMongoObjects.getArrayOfString(_document, key, addIfNone);
    }

    public List<Document> getArrayOfDocument(final String key) {
        return LyjMongoObjects.getArrayOfDocument(_document, key);
    }

    public List<Document> getArrayOfDocument(final String key, final boolean addIfNone) {
        return LyjMongoObjects.getArrayOfDocument(_document, key, addIfNone);
    }

    public int getInteger(final String key) {
        return LyjMongoObjects.getInteger(_document, key);
    }

    public long getLong(final String key) {
        return LyjMongoObjects.getLong(_document, key);
    }

    public String getString(final String key) {
        return LyjMongoObjects.getString(_document, key);
    }

    public final Object getId() {
        return get(F_ID);
    }

    public final String getIdString() {
        final Object id = get(F_ID);
        if (null != id) {
            if (id instanceof String) {
                return (String) id;
            } else {
                return id.toString();
            }
        } else {
            return "";
        }
    }

    public final String getCollection() {
        return getString(F_COLLECTION);
    }

    public String UUID() {
        return RandomUtils.randomUUID(true);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected boolean hasField(final String name) {
        if (null != _schema) {
            return _schema.hasField(name);
        }
        return false;
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
            _document.put(F_ID, _id_prefix.concat(this.UUID()));
            _document.put(F_COLLECTION, _schema.getCollectionName());

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
