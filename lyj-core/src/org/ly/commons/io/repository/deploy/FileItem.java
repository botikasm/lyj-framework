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

package org.ly.commons.io.repository.deploy;

import org.ly.commons.logging.Level;
import org.ly.commons.logging.Logger;
import org.ly.commons.logging.util.LoggingUtils;
import org.ly.commons.util.PathUtils;
import org.ly.commons.util.StringUtils;

/**
 * @author angelo.geminiani
 */
public class FileItem {

    private String _fileName;
    private final String _absolutePath;
    private String _relativePath;
    private final String _root;
    private final String _folder;

    public FileItem(final Object deployer,
                    final String root,
                    final String absolutePath) {
        _absolutePath = PathUtils.validateFolderSeparator(absolutePath);
        _root = this.toExternalForm(root, absolutePath);
        _folder = this.lookupFolder(_root, _absolutePath);
        try {
            if (!this.isJar(_absolutePath)) {
                _fileName = PathUtils.subtract(_root, _absolutePath);
                _relativePath = PathUtils.merge(
                        PathUtils.getPackagePath(deployer.getClass()),
                        _fileName);
            } else {
                // root: file:/C:/lib/BEEingX.jar!/org/sf/quickpin/htdocs/
                // absolute path: jar:file:/C:/lib/BEEingX.jar!/org/sf/bee/app/server/web/htdocs/vtlinfo.vhtml
                _fileName = PathUtils.subtract(_root, _absolutePath);
                _relativePath = _absolutePath.substring(_absolutePath.indexOf(".jar!") + 6);
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName()).append("{");
        result.append("fileName: ").append(_fileName);
        result.append(", ");
        result.append("folder: ").append(_folder);
        result.append(", ");
        result.append("root: ").append(_root);
        result.append("}");

        return result.toString();
    }

    public String getFileName() {
        return _fileName;
    }

    public String getRoot() {
        return _root;
    }

    public String getFolder() {
        return _folder;
    }

    public String getAbsolutePath() {
        return _absolutePath;
    }

    public boolean isJar() {
        return this.isJar(_absolutePath);
    }

    public String getPackageName() {
        if (this.isJar()) {
            return _relativePath;
        } else {
            return _relativePath;
        }
    }

    public boolean isDirectory() {
        return !StringUtils.hasText(PathUtils.getFilenameExtension(_fileName));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private boolean isJar(final String path) {
        return PathUtils.isJar(path);
    }

    private String toExternalForm(final String root, final String absolutePath) {
        if (this.isJar(absolutePath)) {
            if (!root.startsWith("jar:")) {
                return "jar:".concat(root);
            }
        }
        return root;
    }

    private String lookupFolder(final String root, final String path) {
        return PathUtils.getParent(PathUtils.subtract(root, path));
    }
}
