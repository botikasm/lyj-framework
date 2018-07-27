package org.ly.ose.server.application.programming.tools;

import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.OSEProgramInfo;
import org.lyj.commons.util.StringUtils;

public abstract class OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final String _name;
    private final OSEProgram _program;
    private final OSEProgramInfo _info;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgramTool(final String name,
                          final OSEProgram program) {
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

    public OSEProgramInfo info() {
        return _info;
    }

    public String sessionId() {
        return StringUtils.toString(_info.data().get(OSEProgramInfo.FLD_SESSION_ID));
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected OSEProgram program() {
        return _program;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
