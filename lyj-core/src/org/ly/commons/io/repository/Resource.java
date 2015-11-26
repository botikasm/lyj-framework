
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
import java.io.Reader;

/**
 * Resource represents a pointer to some kind of information
 * from which the content can be fetched
 */
public interface Resource extends Trackable {


    /**
     * Returns the length of the resource's content
     *
     * @return content length
     */
    public long getLength();

    /**
     * Returns an input stream to the content of the resource
     *
     * @return content input stream
     * @throws IOException if a I/O related error occurs
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Returns a reader for the resource using the given character encoding
     *
     * @param encoding the character encoding
     * @return the reader
     * @throws IOException if a I/O related error occurs
     */
    public Reader getReader(String encoding) throws IOException;

    /**
     * Returns a reader for the resource
     *
     * @return the reader
     * @throws IOException if a I/O related error occurs
     */
    public Reader getReader() throws IOException;

    /**
     * Returns the content of the resource in array of bytes
     *
     * @return content
     */
    public byte[] getBytes() throws IOException;

    /**
     * Returns the content of the resource in a given encoding
     *
     * @param encoding
     * @return content
     */
    public String getContent(String encoding) throws IOException;

    /**
     * Returns the content of the resource
     *
     * @return content
     */
    public String getContent() throws IOException;

    /**
     * Returns the short name of the resource with the file extension
     * (everything following the last dot character) cut off.
     *
     * @return the file name without the file extension
     */
    public String getBaseName();

    /**
     * Get the path of this resource relative to its root repository.
     *
     * @return the relative resource path
     */
    public String getRelativePath();


    /**
     * Returns true if the input stream for this resource will look for a
     * first line starting with the characters #! and suppress it if found
     *
     * @return true if Hashbang stripping is enabled
     */
    public boolean getStripHashbang();

    /**
     * Switch shebang stripping on or off
     *
     * @param value true to enable Hashbang stripping
     */
    public void setStripHashbang(boolean value);

}
