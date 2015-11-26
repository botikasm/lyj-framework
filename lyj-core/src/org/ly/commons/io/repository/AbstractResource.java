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

import org.ly.commons.util.FileUtils;

import java.io.*;

public abstract class AbstractResource implements Resource {

    protected AbstractRepository repository;
    protected String path;
    protected String name;
    protected String baseName;
    private boolean _stripHashbang = true;

    protected void setBaseNameFromName(String name) {
        // base name is short name with extension cut off
        int lastDot = name.lastIndexOf(".");
        this.baseName = (lastDot == -1) ? name : name.substring(0, lastDot);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getBaseName() {
        return baseName;
    }

    public Repository getParentRepository() {
        return repository;
    }

    public Repository getRootRepository() {
        return repository.getRootRepository();
    }

    protected InputStream stripShebang(InputStream stream) throws IOException {
        if (_stripHashbang) {
            stream = new BufferedInputStream(stream);
            stream.mark(2);
            if (stream.read() == '#' && stream.read() == '!') {
                // skip a line: a line is terminated by \n or \r or \r\n (just as
                // in BufferedReader#readLine)
                for (int c = stream.read(); c != -1; c = stream.read()) {
                    if (c == '\n') {
                        break;
                    } else if (c == '\r') {
                        stream.mark(1);
                        if (stream.read() != '\n') {
                            stream.reset();
                        }
                        break;
                    }
                }
            } else {
                stream.reset();
            }
        }
        return stream;
    }

    public Reader getReader(String encoding) throws IOException {
        return new InputStreamReader(getInputStream(), encoding);
    }

    public Reader getReader() throws IOException {
        return new InputStreamReader(getInputStream());
    }

    public byte[] getBytes() throws IOException {
        final InputStream in = this.getInputStream();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileUtils.copy(in, out);
            return out.toByteArray();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public String getContent(final String encoding) throws IOException {
        final InputStream in = getInputStream();
        try {
            byte[] buf = new byte[1024];
            int read = 0;
            while (true) {
                int r = in.read(buf, read, buf.length - read);
                if (r == -1) {
                    break;
                }
                read += r;
                if (read == buf.length) {
                    byte[] b = new byte[buf.length * 2];
                    System.arraycopy(buf, 0, b, 0, buf.length);
                    buf = b;
                }
            }
            return encoding == null ?
                    new String(buf, 0, read) :
                    new String(buf, 0, read, encoding);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public String getContent() throws IOException {
        return getContent("utf-8");
    }

    /**
     * Get the path of this resource relative to its root repository.
     *
     * @return the relative resource path
     */
    public String getRelativePath() {
        if (repository == null) {
            return name;
        } else {
            return repository.getRelativePath() + name;
        }
    }

    /**
     * Utility method to get the name for the module defined by this resource.
     *
     * @return the module name according to the securable module spec
     */
    public String getModuleName() {
        if (repository == null) {
            return baseName;
        } else {
            return repository.getRelativePath() + baseName;
        }
    }

    public long getChecksum() {
        return lastModified();
    }

    public boolean getStripHashbang() {
        return _stripHashbang;
    }

    public void setStripHashbang(boolean value) {
        _stripHashbang = value;
    }

    /**
     * Set this Resource to absolute mode. This will cause all its
     * relative path operations to use absolute paths instead.
     *
     * @param absolute true to operate in absolute mode
     */
    public void setAbsolute(boolean absolute) {
        repository.setAbsolute(absolute);
    }

    /**
     * Return true if this Resource is in absolute mode.
     *
     * @return true if absolute mode is on
     */
    public boolean isAbsolute() {
        return repository.isAbsolute();
    }
}
