package org.lyj.ext.mongo.model;

import org.bson.Document;
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

        this.initDefaults(false);
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

    public void putAll(final Document document, final boolean overwrite){
        LyjMongoObjects.extend(_document, document, overwrite);
    }

    public Object get(final String key) {
        return null != _document ? _document.get(key) : null;
    }

    public List getArray(final String key) {
        return LyjMongoObjects.getArray(_document, key);
    }

    public List getArray(final String key, final boolean addIfNone) {
        return LyjMongoObjects.getArray(_document, key, addIfNone);
    }

    public List<String> getArrayOfString(final String key) {
        return LyjMongoObjects.getArrayOfString(_document, key);
    }

    public List<String> getArrayOfString(final String key, final boolean addIfNone) {
        return LyjMongoObjects.getArrayOfString(_document, key, addIfNone);
    }

    public Document getDocument(final String key) {
        return LyjMongoObjects.getDocument(_document, key);
    }

    public Document getDocument(final String key, final boolean addIfNone) {
        return LyjMongoObjects.getDocument(_document, key, addIfNone);
    }

    public List<Document> getArrayOfDocument(final String key) {
        return LyjMongoObjects.getArrayOfDocument(_document, key);
    }

    public List<Document> getArrayOfDocument(final String key, final boolean addIfNone) {
        return LyjMongoObjects.getArrayOfDocument(_document, key, addIfNone);
    }

    public int getInteger(final String key) {
        return this.getInteger(key, 0);
    }

    public int getInteger(final String key, final int defVal) {
        return LyjMongoObjects.getInteger(_document, key, defVal);
    }

    public long getLong(final String key) {
        return this.getLong(key, 0L);
    }

    public long getLong(final String key, final long defVal) {
        return LyjMongoObjects.getLong(_document, key, defVal);
    }

    public double getDouble(final String key) {
        return this.getDouble(key, 0d);
    }

    public double getDouble(final String key, final Double defVal) {
        return LyjMongoObjects.getDouble(_document, key, defVal);
    }

    public double getDouble(final String key, final int decimalPlace, final Double defVal) {
        return LyjMongoObjects.getDouble(_document, key, decimalPlace, defVal);
    }

    public String getString(final String key) {
        return this.getString(key, "");
    }

    public String getString(final String key, final String defVal) {
        return LyjMongoObjects.getString(_document, key, defVal);
    }

    public boolean getBoolean(final String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(final String key, final boolean defVal) {
        return LyjMongoObjects.getBoolean(_document, key, defVal);
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

    public final void setId(final Object value){
        put(F_ID, value);
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

    private void initDefaults(final boolean overwrite) {
        if (null != _schema) {
            if(!_document.containsKey(F_ID)){
                _document.put(F_ID, _id_prefix.concat(this.UUID()));
            }
            if(overwrite || !_document.containsKey(F_COLLECTION)){
                _document.put(F_COLLECTION, _schema.getCollectionName());
            }

            final LyjMongoField[] fields = _schema.fields();
            for (final LyjMongoField field : fields) {
                final Object value = field.getDefaultValue();
                if (null != value) {
                    if(overwrite || !_document.containsKey(field.getName())){
                        _document.put(field.getName(), value); // 1450804004452
                    }
                }
            }
        }
    }

}
