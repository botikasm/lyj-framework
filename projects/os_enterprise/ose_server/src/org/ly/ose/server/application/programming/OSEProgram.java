package org.ly.ose.server.application.programming;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.ly.ose.server.application.programming.tools.persistence.Tool_db;
import org.lyj.commons.async.future.Loop;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.ScriptController;
import org.lyj.ext.script.program.Program;
import org.lyj.ext.script.utils.Converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Program wrapper.
 * Use this class to invoke methods from the program.
 */
public class OSEProgram {

    // ------------------------------------------------------------------------
    //                      c o n s t a n t s
    // ------------------------------------------------------------------------

    private static final String SCRIPT_PREFIX = "$";

    private static final String ON_INIT = "_init";
    private static final String ON_EXPIRE = "_expire"; // session expired
    private static final String ON_LOOP = "_loop";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final OSEProgramInfo _program_info;
    private final String _uid;
    private OSEProgramLogger _logger;
    private final Program _program;
    private final Loop _loop_ticker;

    private ScriptObjectMirror _script_object;
    private Object _init_response;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgram(final OSEProgramInfo program_info) {
        _program_info = program_info;
        _root = program_info.installationRoot();
        _uid = program_info.uid();

        _logger = new OSEProgramLogger(_uid);

        _program = ScriptController.instance().create(_logger).root(_root);

        _loop_ticker = new Loop();
        _loop_ticker.runInterval(program_info.loopInterval());

        this.init();
    }

    @Override
    public String toString() {
        return _program_info.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uid() {
        return _uid;
    }

    public OSEProgramInfo info() {
        return _program_info;
    }

    public Object open() {
        if (null == _script_object) {
            if (this.createScriptObject()) {
                // ready for onInit method
                final Object init_response = this.onInit();
                if (null != init_response) {
                    _init_response = Converter.toJsonCompatible(init_response);
                }

                // start loop ticker
                _loop_ticker.start(this::onTick);
            }
        }
        return _init_response;
    }

    public void close() {
        _loop_ticker.interrupt(); // stop loop ticker
        if (null != _program) {
            this.finish();
            _program.close();
        }
        _script_object = null;
    }

    public String script() {
        if (null != _program) {
            return _program.script();
        }
        return "";
    }

    public boolean hasMember(final String memberName) {
        return null != _script_object && _script_object.hasMember(memberName);
    }

    public Object callMember(final String scriptName,
                             final Object... args) {
        synchronized (this) {
            Object script_response = null;

            if (this.hasMember(scriptName)) {
                script_response = _script_object.callMember(scriptName, args);
            } else {
                _logger.warn(FormatUtils.format("Missing handler. Required at least '%s'", scriptName));
            }
            return script_response;
        }
    }

    public Map<String, Object> getContext() {
        if (null != _program) {
            return _program.context();
        }
        return new HashMap<>();
    }

    public Object getContextValue(final String key) {
        if (null != _program) {
            return _program.context().get(key);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    Object onInit() {
        if (this.hasMember(ON_INIT)) {
            return this.callMember(ON_INIT, this);
        }
        return null;
    }

    Object onExpire() {
        if (this.hasMember(ON_EXPIRE)) {
            return this.callMember(ON_EXPIRE, this);
        }
        return null;
    }

    Object onLoop() {
        if (this.hasMember(ON_LOOP)) {
            return this.callMember(ON_LOOP, this);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

        // extend program with custom context
        _program.context().put(ensureScriptPrefix(Tool_db.NAME), new Tool_db(this));

    }

    private void finish() {
        try {
            final Set<String> keys = _program.context().keySet();
            for (final String key : keys) {
                try {
                    final Object item = _program.context().get(key);
                    if (item instanceof OSEProgramTool) {
                        ((OSEProgramTool) item).close();
                    }
                } catch (Throwable ignored) {

                }
            }
        } catch (Throwable ignored) {

        }
    }

    private boolean createScriptObject() {
        // launch
        try {
            final Object script = _program.run();
            if (script instanceof ScriptObjectMirror) {
                _script_object = (ScriptObjectMirror) script;
                return true;
            } else {
                _logger.error("OSEProgram.createScriptObject", new Exception("Program is malformed. It should return a valid program instance."));
            }
        } catch (Throwable t) {
            _logger.error("OSEProgram.createScriptObject", t);
        }
        return false;
    }

    private void onTick(final Loop.LoopInterruptor interruptor) {
        synchronized (this) {
            try {
                this.onLoop();
            } catch (Throwable ignored) {
                // ignored
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static boolean isProtected(final String namespace) {
        if (StringUtils.hasText(namespace)) {
            final String[] tokens = StringUtils.split(namespace, "._", true);
            return CollectionUtils.contains(IConstants.PROTECTED_NAMESPACES, tokens[0]);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
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
