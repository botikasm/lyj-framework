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

import org.ly.commons.Delegates;
import org.ly.commons.async.Async;
import org.ly.commons.cryptograph.GUID;
import org.ly.commons.util.CompareUtils;
import org.ly.commons.util.StringUtils;

import java.util.*;

/**
 * Multipart Message aggregator.
 */
public class Multipart {

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    public static interface OnPartListener {
        public void handle(Multipart sender, MultipartMessagePart part);
    }

    public static interface OnFullListener {
        public void handle(Multipart sender);
    }

    public static interface OnTimeOutListener {
        public void handle(Multipart sender);
    }

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Collection<OnPartListener> _listeners_OnPart;
    private final Collection<OnFullListener> _listeners_OnFull;
    private final String _uid;
    private final long _creationDate;
    private final List<MultipartMessagePart> _list;

    private int _capacity;
    private Object _userData; // custom data
    private long _lastActivityDate;

    //-- readonly from part --//
    private MultipartInfo.MultipartInfoType _type;
    private String _name;
    private String _userToken;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Multipart(final int capacity) {
        this(null, capacity);
    }

    public Multipart(final String uid, final int capacity) {
        _uid = StringUtils.hasText(uid) ? uid : GUID.create();
        _creationDate = System.currentTimeMillis();
        _lastActivityDate = System.currentTimeMillis();
        _list = Collections.synchronizedList(new ArrayList<MultipartMessagePart>(capacity));
        _listeners_OnFull = Collections.synchronizedCollection(new ArrayList<OnFullListener>());
        _listeners_OnPart = Collections.synchronizedCollection(new ArrayList<OnPartListener>());
        _capacity = capacity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("UID:").append(this.getUid());
        sb.append(", ");
        sb.append("Type:").append(this.getType());
        sb.append(", ");
        sb.append("Alive Time:").append(this.getAliveTime());
        sb.append(", ");
        sb.append("Part Count:").append(this.count());
        sb.append(", ");
        sb.append("Capacity:").append(this.getCapacity());
        sb.append(", ");
        sb.append("Is Full:").append(this.isFull());
        sb.append(", ");
        sb.append("Name:").append(this.getName());
        sb.append("}");

        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Multipart) &&
                _uid.equalsIgnoreCase(((Multipart) obj).getUid());
    }

    @Override
    public int hashCode() {
        return _uid.hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _userData = null;
            _listeners_OnFull.clear();
            _listeners_OnPart.clear();
            _list.clear();
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getUid() {
        return _uid;
    }

    public Object getUserData() {
        return _userData;
    }

    public void setUserData(final Object value) {
        if (null != value) {
            _userData = value;
        }
    }

    public MultipartInfo.MultipartInfoType getType() {
        return _type;
    }

    public String getName() {
        return _name;
    }

    public String getUserToken() {
        return _userToken;
    }

    public boolean isType(final MultipartInfo.MultipartInfoType type) {
        return CompareUtils.equals(_type, type);
    }

    public boolean isTypeString() {
        return this.isType(MultipartInfo.MultipartInfoType.String);
    }

    public boolean isTypeFile() {
        return this.isType(MultipartInfo.MultipartInfoType.File);
    }

    public int getCapacity() {
        return _capacity;
    }

    public boolean hasError() {
        synchronized (_list) {
            for (final MultipartMessagePart part : _list) {
                if (part.hasError()) {
                    return true;
                }
            }
            return false;
        }
    }

    public Throwable getError() {
        synchronized (_list) {
            for (final MultipartMessagePart part : _list) {
                if (part.hasError()) {
                    return part.getError();
                }
            }
            return null;
        }
    }

    public MultipartMessagePart[] getParts() {
        synchronized (_list) {
            Collections.sort(_list);
            return _list.toArray(new MultipartMessagePart[_list.size()]);
        }
    }

    public String[] getPartNames() {
        synchronized (_list) {
            final List<String> result = new LinkedList<String>();
            Collections.sort(_list);
            for (final MultipartMessagePart part : _list) {
                result.add(part.getInfo().getPartName());
            }
            return result.toArray(new String[result.size()]);
        }
    }

    public double getAliveTime() {
        return System.currentTimeMillis() - _creationDate; // DateUtils.dateDiff(DateUtils.now(), _creationDate, DateUtils.MILLISECOND);
    }

    public double getExpirationTime() {
        return System.currentTimeMillis() - _lastActivityDate; // DateUtils.dateDiff(DateUtils.now(), _lastActivityDate, DateUtils.MILLISECOND);
    }

    public boolean isExpired(final long millisecondsTimeout) {
        return this.getExpirationTime() < millisecondsTimeout;
    }

    public boolean isFull() {
        return _capacity == _list.size();
    }

    public int count() {
        synchronized (_list) {
            return _list.size();
        }
    }

    public void add(final MultipartMessagePart part) {
        synchronized (_list) {
            if (!_list.contains(part) && !this.isFull()) {
                // reset expiration timer
                _lastActivityDate = System.currentTimeMillis();
                // add uid to part
                part.setUid(this.getUid());
                // add part to internal list
                _list.add(part);
                // raise event
                this.doOnPart(part);
                // set parent properties from part
                this.setProperties(part);
                // check if full
                this.checkCapacity();
            }
        }
    }

    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onPart(final OnPartListener listener) {
        synchronized (_listeners_OnPart) {
            _listeners_OnPart.add(listener);
        }
    }

    public void onFull(final OnFullListener listener) {
        synchronized (_listeners_OnFull) {
            _listeners_OnFull.add(listener);
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void checkCapacity() {
        if (_list.size() >= _capacity) {
            //-- call event listeners --//
            this.doOnFull();
        }
    }

    private void doOnFull() {
        synchronized (_listeners_OnFull) {
            for (final OnFullListener listener : _listeners_OnFull) {
                Async.Action(new Delegates.Action() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle((Multipart) args[0]);
                    }
                }, this);
            }
        }
    }

    private void doOnPart(final MultipartMessagePart part) {
        synchronized (_listeners_OnPart) {
            for (final OnPartListener listener : _listeners_OnPart) {
                Async.Action(new Delegates.Action() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle((Multipart) args[0], (MultipartMessagePart) args[1]);
                    }
                }, this, part);
            }
        }
    }

    private void setProperties(final MultipartMessagePart part) {
        if (null != part) {
            if (null == _type) {
                _type = part.getInfo().getType();
            }
            if (null == _name) {
                _name = part.getInfo().getParentName();
            }
            if (null == _userToken) {
                _userToken = part.getUserToken();
            }
        }
    }
}
