import lang from "./lang";

export default class strings {

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Replace all occurrences of 'find' parameter with 'replace' parameter in a string 'str'.
     * @param {string[] | string} find Parameter to find
     * @param {string} replace Replace value
     * @param {string} str Source string
     * @return {string} String with replaced values
     */
    static replaceAll(find: string[] | string, replace: string, str: string): string {
        const rep_array: string[] = [];
        if (lang.isString(find)) {
            rep_array.push(find as string);
        } else {
            rep_array.push(...(find as Array<string>));
        }

        let result = str;
        for (let i = 0; i < rep_array.length; i++) {
            result = strings._replaceAll(rep_array[i], replace, result);
        }
        return result;
    }

    static endWith(str: string, suffix: string): boolean {
        if (str === null || suffix === null)
            return false;
        return str.indexOf(suffix, str.length - suffix.length) !== -1;
    }

    static startWith(str: string, prefix: string): boolean {
        if (str === null || prefix === null)
            return false;
        return str.indexOf(prefix) === 0;
    }

    public static fillLeft(value: string, fill: string, size: number): string {
        while (value.length < size) {
            value = fill + value;
        }
        return value;
    }

    public static fillRight(value: string, fill: string, size: number): string {
        while (value.length < size) {
            value = value + fill;
        }
        return value;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static _escapeRegExp(value: string): string {
        return value.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
    }

    private static _replaceAll(find: string, replace: string, str: string): string {
        return str.replace(new RegExp(strings._escapeRegExp(find), 'g'), replace);
    }
}