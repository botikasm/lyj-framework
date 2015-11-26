/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * 
 */
package org.ly.commons.io.serialization.json.serializer;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 *
 * @author angelo.geminiani
 */
public class BeanProcessed {

    private Object _bean;
    private Object _parent;
    private JSONObject _jsonObject;
    private BeanData _data;
    private Object _beanId;

    public Object getBeanId() {
        return _beanId;
    }

    public BeanData getBeanData() {
        return _data;
    }

    public Object getBean() {
        return _bean;
    }

    public void setBean(Object bean) {
        _bean = bean;
        this.initBeanData(bean);
    }

    public Object getParent() {
        return _parent;
    }

    public void setParent(Object parent) {
        _parent = parent;
    }

    public JSONObject getJsonObject() {
        return _jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        _jsonObject = jsonObject;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private void initBeanData(final Object bean) {
        if (null != _bean) {
            _data = new BeanData(bean);
            _beanId = this.getId(_bean, _data, _bean.hashCode());
        }
    }

    private Object getId(final Object bean, final BeanData data,
            final Object defaultValue) {
        if (null != data
                && data.getReadableProps().containsKey("id")) {
            final Method method = data.getReadableProps().get("id");
            try {
                return method.invoke(bean, new Object[0]);
            } catch (Throwable t) {
            }
        }
        return defaultValue;
    }
}
