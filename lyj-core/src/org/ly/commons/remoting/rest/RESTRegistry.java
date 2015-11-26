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

package org.ly.commons.remoting.rest;


import org.ly.commons.util.StringUtils;
import org.ly.commons.remoting.rest.wrapper.MethodWrapper;
import org.ly.commons.remoting.rest.wrapper.ServiceWrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RESTRegistry {

    private final Map<String, Map<String, MethodWrapper>> _repo;

    private RESTRegistry() {
        _repo = Collections.synchronizedMap(new HashMap<String, Map<String, MethodWrapper>>());
    }

    public void add(final ServiceWrapper wrapper) {
        if (null != wrapper && wrapper.hasMethods()) {
            synchronized (_repo) {
                final Map<String, MethodWrapper> repo_methods = this.getRepoValue(wrapper.getPath());
                final MethodWrapper[] methods = wrapper.getMethods();
                for(final MethodWrapper m:methods){
                    repo_methods.put(m.getId(), m);
                }
            }
        }
    }

    public boolean containsKey(final String key){
        return _repo.containsKey(key);
    }

    public MethodWrapper lookup(final String httpMethod, final String url){
        final String[] tokens = StringUtils.split(url, "/");
        if(tokens.length>0){
            final String key = "/".concat( tokens[0] );
            if(_repo.containsKey(key)){
                final Collection<MethodWrapper> methods = _repo.get(key).values();
                for(final MethodWrapper m:methods){
                    if(m.match(httpMethod, url)){
                        return m;
                    }
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Map<String, MethodWrapper> getRepoValue(final String key) {
        if (!_repo.containsKey(key)) {
            _repo.put(key, new HashMap<String, MethodWrapper>());
        }
        return _repo.get(key);
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static RESTRegistry __instance;

    private static RESTRegistry getInstance() {
        if (null == __instance) {
            __instance = new RESTRegistry();
        }
        return __instance;
    }

    public static void register(final Class<? extends RESTService> service) throws Exception {
        final ServiceWrapper wrapper = new ServiceWrapper(service);
        getInstance().add(wrapper);
    }

    public static boolean contains(final String key){
        return getInstance().containsKey(key);
    }

    public static MethodWrapper getMethod (final String httpMethod, final String url){
        return getInstance().lookup(httpMethod, url);
    }
}
