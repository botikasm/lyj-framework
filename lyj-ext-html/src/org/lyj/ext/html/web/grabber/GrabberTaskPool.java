package org.lyj.ext.html.web.grabber;

import org.lyj.commons.async.FixedBlockingPool;

/**
 *
 */
public class GrabberTaskPool
        extends FixedBlockingPool {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private GrabberTaskPool() {
        super.capacity(100); // starts with 100 threads in queue

        super.corePoolSize(10);     // min 10 threads
        super.maximumPoolSize(30);  // max 100 threads
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static GrabberTaskPool __instance;

    public static GrabberTaskPool instance() {
        if (null == __instance) {
            __instance = new GrabberTaskPool();
        }
        return __instance;
    }

}
