package org.lyj.ext.script.program.exceptions;

public class ScriptEvalException extends Exception {

    public ScriptEvalException(final String message) {
        super(message);
    }

    public ScriptEvalException(final Exception ex) {
        super(ex);
    }

}
