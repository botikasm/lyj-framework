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

package org.lyj.commons.io.repository;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository implementation for directories providing file resources
 */
public class FileRepository extends AbstractRepository {

    // Directory serving sub-repositories and file resources
    protected final File directory;

    protected long lastModified = -1;
    protected long lastChecksum = 0;
    protected long lastChecksumTime = 0;

    /**
     * Defines how long the checksum of the repository will be cached
     */
    final long cacheTime = 1000L;

    /**
     * Constructs a FileRepository using the given argument
     *
     * @param path absolute path to the directory
     * @throws IOException if canonical path couldn't be resolved
     */
    public FileRepository(String path) throws IOException {
        this(new File(path), null);
    }

    /**
     * Constructs a FileRepository using the given directory as top-level
     * repository
     *
     * @param dir directory
     * @throws IOException if canonical path couldn't be resolved
     */
    public FileRepository(File dir) throws IOException {
        this(dir, null);
    }

    /**
     * Constructs a FileRepository using the given directory and top-level
     * repository
     *
     * @param dir    directory
     * @param parent top-level repository
     * @throws IOException if canonical path couldn't be resolved
     */
    protected FileRepository(File dir, FileRepository parent) throws IOException {
        // make sure our directory has an absolute path,
        // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4117557
        directory = dir; //dir.getCanonicalFile();

        this.parent = parent;
        // We intentionally get the name from original file,
        // canonical path's file name may be different in case of symlink.
        name = dir.getName();
        path = directory.getPath();
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
    }

    /**
     * Check whether the repository exists.
     *
     * @return true if the repository exists.
     */
    public boolean exists() {
        return directory.isDirectory();
    }

    /**
     * Create a child repository with the given name
     *
     * @param name the name of the repository
     * @return the child repository
     */
    public AbstractRepository createChildRepository(String name) throws IOException {
        File f = new File(directory, name);
        return new FileRepository(f, this);
    }

    /**
     * Get this repository's parent repository.
     */
    @Override
    public AbstractRepository getParentRepository() {
        if (parent == null) {
            // allow to escape file repository root
            try {
                SoftReference<AbstractRepository> ref = repositories.get("..");
                AbstractRepository repo = ref == null ? null : ref.get();
                if (repo == null) {
                    repo = new FileRepository(directory.getParentFile());
                    repo.setAbsolute(true);
                    repositories.put("..", new SoftReference<AbstractRepository>(repo));
                }
                return repo;
            } catch (IOException iox) {
                // fall through
            }
        }
        return parent;
    }

    /**
     * Returns the date the repository was last modified.
     *
     * @return last modified date
     */
    public long lastModified() {
        return directory.lastModified();
    }

    /**
     * Checksum of the repository and all its contained resources. Implementations
     * should make sure to return a different checksum if any contained resource
     * has changed.
     *
     * @return checksum
     */
    public synchronized long getChecksum() throws IOException {
        // delay checksum check if already checked recently
        if (System.currentTimeMillis() > lastChecksumTime + cacheTime) {
            // FIXME
            long checksum = lastModified;

            for (Resource res : resources.values()) {
                checksum += res.lastModified();
            }

            lastChecksum = checksum;
            lastChecksumTime = System.currentTimeMillis();
        }

        return lastChecksum;
    }

    /**
     * Called to create a child resource for this repository
     */
    @Override
    protected Resource lookupResource(String name) throws IOException {
        AbstractResource res = resources.get(name);
        if (res == null) {
            res = new FileResource(new File(directory, name), this);
            resources.put(name, res);
        }
        return res;
    }

    protected void getResources(final List<Resource> list, final boolean recursive)
            throws IOException {
        final File[] dir = directory.listFiles();

        for (final File file : dir) {
            if (file.isFile()) {
                final Resource resource = this.lookupResource(file.getName());
                list.add(resource);
            } else if (recursive && file.isDirectory()) {
                AbstractRepository repo = lookupRepository(file.getName());
                repo.getResources(list, true);
            }
        }
    }

    public Repository[] getRepositories() throws IOException {
        final File[] dir = directory.listFiles();
        final List<Repository> list = new ArrayList<Repository>(dir.length);

        for (final File file : dir) {
            if (file.isDirectory()) {
                list.add(lookupRepository(file.getName()));
            }
        }
        return list.toArray(new Repository[list.size()]);
    }

    public URL getUrl() throws MalformedURLException {
        // Trailing slash on directories is required for ClassLoaders
        return new URL("file:" + path);
    }

    @Override
    public int hashCode() {
        return 17 + (37 * directory.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FileRepository &&
                directory.equals(((FileRepository) obj).directory);
    }

    @Override
    public String toString() {
        return new StringBuffer("FileRepository[").append(path).append("]").toString();
    }
}
