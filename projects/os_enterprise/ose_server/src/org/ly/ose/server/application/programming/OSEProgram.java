package org.ly.ose.server.application.programming;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;
import org.ly.ose.server.application.programming.tools.ai.Tool_nlp;
import org.ly.ose.server.application.programming.tools.i18n.Tool_i18n;
import org.ly.ose.server.application.programming.tools.persistence.Tool_db;
import org.ly.ose.server.application.programming.tools.persistence.Tool_session;
import org.ly.ose.server.application.programming.tools.request.Tool_request;
import org.ly.ose.server.application.programming.tools.utils.Tool_resource;
import org.ly.ose.server.application.programming.tools.utils.Tool_rnd;
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
    private Program _program;
    private Loop _loop_ticker;

    private ScriptObjectMirror _script_object;
    private Object _init_response;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgram(final OSEProgramInfo program_info) {
        // clone program info
        _program_info = program_info;

        _root = program_info.installationRoot();
        _uid = program_info.uid();

        _logger = new OSEProgramLogger(this);

        _program = ScriptController.instance().create(_logger).root(_root);

        if (program_info.loopInterval() > 0) {
            _loop_ticker = new Loop();
            _loop_ticker.runInterval(program_info.loopInterval());
        }

        this.init();
    }

    @Override
    public String toString() {
        return _program_info.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public String uid() {
        return _uid;
    }

    public OSEProgramInfo info() {
        return _program_info;
    }

    public OSEProgramLogger logger() {
        return _logger;
    }

    public Object open() throws Exception {
        if (null == _script_object) {
            if (this.createScriptObject()) {
                // ready for onInit method
                final Object init_response = this.onInit();
                if (null != init_response) {
                    _init_response = Converter.toJsonCompatible(init_response);
                }

                // start loop ticker
                if (null != _loop_ticker) {
                    _loop_ticker.start(this::onTick);
                }
            }
        }
        return _init_response;
    }

    public void close() {
        if (null != _loop_ticker) {
            try {
                _loop_ticker.interrupt(); // stop loop ticker
            } catch (Throwable ignored) {
            }
            _loop_ticker = null;
        }

        if (null != _program) {
            this.finish();
            _program.close();
        }
        _script_object = null;
        _program = null;

        _logger = null;
        _init_response = null;
    }

    public String script() {
        if (null != _program) {
            return _program.script();
        }
        return "";
    }

    public boolean hasMember(final String memberName) {
        return hasMember(_script_object, memberName);
    }

    public Object callMember(final String scriptName,
                             final Object... args) throws Exception {
        synchronized (this) {
            Object script_response = null;

            if (this.hasMember(scriptName)) {
                script_response = callMember(_script_object, scriptName, args);
            } else {
                throw new Exception(FormatUtils.format("Missing method or handler. You invoked '%s', but I didn't find a script member with this name.", scriptName));
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

    public Object setContextValue(final String key,
                                  final Object value) {
        if (null != _program) {
            return _program.context().put(key, value);
        }
        return null;
    }

    public OSEProgramTool getContextTool(final String name) {
        if (null != _program) {
            return (OSEProgramTool) _program.context().get(ensureScriptPrefix(name));
        }
        return null;
    }

    public void request(final OSERequest request) {
        if (null != _program) {
            final Set<String> keys = _program.context().keySet();
            for (final String key : keys) {
                try {
                    final Object item = _program.context().get(key);
                    if (item instanceof OSEProgramToolRequest) {
                        ((OSEProgramToolRequest) item).set_native_request_value(request);
                    }
                } catch (Throwable ignored) {

                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    Object onInit() throws Exception {
        if (this.hasMember(ON_INIT)) {
            return this.callMember(ON_INIT, this);
        }
        return null;
    }

    Object onExpire() throws Exception {
        if (this.hasMember(ON_EXPIRE)) {
            return this.callMember(ON_EXPIRE, this);
        }
        return null;
    }

    Object onLoop() throws Exception {
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
        _program.context().put(ensureScriptPrefix(Tool_rnd.NAME), new Tool_rnd(this));
        _program.context().put(ensureScriptPrefix(Tool_session.NAME), new Tool_session(this));
        _program.context().put(ensureScriptPrefix(Tool_resource.NAME), new Tool_resource(this));

        // request tools
        _program.context().put(ensureScriptPrefix(Tool_request.NAME), new Tool_request(this)); // all request
        _program.context().put(ensureScriptPrefix(Tool_i18n.NAME), new Tool_i18n(this)); // uses lang
        _program.context().put(ensureScriptPrefix(Tool_nlp.NAME), new Tool_nlp(this)); // uses lang
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

    public static void clearCache(final String root) {
        Program.clearCache(root);
    }

    public static boolean isProtected(final String namespace) {
        if (StringUtils.hasText(namespace)) {
            final String[] tokens = StringUtils.split(namespace, "._", true);
            return CollectionUtils.contains(IConstants.PROTECTED_NAMESPACES, tokens[0]);
        }
        return false;
    }

    public static String ensureScriptPrefix(final String name) {
        if (StringUtils.hasText(name)) {
            if (!name.startsWith(SCRIPT_PREFIX)) {
                return SCRIPT_PREFIX.concat(name);
            }
        }
        return name;
    }

    public static boolean hasMember(final ScriptObjectMirror script,
                                    final String scriptName) {
        if (null != script && StringUtils.hasText(scriptName)) {
            try {
                if (scriptName.contains(".")) {
                    final String[] tokens = StringUtils.split(scriptName, ".");
                    ScriptObjectMirror tmp = null;
                    for (int i = 0; i < tokens.length; i++) {
                        final boolean latest = i == tokens.length - 1;
                        final String method = tokens[i];
                        if (latest) {
                            return null != tmp && tmp.hasMember(method);
                        } else {
                            tmp = (ScriptObjectMirror) script.callMember(method);
                        }
                    }
                } else {
                    return script.hasMember(scriptName);
                }
            } catch (Throwable ignored) {
                // ignored
            }
        }
        return false;
    }

    public static Object callMember(final ScriptObjectMirror script,
                                    final String scriptName,
                                    final Object... args) {
        if (null != script && StringUtils.hasText(scriptName)) {

            if (scriptName.contains(".")) {
                final String[] tokens = StringUtils.split(scriptName, ".");
                ScriptObjectMirror tmp = null;
                for (int i = 0; i < tokens.length; i++) {
                    final boolean latest = i == tokens.length - 1;
                    final String method = tokens[i];
                    if (latest) {
                        return null != tmp ? tmp.callMember(method, args) : null;
                    } else {
                        tmp = (ScriptObjectMirror) script.callMember(method);
                    }
                }
            } else {
                return script.callMember(scriptName, args);
            }
        }
        return null;
    }

}
