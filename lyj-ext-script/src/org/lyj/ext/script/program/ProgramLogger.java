package org.lyj.ext.script.program;

import org.lyj.commons.logging.ILogEmitter;
import org.lyj.commons.logging.Level;
import org.lyj.commons.util.FormatUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 */
public abstract class ProgramLogger
        extends Writer
        implements ILogEmitter {

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

    @Override
    public void log(final Level level, final String methodName, final String message) {
        this.handle(level, methodName, message);
    }

    @Override
    public void log(final Level level, final String methodName, final String message, final Throwable t) {
        this.handle(level, methodName, message, t);
    }

    @Override
    public void debug(final String methodName, final String message) {
        this.handle(Level.FINE, methodName, message);
    }

    @Override
    public void info(final String methodName, final String message) {
        this.handle(Level.INFO, methodName, message);
    }

    @Override
    public void error(final String methodName, final String message) {
        this.handle(Level.SEVERE, methodName, message);
    }

    @Override
    public void error(final String methodName, final String template, final Object... args) {
        this.handle(Level.SEVERE, methodName, FormatUtils.format(template, args));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    protected abstract void handle(final Level level, final Object... values);


}
