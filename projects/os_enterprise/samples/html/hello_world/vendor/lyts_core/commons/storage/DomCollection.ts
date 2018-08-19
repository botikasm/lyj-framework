import console from "../console";
import {AbstractCollection} from "./AbstractCollection";
import ly from "../../ly";

/**
 * Generic persistence storage collection using localStorage if available.
 */

const DOM_PREFIX: string = '_dom_coll_';

class DomCollection
    extends AbstractCollection {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(name: string) {
        super(name);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected save(): void {
        try {
            const value: string = JSON.stringify(this.items());
            this.write(value);
        } catch (err) {
            console.error('DomCollection.save', err);
        }
    }

    protected load(): void {
        try {
            const text: string = this.read();
            const items: any[] = JSON.parse(text || '[]') as Array<any>;
            this.items().push(...items);
        } catch (err) {
            console.error('DomCollection.load', err);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private read(): string {
        try {
            const id: string = DOM_PREFIX + this.name;
            const tag: HTMLElement | null = ly.dom.getElementById(id);
            if (!!tag) {
                return tag.textContent ||'';
            }
        } catch (err) {
            console.error('DomCollection.read', err);
        }
        return '';
    }

    private write(text: string): void {
        const id: string = DOM_PREFIX + this.name;
        let tag: HTMLElement | null = ly.dom.getElementById(id);
        if (!tag) {
            tag = ly.dom.createElement('script');
            tag['id'] = id;
            tag.setAttribute('type', 'text/template');
        }
        if (!!tag) {
            tag.textContent = text;
        }
    }

}

export default DomCollection;