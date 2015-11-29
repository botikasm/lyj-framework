
/*
 * LY (lyj framework)
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

package org.lyj.commons.io.repository;

import java.io.File;
import java.io.IOException;

/**
 * Repository represents an abstract container of resources.
 * In addition to resources, repositories may contain other repositories, building
 * a hierarchical structure.
 */
public interface Repository extends Trackable {

    /**
     * String containing file separator characters. Always include slash character,
     * plus the native separator char if it isn't the slash.
     */
    final public static String SEPARATOR = File.separatorChar == '/' ? "/" : File.separator + "/";

    /**
     * Returns a specific direct resource of the repository
     *
     * @param resourceName name of the child resource to return
     * @return specified child resource
     */
    public Resource getResource(String resourceName) throws IOException;

    /**
     * Get a list of resources contained in this repository identified by the
     * given local name.
     *
     * @return a list of all direct child resources
     */
    public Resource[] getResources() throws IOException;

    /**
     * Get a list of resources contained in this repository identified by the
     * given local name.
     *
     * @param recursive whether to include nested resources
     * @return a list of all nested child resources
     */
    public Resource[] getResources(boolean recursive) throws IOException;

    /**
     * Get a list of resources contained in this repository identified by the
     * given local name.
     *
     * @param resourcePath the repository path
     * @param recursive    whether to include nested resources
     * @return a list of all nested child resources
     */
    public Resource[] getResources(String resourcePath, boolean recursive) throws IOException;

    /**
     * Returns this repository's direct child repositories
     *
     * @return direct repositories
     * @throws IOException an I/O error occurred
     */
    public Repository[] getRepositories() throws IOException;

    /**
     * Get a child repository with the given path
     *
     * @param path the path of the repository
     * @return the child repository
     * @throws IOException an IOException occurred
     */
    public Repository getChildRepository(String path) throws IOException;

    /**
     * Mark this repository as root repository, disabling any parent access.
     */
    public void setRoot();

    /**
     * Get the path of this repository relative to its root repository.
     *
     * @return the repository path
     */
    public String getRelativePath();

}