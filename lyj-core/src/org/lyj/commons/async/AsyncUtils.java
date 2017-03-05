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

package org.lyj.commons.async;

import org.lyj.commons.Delegates;
import org.lyj.commons.util.MapBuilder;

import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 *
 */
public class AsyncUtils {

    /**
     * The minimum priority that a thread can have.
     */
    private final static int MIN_PRIORITY = Thread.MIN_PRIORITY;

    /**
     * The default priority that is assigned to a thread.
     */
    private final static int NORM_PRIORITY = Thread.NORM_PRIORITY;

    /**
     * The maximum priority that a thread can have.
     */
    private final static int MAX_PRIORITY = Thread.MAX_PRIORITY;

    public final static Map<Integer, String> PRIORITIES = MapBuilder.createIS()
            .put(MIN_PRIORITY, "minimum")
            .put(NORM_PRIORITY, "default")
            .put(MAX_PRIORITY, "maximum")
            .toMap();

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /**
     * Creates array of Async actions
     *
     * @param length
     * @param callback
     * @return
     */
    public static Thread[] createArray(final int length, final Delegates.CreateRunnableCallback callback) {
        Thread[] result = new Thread[length];
        for (int i = 0; i < length; i++) {
            final Runnable action = null != callback ? callback.handle(i, length) : getEmptyAction();
            result[i] = new Thread(action);
            result[i].setDaemon(true);
        }
        return result;
    }

    public static int countThreads() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }

    public static int countThreadsInGroup() {
        return Thread.activeCount();
    }

    public static Thread[] enumerateActiveThreads() {
        final Thread[] array = new Thread[Thread.activeCount()];
        Thread.enumerate(array);
        return array;
    }

    public static String reportActiveThreads() {
        final StringBuilder sb = new StringBuilder();
        final Thread[] threads = enumerateActiveThreads();
        sb.append("------------------------------------------------").append("\n");
        sb.append("ACTIVE THREADS: ").append(threads.length).append("\n");
        sb.append("TOTAL THREADS: ").append(countThreads()).append("\n");
        sb.append("------------------------------------------------");
        for (final Thread t : threads) {
            sb.append("\n");
            sb.append("[").append(t.getId()).append("]").append(" ");
            sb.append(t.getName()).append(": ");
            sb.append("(");
            sb.append("priority=").append(PRIORITIES.get(t.getPriority())).append(", ");
            sb.append("state=").append(t.getState()).append(", ");
            sb.append("group=").append(t.getThreadGroup().getName());
            sb.append(")");
        }
        sb.append("\n");
        sb.append("------------------------------------------------");
        return sb.toString();
    }
    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static Runnable getEmptyAction() {
        return new Runnable() {
            @Override
            public void run() {
                // nothing to to, just a stub
            }
        };
    }

}
