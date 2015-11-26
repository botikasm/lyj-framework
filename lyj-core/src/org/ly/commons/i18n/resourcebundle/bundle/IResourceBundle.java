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
 * IResourceBundle.java
 */

package org.ly.commons.i18n.resourcebundle.bundle;

import java.util.Properties;

/**
 * Common interface for all XPoint resource bundles.
 * All bundles must implement this class.
 *
 * @author
 */
public interface IResourceBundle {

    public abstract boolean isActive();

    public abstract String getString(String key);

    public abstract Properties getProperties();

    public abstract Throwable getError();

}
