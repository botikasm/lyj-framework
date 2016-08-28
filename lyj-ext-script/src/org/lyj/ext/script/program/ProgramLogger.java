package org.lyj.ext.script.program;

import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 */
public abstract class ProgramLogger
        extends Writer {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private StringWriter _writer;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ProgramLogger() {
        _writer = new StringWriter();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        _writer.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        _writer.flush();
        this.handle(null, _writer.toString().trim());

        // reset
        _writer.close();
        _writer = null;
        _writer = new StringWriter();
    }

    @Override
    public void close() throws IOException {
        _writer.close();
    }

    public void log(final Object... values) {
        this.handle(Level.INFO, values);
    }

    public void error(final Object... values) {
        this.handle(Level.SEVERE, values);
    }

    public void error(final String methodName, final Throwable t) {
        this.handle(Level.SEVERE, methodName, t.toString());
    }

    public void info(final Object... values) {
        this.handle(Level.INFO, values);
    }

    public void debug(final Object... values) {
        this.handle(Level.FINE, values);
    }

    public void config(final Object... values) {
        this.handle(Level.CONFIG, values);
    }

    public void warn(final Object... values) {
        this.handle(Level.WARNING, values);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    protected abstract void handle(final Level level, final Object... values);



}
