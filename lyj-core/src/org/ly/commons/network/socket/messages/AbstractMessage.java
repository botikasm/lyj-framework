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

package org.ly.commons.network.socket.messages;

import org.ly.commons.lang.CharEncoding;
import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.util.ByteUtils;
import org.ly.commons.util.DateUtils;
import org.ly.commons.util.StringUtils;
import org.ly.commons.util.ZipUtils;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Date;

/**
 *
 */
public abstract class AbstractMessage
        implements Serializable {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private long _creationDate = System.currentTimeMillis();

    private String _userToken = "";
    private String _charset = CharEncoding.getDefault();
    private long _compressionThreshold = 0; // -1 does not use Compression, 0 always, >0 threshold

    private byte[] _data = new byte[0];

    // ------------------------------------------------------------------------
    //                      o v e r r i d e s
    // ------------------------------------------------------------------------

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("CreationDate: ").append(new Date(this.getCreationDate()));
        sb.append(", ");
        sb.append("Elapsed: ").append(this.getElapsedTime());
        sb.append(", ");
        sb.append("UserToken: ").append(this.getUserToken());
        sb.append("}");

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    /**
     * Compression Threshold Supported Values
     * <ul>
     * <li>-1=no compression</li>
     * <li>0=always</li>
     * <li>bigger than zero=use this value to check when compress data.</li>
     * </ul>
     *
     * @return Compression Threshold
     */
    public long getCompressionThreshold() {
        return _compressionThreshold;
    }

    /**
     * Compression Threshold Supported Values
     * <ul>
     * <li>-1=no compression</li>
     * <li>0=always</li>
     * <li>bigger than zero=use this value to check when compress data.</li>
     * </ul>
     *
     * @param value Threshold to use as compress limit.
     * @return Instance of current Object
     */
    public AbstractMessage setCompressionThreshold(final long value) {
        _compressionThreshold = value;
        return this;
    }

    public boolean isCompressed() {
        return _compressionThreshold > -1;
    }

    public AbstractMessage setCharset(final String charsetName) {
        return this.setCharset(Charset.forName(charsetName));
    }

    public AbstractMessage setCharset(final Charset charset) {
        _charset = charset.name();
        return this;
    }

    public String getCharset() {
        return _charset;
    }

    public long getCreationDate() {
        return _creationDate;
    }

    public double getElapsedTime() {
        return DateUtils.dateDiff(new Date(System.currentTimeMillis()), new Date(_creationDate), DateUtils.MILLISECOND);
    }

    public String getUserToken() {
        return _userToken;
    }

    public AbstractMessage setUserToken(final String value) {
        _userToken = value;
        return this;
    }

    public AbstractMessage clearData() {
        _data = null;
        this.setData(new byte[0]);
        return this;
    }

    public AbstractMessage setData(final byte[] value) {
        this.setRawData(value);
        return this;
    }

    public AbstractMessage setData(final String value) {
        this.setData(value, _charset);
        return this;
    }

    public AbstractMessage setData(final String value,
                                   final String charset) {
        try {
            this.setData(StringUtils.hasText(value) ? value.getBytes(charset) : new byte[0]);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
            try {
                this.setData(value.getBytes());
            } catch (Throwable tt) {
                this.getLogger().log(Level.SEVERE, null, tt);
                this.setData(new byte[0]);
            }
        }
        return this;
    }

    public AbstractMessage setData(final Serializable value) {
        this.setData(ByteUtils.optBytes(value));
        return this;
    }

    public boolean hasData() {
        return this.getRawDataLength() > 0;
    }

    public int getDataLength() {
        return this.getRawDataLength();
    }

    public byte[] getDataBytes() {
        return this.getRawData();
    }

    public Serializable getData() {
        return (Serializable) ByteUtils.optObject(this.getRawData());
    }

    public String getDataString() {
        try {
            return new String(this.getRawData(), _charset);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
            return "";
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void setRawData(final byte[] data) {
        _data = isCompressed() ? zip(data) : data;
    }

    private byte[] getRawData() {
        return isCompressed() ? unzip(_data) : _data;
    }

    private int getRawDataLength() {
        return null != _data ? _data.length : 0;
    }

    private byte[] zip(final byte[] data) {
        if (null != data && data.length > 0) {
            try {
                return ZipUtils.gzip(data);
            } catch (Throwable t) {
                _compressionThreshold = -1; // disable compression
                this.getLogger().warning("Compression disabled: " + t.toString());
                return data;
            }
        }
        return new byte[0];
    }

    private byte[] unzip(final byte[] data) {
        if (null != data && data.length > 0) {
            try {
                return ZipUtils.gunzip(data);
            } catch (Throwable t) {
                _compressionThreshold = -1; // disable compression
                this.getLogger().warning("Compression disabled: " + t.toString());
                return data;
            }
        }
        return new byte[0];
    }

}
