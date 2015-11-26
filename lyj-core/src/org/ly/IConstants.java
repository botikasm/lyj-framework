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

package org.ly;

public interface IConstants {

    public static final String SYSPROP_HOME = "smartly.home";
    public static final String SYSPROP_CHARSET = "smartly.charset";
    public static final String SYSPROP_USE_PROXIES = "smartly.useSystemProxies";

    public static final String USER_DIR = System.getProperty("user.dir");   // application directory
    public static final String USER_HOME = System.getProperty("user.home"); // user home directory

    /**
     * Line separator. i.e. "\n" *
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String FOLDER_SEPARATOR = "/";
    public static final String PATH_WHILDCHAR = "*";
    public static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    public static final String TOP_PATH = "..";
    public static final String CURRENT_PATH = ".";
    public static final char EXTENSION_SEPARATOR = '.';
    public static final String PLACEHOLDER_PREFIX = "{";
    public static final String PLACEHOLDER_SUFFIX = "}";
    public static final String NULL = "NULL";

    public static final String PATH_PACKAGES = "./packages";
    public static final String PATH_LIBRARIES = "./lib";
    public static final String PATH_LOG = "./logs";
    public static final String PATH_CONFIGFILES = "./config";
    public static final String PATH_CONFIGFILES_DATABASES = PATH_CONFIGFILES + "/databases";

    public static final String DEF_LANG = "en";
    public static final String DEF_COUNTRY = "US";

    //-- Content-Type --//
    public static final String TYPE_JSON = "application/json";
    public static final String TYPE_PNG = "image/png";
    public static final String TYPE_TEXT = "text/plain;charset=UTF-8";
    public static final String TYPE_HTML = "text/html;charset=UTF-8";
    public static final String TYPE_ZIP = "application/zip";
    public static final String TYPE_XML = "application/xml";
    public static final String TYPE_ALL ="text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
}
