interface Items<T> {
    [index: string]: T;
}

export type Keys = string[];
export type Values<T> = T[];

export class Dictionary<T> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _items: Items<T> = {};
    private _count: number = 0;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(o?: Items<T>) {
        if (!!o) {
            for (let key in o) {
                if (o.hasOwnProperty(key)) {
                    this.put(key, o[key]);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public put(key: string, value: T) {
        this._items[key] = value;
        this._count++;
    }

    public get(key: string): T {
        return this._items[key];
    }

    public containsKey(key: string): boolean {
        return this._items.hasOwnProperty(key);
    }

    public count(): number {
        return this._count;
    }

    public isEmpty(): boolean {
        return this._count === 0;
    }

    public keys(): Keys {
        let Keys: Keys = [];
        // tslint:disable-next-line:forin
        for (let key in this._items) {
            Keys.push(key);
        }
        return Keys;
    }

    public remove(key: string): T {
        let val = this._items[key];
        delete this._items[key];
        this._count--;
        return val;
    }

    public values(): Values<T> {
        let values: Values<T> = [];
        // tslint:disable-next-line:forin
        for (let key in this._items) {
            values.push(this._items[key]);
        }
        return values;
    }

    public clear(): void {
        if (!this.isEmpty()) {
            this._items = {};
        }
    }

}
