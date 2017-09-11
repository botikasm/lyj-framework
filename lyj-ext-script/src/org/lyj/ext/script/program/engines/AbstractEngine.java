package org.lyj.ext.script.program.engines;

import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.ScriptLogger;
import org.lyj.ext.script.program.Program;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public abstract class AbstractEngine
        extends ScriptLogger {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Program _program;
    private final Map<String, Object> _context;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractEngine(final Program program) {
        _program = program;
        _context = new HashMap<>(program.context());
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    public abstract void close();

    public abstract Object eval(final String script) throws Exception;

    public abstract Object eval(final String script, final Map<String, Object> context) throws Exception;

    public abstract void addRuntimeAttribute(final String key, final Object value);

    public abstract Object getRuntimeAttribute(final String key);

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    protected Program program() {
        return _program;
    }

    public Map<String, Object> context() {
        return _context;
    }

    public String loadResource(final String resourcePath) {
        return ClassLoaderUtils.getResourceAsString(this.getClass().getClassLoader(), this.getClass(), resourcePath, _program.encoding());
    }

    public Object eval() throws Exception {
        return this.eval(new HashMap<>());
    }

    public Object eval(final Map<String, Object> context) throws Exception {
        final String script = _program.script();
        if (StringUtils.hasText(script)) {
            return this.eval(script, context);
        } else {
            // nothing to evaluate in a script program without a script
            _program.logger().warn("Cannot run a program with no script or files to execute!");
        }
        return null;
    }

}
