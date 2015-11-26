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
 * 
 */
package org.ly.commons.io;

import org.ly.commons.util.MimeTypeUtils;

/**
 * Binary Data Wrapper.
 *
 * @author angelo.geminiani
 */
public class BinaryData {

    private byte[] _bytes;
    private String _mimetype;

    public BinaryData() {
        _mimetype = MimeTypeUtils.MIME_IMAGEPNG;
        _bytes = new byte[0];
    }

    public BinaryData(final String type) {
        _mimetype = MimeTypeUtils.getMimeType(type);
        _bytes = new byte[0];
    }

    public BinaryData(final byte[] bytes) {
        _mimetype = MimeTypeUtils.MIME_IMAGEPNG;
        _bytes = bytes;
    }

    public BinaryData(final byte[] bytes, final String type) {
        _mimetype = MimeTypeUtils.getMimeType(type);
        _bytes = bytes;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append("{");
        result.append("size: ").append(this.size());
        result.append(", ");
        result.append("type: ").append(_mimetype);
        result.append("}");
        return result.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    public byte[] getBytes() {
        return _bytes;
    }

    public void setBytes(byte[] bytes) {
        this._bytes = bytes;
    }

    public String getMimetype() {
        return _mimetype;
    }

    public void setMimetype(String mimetype) {
        this._mimetype = MimeTypeUtils.getMimeType(mimetype);
    }

    public int size() {
        return null != _bytes ? _bytes.length : 0;
    }
}
