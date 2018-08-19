import Events, {Listener} from "./Events";
import BaseObject from "../BaseObject";
import {Dictionary} from "../collections/Dictionary";

/**
 * Class that emit events with a scope.
 */
export default class EventEmitter
    extends BaseObject {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _listeners: Dictionary<Events>;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor() {
        super();
        this._listeners = new Dictionary<Events>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public on(scope: BaseObject, eventName: string, listener: Listener): void {
        let key: string = EventEmitter.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new Events());
        }
        this._listeners.get(key).on(eventName, listener.bind(scope));
    }

    public once(scope: BaseObject, eventName: string, listener: Listener): void {
        let key: string = EventEmitter.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new Events());
        }
        this._listeners.get(key).once(eventName, listener.bind(scope));
    }

    public off(scope: BaseObject, eventName?: string): void {
        let key: string = EventEmitter.key(scope);
        if (this._listeners.containsKey(key)) {
            this._listeners.get(key).off(eventName);
        }
    }

    public clear(): void {
        if(!!this._listeners) {
            let keys: string[] = this._listeners.keys();
            for (let key of keys) {
                if (this._listeners.containsKey(key)) {
                    this._listeners.get(key).clear();
                }
            }
        }
    }

    public emit(eventName: string, ...args: any[]): void {
        if(!!this._listeners){
            let keys: string[] = this._listeners.keys();
            for (let key of keys) {
                if (this._listeners.containsKey(key)) {
                    this._listeners.get(key).emit(eventName, ...args);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static key(scope: BaseObject): string {
        try {
            return scope.uid;
        } catch (err) {
            console.warn("ApplicationEvents.key()", "BINDING EVENT ON DEFAULT KEY!");
            return '_default';
        }
    }

}