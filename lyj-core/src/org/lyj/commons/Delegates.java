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

package org.lyj.commons;

import org.lyj.commons.async.Async;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.BeanUtils;
import org.lyj.commons.util.FormatUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Common listeners repository.
 */
public class Delegates {


    @FunctionalInterface
    public static interface Handler {
        void handle();
    }

    // --------------------------------------------------------------------
    //               F u n c t i o n   D e l e g a t e s
    // --------------------------------------------------------------------

    @FunctionalInterface
    public static interface CreateRunnableCallback {
        Runnable handle(final int index, final int length);
    }

    @FunctionalInterface
    public static interface FunctionArgs<T> {
        T handle(final Object... args);
    }

    /**
     * Callback is defined with two generic parameters: the first parameter
     * specifies the type of the object passed in to the <code>call</code> method,
     * with the second parameter specifying the return type of the method.
     *
     * @param <P> The type of the argument provided to the <code>call</code> method.
     * @param <R> The type of the return type of the <code>call</code> method.
     */
    @FunctionalInterface
    public static interface FunctionArg<P, R> {
        R call(P param);
    }

    @FunctionalInterface
    public static interface Function<T> {
        T handle();
    }


    // --------------------------------------------------------------------
    //               E v e n t s
    // --------------------------------------------------------------------

    @FunctionalInterface
    public static interface ExceptionCallback {
        void handle(final String message, final Throwable err);
    }

    /**
     * Simple handler for Async Action
     */
    @FunctionalInterface
    public static interface VarArgsCallback {
        void handle(final Object... args);
    }

    @FunctionalInterface
    public static interface Callback<T> {
        void handle(final T data);
    }

    @FunctionalInterface
    public static interface SingleResultCallback<T> {
        void handle(final Throwable err, final T data);
    }

    @FunctionalInterface
    public static interface ResultCallback {
        void handle(final Throwable err, final Object... data);
    }

    /**
     * Callback for progress indicators.
     */
    @FunctionalInterface
    public static interface ProgressCallback {
        void handle(final int index, final int length, final double progress);
    }


    // --------------------------------------------------------------------
    //               C o l l e c t i o n s
    // --------------------------------------------------------------------

    @FunctionalInterface
    public static interface IterationBoolCallback<T> {
        boolean handle(final T item, final int index, final Object key);
    }

    // --------------------------------------------------------------------
    //               I n v o k e r
    // --------------------------------------------------------------------


    public static void invoke(final Handler callback) {
        try {
            if (null != callback) {
                callback.handle();
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static <T> T invoke(final FunctionArgs<T> callback, final Object... args) {
        try {
            if (null != callback) {
                return callback.handle(args);
            }
            return null;
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static void invoke(final ExceptionCallback callback, final String message, final Throwable err) {
        try {
            if (null != callback) {
                callback.handle(message, err);
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static void invoke(final VarArgsCallback callback, final Object... args) {
        try {
            if (null != callback) {
                callback.handle(args);
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static <T> void invoke(final Callback<T> callback, final T data) {
        try {
            if (null != callback) {
                callback.handle(data);
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static <T> void invoke(final SingleResultCallback<T> callback, final Throwable err, final T data) {
        try {
            if (null != callback) {
                callback.handle(err, data);
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static void invoke(final ResultCallback callback, final Throwable err, final Object... data) {
        try {
            if (null != callback) {
                callback.handle(err, data);
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static void invoke(final ProgressCallback callback, final int index, final int length, final double progress) {
        try {
            if (null != callback) {
                callback.handle(index, length, progress);
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    public static <T> boolean invoke(final IterationBoolCallback<T> callback, final T item, final int index, final Object key) {
        try {
            if (null != callback) {
                return callback.handle(item, index, key);
            } else {
                return false;
            }
        } catch (Throwable t) {
            logger().error("invoke", t);
            throw new RuntimeException(t);
        }
    }

    // --------------------------------------------------------------------
    //               P R I V A T E
    // --------------------------------------------------------------------

    private static Logger logger() {
        return LoggingUtils.getLogger(Delegates.class);
    }

    // --------------------------------------------------------------------
    //               E v e n t   H a n d l e r s    P o o l
    // --------------------------------------------------------------------

    public static final class Handlers {

        private final Map<Class, List<Object>> _handlers;

        public Handlers() {
            _handlers = Collections.synchronizedMap(new HashMap<Class, List<Object>>());
        }

        public void clear() {
            synchronized (_handlers) {
                if (!_handlers.isEmpty()) {
                    final Collection<List<Object>> values = _handlers.values();
                    for (final List<Object> list : values) {
                        list.clear();
                    }
                }
                _handlers.clear();
            }
        }

        public int size() {
            return _handlers.size();
        }

        public int size(final Class hclass) {
            synchronized (_handlers) {
                return _handlers.containsKey(hclass) ? _handlers.get(hclass).size() : 0;
            }
        }

        public boolean contains(final Class hclass) {
            return _handlers.containsKey(hclass);
        }

        public void add(final Object handler) {
            if (null != handler) {
                final Class hclass = this.getInterfaceClass(handler.getClass());
                this.add(hclass, handler);
            }
        }


        public void triggerAsync(final Class hclass, final Object... args) {
            synchronized (_handlers) {
                if (_handlers.containsKey(hclass)) {
                    this.trigger(true, _handlers.get(hclass), args);
                } else {
                    this.getLogger().fine(FormatUtils.format("No handlers of type '{0}'!", hclass.getName()));
                }
            }
        }

        public void trigger(final Class hclass, final Object... args) {
            synchronized (_handlers) {
                if (_handlers.containsKey(hclass)) {
                    this.trigger(false, _handlers.get(hclass), args);
                } else {
                    this.getLogger().fine(FormatUtils.format("No handlers of type '{0}'!", hclass.getName()));
                }
            }
        }

        //--   p r i v a t e   --//

        private Logger getLogger() {
            return LoggingUtils.getLogger(this);
        }

        private void add(final Class hclass, final Object handler) {
            synchronized (_handlers) {
                if (!_handlers.containsKey(hclass)) {
                    final List<Object> list = new LinkedList<Object>();
                    _handlers.put(hclass, list);
                }
                _handlers.get(hclass).add(handler);
            }
        }

        private void trigger(final boolean async, final List<Object> handlers,
                             final Object... args) {
            for (final Object handler : handlers) {
                final Class hclass = this.getInterfaceClass(handler);
                final Method method = null != args && args.length > 0
                        ? BeanUtils.getMethodIfAny(hclass, "handle", args)
                        : BeanUtils.getMethodIfAny(hclass, "handle");
                if (null != method) {
                    if (async) {
                        //Async.Action((args2)->{});
                        Async.invoke(new VarArgsCallback() {
                            @Override
                            public void handle(final Object... args2) {
                                try {
                                    method.invoke(handler, args2);
                                } catch (Throwable t) {
                                    // manage execution error
                                }
                            }
                        }, args);
                    } else {
                        try {
                            method.invoke(handler, args);
                        } catch (Throwable t) {
                            // manage execution error
                        }
                    }
                }
            }
        }

        private Class getInterfaceClass(final Object instance) {
            return null != instance
                    ? this.getInterfaceClass(instance.getClass())
                    : null;
        }

        private Class getInterfaceClass(final Class hclass) {
            try {
                return hclass.getInterfaces()[0];
            } catch (Throwable ignored) {
            }
            return null;
        }
    }

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    private Delegates() {
    }

}
