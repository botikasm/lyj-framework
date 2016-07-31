package org.lyj.ext.bot.messenger;

import java.util.Map;

/**
 * Main controller
 */
public class Messenger {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Messenger() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void handle(Map<String, Object> raw_request){
        this.handle(new FacebookRequest(raw_request));
    }

    public void handle(final FacebookRequest request){

    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static Messenger __instance;

    public static Messenger instance() {
        if (null == __instance) {
            __instance = new Messenger();
        }
        return __instance;
    }

}
