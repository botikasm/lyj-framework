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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class FileResource extends AbstractResource {

    File file;

    public FileResource(String path) throws IOException {
        this(new File(path), null);
    }

    public FileResource(File file) throws IOException {
        this(file, null);
    }

    protected FileResource(File file, FileRepository repository) throws IOException {
        // make sure our directory has an absolute path,
        // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4117557
        file = file.getAbsoluteFile();

        repository = repository == null ?
                new FileRepository(file.getParentFile()) : repository;
        // Make sure path is canonical for all directories, while acutal file may be a symlink
        file = new File(repository.getPath(), file.getName());
        path = file.getPath();
        name = file.getName();
        this.file = file;
        this.repository = repository;
        // base name is short name with extension cut off
        int lastDot = name.lastIndexOf(".");
        baseName = (lastDot == -1) ? name : name.substring(0, lastDot);
    }

    public InputStream getInputStream() throws IOException {
        return stripShebang(new FileInputStream(file));
    }

    public URL getUrl() throws MalformedURLException {
        return new URL("file:" + file.getAbsolutePath());
    }

    public long lastModified() {
        return file.lastModified();
    }

    public long getLength() {
        return file.length();
    }

    public boolean exists() {
        // not a resource if it's a directory
        return file.isFile();
    }

    @Override
    public int hashCode() {
        return 17 + path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FileResource && path.equals(((FileResource) obj).path);
    }

    @Override
    public String toString() {
        return getPath();
    }
}
