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
package org.lyj.commons.i18n.resourcebundle;

import org.lyj.commons.i18n.resourcebundle.bundle.ResourceBundleManager;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.util.Locale;
import java.util.Properties;


/**
 * Extends this class for a filesystem resource bundle.<br>
 * <p>
 * Filesystem Resource Bundles get resources from files.
 * Base Name is name of resource file without extension.
 * ex: "/User/path_resources/i18n_bundle"
 * <p>
 * Files should be:
 * "/User/path_resources/i18n_bundle.properties" // default
 * "/User/path_resources/i18n_bundle_it.properties" // italian
 * "/User/path_resources/i18n_bundle_en.properties" // english
 *
 * @author Gian Angelo Geminiani
 */
public abstract class FileI18nBundle {

    private final String _base_name; // full path of resource file without extension. ex: "/User/path_resources/i18n_bundle"
    private boolean _lookupForFileResource = false;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    /**
     * Create a file resource bundle staring from a filesystem path
     *
     * @param base_name Base Name is name of resource file without extension.
     *                  ex: "/User/path_resources/i18n_bundle"
     *                  Files should be:
     *                  "/User/path_resources/i18n_bundle.properties" // default
     *                  "/User/path_resources/i18n_bundle_it.properties" // italian
     *                  "/User/path_resources/i18n_bundle_en.properties" // english
     */
    public FileI18nBundle(final String base_name) {
        _base_name = base_name;
    }

    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    public void setLookupForFileResource(final boolean value) {
        _lookupForFileResource = value;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void clearBundle() {
        ResourceBundleManager.clearBundle();
    }

    public void refreshBundle() {
        ResourceBundleManager.refreshBundle(_base_name, null);
    }

    public void refreshBundle(final String lang) {
        this.refreshBundle(LocaleUtils.getLocaleByLang(lang));
    }

    public void refreshBundle(final Locale locale) {
        ResourceBundleManager.refreshBundle(_base_name, locale);
    }

    public String getMessage(final String key,
                             final String lang) {
        return getMessage(key, LocaleUtils.getLocaleByLang(lang));
    }

    public String getMessage(final String key,
                             final Locale locale) {
        final String resource_value = ResourceBundleManager.getString(
                _base_name,
                key,
                null != locale ? locale : Locale.ENGLISH,
                "");

        return this.validate(resource_value);
    }

    public Properties getBundleProperties(final Locale locale) {
        try {
            return ResourceBundleManager.getBundleProperties(
                    _base_name, locale);
        } catch (Exception ex) {
            return new Properties();
        }
    }

    public Properties getProperties(final Locale locale) {
        try {
            return ResourceBundleManager.getProperties(
                    _base_name,
                    null != locale ? locale : Locale.ENGLISH);
        } catch (Exception ex) {
            return new Properties();
        }
    }

    public abstract String getName();

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private String validate(final String value) {
        // should check if value is a file resource?
        if (_lookupForFileResource) {
            if (StringUtils.hasText(PathUtils.getFilenameExtension(value))) {
                try {
                    return this.readFile(value);
                } catch (Throwable ignored) {
                }
            }
        }

        return value;
    }

    private String readFile(final String fileName) throws Exception {
        final String path = PathUtils.isAbsolute(fileName) ? fileName : PathUtils.combine(_base_name, fileName);
        final File file = new File(path);
        final String result = FileUtils.readFileToString(file);
        if (null == result) {
            throw new Exception("not a file");
        }
        return result;
    }

}
