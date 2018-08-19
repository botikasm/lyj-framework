/**
 *
 *  MANAGE APP DESKTOP INSTALLATION:
 *  https://developers.google.com/web/fundamentals/app-install-banners/
 *
 */
import console from "../commons/console";

class installer {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _install_event: any;
    private _is_ready_callback: Function | null;
    private _finish_callback: Function | null;

    private _install_status: Number; // -1=unassigned, 0=false, 1=true

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {
        this._install_status = 9999; // not initialized

        //-- event hooks --//
        if (!!window) {
            window.removeEventListener('beforeinstallprompt', this._on_beforeinstallprompt);
            window.addEventListener('beforeinstallprompt', this._on_beforeinstallprompt);
            console.log('installer.constructor', 'OK');
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get installed(): Boolean {
        return this._install_status === 1;
    }

    /**
     * Invoke callback if application manifest is valid for desktop installation.
     */
    public init(callback: () => void): installer {
        if (!this._is_ready_callback) {
            this._is_ready_callback = callback;
        }
        if (!!this._install_event) {
            this.doInit();
        }
        return this;
    }

    public prompt(): installer {
        try {
            if (!!this._install_event && !!this._install_event.prompt) {
                this._install_event.prompt();
            }
        } catch (err) {
            console.error('installer.prompt', err);
        }
        return this;
    }

    public finish(callback: () => void): installer {
        if (!this._finish_callback) {
            this._finish_callback = callback;
        }
        if (this.prompted) {
            this.doFinish();
        }
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private doInit(): void {
        if (!!this._is_ready_callback) {
            this._is_ready_callback();
            this._is_ready_callback = null; // RESET
        }
    }

    private doFinish(): void {
        if (!!this._finish_callback) {
            this._finish_callback();
            this._finish_callback = null; // RESET
        }
    }

    private get prompted(): Boolean {
        return this._install_status != -1 &&
            this._install_status != 9999;
    }

    private _on_beforeinstallprompt(install_event: any): void {
        // log if manifest allow installation
        console.log('installer._on_beforeinstallprompt', 'Found a valid manifest.json for desktop installation.');

        this._install_status = -1;

        // set or update install event
        this._install_event = install_event;

        // wait for the user to respond to the prompt
        this._install_event.userChoice
            .then((choiceResult: any) => {
                if (choiceResult.outcome === 'accepted') {
                    console.log('installer._on_beforeinstallprompt', 'User accepted the A2HS prompt');
                    this._install_status = 1;
                } else {
                    console.log('installer._on_beforeinstallprompt', 'User dismissed the A2HS prompt');
                    this._install_status = 0;
                }
                // reset
                this._install_event = null;
                this.doFinish();
            });

        this.doInit();
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: installer;

    public static instance(): installer {
        if (null == installer.__instance) {
            installer.__instance = new installer();
        }
        return installer.__instance;
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------

export default installer.instance();
