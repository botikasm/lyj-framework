import lang from "./lang";
import strings from "./strings";
import objects from "./objects";
import DateTimeFormatOptions = Intl.DateTimeFormatOptions;


/**
 * Utility class
 */
export default class format {


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static template(template: string, model: any = {}): string {

        const VAR_MATCH_REGEX = /\{\{\s*(.*?)\s*\}\}/g;

        // don't touch the template if it is not a string
        if (typeof template !== 'string') {
            return template;
        }

        return template.replace(VAR_MATCH_REGEX, function (match, varName) {
            try {
                // defaultResolver never throws
                return lang.toString(objects.get(model, varName));
            } catch (e) {
                // if your resolver throws, we proceed with the default resolver
                return '';
            }
        });
    }

    public static date(date: Date,
                       locales?: string[] | string,
                       options?: DateTimeFormatOptions): string {
        try {
            if (!!locales) {
                if (lang.isArray(locales) || locales.length < 6) {
                    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/toLocaleString
                    return date.toLocaleString(locales, options);
                } else if (lang.isString(locales)) {
                    // uses pattern
                    let nm = format.getMonthName(date);
                    let nd = format.getDayName(date);
                    let f: string = locales as string;
                    f = f.replace(/yyyy/g, date.getFullYear() + "");
                    f = f.replace(/yy/g, String(date.getFullYear()).substr(2, 2));
                    f = f.replace(/MMM/g, nm.substr(0, 3).toUpperCase());
                    f = f.replace(/Mmm/g, nm.substr(0, 3));
                    f = f.replace(/MM\*/g, nm.toUpperCase());
                    f = f.replace(/Mm\*/g, nm);
                    f = f.replace(/mm/g, strings.fillLeft(String(date.getMonth() + 1), '0', 2));
                    f = f.replace(/DDD/g, nd.substr(0, 3).toUpperCase());
                    f = f.replace(/Ddd/g, nd.substr(0, 3));
                    f = f.replace(/DD\*/g, nd.toUpperCase());
                    f = f.replace(/Dd\*/g, nd);
                    f = f.replace(/dd/g, strings.fillLeft(String(date.getDate()), '0', 2));
                    f = f.replace(/d\*/g, date.getDate() + "");
                    return f;
                }
            }
            return date.toLocaleString([], options);
        } catch (err) {
        }
        return '';
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static getMonthName(date: Date): string {
        return date.toLocaleString().replace(/[^a-z]/gi, '');
    }

    private static getDayName(date: Date): string {
        switch (date.getDay()) {
            case 0:
                return 'Sunday';
            case 1:
                return 'Monday';
            case 2:
                return 'Tuesday';
            case 3:
                return 'Wednesday';
            case 4:
                return 'Thursday';
            case 5:
                return 'Friday';
            case 6:
                return 'Saturday';
        }
        return '';
    }

}