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

package org.ly.commons.network.socket.messages.multipart;

import org.ly.commons.network.socket.messages.AbstractMessage;

/**
 * Single Part of a multipart message
 */
public class MultipartMessagePart extends AbstractMessage
        implements Comparable<MultipartMessagePart> {

    private String _uid;
    private MultipartInfo _info;
    private Throwable _error;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MultipartMessagePart() {
        _info = new MultipartInfo();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("UID: ").append(_uid);
        sb.append(", ");
        sb.append("Info: ").append(_info);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int compareTo(final MultipartMessagePart o) {
        if (null != o) {
            if (o.getInfo().getIndex() == this.getInfo().getIndex()) {
                return 0;
            }
            return o.getInfo().getIndex() > this.getInfo().getIndex() ? -1 : 1;
        }
        return -1;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getUid() {
        return _uid;
    }

    public void setUid(final String uid) {
        _uid = uid;
    }

    public void setError(final Throwable value) {
        _error = value;
    }

    public Throwable getError() {
        return _error;
    }

    public boolean hasError() {
        return null != _error;
    }

    public MultipartMessagePart setData(final byte[] value) {
        final byte[] data = super.setData(value).getDataBytes();
        if (null != data && null != _info) {
            _info.setPartLength(data.length);
        }
        return this;
    }


    public MultipartInfo getInfo() {
        return _info;
    }

    public void setInfo(MultipartInfo info) {
        _info = info;
    }

    public int getPartIndex() {
        if (null != _info) {
            return _info.getIndex();
        }
        return 0;
    }

    public int getPartCount() {
        if (null != _info) {
            return _info.getCount();
        }
        return 0;
    }


}
