import {Listener} from "../../commons/events/Events";
import random from "../../commons/random";
import dom from "../dom";
import Component from "./Component";

/**
 * Wrap a native HTMLElement to expose at Component methods.
 */
class ElementWrapper {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static readonly HASH_ATTRIBUTE: string = "__hash__";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _owner: Component;
    private _element: HTMLElement | null;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(owner: Component, elem: HTMLElement | null) {
        this._owner = owner;
        this._element = elem;

        this._hash_all();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Warning: do not attach events to this object.
     * Use instead "addEventListener" method.
     * @return {HTMLElement}
     */
    public get htmlElement(): HTMLElement | null {
        return this._element;
    }

    public hasElement(): boolean {
        return !!this._element;
    }

    public remove(): void {
        if (!!this._element) {
            this._element.remove();
        }
    }

    public appendChild(child: HTMLElement | ElementWrapper): void {
        if (!!this._element) {
            if (child instanceof ElementWrapper) {
                const elem: ElementWrapper = child as ElementWrapper;
                if (!!elem._element) {
                    this._element.appendChild(elem._element);
                }
            } else {
                this._element.appendChild(child as HTMLElement);
            }
        }
    }

    public appendTo(parent: HTMLElement | ElementWrapper): void {
        if (!!this._element && !!parent) {
            if (parent instanceof ElementWrapper) {
                (parent as ElementWrapper).appendChild(this._element);
            } else {
                (parent as HTMLElement).appendChild(this._element);
            }
        }
    }

    public set innerHTML(value: string) {
        if (!!this._element) {
            this._element.innerHTML = value;
        }
    }

    public get innerHTML() {
        if (!!this._element) {
            return this._element.innerHTML;
        }
        return '';
    }

    public set scrollTop(value: number) {
        if (!!this._element) {
            this._element.scrollTop = value;
        }
    }

    public get scrollTop(): number {
        if (!!this._element) {
            return this._element.scrollTop;
        }
        return 0;
    }

    public set scrollLeft(value: number) {
        if (!!this._element) {
            this._element.scrollLeft = value;
        }
    }

    public get scrollLeft(): number {
        if (!!this._element) {
            return this._element.scrollLeft;
        }
        return 0;
    }

    public get scrollWidth(): number {
        if (!!this._element) {
            return this._element.scrollWidth;
        }
        return 0;
    }

    public get scrollHeight(): number {
        if (!!this._element) {
            return this._element.scrollHeight;
        }
        return 0;
    }

    public scrollBy(x: number, y: number): void {
        if (!!this._element) {
            return this._element.scrollBy(x, y);
        }
    }

    public scrollTo(x: number, y: number): void {
        if (!!this._element) {
            return this._element.scrollTo(x, y);
        }
    }

    public get offsetWidth(): number {
        if (!!this._element) {
            return this._element.offsetWidth;
        }
        return 0;
    }

    public get offsetHeight(): number {
        if (!!this._element) {
            return this._element.offsetHeight;
        }
        return 0;
    }

    public get offsetTop(): number {
        if (!!this._element) {
            return this._element.offsetTop;
        }
        return 0;
    }

    public get clientWidth(): number {
        if (!!this._element) {
            return this._element.clientWidth;
        }
        return 0;
    }

    public get clientHeight(): number {
        if (!!this._element) {
            return this._element.clientHeight;
        }
        return 0;
    }

    public get clientLeft(): number {
        if (!!this._element) {
            return this._element.clientLeft;
        }
        return 0;
    }

    public get clientTop(): number {
        if (!!this._element) {
            return this._element.clientTop;
        }
        return 0;
    }

    public get children(): Array<ElementWrapper> {
        let response: Array<ElementWrapper> = [];
        if (!!this._element) {
            dom.forEachChild(this._element, (elem) => {
                response.push(new ElementWrapper(this._owner, elem));
            });
        }
        return response;
    }


    public addEventListener(event_name: string, listener: Listener): void {
        if (null != this._element && !!this._owner) {
            let hash_code: string = ElementWrapper.hash(this._element);
            if (!!hash_code) {
                let selector: string = "[" + ElementWrapper.HASH_ATTRIBUTE + "=" + hash_code + "]";
                this._owner.addEventListener(selector, event_name, listener);
            }
        } else {
            if (!this._element) {
                console.error("ElementWrapper.addEventListener()", "Missing HTML Element.", this);
            } else {
                console.error("ElementWrapper.addEventListener()", "Component Owner.", this._element);
            }
        }
    }

    public removeEventListener(event_names?: string | string[]): void {
        if (null != this._element && !!this._owner) {
            const hash_code: string = ElementWrapper.hash(this._element);
            if (!!hash_code) {
                const selector: string = "[" + ElementWrapper.HASH_ATTRIBUTE + "=" + hash_code + "]";
                this._owner.removeEventListener(selector, event_names);
            }
        } else {
            console.error("ElementWrapper.removeEventListener()", "Missing HTML Element or Component Owner.");
        }
    }

    public classAdd(class_name: string | string[]): boolean {
        return dom.classAdd(this._element, class_name);
    }

    public classRemove(class_name: string | string[]): boolean {
        return dom.classRemove(this._element, class_name);
    }

    public hasAttribute(name: string): boolean {
        if (!!this._element) {
            return this._element.hasAttribute(name);
        }
        return false;
    }

    public setAttribute(name: string, value: string): void {
        if (!!this._element) {
            this._element.setAttribute(name, value);
        }
    }

    public getAttribute(name: string): string {
        if (!!this._element) {
            return this._element.getAttribute(name) || "";
        }
        return "";
    }

    public removeAttribute(name: string): void {
        if (!!this._element) {
            this._element.removeAttribute(name);
        }
    }

    public createAttribute(name: string): void {
        if (!!this._element) {
            if (!this._element.hasAttribute(name)) {
                this._element.setAttributeNode(dom.createAttribute(name));
            }
        }
    }

    public value(value?: any): any {
        try {
            if (!!this._element) {
                if (value != undefined) {
                    dom.setValue(this._element, value);
                }
                return dom.getValue(this._element);
            }
        } catch (err) {
            console.error("ElementWrapper.value()", err);
        }
        return '';
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private _hash_all(): void {
        if (null != this._element) {
            // events on root
            ElementWrapper.hash(this._element);
            // events on child
            dom.forEachChild(this._element, (elem: HTMLElement) => {
                ElementWrapper.hash(elem);
            }, true);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static hash(elem: HTMLElement | null): string {
        if (!!elem && !!elem.hasAttribute) {
            if (!elem.hasAttribute(ElementWrapper.HASH_ATTRIBUTE)) {
                let hash_code = random.id();
                elem.setAttribute(ElementWrapper.HASH_ATTRIBUTE, hash_code);
            }
            return elem.getAttribute(ElementWrapper.HASH_ATTRIBUTE) || '';
        }
        return '';
    }

}

export default ElementWrapper;