/**
 * templates.js
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

    var FILE = 'templates.js';// used only for logs

    // ------------------------------------------------------------------------
    //              f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //              i n s t a n c e
    // ------------------------------------------------------------------------

    var instance = {};

    /**
     * https://localhost:4000/api/program/invoke/iuhdiu87w23ruh897dfyc2w3r/it/session_12234/system.license/templates.resource/array:email_admin,html
     */
    instance.resource = function (name, extension) {
        try {
            console.info(FILE + '#resource', name, extension);
            return get(name, extension); // get localized resource
        } catch (err) {
            console.error(FILE + '#resource', err);
            return err;
        }
    };

    /**
     * https://localhost:4000/api/program/invoke/iuhdiu87w23ruh897dfyc2w3r/it/session_12234/system.license/templates.email_admin/null
     */
    instance.email_admin = function () {
        try {
            var email_admin = get('email_admin'); // get localized resource
            return email_admin;
        } catch (err) {
            console.error(FILE + '#email_admin', err);
            return err;
        }
    };


    // ------------------------------------------------------------------------
    //              p r i v a t e
    // ------------------------------------------------------------------------

    function get(name, extension) {
        try {
            extension = extension || '.html';
            console.info(FILE + '#get', name, extension);
            return $i18n.load('/html/' + name, extension).content();
        } catch (err) {
            console.error(FILE + '#get("' + name + '")', err);
            return err;
        }
    }

    // ------------------------------------------------------------------------
    //              e x p o r t s
    // ------------------------------------------------------------------------

    return instance;


})();