import globals from "../globals";

/**
 * Main message controller.
 * Use this singleton to show message.
 */
export default class ToastController {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    public static readonly SHORT: number = 3000;
    public static readonly NORMAL: number = 5000;
    public static readonly MEDIUM: number = 10000;
    public static readonly LONG: number = 20000;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    private constructor() {

    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    public showError(msg: string, duration?: number): void {
        this.show(msg, 'red darken-2', duration);
    }

    public showWarning(msg: string, duration?: number): void {
        this.show(msg, 'orange darken-2', duration);
    }

    public showInfo(msg: string, duration?: number): void {
        this.show(msg, 'green darken-2', duration);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private show(msg: string, classes: string, duration?: number): void {
        if (!!globals && !!globals.Materialize) {
            const M:any = globals.Materialize;
            M.toast(msg);
            /**
            M.toast(
                {
                    html: msg,
                    classes: !!classes ? 'rounded ' + classes : 'rounded',
                    displayLength: duration ? duration : ToastController.NORMAL
                }
            );
             **/
        }
    }


    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: ToastController;

    public static instance(): ToastController {
        if (null == ToastController.__instance) {
            ToastController.__instance = new ToastController();
        }
        return ToastController.__instance;
    }
}


