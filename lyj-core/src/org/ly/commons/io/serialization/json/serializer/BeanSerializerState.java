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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author angelo.geminiani
 */
public class BeanSerializerState {

    private Map<Object, BeanProcessed> _processedObjects;

    public BeanSerializerState() {
        _processedObjects = new HashMap<Object, BeanProcessed>();
    }

    public boolean isProcessed(final Object bean){
        return _processedObjects.containsKey(bean);
    }

    public BeanProcessed getProcessedBean(final Object bean){
        return _processedObjects.get(bean);
    }

    public BeanProcessed addProcessedBean(final Object parent, final Object bean){
        final BeanProcessed item = new  BeanProcessed();
        item.setBean(bean);
        item.setParent(parent);
        _processedObjects.put(bean, item);
        return item;
    }
}
