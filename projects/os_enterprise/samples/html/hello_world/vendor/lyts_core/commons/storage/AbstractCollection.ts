import ly from "../../ly";
import console from "../console";

/**
 * Generic persistence storage
 */
abstract class AbstractCollection {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _name: string;
    private _items: any[];

    private _limit: number;
    private _enabled: boolean;

    private _initialized: boolean;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(name: string) {
        this._name = name;
        this._items = [];
        this._limit = 0; // no, limits
        this._initialized = false;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get name(): string {
        return this._name;
    }

    public get length(): number {
        return this.items().length;
    }

    public get limit(): number {
        return this._limit;
    }

    public set limit(value: number) {
        this._limit = value;
    }

    public get enabled(): boolean {
        return this._enabled;
    }

    public set enabled(value: boolean) {
        this._enabled = value;
    }

    public isEmpty(): boolean {
        return this.items().length === 0;
    }

    public clear(): void {
        this._items = [];
        this.save();
    }

    public push(item: any) {
        if (this.enabled && !!item) {
            this.items().push(item);
            if (this._limit > 0 && this.items().length > this._limit) {
                this.items().shift();
            }
            this.save();
        }
    }

    public get(index_or_key: number | string): any {
        const items = this.items();
        if (ly.lang.isNumber(index_or_key)) {
            const index: number = index_or_key as number;
            if (index < items.length) {
                return items[index];
            }
        } else if (ly.lang.isString(index_or_key)) {
            const key: string = index_or_key as string;
            return this.findOne(key);
        }
        return null;
    }

    public forEach(callback: Function): void {
        const items: Array<any> = this.items();
        for (let item of items) {
            if (!!ly.lang.funcInvoke(callback, item)) {
                break;
            }
        }
    }

    public findOne(query: any): any {
        const response: Array<any> = this.find(query, 0, 1);
        return response.length == 1 ? response[0] : null;
    }

    public find(query: any, skip: number = 0, limit: number = 0): Array<any> {
        const response: Array<any> = [];
        const items: Array<any> = this.items();
        let count = 0;
        let count_limit = 0;
        for (let item of items) {
            if (skip > 0 && skip < count) {
                continue;
            }
            if (limit > 0 && count_limit > limit) {
                break;
            }
            if (this.match(query, item)) {
                response.push(item);
                count_limit++;
            }
            count++;
        }
        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected abstract save(): void;

    protected abstract load(): void;

    protected items(): Array<any> {
        if (!this._initialized && this._items.length === 0) {
            this._initialized = true;
            this.load();
        }
        return this._items;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private match(query: any, item: any): boolean {
        try {
            if (ly.lang.isString(query)) {
                const key_value: string = query as string;
                const value: string = item['_key'] || item['_id'] || item['key'] || item['id'];
                return value === key_value;
            }
        } catch (err) {
            console.error('AbstractCollection.match', err);
        }
        return false;
    }

}

export {AbstractCollection};