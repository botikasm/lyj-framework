package org.lyj.ext.script.program.tools.sys;

import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.program.Program;

import java.io.File;

/**
 * System Tool.
 * <p>
 * Use "require" to import content from other files.
 * <p>
 * Usage:
 * requirer.require('dir/file');
 */
public class ToolRequire {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "requirer";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Program _program;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ToolRequire(final Program program) {
        _program = program;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String require(final String raw_relative_path) {
        final String ext = PathUtils.getFilenameExtension(raw_relative_path, false);
        final String relative_path;
        if (!StringUtils.hasText(ext)) {
            relative_path = _program.hasFiles()
                    ? _program.files().filename(raw_relative_path)
                    : raw_relative_path.concat(_program.engineNameFileExtension());
        } else {
            relative_path = raw_relative_path;
        }
        try {
            final String path = _program.hasFiles()
                    ? _program.files().path(relative_path)
                    : PathUtils.getAbsolutePath(relative_path);
            return FileUtils.readFileToString(new File(path));
        } catch (Throwable t) {
            _program.logger().error("require", t);
        }
        return "";
    }

}
