export default class paths {

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    static concat(...args: string[]): string {
        const separator = '/';
        const replace   = new RegExp(separator+'{1,}', 'g');
        return args.join(separator).replace(replace, separator);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}