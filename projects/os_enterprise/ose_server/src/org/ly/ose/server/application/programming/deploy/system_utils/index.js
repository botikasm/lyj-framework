/**
 * Program launcher
 */
ly.new(function () {

    // ------------------------------------------------------------------------
    //              i m p o r t s
    // ------------------------------------------------------------------------

    var _FUNCTIONS = require('/scripts/functions');

    // ------------------------------------------------------------------------
    //              c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //              f i e l d s
    // ------------------------------------------------------------------------

    var _count = 0;

    // ------------------------------------------------------------------------
    //                      onInit (run once when program is initialized)
    // ------------------------------------------------------------------------

    this._init = function (program) {
        try {
            //console.log("_init: ", "PROGRAM: " + program);

            // no return expected
            return {
                'version': _FUNCTIONS.version()
            };
        } catch (err) {
            return {
                error: err
            };
        }
    };

    this._expire = function (program) {
        //console.log("_expire: ", "PROGRAM: " + program);
    };

    this._loop = function (program) {
        _count++;
        //console.log("_loop: ", "COUNT", _count, "PROGRAM: " + program);
    };

    // ------------------------------------------------------------------------
    //                 exposed functions
    // ------------------------------------------------------------------------

    this.version = function () {
        return _FUNCTIONS.version();
    };

    this.echo = function (value) {
        return _FUNCTIONS.echo(value);
    };

    this.count = function () {
        return _FUNCTIONS.count();
    };

});