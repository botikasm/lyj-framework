package org.lyj.ext.script.program.tools.sys;

import org.lyj.ext.script.program.Program;

/**
 * Special variable to access engine properties
 */
public class ToolEngine {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "__engine__";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Program _program;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ToolEngine(final Program program) {
        _program = program;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void addAttribute(final String key, final Object value) {
        try {
            _program.engine().addRuntimeAttribute(key, value);
        } catch (Throwable t) {
            _program.logger().error("[ToolEngine.addAttribute]", t);
        }
    }

    public Object getAttribute(final String key) {
        try {
            return _program.engine().getRuntimeAttribute(key);
        } catch (Throwable t) {
            _program.logger().error("[ToolEngine.getAttribute]", t);
        }
        return false;
    }




}
