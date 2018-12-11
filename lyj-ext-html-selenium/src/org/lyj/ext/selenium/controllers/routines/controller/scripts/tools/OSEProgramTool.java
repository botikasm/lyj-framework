package org.lyj.ext.selenium.controllers.routines.controller.scripts.tools;

import org.lyj.ext.selenium.controllers.routines.controller.scripts.ScriptProgram;
import org.lyj.ext.selenium.controllers.routines.model.ModelPackage;

public abstract class OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final String _name;
    private final ScriptProgram _program;
    private final ModelPackage _info;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgramTool(final String name,
                          final ScriptProgram program) {
        _name = name;
        _program = program;
        _info = _program.info();
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    public abstract void close();

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String name() {
        return _name;
    }

    public ModelPackage info() {
        return _info;
    }


    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected ScriptProgram program() {
        return _program;
    }

    protected OSEProgramTool tool(final String name) {
        return _program.getContextTool(name);
    }

    protected Object firstValueOf(final Object... values) {
        if (null != values && values.length > 0) {
            return values[0];
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
