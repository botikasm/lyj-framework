package org.lyj.ext.selenium.controllers.routines.controller;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.PathUtils;

public class RoutineLogger
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    RoutineLogger(final String root,
                  final String name){
        super(logger(name, root));
        super.useConsole(false); // log only in script file
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    private static Logger logger(final String key, final String root){
        final String path =  PathUtils.concat(root, key);
        LoggingRepository.getInstance().setAbsoluteLogFileName(key, PathUtils.concat(path, key + ".log"));
        return LoggingUtils.getLogger(key);
    }

}
