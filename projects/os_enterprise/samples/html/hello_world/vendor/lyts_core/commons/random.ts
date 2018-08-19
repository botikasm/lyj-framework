export default class random {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    static _id_counter: number = 0;

    // ------------------------------------------------------------------------
    //                      random and GUID
    // ------------------------------------------------------------------------

    // A (possibly faster) way to get the current timestamp as an integer.
    static now(): number {
        return !!Date.now ? Date.now() : new Date().getTime();
    }

    static rnd(arg1: number, arg2?: number): number {
        try {
            if (null === arg2) {
                return Math.random() * arg1;
            } else {
                return Math.floor(Math.random() * (arg2 || arg1)) + arg1;
            }
        } catch (err) {
        }
        return Math.random();
    }

    static guid(): string {
        return random._s4() + random._s4() + '-' + random._s4() + '-' + random._s4() + '-' +
            random._s4() + '-' + random._s4() + random._s4() + random._s4();
    }

    static s4(): string {
        return random._s4();
    }

    static id(): string {
        return random._s4() + random._s4();
    }

    static uniqueId(prefix: string): string {
        let id = ++random._id_counter + '';
        return prefix ? prefix + id : id;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static _s4(): string {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }
}