import Events, {Listener} from "../commons/events/Events";
import {Dictionary} from "../commons/collections/Dictionary";
import BaseObject from "../commons/BaseObject";
import i18n from "../view/i18n";
import EventEmitter from "../commons/events/EventEmitter";
import lang from "../commons/lang";


class ApplicationEvents {

    private readonly _listeners: Dictionary<Events>;

    constructor() {
        this._listeners = new Dictionary<Events>();
    }

    public on(scope: BaseObject, eventName: string, listener: Listener): void {
        let key: string = ApplicationEvents.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new Events());
        }
        this._listeners.get(key).on(eventName, listener.bind(scope));
    }

    public once(scope: BaseObject, eventName: string, listener: Listener): void {
        let key: string = ApplicationEvents.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new Events());
        }
        this._listeners.get(key).once(eventName, listener.bind(scope));
    }

    public off(scope: BaseObject, eventName: string): void {
        let key: string = ApplicationEvents.key(scope);
        if (this._listeners.containsKey(key)) {
            this._listeners.get(key).off(eventName);
        }
    }

    public emit(eventName: string, ...args: any[]): void {
        let keys: string[] = this._listeners.keys();
        for (let key of keys) {
            if (this._listeners.containsKey(key)) {
                this._listeners.get(key).emit(eventName, ...args);
            }
        }
    }

    public clear(): void {
        let keys: string[] = this._listeners.keys();
        for (let key of keys) {
            if (this._listeners.containsKey(key)) {
                this._listeners.get(key).clear();
            }
        }
    }

    private static key(scope: BaseObject): string {
        try {
            return scope.uid;
        } catch (err) {
            console.warn("ApplicationEvents.key()", "BINDING EVENT ON DEFAULT KEY!");
            return '_default';
        }
    }
}

/**
 * Main Application Controller.
 * This is a singleton
 *
 * Events:
 * i18n.EVENT_CHANGE_LANG: Application propagates i18n EVENT_CHANGE_LANG for a centralized and
 * managed access to this event.
 *
 */
class Application
    extends BaseObject {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static _EVENT_CHANGE_LANG: string = i18n.EVENT_CHANGE_LANG;
    private static _EVENT_LOCALIZED: string = i18n.EVENT_LOCALIZED;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    //-- main event bus --//
    private readonly _events: EventEmitter;

    //-- application global scope --//
    private readonly _scope: Dictionary<any>;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {
        super();
        this._events = new EventEmitter();
        this._scope = new Dictionary();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public get EVENT_CHANGE_LANG(): string {
        return Application._EVENT_CHANGE_LANG;
    }

    public get EVENT_LOCALIZED(): string {
        return Application._EVENT_LOCALIZED;
    }

    public get events(): EventEmitter {
        return this._events;
    }

    public get scope(): Dictionary<any> {
        return this._scope;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public clear(): void {
        if (!!this._scope) {
            this._scope.clear();
        }
        if (!!this._events) {
            this._events.clear();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {
        // i18n event (debounced to avoid multiple events)
        i18n.on(this, this.EVENT_CHANGE_LANG, lang.funcDebounce(this, this.oni18nLangChange, 400, true));
        i18n.on(this, this.EVENT_LOCALIZED, lang.funcDebounce(this, this.oni18nLocalized, 400, true));
    }

    private oni18nLangChange(lang: string, dictionary: Dictionary<string>) {
        this.events.emit(this.EVENT_CHANGE_LANG, lang, dictionary);
    }

    private oni18nLocalized(lang: string, dictionary: Dictionary<string>) {
        this.events.emit(this.EVENT_LOCALIZED, lang, dictionary);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: Application;

    public static instance(): Application {
        if (null == Application.__instance) {
            Application.__instance = new Application();
        }
        return Application.__instance;
    }

}

export default Application.instance();