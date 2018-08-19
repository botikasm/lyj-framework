import {Dictionary} from "../collections/Dictionary";
import lang from "../lang";


// ------------------------------------------------------------------------
//                      T Y P E S
// ------------------------------------------------------------------------

interface Listener {
    (...args: any[]): void;
}

type Listeners = Array<Listener>;

/**
 * Events controller.
 *
 * <code>
 *
 * import {Events} from "./events/Events";
 *
 * class MyEmitter extends Events{}
 *
 * let myEmitter = new MyEmitter();
 * myEmitter.on('event', () => {
 *   console.log('event occured')
 * });
 *
 * myEmitter.emit('event');
 *
 * </code>
 *
 *
 */
class Events {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static DEFAULT_MAX_LISTENERS: number = 10; // max listener for each event name

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _events = new Dictionary<Listeners>();

    private _maxListeners: number = 0;

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public getMaxListeners(): number {
        return this._maxListeners === 0 ? Events.DEFAULT_MAX_LISTENERS : this._maxListeners;
    }

    public setMaxListeners(limit: number): Events {
        this._maxListeners = limit;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public addListener(eventName: string, listener: Listener): Events {
        return this.on(eventName, listener);
    }

    public on(eventName: string, listener: Listener): Events {
        this._registerEvent(eventName, listener, false);
        return this;
    }

    public once(eventName: string, listener: Listener): Events {
        this._registerEvent(eventName, listener, true);
        return this;
    }

    public off(event_names?: string | string[], listener?: Listener): Events {
        const names: string[] = lang.isArray(event_names)
            ? event_names as string[]
            : !!event_names ? [event_names as string] : [];
        if (!!listener) {
            for (let name of names) {
                this.removeListener(name, listener);
            }
        } else {
            if (names.length > 0) {
                this.removeAllListeners(names);
            } else {
                this.removeAllListeners();
            }
        }
        return this;
    }

    public emit(eventName: string, ...args: any[]): boolean {
        let listeners: Listeners = this._events.get(eventName);
        let listenerCount = this.listenerCount(eventName);
        if (listeners) {
            listeners.map(listener => listener(...args));
        }
        return listenerCount !== 0;
    }

    public eventNames(): string[] {
        return this._events.keys();
    }


    public listeners(eventName: string): Listeners {
        return this._events.get(eventName);
    }

    public listenerCount(eventName: string): number {
        let listeners: Listeners = this._events.get(eventName);
        return listeners === undefined ? 0 : listeners.length;
    }

    public removeAllListeners(eventNames?: Array<string>): Events {
        if (!eventNames) {
            eventNames = this._events.keys();
        }
        eventNames.forEach(eventName => this._events.remove(eventName));
        return this;
    }

    public removeListener(eventName: string, listener: Listener): Events {
        let listeners: Listeners = this.listeners(eventName);
        let filtered_listeners: Listeners = !!listeners
            ? listeners.filter(item => item === listener) // filter only valid
            : [];
        this._events.put(eventName, filtered_listeners);
        return this;
    }

    public clear(): void {
        this._events.clear();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private _registerEvent(eventName: string, listener: Listener, type: boolean): void {
        if (this._listenerLimitReached(eventName)) {
            console.warn("Maximum listener reached, new Listener not added");
            return;
        }
        if (type === true) {
            listener = this._createOnceListener(listener, eventName);
        }
        let listeners = Events._createListeners(listener, this.listeners(eventName));
        this._events.put(eventName, listeners);
        return;
    }

    private _createOnceListener(listener: Listener, eventName: string): Listener {
        return (...args: any[]) => {
            this.removeListener(eventName, listener);
            return listener(...args);
        };
    }

    private _listenerLimitReached(eventName: string): boolean {
        return this.listenerCount(eventName) >= this.getMaxListeners();
    }

    private static _createListeners(listener: Listener, listeners?: Listeners): Listeners {
        if (!listeners) {
            listeners = [];
        }
        listeners.push(listener);
        return listeners;
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

export default Events;

export {Listener, Listeners};