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
    //                      onInit (run once when program is initialized)
    // ------------------------------------------------------------------------

    this.init = function (request) {
        try {
            console.log("onInit: ", "REQUEST: " + request);

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

    // ------------------------------------------------------------------------
    //                 exposed functions
    // ------------------------------------------------------------------------

    this.version = function(){
        return _FUNCTIONS.version();
    };

    this.echo = function(value){
        return _FUNCTIONS.echo(value);
    };


});