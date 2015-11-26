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
package org.ly.commons.network.shorturl;

import org.ly.commons.network.shorturl.impl.TinyUrl;
import org.ly.commons.util.CollectionUtils;
import org.ly.commons.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author angelo.geminiani
 */
public class ShortURL {

    public static final String TINYURL = "tinyurl";
    private static final String DEFAULT_SERVICE = TINYURL;
    private final Map<String, Class<? extends IURLShortener>> _services;

    private ShortURL() {
        _services = Collections.synchronizedMap(
                new HashMap<String, Class<? extends IURLShortener>>());
        this.init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _services.clear();
        } catch (Exception ignored) {
        }
        super.finalize();
    }

    public final String getShortUrl(final String serviceId, final String url) {
        try {
            return tryShortUrl(serviceId, url);
        } catch (Exception e) {
            return this.tryShortUrl(url);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private void init() {
        //-- register services --//
        _services.put(TINYURL, TinyUrl.class);
    }

    private IURLShortener getService(final String serviceId) {
        synchronized (_services) {
            final Class srvcClass = _services.get(serviceId);
            final IURLShortener result = this.createService(srvcClass);
            return null != result ? result : new TinyUrl();
        }
    }

    private IURLShortener createService(final Class srvcClass) {
        if (null != srvcClass) {
            try {
                return (IURLShortener) srvcClass.newInstance();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private String tryShortUrl(final String url) {
        synchronized (_services) {
            if (!CollectionUtils.isEmpty(_services)) {
                final Collection<Class<? extends IURLShortener>> services = _services.values();
                for (final Class<? extends IURLShortener> srvcClass : services) {
                    final IURLShortener srvc = this.createService(srvcClass);
                    if (null != srvc) {
                        try {
                            return srvc.getShortUrl(url);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            return url;
        }
    }

    private String tryShortUrl(final String serviceId, final String url)
            throws Exception {
        if (StringUtils.hasText(url)) {
            final IURLShortener srvc = this.getService(serviceId);
            if (null != serviceId) {
                final String shorturi = srvc.getShortUrl(url);
                if (StringUtils.hasText(shorturi)) {
                    if (shorturi.length() < url.length()) {
                        return shorturi;
                    }
                }
            }
        }
        return url;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static ShortURL __instance;

    private static ShortURL getInstance() {
        if (null == __instance) {
            __instance = new ShortURL();
        }
        return __instance;
    }

    public static String get(final String url) {
        final ShortURL instance = getInstance();
        return instance.getShortUrl(DEFAULT_SERVICE, url);
    }

    public static String get(final String serviceId, final String url) {
        final ShortURL instance = getInstance();
        return instance.getShortUrl(serviceId, url);
    }
}
