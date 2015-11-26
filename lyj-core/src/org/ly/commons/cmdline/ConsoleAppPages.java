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

package org.ly.commons.cmdline;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manage Pages.
 */
public final class ConsoleAppPages {

    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    private static final String DEFAULT_PAGE = "default";

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Map<String, ConsoleAppPage> _pages; // key/value pair (key, instance)

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public ConsoleAppPages() {
        _pages = new HashMap<String, ConsoleAppPage>();
        this.init();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    @Override
    public String toString() {
        if (count() == 1) {
            return getDefault().toString();
        } else {
            // enum all pages
            final StringBuilder sb = new StringBuilder();
            final Set<String> keys = _pages.keySet();
            for (final String key : keys) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                final ConsoleAppPage page = _pages.get(key);
                sb.append("[").append(key).append("] ");
                sb.append(page.getName()).append(": ").append(page.getDescription());
            }
            return sb.toString();
        }
    }

    public int count() {
        return _pages.size();
    }

    public ConsoleAppPage getDefault() {
        return _pages.get(DEFAULT_PAGE);
    }

    public ConsoleAppPage getPage(final String key) {
        return _pages.get(key);
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void init() {
        _pages.put(DEFAULT_PAGE, new ConsoleAppPage());
    }

}
