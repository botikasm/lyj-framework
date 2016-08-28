package org.lyj.ext.script.program;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.IScriptConstants;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Files controller.
 * Everything starts from a folder.
 * <p>
 * Root folder is the program starting point.
 */
public class ProgramFiles
        implements IScriptConstants {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String DEF_ENGINE = ENGINE_JAVASCRIPT;
    private static final String DEF_INDEX = "index"; // default is "index"

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;

    private String _index;
    private String _engine;
    private String _ext;
    private boolean _initialized;
    private File[] _files;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ProgramFiles(final File root) {
        this(root.getAbsolutePath());
    }

    public ProgramFiles(final String root) {
        _root = root;
        _index = DEF_INDEX;
        this.engine(DEF_ENGINE);

        _initialized = false;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public String path(final String relativePath) {
        return StringUtils.hasText(relativePath) ? PathUtils.combine(_root, relativePath) : _root;
    }

    public String engine() {
        return _engine;
    }

    ProgramFiles engine(final String value) {
        if (CollectionUtils.contains(ENGINES, value)) {
            _engine = value;
            _ext = ENGINES_EXT.get(value);
            _initialized = false;
        }
        return this;
    }

    public String index() {
        return _index;
    }

    public ProgramFiles index(final String value) {
        if (StringUtils.hasText(value)) {
            _index = PathUtils.getFilename(value, false);
            _ext = PathUtils.getFilenameExtension(value, true);
            if (!StringUtils.hasText(_ext)) {
                _ext = ENGINES_EXT.get(this.engine());
            }
            _initialized = false;
        }
        return this;
    }

    public String indexFilename() {
        return this.path(_index.concat(_ext));
    }

    public File indexFile() {
        return new File(this.indexFilename());
    }

    public File[] files() {
        this.init();
        return _files;
    }

    /**
     * Return a valid program file name with engine extension.
     *
     * @param raw_name
     * @return
     */
    public String filename(final String raw_name) {
        final String ext = PathUtils.getFilenameExtension(raw_name);
        return StringUtils.hasText(ext) ? raw_name : raw_name.concat(_ext);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!_initialized) {
            _initialized = true;

            final List<File> files = new LinkedList<>();
            FileUtils.listFiles(files, new File(this.root()));

            _files = files.toArray(new File[files.size()]);
        }
    }


}
