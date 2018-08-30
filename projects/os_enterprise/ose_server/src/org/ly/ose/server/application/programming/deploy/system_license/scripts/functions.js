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

    var FILE = 'functions.js';// used only for logs

    // ------------------------------------------------------------------------
    //              f i e l d s
    // ------------------------------------------------------------------------

    var _counter = 0;

    // ------------------------------------------------------------------------
    //              i n s t a n c e
    // ------------------------------------------------------------------------

    var instance = {};

    /**
     * Return script version
     * @return {*}
     */
    instance.version = function () {
        try {
            return version();
        } catch (err) {
            console.error(FILE + '#version', err);
            return err;
        }
    };

    instance.init = function () {
        // INITIALIZE EMAIL FOR ADMINS
        $license.smtp = require('/config/smtp.json');
        $license.trialDays = 30;
    };

    instance.register = function (license_uid, email, name) {
        var license = $license.createLicense(license_uid, email, name);
        if (!!license) {
            return license.data;
        }
        return false;
    };

    /**
     * Return a License, if already stored into internal registry.
     * @param license_uid  UID of a license
     * @return License object or False if license does not exists.
     */
    instance.get = function (license_uid) {
        var license = $license.getLicense(license_uid);
        if (!!license) {
            return license.data;
        }
        return false;
    };

    /**
     * Return true if a License exists.
     * @param license_uid  UID of a license
     * @return True if exists.
     */
    instance.has = function (license_uid) {
        return $license.hasLicense(license_uid);
    };

    instance.expired = function (license_uid) {
        var license = $license.getLicense(license_uid);
        if (!!license) {
            return license.expired;
        }
        return true;
    };


    /**
     * Add (or subtract if days is a negative number) days from licence time.
     * @param license_uid UID of a license
     * @param days Positive or negative number
     * @return License object or False if license does not exists.
     */
    instance.postpone = function (license_uid, days) {
        var license = $license.getLicense(license_uid);
        days = $convert.toInt(days);
        if (!!license) {
            license.postpone(days);
            return license.data;
        }
        return false;
    };

    this.setAttribute = function (license_uid, attr_name, attr_value) {
        var license = $license.getLicense(license_uid);
        if (!!license) {
            license.attribute(attr_name, attr_value);
            return license.attribute(attr_name);
        }
        return false;
    };

    this.getAttribute = function (license_uid, attr_name) {
        var license = $license.getLicense(license_uid);
        if (!!license) {
            return license.attribute(attr_name);
        }
        return false;
    };

    // ------------------------------------------------------------------------
    //              p r i v a t e
    // ------------------------------------------------------------------------

    function version() {
        return _CONST.version;
    }


    // ------------------------------------------------------------------------
    //              e x p o r t s
    // ------------------------------------------------------------------------

    return instance;


})();