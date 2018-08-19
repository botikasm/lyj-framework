import {Listener} from "../../vendor/lyts_core/commons/events/Events";
import ly from "../../vendor/lyts_core/ly";
import ModelApplicationContext from "../model/ModelApplicationContext";
import EventEmitter from "../../vendor/lyts_core/commons/events/EventEmitter";

/**
 * Main APPLICATION controller.
 * Use this singleton to access application status ang global methods.
 */
class ApplicationController {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _app_context: ModelApplicationContext;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {

    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public get events(): EventEmitter {
        return ly.Application.events;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Return application context when ready
     * @param {Listener} callback Pass to listener a Context object
     * @param {boolean} force Use to reload app_context
     */
    public ready(callback: Listener, force?: boolean): void {
        if (!!this._app_context || !!force) {
            ly.lang.funcInvoke(callback, this._app_context);
        } else {
            // creates application context
            this._app_context = new ModelApplicationContext();

            ly.lang.funcInvoke(callback, this._app_context);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: ApplicationController;

    public static instance(): ApplicationController {
        if (null == ApplicationController.__instance) {
            ApplicationController.__instance = new ApplicationController();
        }
        return ApplicationController.__instance;
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------

export default ApplicationController.instance();