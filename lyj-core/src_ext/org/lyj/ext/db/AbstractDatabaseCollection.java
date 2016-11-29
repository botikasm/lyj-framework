package org.lyj.ext.db;

import org.json.JSONObject;
import org.lyj.commons.util.BeanUtils;
import org.lyj.commons.util.StringUtils;

/**
 *
 */
public abstract class AbstractDatabaseCollection<T>
        implements IDatabaseCollection<T> {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final IDatabase _database;
    private final Class<T> _entity_class;
    private final String _name;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractDatabaseCollection(final IDatabase database,
                                      final String name,
                                      final Class<T> entity_class) {
        _database = database;
        _name = name;
        _entity_class = entity_class;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public String name() {
        return _name;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected IDatabase database() {
        return _database;
    }

    protected Class<T> entityClass() {
        return _entity_class;
    }

    protected Object getFieldValue(final Object entity, final String name) {
        try {
            final Object bean;
            if (StringUtils.isJSONObject(entity)) {
                bean = new JSONObject(entity.toString());
            } else {
                bean = entity;
            }
            return bean instanceof String ? bean : BeanUtils.getValue(bean, name);
        } catch (Throwable ignored) {
        }
        return entity;
    }

}
