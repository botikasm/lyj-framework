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

package org.lyj.commons.io.repository.deploy;

import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper for file deployer settings
 */
public class FileDeployerSettings {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String SUFFIX_MIN = ".mini";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, String> _compileFiles = new HashMap<String, String>(); // pair: source ext, target ext. i.e. ".less", ".css"
    private final Set<String> _compressFiles = new HashSet<String>();
    private final Set<String> _excludeFiles = new HashSet<String>();
    private final Set<String> _preprocessorFiles = new HashSet<String>(); // Arrays.asList(PREPROCESS_FILES)
    private final Map<String, String> _preprocessorValues = new HashMap<String, String>();

    private DeployerCallback _callback;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileDeployerSettings() {

    }

    public FileDeployerSettings(final FileDeployerSettings parent) {
        if (null != parent) {
            _compileFiles.putAll(parent.compileFileExts());
            _compressFiles.addAll(parent.compressFileExts());
            _excludeFiles.addAll(parent.excludeFileOrExts());
            _preprocessorFiles.addAll(parent.preprocessorFileExts());
            _preprocessorValues.putAll(parent.preprocessorModel());
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public FileDeployerSettings clear() {
        _compileFiles.clear();
        _excludeFiles.clear();
        _compressFiles.clear();
        _preprocessorFiles.clear();
        _preprocessorValues.clear();
        return this;
    }

    public FileDeployerSettings callback(final DeployerCallback callback) {
        _callback = callback;
        return this;
    }

    public Set<String> excludeFileOrExts() {
        return _excludeFiles;
    }

    public Set<String> preprocessorFileExts() {
        return _preprocessorFiles;
    }

    public Map<String, String> preprocessorModel() {
        return _preprocessorValues;
    }

    public Set<String> compressFileExts() {
        return _compressFiles;
    }

    public Map<String, String> compileFileExts() {
        return _compileFiles;
    }

    public boolean isExcluded(final String file_or_ext) {
        final String ext = PathUtils.getFilenameExtension(file_or_ext, true);
        return StringUtils.hasText(ext)
                ? _excludeFiles.contains(ext) || _excludeFiles.contains(file_or_ext)
                : _excludeFiles.contains(file_or_ext);
    }

    public boolean isPreProcessableFile(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return isPreProcessableExt(ext);
    }

    public boolean isPreProcessableExt(final String ext) {
        return _preprocessorFiles.contains(ext);
    }


    public boolean isCompilableFile(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return isCompilableExt(ext);
    }

    public boolean isCompilableExt(final String ext) {
        return _compileFiles.containsKey(ext);
    }

    public boolean isCompressibleFile(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return isCompressibleExt(ext);
    }

    public boolean isCompressibleExt(final String ext) {
        return _compressFiles.contains(ext);
    }


    public String getMiniFilename(final String sourcePath) {
        if (this.isCompressibleFile(sourcePath)) {
            final String name = PathUtils.getFilename(sourcePath, false);
            final String ext = PathUtils.getFilenameExtension(sourcePath, true);
            if (!name.endsWith(".min") && !name.endsWith("_min") && !name.endsWith("-min")) {
                return PathUtils.changeFileName(sourcePath, name.concat(SUFFIX_MIN).concat(ext));
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    byte[] callback(final byte[] data, final String resourceName) {
        if (null != _callback) {
            try {
                _callback.call(data, resourceName);
            } catch (Throwable ignored) {
            }
        }
        return data;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      e m b e d d e d
    // ------------------------------------------------------------------------

    @FunctionalInterface
    public static interface DeployerCallback {
        byte[] call(byte[] bytes, String resourceName);
    }


}
