/**
 * Program launcher
 */
ly.new(function () {

    // ------------------------------------------------------------------------
    //              i m p o r t s
    // ------------------------------------------------------------------------

    var _FUNCTIONS = require('/scripts/functions');
    var _DATABASE = require('/scripts/database');
    var _SESSION = require('/scripts/session');
    var _HTTP = require('/scripts/http');
    var _OSE = require('/scripts/ose');
    var _TEMPLATES = require('/scripts/templates');

    var _OBJECT = require('/scripts/object.json');
    var _ARRAY = require('/scripts/array.json');

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
            console.debug("_init: ", "PROGRAM: " + program);

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
        console.debug("_expire: ", "PROGRAM: " + program);
    };

    this._loop = function (program) {
        _count++;
        console.debug("_loop: ", "COUNT", _count, "PROGRAM: " + program);
    };

    // ------------------------------------------------------------------------
    //                 exposed functions
    // ------------------------------------------------------------------------

    this.database = function () {
        return _DATABASE;
    };

    this.session = function () {
        return _SESSION;
    };

    this.http = function () {
        return _HTTP;
    };

    this.ose = function () {
        return _OSE;
    };

    this.templates = function () {
        return _TEMPLATES;
    };

    // ------------------------------------------------------------------------
    //                 direct access
    // ------------------------------------------------------------------------

    this.version = function () {
        return _FUNCTIONS.version();
    };

    this.recursive = function () {
        return _FUNCTIONS.version();
    };

    this.database_findEqualAsc = function () {
        return _DATABASE.findEqualAsc();
    };

    this.object_name = function () {
        return _OBJECT.name;
    };

    this.array_len = function () {
        return _ARRAY.length;
    };

    this.i18n = function (key) {
        var response = {};

        response.dic_phrase_base = $i18n.get('hello');
        response.dic_phrase_it = $i18n.get('it', 'hello');

        response.content_base = $i18n.load('/i18n').content();
        response.content_it = $i18n.load('/i18n').content('it');

        response.data_base = $i18n.load('/i18n/data').content();
        response.data_it = $i18n.load('/i18n/data').content('it');
        response.data_len_it = $i18n.load('/i18n/data').content('it').length;
        response.data_len_base = $i18n.load('/i18n/data').content().length;

        return response;
    };

    this.resource = function () {
        var response = {};

        response.data_base_json = $resource.load('/i18n/data/base.json').string;

        return response;
    };

});