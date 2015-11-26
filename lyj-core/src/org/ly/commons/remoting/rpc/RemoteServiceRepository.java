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
package org.ly.commons.remoting.rpc;

import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.util.BeanUtils;
import org.ly.commons.util.CollectionUtils;
import org.ly.commons.util.StringUtils;
import org.ly.commons.remoting.rpc.descriptor.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Services are registered into internal Map with a compound key ("endpoint.name")
 *
 * @author angelo.geminiani
 */
public class RemoteServiceRepository {

    private static final String SEP = ".";
    private final Map<String, ServiceDescriptor> _services;

    public RemoteServiceRepository() {
        _services = Collections.synchronizedMap(new HashMap<String, ServiceDescriptor>());
    }

    public ServiceDescriptor getService(final String endpoint,
                                     final String serviceName) {
        synchronized (_services) {
            final String name = this.getName(endpoint, serviceName);
            return _services.get(name);
        }
    }

    public void registerService(final String name,
                                final Class<?> serviceClass)
            throws Exception {
        this.registerService("", name,
                this.getServiceFromClass(name, serviceClass));
    }

    public void registerService(final String endpoint,
                                final String name,
                                final Class<?> serviceClass)
            throws Exception {
        this.registerService(endpoint, name,
                this.getServiceFromClass(name, serviceClass));
    }

    public ServiceDescriptor registerService(final String endpoint,
                                          final String serviceName,
                                          final ServiceDescriptor service) throws Exception {
        final String name = this.getName(endpoint, serviceName);
        synchronized (_services) {
            final ServiceDescriptor exists = _services.get(name);
            if (exists != null && exists != service) {
                throw new Exception("different service already registered as " + name);
            }
            if (exists == null) {
                _services.put(name, service);
            }
        }
        final Logger logger = this.getLogger();
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                    "registered service {0} as {1}",
                    new Object[]{service.getName(), name});
        }
        return service;
    }

    /**
     * Unregisters a class exported with registerClass. <p/> The JSONBridge will
     * unexport all static methods of the class.
     *
     * @param serviceName The registered name of the class to unexport static methods
     *                    from.
     */
    public void unregisterService(final String endpoint,
                                  final String serviceName) {
        synchronized (_services) {
            final String name = this.getName(endpoint, serviceName);
            final ServiceDescriptor service = _services.get(name);
            if (service != null) {
                _services.remove(name);
                final Logger logger = this.getLogger();
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,
                            "unregistered class {0} from {1}",
                            new Object[]{service.getName(), name});
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private String getName(final String endpoint,
                           final String serviceName) {
        if (StringUtils.hasText(endpoint)) {
            return endpoint.concat(SEP).concat(serviceName);
        } else {
            return serviceName;
        }
    }

    private ServiceDescriptor getServiceFromClass(final String name,
                                               final Class<?> aclass) {
        final ServiceDescriptor service = new ServiceDescriptor(name);
        service.setServiceClass(aclass);
        // methods
        final Method[] methods = BeanUtils.getPublicMethods(aclass);
        if (methods.length > 0) {
            for (final Method method : methods) {
                final String methodname = method.getName();
                final Class[] types = method.getParameterTypes();
                final MethodDescriptor restmethod = service.getOrCreateMethod(methodname);
                if (!CollectionUtils.isEmpty(types)) {
                    for (int i = 0; i < types.length; i++) {
                        final Class ctype = types[i];
                        // add parameter
                        restmethod.addParameter(ctype);
                    }
                }
            }
        }
        return service;
    }
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    /**
     * Global bridge (for exporting to all users)
     */
    private final static RemoteServiceRepository _instance = new RemoteServiceRepository();

    /**
     * This method retrieves the global bridge singleton. <p/> It should be used
     * with care as objects should generally be registered within session specific
     * bridges for security reasons.
     *
     * @return returns the global bridge object.
     */
    public static RemoteServiceRepository getInstance() {
        return _instance;
    }
}
