package org.lyj.commons.logging;

/**
 * Log emitter.
 */
public interface ILogEmitter {

    //Logger logger();

    void log(final Level level, final String methodName, final String message);

    void log(final Level level, final String methodName, final String message, final Throwable t);

    void debug(final String methodName, final String message);

    void info(final String methodName, final String message);

    void error(final String methodName, final String message);

    void error(final String methodName, final String template, final Object... args);

    void error(final String methodName, final Throwable error);

}
