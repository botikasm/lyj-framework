/**
 * Extends standard console
 */
import random from "./random";

class console_ext {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _uid: string;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {
        this._uid = random.guid();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get uid() {
        return this._uid;
    }

    public set uid(value: string) {
        this._uid = value;
    }

    public error(scope: string, error: Error | string, ...args: any[]): void {
        console.error("[" + this.uid + "] " + scope, error, ...args);
    };

    public warn(scope: string, ...args: any[]): void {
        console.warn("[" + this.uid + "] " + scope, ...args);
    };

    public log(scope: string, ...args: any[]): void {
        console.log("[" + this.uid + "] " + scope, ...args);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {
        this.uid = random.guid();
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: console_ext;

    public static instance(): console_ext {
        if (null == console_ext.__instance) {
            console_ext.__instance = new console_ext();
        }
        return console_ext.__instance;
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------

export default console_ext.instance();


