/**
 * Extends javascript engine.
 */

(function () {

    'use strict';

    var EMPTY_ARRAY = [];
    var EMPTY_FUNC = function () {
    };

    // Establish the object that gets returned to break out of a loop iteration.
    var breaker = {};

    var ArrayProto = Array.prototype, ObjProto = Object.prototype, FuncProto = Function.prototype;

    var
        push = ArrayProto.push,
        slice = ArrayProto.slice,
        concat = ArrayProto.concat,
        toString = ObjProto.toString,
        hasOwnProperty = ObjProto.hasOwnProperty;

    var
        nativeIsArray = Array.isArray,
        nativeKeys = Object.keys,
        nativeBind = FuncProto.bind;

    var ly = {};


    function _validateEmail(email) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    }

    ly.extension = function extension(filename) {
        return filename.indexOf(".") > -1 ? filename.split('.').pop() : "";
    };

    ly.parseJSON = function parseJSON(text) {
        try {
            return JSON.parse(text);
        } catch (err) {
            console.error(err);
            return false;
        }
    };

    // ---------------------------------------------
    //          is ...
    // ---------------------------------------------

    ly.isObject = function (obj) {
        return obj === Object(obj);
    };

    ly.isFunction = function (value) {
        return typeof value == 'function';
    };

    ly.isArray = nativeIsArray || function (value) {
        return value && typeof value == 'object' && typeof value.length == 'number' &&
            toString.call(value) == '[object Array]' || false;
    };

    ly.isArguments = function (value) {
        return value && typeof value == 'object' && typeof value.length == 'number' &&
            toString.call(value) == '[object Arguments]' || false;
    };

    ly.isBoolean = function (value) {
        return value === true || value === false ||
            value && typeof value == 'object' && toString.call(value) == '[object Boolean]' || false;
    };

    ly.isString = function (value) {
        return typeof value == 'string' ||
            value && typeof value == 'object' && toString.call(value) == '[object String]' || false;
    };

    ly.isNumber = function (value) {
        return typeof value === 'number' ||
            value && typeof value == 'object' && toString.call(value) == '[object Number]' || false;
    };

    // Is the given value `NaN`? (NaN is the only number which does not equal itself).
    ly.isNaN = function (obj) {
        return ly.isNumber(obj) && obj != +obj;
    };

    ly.isDate = function (value) {
        return value && typeof value == 'object' && toString.call(value) == '[object Date]' || false;
    };

    ly.isUndefined = function (value) {
        return typeof value == 'undefined';
    };

    ly.isRegExp = function (value) {
        return value && typeof value == 'object' && toString.call(value) == '[object RegExp]' || false;
    };

    ly.isEmail = function (value) {
        return ly.isString(value) && _validateEmail(value);
    };

    ly.isHtml = function (text) {
        if (ly.isString(text)) {
            text = text.trim();
            return ( text.charAt(0) === "<" && text.charAt(text.length - 1) === ">" && text.length >= 3 );
        }
        return false;
    };

    // ---------------------------------------------
    //          object
    // ---------------------------------------------

    ly.new = function (aclass) {
        if (ly.isFunction(aclass)) {
            return new aclass();
        } else {
            return aclass;
        }
    };

    ly.object = function (obj) {
        if (!!obj) {
            try {
                if (ly.isString(obj)) {
                    return ly.parseJSON(obj);
                } else if (!!obj.toString) {
                    return ly.parseJSON(obj.toString());
                } else {
                    return obj;
                }
            } catch (err) {
                // conversion error
                console.error(err);
            }
        }
        return false;
    };

    ly.keys = function (obj) {
        var result = [];
        obj = ly.object(obj);
        if (!!obj) {
            for (var prop in obj) {
                if (!obj.hasOwnProperty(prop)) continue;
                result.push(prop);
            }
        }
        return result;
    };

    ly.values = function (obj) {
        if (ly.isArray(obj)) {
            return obj;
        }
        var result = [];
        obj = ly.object(obj);
        if (!!obj) {
            for (var prop in obj) {
                if (!obj.hasOwnProperty(prop)) continue;
                result.push(obj[prop]);
            }
        }
        return result;
    };

    // ------------------------------------
    //  require('<PATH>');
    // ------------------------------------
    __engine__.addAttribute("require", function (path) {
        if (!!requirer) {
            var text = requirer.require(path);
            if (!!text) {
                var ext = ly.extension(path) || "js";
                if (ext === 'js') {
                    var module = {};
                    var exports = false;
                    module.exports = exports;
                    try {
                        var jsobj = eval(text); // test if is a valid javascript code
                        //CommonJs compliant
                        if (!!exports) {
                            return exports;
                        } else {
                            return jsobj;
                        }
                    } catch (err) {
                        console.error(err);
                        return !!err.message ? err.message : text;
                    }
                } else {
                    var json = ly.parseJSON(text);
                    return !!json ? json : text;
                }
            } else {
                return undefined;
            }
        } else {
            console.error("Missing Tool: 'requirer'");
        }
    });


    // ------------------------------------
    //  add ly object with methods extensions
    // ------------------------------------
    __engine__.addAttribute("ly", ly);


})();