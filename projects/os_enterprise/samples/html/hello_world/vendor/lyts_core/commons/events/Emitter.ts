import Events, {Listener} from "./Events";
import BaseObject from "../BaseObject";

/**
 * Simple emitter without a context
 */
export default class Emitter
    extends BaseObject {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _events: Events;

    // ------------------------------------------------------------------------
    //                      c o ns t r u c t o r
    // ------------------------------------------------------------------------

    constructor() {
        super();
        this._events = new Events;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public on(event_name: string, listener: Listener): void {
        // ly events
        this._events.removeListener(event_name, listener);
        this._events.on(event_name, listener);
    }

    public off(event_names?: string | string[], listener?: Listener): void {
        this._events.off(event_names, listener);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected emit(eventName: string, ...args: any[]): boolean {
        return this._events.emit(eventName, ...args);
    }


}