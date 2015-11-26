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

package org.ly.commons.lang.compilers;

import org.ly.commons.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class CompilerRegistry {

    //-- repos: key=extension, value=compiler --//
    private final Map<String, Class<? extends ICompiler>> _classes;
    private final Map<String, ICompiler> _objects;

    private CompilerRegistry() {
        _classes = Collections.synchronizedMap(new HashMap<String, Class<? extends ICompiler>>());
        _objects = Collections.synchronizedMap(new HashMap<String, ICompiler>());
    }

    public void registerClass(final String ext, final Class<? extends ICompiler> compilerClass) {
        synchronized (_classes) {
            _classes.put(this.removeDot(ext), compilerClass);
        }
    }

    public void registerInstance(final String ext, final ICompiler compilerInstance) {
        synchronized (_objects) {
            _objects.put(this.removeDot(ext), compilerInstance);
        }
    }

    public void removeAll(final String ext) {
        synchronized (_objects) {
            _objects.remove(this.removeDot(ext));
        }
        synchronized (_classes) {
            _classes.remove(this.removeDot(ext));
        }
    }

    public ICompiler getCompiler(final String ext) {
        synchronized (_objects) {
            final String key = removeDot(ext);
            if (_objects.containsKey(key)) {
                return _objects.get(key);
            } else {
                //-- creates compile instance --//
                synchronized (_classes) {
                    final ICompiler instance = this.createCompiler(key);
                    if (null != instance) {
                        _objects.put(key, instance);
                    }
                    return instance;
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String removeDot(final String ext) {
        return StringUtils.replace(ext, ".", "");
    }

    private ICompiler createCompiler(final String key) {
        if (_classes.containsKey(key)) {
            try {
                return _classes.get(key).newInstance();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static CompilerRegistry __instance;

    private static CompilerRegistry getInstance() {
        if (null == __instance) {
            __instance = new CompilerRegistry();
        }
        return __instance;
    }

    public static void register(final String ext, final Class<? extends ICompiler> compilerClass) {
        getInstance().registerClass(ext, compilerClass);
    }

    public static void register(final String ext, final ICompiler compilerInstance) {
        getInstance().registerInstance(ext, compilerInstance);
    }

    public static void remove(final String ext) {
        getInstance().removeAll(ext);
    }

    public static ICompiler get(final String ext) {
        return getInstance().getCompiler(ext);
    }
}
