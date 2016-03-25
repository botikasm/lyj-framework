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

    private static final int RUN_EVERY_SECONDS = 10;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _running;
    private boolean _stopped;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopMonitor(){
        super(TimeUnit.SECONDS, 0, RUN_EVERY_SECONDS, 0, 0);
        super.setMaxThreads(3);

        _running = false;
        _stopped = false;

        super.start((t)->{
            try{
                if(!_running && !_stopped){
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

    @Override
    public void stop(boolean interrupt_if_running) {
        super.stop(interrupt_if_running);
        _stopped = true;
        super.logger().info("DesktopMonitor stopped.");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void run() throws Exception {
        // start catalogueDesktop all desktop files, move all into archive folder in USERHOME/desktop-fences/archive
        DesktopController.instance().catalogueDesktop(true);
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
            __instance = null;
            DesktopController.instance().close();
        }
    }


}
