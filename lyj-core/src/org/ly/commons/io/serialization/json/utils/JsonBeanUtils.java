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
package org.ly.commons.io.serialization.json.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.ly.commons.util.ExceptionUtils;
import org.ly.commons.util.StringUtils;

import java.util.Date;

/**
 * @author angelo.geminiani
 */
public class JsonBeanUtils {

    public static final String TAG_VALUE = "jvalue";
    public static final String TAG_CLASS = "jclass";
    public static final String TAG_TYPE = "jtype";
    public static final String TAG_MESSAGE = "jmessage";
    public static final String TAG_CAUSE = "jcause";
    //
    public static final String TYPE_OBJECT = "object";
    public static final String TYPE_ERROR = "error";

    public static String notEmpty(final Object value) {
        return StringUtils.notEmpty(value);
    }

    public static JSONObject addError(final JSONObject json, final Throwable t) {
        try {
            if (null != json && null != t) {
                json.put(TAG_TYPE, TYPE_ERROR);
                json.put(TAG_CLASS, t.getClass().getName());
                json.put(TAG_VALUE, notEmpty(t.toString()));
                json.put(TAG_MESSAGE, notEmpty(ExceptionUtils.getMessage(t)));
                json.put(TAG_CAUSE, notEmpty(ExceptionUtils.getRealMessage(t)));
            }
        } catch (Throwable ex) {
        }
        return json;
    }

    public static JSONObject addObject(final JSONObject json,
                                       final String tagClass, final Object serialized) {
        try {
            if (null != json) {
                json.put(JsonBeanUtils.TAG_CLASS, notEmpty(tagClass));
                json.put(JsonBeanUtils.TAG_TYPE, JsonBeanUtils.TYPE_OBJECT);
                json.put(JsonBeanUtils.TAG_VALUE, notEmpty(serialized));
                json.put(JsonBeanUtils.TAG_MESSAGE, notEmpty(""));
                json.put(JsonBeanUtils.TAG_CAUSE, notEmpty(""));
            }
        } catch (Throwable ex) {
        }
        return json;
    }

    public static void putValues(final JSONObject json,
                                 final String tagType, final String tagClass,
                                 final String tagValue, final String tagMessage,
                                 final String tagCause) {
        if (null != json) {
            try {
                json.put(TAG_TYPE, notEmpty(tagType));
            } catch (JSONException ex) {
            }
            try {
                json.put(TAG_CLASS, notEmpty(tagClass));
            } catch (JSONException ex) {
            }
            try {
                json.put(TAG_VALUE, notEmpty(tagValue));
            } catch (JSONException ex) {
            }
            try {
                json.put(TAG_MESSAGE, notEmpty(tagMessage));
            } catch (JSONException ex) {
            }
            try {
                json.put(TAG_CAUSE, notEmpty(tagCause));
            } catch (JSONException ex) {
            }
        }
    }

    public static boolean isNative(final Object object) {
        return null != object
                ? isNative(object.getClass().getName())
                : false;
    }

    public static boolean isNative(final String className) {
        return className.equals(String.class.getName())
                || className.equals(Boolean.class.getName())
                || className.equals(Character.class.getName())
                || className.equals(Long.class.getName())
                || className.equals(Integer.class.getName())
                || className.equals(Double.class.getName())
                || className.equals(Byte.class.getName())
                || className.equals(boolean.class.getName())
                || className.equals(char.class.getName())
                || className.equals(long.class.getName())
                || className.equals(int.class.getName())
                || className.equals(double.class.getName())
                || className.equals(byte.class.getName());
    }

    public static boolean isDate(final Object object) {
        return null != object
                ? isDate(object.getClass().getName())
                : false;
    }

    public static boolean isDate(final String className) {
        return className.equals(Date.class.getName());
    }
}
