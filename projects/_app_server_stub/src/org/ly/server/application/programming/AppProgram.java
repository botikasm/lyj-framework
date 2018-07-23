package org.ly.server.application.programming;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.ScriptController;
import org.lyj.ext.script.program.Program;
import org.lyj.ext.script.utils.Converter;

import java.util.HashMap;
import java.util.Map;

public class AppProgram {

    // ------------------------------------------------------------------------
    //                      c o n s t a n t s
    // ------------------------------------------------------------------------

    private static final String SCRIPT_PREFIX = "$";

    private static final String ON_INIT = "init";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final AppProgramInfo _program_info;
    private final String _uid;
    private AppProgramLogger _logger;
    private final Program _program;

    private ScriptObjectMirror _script;
    private Object _init_response;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AppProgram(final AppProgramInfo program_info) {
        _program_info = program_info;
        _root = program_info.installationRoot();
        _uid = program_info.uid();
        _logger = new AppProgramLogger(_uid);
        _program = ScriptController.instance().create(_logger).root(_root);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uid() {
        return _uid;
    }


    public Object open() {
        if (null == _script) {
            if (this.createScriptObject()) {
                // ready for onInit method
                final Object init_response = this.callMember(ON_INIT);
                if (null != init_response) {
                    _init_response = Converter.toJsonCompatible(init_response);
                }
            }
        }
        return _init_response;
    }

    public void close() {
        _program.close();
        _script = null;
    }

    public String script() {
        if (null != _program) {
            return _program.script();
        }
        return "";
    }

    public boolean hasMember(final String memberName) {
        return null != _script && _script.hasMember(memberName);
    }

    public Object callMember(final String scriptName,
                             final Object... args) {
        Object script_response = null;

        if (this.hasMember(scriptName)) {
            script_response = _script.callMember(scriptName, args);
        } else {
            _logger.warn(FormatUtils.format("Missing handler. Required at least '%s'", scriptName));
        }
        return script_response;
    }

    public Map<String, Object> getContextMap() {
        if (null != _program) {
            return _program.context();
        }
        return new HashMap<>();
    }

    public Object getContextObject(final String key) {
        if (null != _program) {
            return _program.context().get(key);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

        // extend program with custom context
        //_program.context().put(ensureScriptPrefix(BotPlugin.NAME), new BotPlugin(_bot));

    }

    private boolean createScriptObject() {
        // launch
        try {
            final Object script = _program.run();
            if (script instanceof ScriptObjectMirror) {
                _script = (ScriptObjectMirror) script;
                return true;
            } else {
                _logger.error("OSEProgram.createScriptObject", new Exception("Program is malformed. It should return a valid program instance."));
            }
        } catch (Throwable t) {
            _logger.error("OSEProgram.createScriptObject", t);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static String ensureScriptPrefix(final String name) {
        if (StringUtils.hasText(name)) {
            if (!name.startsWith(SCRIPT_PREFIX)) {
                return SCRIPT_PREFIX.concat(name);
            }
        }
        return name;
    }

}
