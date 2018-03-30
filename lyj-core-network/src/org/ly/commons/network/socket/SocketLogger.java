package org.ly.commons.network.socket;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.LoggingRepository;

/**
 * Socket Logger
 */
public class SocketLogger
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketLogger() {
        // logger
        LoggingRepository.getInstance().setLogFileName(this.getClass(), "socket.log");
    }


}
