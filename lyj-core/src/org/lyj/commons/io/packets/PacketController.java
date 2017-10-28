package org.lyj.commons.io.packets;


import org.lyj.commons.Delegates;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Get a zip archive and move into temp folder, then check package type and
 */
public class PacketController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String[] VALID_EXTENSIONS = new String[]{
            "zip"
    };

    private static final String[] INVALID_PATHS = new String[]{
            "MACOSX",
            ".DS_Store"
    };

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final List<String> _valid_extensions;
    private final List<String> _invalid_paths;

    private boolean _remove_source;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public PacketController() {
        _valid_extensions = Arrays.asList(VALID_EXTENSIONS);
        _invalid_paths = Arrays.asList(INVALID_PATHS);
        _remove_source = true;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    /**
     * List of valid file extensions.
     * Default value is "zip".
     * This list is used to check which files are valid zip archives
     */
    public List<String> validExtensions() {
        return _valid_extensions;
    }

    /**
     * List of invalid paths or sub-path.
     * This paths are used to check which files include in response.
     */
    public List<String> invalidPaths() {
        return _invalid_paths;
    }

    public boolean flagRemoveSource() {
        return _remove_source;
    }

    public PacketController flagRemoveSource(final boolean value) {
        _remove_source = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isValidPacket(final File file) {
        final String ext = PathUtils.getFilenameExtension(file.getName(), false);
        return CollectionUtils.contains(_valid_extensions, ext) && !this.containsInvalidPath(file);
    }

    /**
     * Process package file and remove it if valid.
     *
     * @param file     Package file to process. Only valid packages are processed.
     * @param callback Callback invoked is valid files were found
     */
    public boolean process(final File file,
                           final Delegates.Callback<List<File>> callback) throws IOException {
        // invalid files are not precessed
        if (this.isValidPacket(file)) {
            try {
                // unzip files and then remove temp directory
                this.unzip(file, (files) -> {
                    final List<File> valid_files = new LinkedList<>();
                    for (final File tmp_file : files) {
                        if (!this.containsInvalidPath(tmp_file)) {
                            valid_files.add(tmp_file);
                        }
                    }
                    if (!valid_files.isEmpty()) {
                        Delegates.invoke(callback, valid_files);
                    }
                });
                return true;
            } finally {
                if (_remove_source) {
                    // remove source
                    FileUtils.delete(file.getAbsolutePath());
                }
            }
        }

        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean containsInvalidPath(final File file) {
        for (final String invalid : _invalid_paths) {
            if (file.getAbsolutePath().contains(invalid)) {
                return true;
            }
        }
        return false;
    }

    private void unzip(final File file,
                       final Delegates.Callback<List<File>> callback) throws IOException {
        if (FileUtils.getSize(file) > 0) {
            final String root = file.getParent();
            final String tmp_root = this.getTempPath();
            try {
                FileUtils.mkdirs(tmp_root);
                ZipUtils.unzip(file.getAbsolutePath(), tmp_root);
                // get unzipped files
                final List<File> files = new LinkedList<>();
                FileUtils.listFiles(files, new File(tmp_root), "*.*", "", -1);
                if (!files.isEmpty()) {
                    Delegates.invoke(callback, files);
                }
            } finally {
                // remove temporary directory
                FileUtils.delete(tmp_root);
            }
        }
    }

    private String getTempPath() {
        return PathUtils.getTemporaryDirectory(RandomUtils.randomUUID());
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


}
