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

package org.lyj;

import org.lyj.commons.util.PathUtils;

public interface IConstants {

    String VERSION = "1.0.1";

    String SYSPROP_HOME = "lyj.home";
    String SYSPROP_CHARSET = "lyj.charset";
    String SYSPROP_USE_PROXIES = "lyj.useSystemProxies";

    String USER_DIR = PathUtils.toUnixPath(System.getProperty("user.dir"));   // application directory
    String USER_HOME = PathUtils.toUnixPath(System.getProperty("user.home")); // user home directory

    /**
     * Line separator. i.e. "\n" *
     */
    String LINE_SEPARATOR = System.getProperty("line.separator");

    String FOLDER_SEPARATOR = "/";
    String PATH_WHILDCHAR = "*";
    String WINDOWS_FOLDER_SEPARATOR = "\\";
    String TOP_PATH = "..";
    String CURRENT_PATH = ".";
    char EXTENSION_SEPARATOR = '.';
    String PLACEHOLDER_PREFIX = "{";
    String PLACEHOLDER_SUFFIX = "}";
    String NULL = "NULL";

    String PATH_LOG = "./logs";
    String PATH_CONFIGFILES = "./config";
    String PATH_CONFIGFILES_DATABASES = PATH_CONFIGFILES + "/databases";

    String BASE_LANG = "base"; // undefined language
    String DEF_LANG = "en";
    String DEF_COUNTRY = "US";

    //-- Content-Type --//
    String TYPE_JSON = "application/json";
    String TYPE_PNG = "image/png";
    String TYPE_TEXT = "text/plain;charset=UTF-8";
    String TYPE_HTML = "text/html;charset=UTF-8";
    String TYPE_ZIP = "application/zip";
    String TYPE_XML = "application/xml";
    String TYPE_ALL ="text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
}
