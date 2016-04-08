package org.lyj.desktopgap.app.view.filechooser;

import javafx.stage.FileChooser;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.util.StringUtils;
import org.lyj.desktopgap.app.DesktopGap;
import org.lyj.gui.application.app.utils.PlatformUtils;

import java.io.File;
import java.util.*;

/**
 * Helper class to open a file chooser
 */
public class FileChooserHelper {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, String> _extension_filter;
    private boolean _multiple_files;
    private String _title;
    private String _initial_dir;
    private String _initial_file;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private FileChooserHelper() {
        _extension_filter = new HashMap<>();
        _multiple_files = false;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public Map<String, String> extensionFilter() {
        return _extension_filter;
    }

    /**
     * Add extension filter
     *
     * @param title      Description of extension. ex: "Images"
     * @param extensions Comma separated extensions. ex: "*.png,*.jpg,*.gif"
     * @return Instance
     */
    public FileChooserHelper addExtensionFilter(final String title, final String extensions) {
        _extension_filter.put(title, extensions);
        return this;
    }

    public boolean multipleFiles() {
        return _multiple_files;
    }

    public FileChooserHelper multipleFiles(final boolean value) {
        _multiple_files = value;
        return this;
    }

    public String title() {
        return _title;
    }

    public FileChooserHelper title(final String value) {
        _title = value;
        return this;
    }

    public String initialDir() {
        return _initial_dir;
    }

    public FileChooserHelper initialDir(final String value) {
        _initial_dir = value;
        return this;
    }

    public String initialFile() {
        return _initial_file;
    }

    public FileChooserHelper initialFile(final String value) {
        _initial_file = value;
        return this;
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public List<File> open() {
        final List<File> response = new ArrayList<>();
        if (null != DesktopGap.instance().primaryStage()) {
            // await response
            this.open(response).getSilent();
        }
        return response;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Task<Void> open(final List<File> files) {
        return new Task<Void>((t) -> {
            PlatformUtils.synch(() -> {
                try {
                    FileChooser fileChooser = new FileChooser();
                    this.init(fileChooser);
                    if (_multiple_files) {
                        files.addAll(fileChooser.showOpenMultipleDialog(DesktopGap.instance().primaryStage()));
                    } else {
                        files.add(fileChooser.showOpenDialog(DesktopGap.instance().primaryStage()));
                    }
                    t.success(null);
                } catch (Throwable err) {
                    t.fail(err);
                }
            });
        }).setTimeout(60 * 1000).run();
    }

    private void init(final FileChooser fileChooser) {
        // extension filters
        if (_extension_filter.size() > 0) {
            final Set<String> keys = _extension_filter.keySet();
            for (final String key : keys) {
                final String extensions = _extension_filter.get(key);
                final String[] tokens = StringUtils.split(extensions, ",");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(key, tokens));
            }
        }

        // title
        if (StringUtils.hasText(_title)) {
            fileChooser.setTitle(_title);
        }

        // root
        if (StringUtils.hasText(_initial_dir)) {
            fileChooser.setInitialDirectory(new File(_initial_dir));
        }

        if (StringUtils.hasText(_initial_file)) {
            fileChooser.setInitialFileName(_initial_file);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static FileChooserHelper __instance;

    public static FileChooserHelper instance() {
        if (null == __instance) {
            __instance = new FileChooserHelper();
        }
        return __instance;
    }

}
