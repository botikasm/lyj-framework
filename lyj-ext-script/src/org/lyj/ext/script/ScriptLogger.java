package org.lyj.ext.script;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.LoggingRepository;

/**
 * Logger for scripting
 */
public class ScriptLogger
        extends AbstractLogEmitter {

    public ScriptLogger() {
        LoggingRepository.getInstance().setLogFileName(this.getClass().getName(), "./scripting.log");
    }

}
