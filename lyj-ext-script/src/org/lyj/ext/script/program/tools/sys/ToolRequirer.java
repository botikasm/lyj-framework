package org.lyj.ext.script.program.tools.sys;

import org.lyj.commons.util.FileUtils;
import org.lyj.ext.script.program.Program;
import org.lyj.ext.script.program.ProgramScriptCache;

import java.io.File;

/**
 * System Tool.
 * <p>
 * Use "require" to import content from other files.
 * <p>
 * Usage:
 * requirer.require('dir/file');
 */
public class ToolRequirer {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "requirer";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Program _program;
    private final ProgramScriptCache _cache_script;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ToolRequirer(final Program program) {
        _program = program;
        _cache_script = ProgramScriptCache.instance(); // singleton global cache
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String require(final String raw_relative_path) {
        try {
            final String path = _program.absolutePath(raw_relative_path);
            // update cache
            if (!_cache_script.contains(path)) {
                _cache_script.put(path, FileUtils.readFileToString(new File(path)));
            }
            return _cache_script.get(path); //FileUtils.readFileToString(new File(path));
        } catch (Throwable t) {
            _program.logger().error("require", t);
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
