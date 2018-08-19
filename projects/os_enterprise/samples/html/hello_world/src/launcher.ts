import ly from "../vendor/lyts_core/ly";
import BaseObject from "../vendor/lyts_core/commons/BaseObject";
import console from "../vendor/lyts_core/commons/console";
import {StyleManager} from "../vendor/lyts_core_style/StyleManager";
import constants from "./constants";
import Main from "./views/Main";
import ApplicationController from "./controllers/ApplicationController";

const CONTAINER: string = "#_app_container";

class launcher
    extends BaseObject {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _main: Main;
    private _global_i18n: any;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {
        super();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public start(): void {
        ApplicationController.ready((app_context) => {
            this.loadMain();
        });
    }

    /**
     * This method is expected from ConversaCon controller to remove running app
     */
    public remove(): void {
        try {
            if (!!this._main) {
                this._main.remove();
                console.log("launcher.remove()", constants.version);
            }
            if (!!this._global_i18n) {
                this._global_i18n.off(this); // remove all listeners
            }

            // remove local listeners, too
            ly.Application.events.off(this);

        } catch (err) {
            console.error("launcher.remove()", err)
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {
        // init application scope
        BaseObject.PREFIX = constants.uid + "_"; // application uid become component prefix.

        console.uid = constants.uid;

        // local i18n
        ly.Application.events.on(this, ly.i18n.EVENT_CHANGE_LANG, this.onLocalChangeLang);

        this.initStyles();
    }

    private loadMain(): void {
        this._main = new Main();
        this._main.appendTo(CONTAINER);
    }

    private onLocalChangeLang(lang: string): void {
        // propagate to global
        if (!!this._global_i18n) {
            this._global_i18n.lang = lang;
        }
    }

    public initStyles(): void {
        //StyleManager
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: launcher;

    static instance(): launcher {
        if (null == launcher.__instance) {
            launcher.__instance = new launcher();
        }
        return launcher.__instance;
    }

}

// ------------------------------------------------------------------------
//                      S T A R T   A P P L I C A T I O N
// ------------------------------------------------------------------------


launcher.instance().start();