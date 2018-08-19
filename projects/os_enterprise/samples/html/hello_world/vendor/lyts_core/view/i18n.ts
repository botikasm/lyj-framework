import {Dictionary} from "../commons/collections/Dictionary";
import EventEmitter from "../commons/events/EventEmitter";
import browser from "./browser";
import dom from "./dom";


interface Items {
    [index: string]: string;
}

/**
 * Localization singleton controller.
 * Add dictionary using i18n.register(lang, dictionary);
 *
 * WARN:
 *  Do not listen directly at EVENT_CHANGE_LANG, but use Application events propagation.
 *  Components automatically handle this event, so you do not need to do it by yourself.
 *
 */
class i18n
    extends EventEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static readonly _EVENT_CHANGE_LANG: string = "on_change_lang";
    private static readonly _EVENT_LOCALIZED: string = "on_localized";

    private static readonly _DEF_LANG: string = "base";
    private static readonly _ATTR_DATA_I18N: string = "data-i18n";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _dictionaries: Dictionary<Dictionary<string>>;
    private readonly _browser_lang: string;
    private _lang: string;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {
        super();
        this._dictionaries = new Dictionary<Dictionary<string>>();

        // get lang from browser
        this._browser_lang = browser.lang();

        this.register("", {key: ""});
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get EVENT_CHANGE_LANG(): string {
        return i18n._EVENT_CHANGE_LANG;
    }

    public get EVENT_LOCALIZED(): string {
        return i18n._EVENT_LOCALIZED;
    }

    public get lang(): string {
        return this._lang || this._browser_lang;
    }

    public set lang(lang: string) {
        this._changeLang(lang);
    }

    public register(lang: string, dictionary: Dictionary<string> | Items): void {
        let dic: Dictionary<string> = (dictionary instanceof Dictionary) ? dictionary as Dictionary<string> : new Dictionary(dictionary);
        this._dictionaries.put(lang, dic);
    }

    public registerDefault(dictionary: Dictionary<string> | Items): void {
        let dic: Dictionary<string> = (dictionary instanceof Dictionary) ? dictionary as Dictionary<string> : new Dictionary(dictionary);
        this._dictionaries.put(i18n._DEF_LANG, dic);
    }

    public get(label: string, def_val?: string): string {
        if (this._dictionaries.containsKey(this._lang)) {
            const dic: Dictionary<string> = this._dictionaries.get(this._lang);
            return dic.get(label) || def_val || '';
        } else if (this._dictionaries.containsKey(i18n._DEF_LANG)) {
            const dic: Dictionary<string> = this._dictionaries.get(i18n._DEF_LANG);
            return dic.get(label) || def_val || '';
        }
        return def_val || '';
    }

    public localize(elem: HTMLElement): void {
        this._localize(elem);
        dom.forEachChild(elem, (child: HTMLElement) => {
            this._localize(child);
        }, true);
        const trigger_event: boolean = !!this._lang && this._dictionaries.count() > 0;
        if (trigger_event) {
            super.emit(i18n._EVENT_LOCALIZED, this._lang, this._dictionaries.get(this._lang));
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private _changeLang(value: string): void {
        const new_lang: string = !!value ? value.split('-')[0] : '';
        if (!!new_lang) {
            const lang_changed: boolean = (this._dictionaries.count() > 0) && (new_lang !== this._lang);
            if (lang_changed) {
                this._lang = new_lang;
                super.emit(i18n._EVENT_CHANGE_LANG, this._lang, this._dictionaries.get(this._lang));
            }
        }
    }

    private _localize(elem: HTMLElement): void {
        if (!!elem && !!elem.hasAttribute) {
            const data_i18n: string = elem.getAttribute(i18n._ATTR_DATA_I18N) || '';
            if (!!data_i18n) {
                const value: string = this.get(data_i18n);
                if (!!value) {
                    // console.log("i18n._localize", data_i18n, value);
                    // ready to set i18n text or placeholder
                    if (dom.isInput(elem)) {
                        if (dom.isInputButton(elem)) {
                            dom.setValue(elem, value);
                        } else if (elem.hasAttribute("placeholder")) {
                            elem.setAttribute("placeholder", value);
                        }
                    } else {
                        elem.innerHTML = value;
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: i18n;

    public static instance(): i18n {
        if (null == i18n.__instance) {
            i18n.__instance = new i18n();
        }
        return i18n.__instance;
    }


}

// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------

export default i18n.instance();
