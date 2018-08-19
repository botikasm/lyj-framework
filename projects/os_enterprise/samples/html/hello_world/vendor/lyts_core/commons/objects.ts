import lang from "./lang";

export default class objects {

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Recursively goes through an object trying to resolve a path.
     * <code>
     *      console.log("name.value", ly.lang.get({"name":{"value":1}}, "name.value"));
     *      console.log("[name,value]", ly.lang.get({"name":{"value":1}}, ["name","value"]));
     *      console.log("length", ly.lang.get([1,2], ["length"]));
     * </code>
     * @param {Object} scope - The object to traverse (in each recursive call we dig into this object)
     * @param {string[]} path - An array of property names to traverse one-by-one
     * @param {number} [pathIndex=0] - The current index in the path array
     */
    static get(scope: any, path: any, pathIndex: number = 0): any {
        if (typeof scope !== 'object' || scope === null || scope === undefined) {
            return '';
        }

        path = lang.isArray(path) ? path : path.split('.');

        const varName: string = path[pathIndex];
        const value: any = scope[varName];

        if (pathIndex === path.length - 1) {
            // It's a leaf, return whatever it is
            return value;
        }

        return objects.get(value, path, ++pathIndex);
    }

    static clone<T>(obj: T): T {
        let target = <T>{};
        for (const field in obj) {
            if (obj.hasOwnProperty(field)) {
                target[field] = obj[field];
            }
        }
        return target;
    }

    static isEmpty(value: any): boolean {
        if (!!value) {
            if (value.hasOwnProperty("length")) {
                return value.length === 0;
            } else {
                for (let key in value) {
                    if (value.hasOwnProperty(key)) {
                        return false; // not empty
                    }
                }
            }
        }
        return true;
    }

    static keys(value: any): string[] {
        const result: string[] = [];
        for (let key in value) {
            if (value.hasOwnProperty(key)) {
                result.push(key);
            }
        }
        return result;
    }

    static values(value: any): any[] {
        const result: any[] = [];
        for (let key in value) {
            if (value.hasOwnProperty(key)) {
                result.push(value[key]);
            }
        }
        return result;
    }

}