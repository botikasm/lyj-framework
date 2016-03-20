package org.lyj.desktopfences.app.scheduledtasks;

import org.lyj.commons.async.future.Timed;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.desktopfences.app.controllers.DesktopController;

import java.util.concurrent.TimeUnit;

/**
 * Run periodically to check Desktop for new files.
 *
 */
public class DesktopMonitor
        extends Timed {
    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final int RUN_EVERY_MINUTES = 1;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _running;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopMonitor(){
        super(TimeUnit.MINUTES, 0, RUN_EVERY_MINUTES, 0, 0);

        super.start((t)->{
            try{
                if(!_running){
                    _running = true;
                    this.run();
                }
            } catch(Throwable err){
                super.error("run", FormatUtils.format("Error: %s",
                        ExceptionUtils.getMessage(err)));
            } finally {
                _running = false;
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void run() throws Exception {
        // start catalogue all desktop files, move all into archive folder in USERHOME/desktop-fences/archive
        DesktopController.instance().catalogue();
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static DesktopMonitor __instance;

    public static DesktopMonitor start(){
        if(null==__instance){
            __instance = new DesktopMonitor();
        }
        return __instance;
    }

    public static void stop(){
        if(null!=__instance){
            __instance.stop(true);
        }
    }


}
