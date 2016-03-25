/**
 * Utility methods.
 * Some method are a fork of Underscore: https://github.com/jashkenas/underscore
 * by Jeremy Ashkenas: https://github.com/jashkenas/underscore/blob/master/LICENSE
 *
 *
 */
(function () {

    'use strict';

    // ---------------------------------------------
    //          const
    // ---------------------------------------------

    var root = this;

    var EMPTY_ARRAY = [];
    var EMPTY_FUNC = function () {
    };

    var idCounter = 0;

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

    // ---------------------------------------------
    //          public
    // ---------------------------------------------

    var ly = {};

    // ---------------------------------------------
    //          lang
    // ---------------------------------------------

    ly.identity = function (value) {
        return value;
    };

    ly.has = function (obj, key) {
        return hasOwnProperty.call(obj, key);
    };

    ly.keys = function (obj) {
        if (!ly.isObject(obj)) return [];
        if (nativeKeys) return nativeKeys(obj);
        var keys = [];
        for (var key in obj) if (ly.has(obj, key)) keys.push(key);
        return keys;
    };

    ly.values = function (obj) {
        var keys = ly.keys(obj);
        var length = keys.length;
        var values = new Array(length);
        for (var i = 0; i < length; i++) {
            values[i] = obj[keys[i]];
        }
        return values;
    };

    ly.forEach = _forEach;

    ly.map = function (obj, iterator, context) {
        var results = [];
        if (obj == null) return results;
        ly.forEach(obj, function (value, index, list) {
            results.push(iterator.call(context, value, index, list));
        });
        return results;
    };

    // By default, Underscore uses ERB-style template delimiters, change the
    // following template settings to use alternative delimiters.
    ly.templateSettings = {
        evaluate: /<%([\s\S]+?)%>/g,
        interpolate: /<%=([\s\S]+?)%>/g,
        escape: /<%-([\s\S]+?)%>/g
    };

    // JavaScript micro-templating, similar to John Resig's implementation.
    // Underscore templating handles arbitrary delimiters, preserves whitespace,
    // and correctly escapes quotes within interpolated code.
    ly.template = function (text, data, settings) {
        settings = ly.defaults({}, settings, ly.templateSettings);

        // Combine delimiters into one regular expression via alternation.
        var matcher = new RegExp([
                (settings.escape || noMatch).source,
                (settings.interpolate || noMatch).source,
                (settings.evaluate || noMatch).source
            ].join('|') + '|$', 'g');

        // Compile the template source, escaping string literals appropriately.
        var index = 0;
        var source = "__p+='";
        text.replace(matcher, function (match, escape, interpolate, evaluate, offset) {
            source += text.slice(index, offset).replace(escaper, escapeChar);
            index = offset + match.length;

            if (escape) {
                source += "'+\n((__t=(" + escape + "))==null?'':_.escape(__t))+\n'";
            } else if (interpolate) {
                source += "'+\n((__t=(" + interpolate + "))==null?'':__t)+\n'";
            } else if (evaluate) {
                source += "';\n" + evaluate + "\n__p+='";
            }

            // Adobe VMs need the match returned to produce the correct offest.
            return match;
        });
        source += "';\n";

        // If a variable is not specified, place data values in local scope.
        if (!settings.variable) source = 'with(obj||{}){\n' + source + '}\n';

        source = "var __t,__p='',__j=Array.prototype.join," +
            "print=function(){__p+=__j.call(arguments,'');};\n" +
            source + "return __p;\n";

        try {
            var render = new Function(settings.variable || 'obj', '_', source);
        } catch (e) {
            e.source = source;
            throw e;
        }

        if (data) return render(data, _);
        var template = function (data) {
            return render.call(this, data, _);
        };

        // Provide the compiled source as a convenience for precompilation.
        var argument = settings.variable || 'obj';
        template.source = 'function(' + argument + '){\n' + source + '}';

        return template;
    };

    // ---------------------------------------------
    //          promises
    // ---------------------------------------------

    ly.promiseForEach = _promiseForEach;

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
        return typeof value == 'number' ||
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
    //          to ...
    // ---------------------------------------------

    ly.toType = function (obj) {
        return ({}).toString.call(obj).match(/\s([a-zA-Z]+)/)[1].toLowerCase();
    };

    ly.toArray = function (obj) {
        if (!obj) return [];
        if (ly.isArray(obj)) return slice.call(obj);
        if (obj.length === +obj.length) return ly.map(obj, ly.identity);
        return ly.values(obj);
    };

    ly.toBoolean = function (value) {
        return !!value ? value != 'false' && value != '0' : false;
    };

    ly.toFloat = function (value, def_value, min, max) {
        var result = parseFloat(value.replace(/,/g, '.'));
        max = parseFloat(max);
        min = parseFloat(min);
        def_value = parseFloat(def_value) || 0.0;
        result = ly.isNaN(result) ? def_value : result;
        if (!ly.isNaN(max) && result > max) result = max;
        if (!ly.isNaN(min) && result < min) result = min;
        return result;
    };

    ly.toInt = function (value, def_value, min, max) {
        var result = parseInt(value);
        max = parseInt(max);
        min = parseInt(min);
        def_value = parseInt(def_value) || 0;
        result = ly.isNaN(result) ? def_value : result;
        if (!ly.isNaN(max) && result > max) result = max;
        if (!ly.isNaN(min) && result < min) result = min;
        return result;
    };

    // ---------------------------------------------
    //          random & GUID
    // ---------------------------------------------

    // A (possibly faster) way to get the current timestamp as an integer.
    ly.now = Date.now || function () {
            return new Date().getTime();
        };

    ly.random = function () {
        try {
            var args = ly.toArray(arguments);
            if (args.length === 1) {
                return Math.random() * args[1];
            } else if (args.length === 2) {
                return Math.floor(Math.random() * args[1]) + args[0];
            }
        } catch (err) {
        }
        return Math.random();
    };

    ly.guid = function () {
        return _s4() + _s4() + '-' + _s4() + '-' + _s4() + '-' +
            _s4() + '-' + _s4() + _s4() + _s4();
    };

    ly.s4 = function () {
        return _s4();
    };

    ly.id = function () {
        return _s4() + _s4();
    };

    ly.uniqueId = function (prefix) {
        var id = ++idCounter + '';
        return prefix ? prefix + id : id;
    };

    // ---------------------------------------------
    //          functions
    // ---------------------------------------------

    // Reusable constructor function for prototype setting.
    var ctor = function () {
    };

    // Create a function bound to a given object (assigning `this`, and arguments,
    // optionally). Delegates to **ECMAScript 5**'s native `Function.bind` if
    // available.
    ly.bind = function (func, context) {
        var args, bound;
        if (nativeBind && func.bind === nativeBind) return nativeBind.apply(func, slice.call(arguments, 1));
        if (!ly.isFunction(func)) throw new TypeError('Bind must be called on a function');
        args = slice.call(arguments, 2);
        return bound = function () {
            if (!(this instanceof bound)) return func.apply(context, args.concat(slice.call(arguments)));
            ctor.prototype = func.prototype;
            var self = new ctor;
            ctor.prototype = null;
            var result = func.apply(self, args.concat(slice.call(arguments)));
            if (Object(result) === result) return result;
            return self;
        };
    };

    ly.invoke = function (func, context) {
        var args = slice.call(arguments, 2);
        if (ly.isFunction(func)) {
            if (args.length === 0) {
                return ly.bind(func, context)();
            } else {
                return ly.bind(func, context).apply(this, args);
            }
        }
        return null;
    };

    // Returns a function that will be executed at most one time, no matter how
    // often you call it. Useful for lazy initialization.
    ly.once = function (func) {
        var ran = false, memo;
        return function () {
            if (ran) return memo;
            ran = true;
            memo = func.apply(this, arguments);
            func = null;
            return memo;
        };
    };

    // Returns a function, that, as long as it continues to be invoked, will not
    // be triggered. The function will be called after it stops being called for
    // N milliseconds. If `immediate` is passed, trigger the function on the
    // leading edge, instead of the trailing.
    ly.debounce = function (func, wait, immediate) {
        var timeout, args, context, timestamp, result;

        var later = function () {
            var last = ly.now() - timestamp;

            if (last < wait && last > 0) {
                timeout = setTimeout(later, wait - last);
            } else {
                timeout = null;
                if (!immediate) {
                    result = func.apply(context, args);
                    context = args = null;
                }
            }
        };

        return function () {
            context = this;
            args = arguments;
            timestamp = ly.now();
            var callNow = immediate && !timeout;
            if (!timeout) {
                timeout = setTimeout(later, wait);
            }
            if (callNow) {
                result = func.apply(context, args);
                context = args = null;
            }

            return result;
        };
    };

    // Delays a function for the given number of milliseconds, and then calls
    // it with the arguments supplied.
    ly.delay = function (func, wait) {
        var args = slice.call(arguments, 2);
        return setTimeout(function () {
            return func.apply(null, args);
        }, wait);
    };


    // ---------------------------------------------
    //          objects
    // ---------------------------------------------

    /**
     * Extend a given object with all the properties in passed-in object(s).
     * @param obj
     * @return {*}
     */
    ly.extend = function (obj) {
        if (!ly.isObject(obj)) return obj;
        ly.forEach(slice.call(arguments, 1), function (source) {
            for (var prop in source) {
                obj[prop] = source[prop];
            }
        });
        return obj;
    };

    /**
     * Fill in a given object with default properties.
     * @param obj
     * @return {*}
     */
    ly.defaults = function (obj) {
        if (!ly.isObject(obj)) return obj;
        ly.forEach(slice.call(arguments, 1), function (source) {
            for (var prop in source) {
                if (obj[prop] === void 0) obj[prop] = source[prop];
            }
        });
        return obj;
    };

    /**
     * Create a (shallow-cloned) duplicate of an object
     */
    ly.clone = function (obj) {
        if (!ly.isObject(obj)) return obj;
        return ly.isArray(obj) ? obj.slice() : ly.extend({}, obj);
    };

    // ---------------------------------------------
    //          utils
    // ---------------------------------------------

    ly.replaceAll = function (find, replace, str) {
        return _replaceAll(find, replace, str);
    };

    // ---------------------------------------------
    //          private
    // ---------------------------------------------

    function _validateEmail(email) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    }

    function _s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    function _escapeRegExp(string) {
        return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
    }

    function _replaceAll(find, replace, str) {
        return str.replace(new RegExp(_escapeRegExp(find), 'g'), replace);
    }

    function _length(obj) {
        var result = 0;
        if (!!obj) {
            if (obj.length === +obj.length) {
                result = obj.length;
            } else {
                var keys = ly.keys(obj);
                result = keys.length;
            }
        }
        return result;
    }

    function _forEach(obj, iterator, context) {
        if (obj == null) return obj;
        if (obj.length === +obj.length) {
            for (var i = 0, length = obj.length; i < length; i++) {
                if (iterator.call(context, obj[i], i, obj) === breaker) return;
            }
        } else {
            var keys = ly.keys(obj);
            for (var i = 0, length = keys.length; i < length; i++) {
                if (iterator.call(context, obj[keys[i]], keys[i], obj) === breaker) return;
            }
        }
        return obj;
    }

    function _promiseForEach(obj, iterator, context) {
        var done_callback = function () {
        };
        var len = _length(obj);
        var counter = len;
        var response_fulfilled = [];
        var response_rejected = [];

        function async_iterator(func, value, index, list) {
            ly.invoke(func, context, value, index, list, {
                fulfill: function (val) {
                    counter--;
                    response_fulfilled.push(val);
                    if (counter == 0) {
                        // time to exit
                        ly.invoke(done_callback, context, response_fulfilled, response_rejected);
                    }
                },
                reject: function (val) {
                    counter--;
                    response_rejected.push(val);
                    if (counter == 0) {
                        // time to exit
                        ly.invoke(done_callback, context, response_fulfilled, response_rejected);
                    }
                }
            });
        }

        return {
            done: function (callback) {
                if (len > 0) {
                    done_callback = callback;
                    _forEach(obj, function (value, index, list) {
                        setTimeout(ly.bind(async_iterator, context, iterator, value, index, list), 50);
                        //ly.invoke(async_iterator, context, iterator, value, index, list);
                    });
                } else {
                    ly.invoke(callback, context, [], []);
                }
            }
        }
    }

    /**
     _promiseForEach(['a', 'b'], function (value, index, list, promise) {
        if (value == 'a') {
            promise.fulfill(value);
        } else {
            promise.reject(value);
        }
    }).done(function (fulfilled, rejected) {
        console.log(fulfilled);
        console.log(rejected);
    });
     **/

    // ---------------------------------------------
    //          exports
    // ---------------------------------------------

    // Node.js
    if (typeof module !== 'undefined' && module.exports) {
        module.exports = ly;
    }
    // AMD / RequireJS
    else if (typeof define !== 'undefined' && define.amd) {
        define([], function () {
            return ly;
        });
    }
    // included directly via <script> tag
    else {
        root.ly = ly;
    }

}());
