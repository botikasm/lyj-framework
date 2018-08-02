/**
 * functions.js
 * ---------------
 * Sample functions
 */
module.exports = (function () {

    // ------------------------------------------------------------------------
    //              i m p o r t s
    // ------------------------------------------------------------------------

    var _CONST = require('/scripts/constants');

    // ------------------------------------------------------------------------
    //              c o n s t
    // ------------------------------------------------------------------------

    var FILE = 'session.js';// used only for logs

    // ------------------------------------------------------------------------
    //              f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //              i n s t a n c e
    // ------------------------------------------------------------------------

    var instance = {};

    instance.id = function () {
        try {
            return $session.id;
        } catch (err) {
            console.error(FILE + '#id', err);
            return err;
        }
    };

    instance.elapsed = function () {
        try {
            return $session.elapsed;
        } catch (err) {
            console.error(FILE + '#elapsed', err);
            return err;
        }
    };

    instance.put = function () {
        try {
            var item = {
                field_1:1,
                field_2:"hello"
            };

            $session.set('my_item', item);

            return $session.get('my_item');
        } catch (err) {
            console.error(FILE + '#put', err);
            return err;
        }
    };

    instance.keys = function () {
        try {
            return $session.keys();
        } catch (err) {
            console.error(FILE + '#keys', err);
            return err;
        }
    };

    /**
     * Run all internal tests
     */
    instance.all = function () {
        try {


            return true;
        } catch (err) {
            console.error(FILE + '#session', err);
            return err;
        }
    };


    // ------------------------------------------------------------------------
    //              p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //              e x p o r t s
    // ------------------------------------------------------------------------


    return instance;


})();