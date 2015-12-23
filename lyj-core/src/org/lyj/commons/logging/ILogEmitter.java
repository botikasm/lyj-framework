package org.lyj.commons.logging;

/**
 * Created by angelogeminiani on 22/12/15.
 */
public interface ILogEmitter {

    Logger getLogger();

    void log(final Level level, final String methodName, final String message);

    void log(final Level level, final String methodName, final String message, final Throwable t);

    void debug(final String methodName, final String message);

    void info(final String methodName, final String message);

    void error(final String methodName, final String message);

}
