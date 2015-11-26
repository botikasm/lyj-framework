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

package org.ly.commons.remoting.rest;

import org.ly.IConstants;

public interface IRESTCons {

    public static final String DEFAULT_PATH = "/";

    public static final String TYPE_JSON = IConstants.TYPE_JSON; // "application/json";
    public static final String TYPE_PNG = IConstants.TYPE_PNG; // "image/png";
    public static final String TYPE_TEXT = IConstants.TYPE_TEXT; // "text/plain;charset=UTF-8";
    public static final String TYPE_ZIP = IConstants.TYPE_ZIP; // "application/zip";
    public static final String TYPE_XML = IConstants.TYPE_XML; // "application/xml";
    public static final String TYPE_HTML = IConstants.TYPE_HTML;

}
