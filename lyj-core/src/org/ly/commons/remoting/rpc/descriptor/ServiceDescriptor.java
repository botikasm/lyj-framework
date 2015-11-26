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
package org.ly.commons.remoting.rpc.descriptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author angelo.geminiani
 */
public class ServiceDescriptor {

    private String _name;
    private Class _serviceClass;
    private final Map<String, MethodDescriptor> _methods;

    public ServiceDescriptor() {
        _methods = Collections.synchronizedMap(new HashMap<String, MethodDescriptor>());
    }

    public ServiceDescriptor(final String name) {
        this();
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public ServiceDescriptor setName(String name) {
        _name = name;
        return this;
    }

    public Class getServiceClass() {
        return _serviceClass;
    }

    public ServiceDescriptor setServiceClass(Class serviceClass) {
        _serviceClass = serviceClass;
        return this;
    }

    public boolean hasMethod(final String name) {
        synchronized (_methods) {
            return _methods.containsKey(name);
        }
    }

    public MethodDescriptor getMethod(final String name) {
        synchronized (_methods) {
            if (_methods.containsKey(name)) {
                return _methods.get(name);
            } else {
                return null;
            }
        }
    }

    public MethodDescriptor getOrCreateMethod(final String name) {
        synchronized (_methods) {
            if (_methods.containsKey(name)) {
                return _methods.get(name);
            } else {
                final MethodDescriptor method = new MethodDescriptor();
                method.setName(name);
                _methods.put(name, method);
                return method;
            }
        }
    }
}
