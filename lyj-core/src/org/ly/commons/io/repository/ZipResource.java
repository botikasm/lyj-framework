
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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipResource extends AbstractResource {

    private String entryName;
    private boolean exists;
    long length;
    long lastModified;

    protected ZipResource(String childName, ZipRepository repository, ZipEntry entry) {
        this.repository = repository;
        this.entryName = childName;
        exists = entry != null && !entry.isDirectory();
        length = exists ? entry.getSize() : 0;
        lastModified = repository.lastModified();

        int slash = entryName.lastIndexOf('/');
        this.name = slash < 0 ? entryName : entryName.substring(slash + 1);
        this.path = repository.getPath() + name;
        setBaseNameFromName(name);
    }

    public long lastModified() {
        return repository.lastModified();
    }

    public long getChecksum() {
        return repository.lastModified();
    }

    public InputStream getInputStream() throws IOException {
        ZipFile zipfile = getZipFile();
        ZipEntry entry = zipfile.getEntry(entryName);
        if (entry == null) {
            throw new IOException("Zip resource " + this + " does not exist");
        }
        return stripShebang(zipfile.getInputStream(entry));
    }

    private void update() {
        try {
            ZipEntry entry = getZipFile().getEntry(entryName);
            exists = entry != null && !entry.isDirectory();
            length = exists ? entry.getSize() : 0;
            lastModified = repository.lastModified();
        } catch (IOException ex) {
            exists = false;
        }

    }

    public boolean exists() throws IOException {
        if (lastModified != repository.lastModified()) {
            update();
        }
        return exists;
    }


    public URL getUrl() throws MalformedURLException {
        // return a Jar URL as defined in
        // http://java.sun.com/j2se/1.5.0/docs/api/java/net/JarURLConnection.html
        return new URL(repository.getUrl() + name);
    }

    public long getLength() {
        if (lastModified != repository.lastModified()) {
            update();
        }
        return length;
    }

    @Override
    public int hashCode() {
        return 17 + path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ZipResource && path.equals(((ZipResource) obj).path);
    }

    @Override
    public String toString() {
        return getPath();
    }

    private ZipFile getZipFile() throws IOException {
        if (!(repository instanceof ZipRepository)) {
            throw new IOException("Parent is not a ZipRepository: " + repository);
        }
        return ((ZipRepository) repository).getZipFile();
    }
}
