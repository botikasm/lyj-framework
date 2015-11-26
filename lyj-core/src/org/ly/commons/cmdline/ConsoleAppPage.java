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

import org.ly.commons.Delegates;
import org.ly.commons.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public final class ConsoleAppPage {
// --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private String _name;
    private String _description;

    private final Map<String, Command> _commands;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public ConsoleAppPage() {
        _commands = new LinkedHashMap<String, Command>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PAGE: ").append(_name);
        if (StringUtils.hasText(_description)) {
            sb.append(" (").append(_description).append(")");
        }
        sb.append("\n");
        sb.append("COMMANDS: ");
        int count = 0;
        final Set<String> keys = _commands.keySet();
        for (final String key : keys) {
            if (count > 0) {
                sb.append(", ");
            }
            count++;
            sb.append(key);
        }
        return sb.toString();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------


    public String getName() {
        return _name;
    }

    public void setName(final String value) {
        _name = value;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(final String value) {
        _description = value;
    }

    public String enumCommands() {
        final StringBuilder sb = new StringBuilder();
        final Set<String> keys = _commands.keySet();
        for (final String key : keys) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            final Command cmd = _commands.get(key);
            sb.append("[").append(key).append("] ");
            sb.append(cmd.getName());
        }
        return sb.toString();
    }

    public boolean hasCommand(final String key) {
        return _commands.containsKey(key);
    }

    public Object runCommand(final String key, final Object... args) {
        final Command cmd = _commands.get(key);
        if (null != cmd) {
            return cmd.run(args);
        }
        return null;
    }

    public void addCommand(final String key, final String name, final Delegates.Function cmd) {
        _commands.put(key, new Command(key, name, cmd));
    }

    // --------------------------------------------------------------------
    //               EMBEDDED
    // --------------------------------------------------------------------

    public class Command {

        private final String _key;
        private final Delegates.Function _cmd;
        private String _name;

        public Command(final String key, final String name, final Delegates.Function cmd) {
            _key = key;
            _cmd = cmd;
            _name = name;
        }

        public String getKey() {
            return _key;
        }

        public Object run(final Object... args) {
            return null != _cmd ? _cmd.handle(args) : null;
        }

        public String getName() {
            return _name;
        }

        public void setName(final String value) {
            _name = value;
        }

    }
}
