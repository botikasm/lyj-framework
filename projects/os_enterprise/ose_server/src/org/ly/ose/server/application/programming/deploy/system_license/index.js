/**
 * Program launcher
 */
ly.new(function () {

    // ------------------------------------------------------------------------
    //              i m p o r t s
    // ------------------------------------------------------------------------

    var _FUNCTIONS = require('/scripts/functions');
    var _TEMPLATES = require('/scripts/templates');

    // ------------------------------------------------------------------------
    //              c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //              f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      onInit (run once when program is initialized)
    // ------------------------------------------------------------------------

    this._init = function (program) {
        try {

            _FUNCTIONS.init();

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
        // _count++;
        //console.log("_loop: ", "COUNT", _count, "PROGRAM: " + program);
    };

    // ------------------------------------------------------------------------
    //                 exposed functions
    // ------------------------------------------------------------------------

    this.templates = function () {
        return _TEMPLATES;
    };

    // ------------------------------------------------------------------------
    //                 direct access
    // ------------------------------------------------------------------------

    this.version = function () {
        return _FUNCTIONS.version();
    };

    this.register = function (license_uid, email, name) {
        return _FUNCTIONS.register(license_uid, email, name);
    };

    /**
     * Return a License, if already stored into internal registry.
     * @param license_uid  UID of a license
     * @return License object or False if license does not exists.
     */
    this.get = function (license_uid) {
        return _FUNCTIONS.get(license_uid);
    };

    /**
     * Return true if a License exists.
     * @param license_uid  UID of a license
     * @return True if exists.
     */
    this.has = function (license_uid) {
        return _FUNCTIONS.has(license_uid);
    };

    this.expired = function (license_uid) {
        return _FUNCTIONS.expired(license_uid);
    };

    /**
     * Add (or subtract if days is a negative number) days from licence time.
     * @param license_uid UID of a license
     * @param days Positive or negative number
     * @return License object or False if license does not exists.
     */
    this.postpone = function (license_uid, days) {
        return _FUNCTIONS.postpone(license_uid, days);
    };

    this.setAttribute = function (license_uid, attr_name, attr_value) {
        return _FUNCTIONS.setAttribute(license_uid, attr_name, attr_value);
    };

    this.getAttribute = function (license_uid, attr_name) {
        return _FUNCTIONS.getAttribute(license_uid, attr_name);
    };

});