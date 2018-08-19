import console from "../console";
import {AbstractCollection} from "./AbstractCollection";

/**
 * Generic persistence storage collection using localStorage if available.
 */
class StorageCollection
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
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    protected save(): void {
        try {
            localStorage[this.name] = JSON.stringify(this.items());
        } catch (err) {
            console.error('StorageCollection.save', err);
        }
    }

    protected load(): void {
        try {
            const items: any[] = JSON.parse(localStorage[this.name] || '[]') as Array<any>;
            this.items().push(...items);
        } catch (err) {
            console.error('StorageCollection.load', err);
        }
    }

}

export default StorageCollection;