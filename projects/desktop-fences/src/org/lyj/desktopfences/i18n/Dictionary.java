package org.lyj.desktopfences.i18n;


import org.lyj.commons.i18n.DictionaryController;
import org.lyj.desktopfences.i18n.error.Error;
import org.lyj.desktopfences.i18n.gui.Gui;

/**
 * Global static Dictionary Helper.
 */
public class Dictionary
        extends DictionaryController {


    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Dictionary(){

        //-- register all dictionaries --//

        super.register(Error.class);
        super.register(Gui.class);
    }


    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static DictionaryController __instance;

    public static DictionaryController getInstance(){
        if(null==__instance){
            __instance = new Dictionary();
        }
        return __instance;
    }



}
