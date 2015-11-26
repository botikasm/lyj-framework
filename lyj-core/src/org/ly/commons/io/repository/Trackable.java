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

package org.ly.commons.io.repository;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Parent interface for both Repository and Resource interfaces.
 * Describes an entity defined by a file-system like path.
 */
public interface Trackable extends Serializable {

    /**
     * Returns the date the resource was last modified
     *
     * @return last modified date
     */
    public long lastModified();

    /**
     * Checksum of the resource content. Implementations should make sure
     * to return a different checksum if the resource's content has changed.
     *
     * @return checksum
     */
    public long getChecksum() throws IOException;

    /**
     * Checks wether this resource actually (still) exists
     *
     * @return true if the resource exists
     */
    public boolean exists() throws IOException;

    /**
     * Returns the path of the resource. The returned string must be in a form so
     * that appending a child name produces a valid resource or repository name.
     * Usually this means that it should end with a file separator character.
     *
     * @return path of the resource
     */
    public String getPath();

    /**
     * Returns the short name of the resource.
     *
     * @return short name of the resource
     */
    public String getName();

    /**
     * Returns an url to the resource if the repository of this resource is
     * able to provide urls.
     *
     * @return url to the resource
     */
    public URL getUrl() throws UnsupportedOperationException, MalformedURLException;

    /**
     * Returns the parent repository containing this resource
     *
     * @return parent repository
     */
    public Repository getParentRepository();

    /**
     * Returns the root repository of this resource
     *
     * @return root repository
     */
    public Repository getRootRepository();

    /**
     * Utility method to get the name for the module defined by this resource.
     *
     * @return the module name according to the securable module spec
     */
    public String getModuleName();

    /**
     * Get the path of this resource relative to its root repository.
     * The returned string must be in a form so that appending a child
     * name produces a valid resource or repository name. Usually this means
     * that it should end with a file separator character.
     *
     * @return the relative resource path
     */
    public String getRelativePath();

    /**
     * Set this Trackable to absolute mode. This will cause all its
     * relative path operations to use absolute paths instead.
     *
     * @param absolute true to operate in absolute mode
     */
    public void setAbsolute(boolean absolute);

    /**
     * Return true if this Trackable is in absolute mode.
     *
     * @return true if absolute mode is on
     */
    public boolean isAbsolute();

}
