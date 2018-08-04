package org.lyj.ext.script.program;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.IScriptConstants;
import org.lyj.ext.script.program.engines.AbstractEngine;
import org.lyj.ext.script.program.engines.javascript.EngineJavascript;
import org.lyj.ext.script.program.tools.sys.ToolConsole;
import org.lyj.ext.script.program.tools.sys.ToolEngine;
import org.lyj.ext.script.program.tools.sys.ToolRequirer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A script program
 */
public class Program
        implements IScriptConstants {

    // ------------------------------------------------------------------------
    //                      d e l e g a t e s
    // ------------------------------------------------------------------------

    @FunctionalInterface
    public static interface OutputCallback {
        void handle(final String level, final Object out);
    }

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Object> _context;
    private final ProgramLogger _logger;
    private final String _encoding;
    private String _engine_name;
    private ProgramFiles _files;
    private String _script;
    private AbstractEngine _engine_impl;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Program(final String encoding,
                   final Map<String, Object> context) {
        this(encoding, context, new ToolConsole(null));
    }

    public Program(final String encoding,
                   final Map<String, Object> context,
                   final OutputCallback out_callback) {
        this(encoding, context, new ToolConsole(out_callback));
    }

    public Program(final String encoding,
                   final Map<String, Object> context,
                   final ProgramLogger logger) {
        _context = null != context ? context : new HashMap<>();
        _logger = null != logger ? logger : new ToolConsole(null);
        _encoding = encoding;
        _engine_name = ENGINE_JAVASCRIPT;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        _context.clear();
        try {
            _logger.close();
        } catch (Throwable ignored) {
        }
    }

    public Map<String, Object> context() {
        return _context;
    }

    public ProgramLogger logger() {
        return _logger;
    }

    public String encoding() {
        return _encoding;
    }

    public String engineName() {
        return _engine_name;
    }

    public Program engineName(final String value) {
        if (CollectionUtils.contains(ENGINES, value)) {
            _engine_name = value;
            if (null != _files) {
                _files.engine(value);
            }
            // reset engine
            _engine_impl = null;
        }
        return this;
    }

    public String engineNameFileExtension() {
        return ENGINES_EXT.get(_engine_name);
    }

    public boolean hasFiles() {
        return null != _files;
    }

    /**
     * Returns ProgramFiles if program is based on a directory.
     * returns null if this is a simple text based program.
     *
     * @return null or ProgramFiles
     */
    public ProgramFiles files() {
        return _files;
    }


    public String root() {
        if (null != _files) {
            return _files.root();
        }
        return "";
    }

    public Program root(final String value) {
        if (StringUtils.hasText(value) && FileUtils.exists(value)) {
            if (null != _files) {
                _files = null;
            }
            _files = new ProgramFiles(value);
        }
        return this;
    }

    public String script() {
        if (!StringUtils.hasText(_script) && null != this.files()) {
            try {
                _script = FileUtils.readFileToString(new File(this.files().indexFilename()));
            } catch (Throwable t) {
                this.logger().error("script", t);
            }
        }
        return _script;
    }

    public Program script(final String value) {
        _script = value;
        return this;
    }

    public AbstractEngine engine() {
        return this.getEngine();
    }

    public synchronized Object run() throws Exception {
        return this.engine().eval();
    }

    public String absolutePath(final String raw_relative_path) {
        final String ext = PathUtils.getFilenameExtension(raw_relative_path, false);
        final String relative_path;
        if (!StringUtils.hasText(ext)) {
            relative_path = this.hasFiles()
                    ? this.files().filename(raw_relative_path)
                    : raw_relative_path.concat(this.engineNameFileExtension());
        } else {
            relative_path = raw_relative_path;
        }
        final String path = this.hasFiles()
                ? this.files().path(relative_path)
                : PathUtils.getAbsolutePath(relative_path);

        return path;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private AbstractEngine getEngine() {
        if (null == _engine_impl) {

            // factory method
            if (_engine_name.equals(ENGINE_JAVASCRIPT)) {
                _engine_impl = new EngineJavascript(this);
            } else {
                // default engine
                _engine_impl = new EngineJavascript(this);
            }

            this.initContext(_engine_impl.context());
        }
        return _engine_impl;
    }

    private void initContext(final Map<String, Object> context) {
        // add console if does not exists
        if (!context.containsKey(ToolConsole.NAME)) {
            context.put(ToolConsole.NAME, _logger);
        }

        if (!context.containsKey(ToolRequirer.NAME)) {
            context.put(ToolRequirer.NAME, new ToolRequirer(this));
        }

        if (!context.containsKey(ToolEngine.NAME)) {
            context.put(ToolEngine.NAME, new ToolEngine(this));
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    /**
     * Clear programs internal cache.
     * use only when you need to refresh the engine cache.
     */
    public static void clearCache() {
        ProgramScriptCache.instance().clear();
    }

    public static void clearCache(final String root) {
        ProgramScriptCache.instance().clear(root);
    }
}
