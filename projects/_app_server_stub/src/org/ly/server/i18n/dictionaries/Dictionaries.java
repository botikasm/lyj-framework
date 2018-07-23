package org.ly.server.i18n.dictionaries;


import org.ly.server.i18n.dictionaries.system.DicSystem;
import org.lyj.commons.i18n.DictionaryController;

/**
 * Global static Dictionary Helper.
 */
public class Dictionaries
        extends DictionaryController {


    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Dictionaries() {

        //-- register all dictionaries --//

        super.register(DicSystem.class);

    }


    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static DictionaryController __instance;

    public static synchronized DictionaryController instance() {
        if (null == __instance) {
            __instance = new Dictionaries();
        }
        return __instance;
    }


}
