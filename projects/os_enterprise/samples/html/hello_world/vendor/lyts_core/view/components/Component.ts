import Events, {Listener, Listeners} from "../../commons/events/Events";
import dom from "../dom";
import lang from "../../commons/lang";
import {Dictionary} from "../../commons/collections/Dictionary";
import EventEmitter from "../../commons/events/EventEmitter";
import i18n from "../i18n";
import ElementWrapper from "./ElementWrapper";


abstract class Component
    extends EventEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _native_events: Dictionary<Events>; // event handlers for element events
    private readonly _native_elements: Dictionary<HTMLElement>; // map childs by hash_code
    private readonly _element: HTMLElement;
    private readonly _element_wrapper: ElementWrapper;

    private readonly _data: any; // simple data container

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor() {
        super();

        this._native_events = new Dictionary<Events>();
        this._native_elements = new Dictionary<HTMLElement>();
        this._element = this._createElement(this.render());
        this._element_wrapper = new ElementWrapper(this, this._element);
        this._data = {};

        this._normalizeElements();

        this.localize();

        // auto-localize
        i18n.on(this, i18n.EVENT_CHANGE_LANG, this.localize);
    }

    public remove(): void {
        this._free();
    }

    protected abstract render(): string;

    protected abstract ready(): void;

    protected abstract free(): void;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get data(): any {
        return this._data;
    }

    public get element(): ElementWrapper {
        return this._element_wrapper;
    }

    public get outerHTML(): string {
        return !!this._element ? this._element.outerHTML : "";
    }

    public get innerHTML(): string {
        return !!this._element ? this._element.innerHTML : "";
    }

    public hashCode(selector?: string): string {
        if (!!selector) {
            return this._hash(this._getFirstElement(selector));
        }
        return this._hash(this._element);
    }

    public hide(): void {
        dom.classAdd(this._element, 'hidden');
    }

    public show(): void {
        dom.classRemove(this._element, 'hidden');
    }

    // ------------------------------------------------------------------------
    //                      d o m
    // ------------------------------------------------------------------------

    public localize(): void {
        i18n.localize(this._element);
    }

    public get(selector: string): Array<ElementWrapper> {
        let result: Array<ElementWrapper> = [];
        let elements: Array<HTMLElement> = dom.get(selector, this._element);
        for (let elem of elements) {
            result.push(new ElementWrapper(this, elem));
        }
        return result;
    }

    public getFirst(selector: string): ElementWrapper {
        return new ElementWrapper(this, dom.getFirst(selector, this._element));
    }

    public getLast(selector: string): ElementWrapper {
        return new ElementWrapper(this, dom.getLast(selector, this._element));
    }

    public appendTo(selector: string | ElementWrapper, clean_parent: boolean = false): void {
        const elem: ElementWrapper = (selector instanceof ElementWrapper)
            ? selector
            : new ElementWrapper(this, dom.getFirst(selector));
        if (!!elem) {
            if (clean_parent) {
                elem.innerHTML = '';
            }
            elem.appendChild(this._element);

            //-- this is ready --//
            this.ready();
        }
    }

    public appendChild(child_html: string, opt_target_selector?: string): void {
        if (!!child_html) {
            const child: HTMLElement = this._createElement(child_html);
            const target: HTMLElement | null = this._resolveElement(opt_target_selector || null, this._element);
            if (!!target) {
                target.appendChild(child);
                // handle events for child
                this._normalizeElement(child);
            }
        }

    }

    /**
     * Return a class list.
     * @param {string} selector Element selector. Only first matched element is returned.
     * @return {string[]} Array of class names
     */
    public classList(selector: string): string[] {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        if (!!elem) {
            return elem.className.split(" ");
        }
        return [];
    }

    /**
     * Check if selected element contains passed class. If passed class is an Array, check for all of them.
     * @param {string} selector Element selector. Only first matched element is returned.
     * @param {string | string[]} class_name Single class name or multiple class names.
     * @return {boolean} Match found or not.
     */
    public classHas(selector: string, class_name: string | string[]): boolean {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        if (!!elem) {
            let classes: string[] = lang.toArray<string>(class_name);
            for (let aclass of classes) {
                if (!elem.classList.contains(aclass)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check the array for at least one match.
     * @param {string} selector  Element selector. Only first matched element is returned.
     * @param {string[]} class_name Array of classes
     * @return {boolean} Match found or not.
     */
    public classHasOne(selector: string, class_name: string[]): boolean {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        if (!!elem) {
            let classes: string[] = lang.toArray<string>(class_name);
            for (let aclass of classes) {
                if (elem.classList.contains(aclass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public classAdd(selector: string, class_name: string | string[]): boolean {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        return dom.classAdd(elem, class_name);
    }

    public classRemove(selector: string, class_name: string | string[]): boolean {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        return dom.classRemove(elem, class_name);
    }

    public classSet(selector: string, value: string): string {
        return this.attrSet(selector, "class", name);
    }


    public attrValue(selector: string, attr_name: string): string {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        if (!!elem) {
            return elem.getAttribute(attr_name) || '';
        }
        return '';
    }

    public attrHas(selector: string, attr_name: string): boolean {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        if (!!elem) {
            return elem.hasAttribute(attr_name);
        }
        return false;
    }

    public attrSet(selector: string, name: string, value: string): string {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        if (!!elem) {
            elem.setAttribute(name, value);
            return elem.getAttribute(name) || '';
        }
        return '';
    }

    public getValue(selector: string): any {
        const elem: HTMLElement | null = this._getFirstElement(selector);
        if (!!elem) {
            return dom.getValue(elem);
        }
        return null;
    }


    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    /**
     * Add event listener to internal HTMLElement
     * @param {string} selector
     * @param {string} event_name
     * @param {Listener} listener
     */
    addEventListener(selector: string, event_name: string, listener: Listener): void {
        const elem: HTMLElement | null = this._resolveElement(selector, this._element);
        if (!!elem) {
            this._addEventListener(elem, event_name, listener);
        } else {
            console.warn("Component.addEventListener()",
                "Unable to add event '" + event_name + "' to '" + selector + "': Element not found!");
        }
    }

    /**
     * Remove event listener from internal HTMLElement
     * @param {string} selector
     * @param {string | string[]} event_names
     */
    removeEventListener(selector: string, event_names?: string | string[]): void {
        const elem: HTMLElement | null = this._resolveElement(selector, this._element);
        if (!!elem) {
            this._removeEventListener(elem, lang.toArray(event_names));
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private _free(): void {
        // remove ly events
        super.off(this);

        // remove native events
        this._freeListeners();

        // remove element from dom
        if (!!this._element) {
            this._element.remove();
        }

        // clear list
        this._native_events.clear();
        this._native_elements.clear();

        i18n.off(this, i18n.EVENT_CHANGE_LANG);

        // call abstract free
        this.free();
    }

    private _freeListeners(): void {
        const hash_codes: string[] = this._native_events.keys();
        for (let hash_code of hash_codes) {
            let elem: HTMLElement = this._native_elements.get(hash_code);
            if (!!elem) {
                let names: string[] = this._native_events.get(hash_code).eventNames();
                let count: number = this._removeEventListener(elem, names);
                //console.log("Component._free()", hash_code, names, count);
            }
        }
    }


    private _normalizeElements(): void {
        // events on root
        this._normalizeElement(this._element);
        // events on child
        dom.forEachChild(this._element, (elem) => {
            this._normalizeElement(elem);
        }, true);
    }

    private _resolveElement(elem_or_selector: HTMLElement | string | null, defVal?: HTMLElement): HTMLElement | null {
        if (!!elem_or_selector) {
            if (lang.isString(elem_or_selector)) {
                let found: HTMLElement | null = this._getFirstElement(elem_or_selector as string);
                if (!!found) {
                    return this._normalizeElement(found);
                }
            } else {
                let found: HTMLElement | null = elem_or_selector as HTMLElement;
                if (!!found) {
                    return this._normalizeElement(found);
                }
            }
        }
        return !!defVal ? this._normalizeElement(defVal) : null;
    }


    private _getElement(selector: string): Array<HTMLElement> {
        return dom.get(selector, this._element);
    }

    private _getFirstElement(selector: string): HTMLElement | null {
        return dom.getFirst(selector, this._element);
    }

    private _getLastElement(selector: string): HTMLElement | null {
        return dom.getLast(selector, this._element);
    }

    private _addEventListener(elem: HTMLElement, event_name: string, listener: Listener): void {
        const hash_code = this._hash(elem);
        if (!this._native_events.containsKey(hash_code)) {
            this._native_events.put(hash_code, new Events());
        }

        // get context binded listener
        let ctx_listener = listener.bind(this);

        // register reference for further removal
        this._native_events.get(hash_code).on(event_name, ctx_listener);
        // attach listener to native event
        elem.addEventListener(event_name, ctx_listener, false);
    }

    private _removeEventListener(elem: HTMLElement, event_names: string[], listener?: Listener): number {
        let counter = 0;
        if (!!elem) {
            const hash_code = this._hash(elem);
            if (this._native_events.containsKey(hash_code)) {
                const events: Events = this._native_events.get(hash_code);
                for (let name of event_names) {
                    if (!!listener) {
                        // remove reference
                        events.removeListener(name, listener);
                        // remove native
                        elem.removeEventListener(name, listener);

                        counter++;
                    } else {
                        const all_listeners: Listeners = events.listeners(name);
                        for (let i = 0; i < all_listeners.length; i++) {
                            const _listener = all_listeners[i];
                            // remove reference
                            events.removeListener(name, _listener);
                            // remove native
                            elem.removeEventListener(name, _listener);

                            counter++;
                        }
                    }
                }
            }
        }
        return counter;
    }

    private _createElement(html: string): HTMLElement {
        html = html.trim();
        return this._normalizeElement(dom.newElement(html));
    }

    private _normalizeElement(elem: HTMLElement): HTMLElement {
        // add hash
        this._hash(elem);

        //... do more stuff here

        return elem;
    }

    private _hash(elem: HTMLElement | null): string {
        if (null != elem) {
            let hash_code: string = ElementWrapper.hash(elem);
            if (!!hash_code) {
                // add new element reference to internal hash dictionary
                this._native_elements.put(hash_code, elem);
            }
            return hash_code;
        }
        return '';
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

export default Component;