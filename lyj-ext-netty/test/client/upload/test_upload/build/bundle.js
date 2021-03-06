/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 18);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__commons_lang__ = __webpack_require__(1);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__commons_format__ = __webpack_require__(19);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__commons_strings__ = __webpack_require__(11);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__commons_objects__ = __webpack_require__(14);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__commons_random__ = __webpack_require__(4);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__view_browser__ = __webpack_require__(6);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__view_cookies__ = __webpack_require__(20);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7__view_dom__ = __webpack_require__(7);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8__view_i18n__ = __webpack_require__(12);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9__commons_collections_Dictionary__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10__commons_events_Events__ = __webpack_require__(8);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11__commons_events_EventEmitter__ = __webpack_require__(5);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_12__net_HttpClient__ = __webpack_require__(21);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_13__view_components_Component__ = __webpack_require__(9);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_14__application_Application__ = __webpack_require__(23);
//-- static --//









//-- classes --//




//-- views --//

//-- singleton --//

var root = window;
// ------------------------------------------------------------------------
//                      l y
// ------------------------------------------------------------------------
var ly = {
    window: root,
    lang: __WEBPACK_IMPORTED_MODULE_0__commons_lang__["a" /* default */],
    format: __WEBPACK_IMPORTED_MODULE_1__commons_format__["a" /* default */],
    strings: __WEBPACK_IMPORTED_MODULE_2__commons_strings__["a" /* default */],
    objects: __WEBPACK_IMPORTED_MODULE_3__commons_objects__["a" /* default */],
    random: __WEBPACK_IMPORTED_MODULE_4__commons_random__["a" /* default */],
    browser: __WEBPACK_IMPORTED_MODULE_5__view_browser__["a" /* default */],
    cookies: __WEBPACK_IMPORTED_MODULE_6__view_cookies__["a" /* default */],
    dom: __WEBPACK_IMPORTED_MODULE_7__view_dom__["a" /* default */],
    i18n: __WEBPACK_IMPORTED_MODULE_8__view_i18n__["a" /* default */],
    Events: __WEBPACK_IMPORTED_MODULE_10__commons_events_Events__["a" /* default */],
    EventEmitter: __WEBPACK_IMPORTED_MODULE_11__commons_events_EventEmitter__["a" /* default */],
    Dictionary: __WEBPACK_IMPORTED_MODULE_9__commons_collections_Dictionary__["a" /* Dictionary */],
    HttpClient: __WEBPACK_IMPORTED_MODULE_12__net_HttpClient__["a" /* HttpClient */],
    //-- v i e w --//
    Component: __WEBPACK_IMPORTED_MODULE_13__view_components_Component__["a" /* default */],
    //-- s i n g l e t o n --//
    Application: __WEBPACK_IMPORTED_MODULE_14__application_Application__["a" /* default */]
};
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (ly);


/***/ }),
/* 1 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__random__ = __webpack_require__(4);
/**
 * Utility class
 */

var langClass = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function langClass() {
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    langClass.prototype.parse = function (value) {
        try {
            if (this.isString(value)) {
                return JSON.parse(value);
            }
        }
        catch (err) {
        }
        return value;
    };
    // ------------------------------------------------------------------------
    //                      t o
    // ------------------------------------------------------------------------
    langClass.prototype.toString = function (value) {
        switch (typeof value) {
            case 'string':
            case 'number':
            case 'boolean':
                return value + '';
            case 'object':
                try {
                    // null is an object but is falsy. Swallow it.
                    return value === null ? '' : JSON.stringify(value);
                }
                catch (jsonError) {
                    return '{...}';
                }
            default:
                // Anything else will be replaced with an empty string
                // For example: undefined, Symbol, etc.
                return '';
        }
    };
    langClass.prototype.toArray = function (value) {
        return !!value
            ? this.isArray(value) ? value : [value]
            : [];
    };
    langClass.prototype.toBoolean = function (value, def_val) {
        return !!value
            ? value !== 'false' && value !== '0'
            : def_val;
    };
    langClass.prototype.toFloat = function (value, def_value, min, max) {
        if (def_value === void 0) { def_value = 0.0; }
        try {
            var result = parseFloat(value.replace(/,/g, '.'));
            result = this.isNaN(result) ? def_value : result;
            if (!this.isNaN(max) && result > (max || 0))
                result = max || 0;
            if (!this.isNaN(min) && result < (min || 0))
                result = min || 0;
            return result;
        }
        catch (err) {
            return def_value;
        }
    };
    langClass.prototype.toInt = function (value, def_value, min, max) {
        if (def_value === void 0) { def_value = 0; }
        try {
            var result = parseInt(value);
            result = this.isNaN(result) ? def_value : result;
            if (!this.isNaN(max) && result > (max || 0))
                result = max || 0;
            if (!this.isNaN(min) && result < (min || 0))
                result = min || 0;
            return result;
        }
        catch (err) {
            return def_value;
        }
    };
    // ------------------------------------------------------------------------
    //                      i s
    // ------------------------------------------------------------------------
    langClass.prototype.isFunction = function (value) {
        return typeof value == 'function';
    };
    langClass.prototype.isObject = function (value) {
        return value === Object(value);
    };
    langClass.prototype.isArray = function (value) {
        return !!Array.isArray
            ? Array.isArray(value)
            : value && typeof value == 'object' && typeof value.length == 'number' && toString.call(value) == '[object Array]' || false;
    };
    langClass.prototype.isArguments = function (value) {
        return value && typeof value == 'object' && typeof value.length == 'number' &&
            toString.call(value) == '[object Arguments]' || false;
    };
    langClass.prototype.isBoolean = function (value) {
        return value === true || value === false ||
            value && typeof value == 'object' && toString.call(value) == '[object Boolean]' || false;
    };
    langClass.prototype.isString = function (value) {
        return typeof value == 'string' ||
            value && typeof value == 'object' && toString.call(value) == '[object String]' || false;
    };
    langClass.prototype.isNumber = function (value) {
        return typeof value == 'number' ||
            value && typeof value == 'object' && toString.call(value) == '[object Number]' || false;
    };
    langClass.prototype.isNaN = function (value) {
        return isNaN(value);
    };
    langClass.isDate = function (value) {
        return value && typeof value == 'object' && toString.call(value) == '[object Date]' || false;
    };
    langClass.prototype.isUndefined = function (value) {
        return typeof value == 'undefined';
    };
    langClass.prototype.isRegExp = function (value) {
        return value && typeof value == 'object' && toString.call(value) == '[object RegExp]' || false;
    };
    langClass.prototype.isEmail = function (value) {
        return this.isString(value) && this._validateEmail(value);
    };
    langClass.prototype.isConstructor = function (f) {
        try {
            return !!f.prototype && !!f.prototype.constructor.name;
        }
        catch (err) {
            return false;
        }
    };
    langClass.prototype.className = function (item) {
        try {
            if (!!item) {
                if (!!item.prototype && !!item.prototype.constructor) {
                    return item.prototype.constructor.name;
                }
                else if (!!item.constructor) {
                    return item.constructor.name;
                }
            }
        }
        catch (err) {
        }
        return '';
    };
    langClass.prototype.funcName = function (func) {
        var response = '';
        try {
            if (!!func) {
                if (!!func.name) {
                    response = func.name;
                }
                else if (!!func.prototype && !!func.prototype.name) {
                    response = func.prototype.name;
                }
            }
        }
        catch (err) {
        }
        return response;
    };
    // ------------------------------------------------------------------------
    //                      u t i l s
    // ------------------------------------------------------------------------
    /**
     * Evaluate a script or an object
     * @param text
     * @return {*}
     */
    langClass.prototype.evalScript = function (text) {
        if (!!text && !!eval) {
            return eval.call(this, text);
        }
        return {};
    };
    langClass.prototype.noCacheLink = function (url) {
        if (url.indexOf("?") === -1)
            url += "?no_cache=" + new Date().getTime();
        else
            url += "&no_cache=" + new Date().getTime();
        return url;
    };
    /**
     * Invoke a function. Shortcut for "func.call(this, ...args)"
     */
    langClass.prototype.funcInvoke = function (func) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        var self = this;
        if (self.isFunction(func)) {
            if (args.length === 0) {
                return func.call(self);
            }
            else {
                return func.call.apply(func, [self].concat(args));
            }
        }
        return null;
    };
    /**
     * Delays a function for the given number of milliseconds, and then calls
     * it with the arguments supplied.
     * NOTE: user "clearTimeout" with funcDelay response to
     */
    langClass.prototype.funcDelay = function (func, wait) {
        var args = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            args[_i - 2] = arguments[_i];
        }
        return setTimeout(function () {
            return func.call.apply(func, [null].concat(args));
        }, wait);
    };
    /**
     * Loop with a delay until callback fun return true.
     * Sample usage:
     * <code>
     *    var count = 0;
     *    ly.this.funcLoop(function () {
     *       count++;
     *       console.log(count);
     *       return count == 3; // exit
     *   }, 1000).done(function () {
     *       console.log("DONE");
     *   });
     * </code>
     * @param func
     * @param wait
     * @param args
     * @return promise {{done: done}}
     */
    langClass.prototype.funcLoop = function (func, wait) {
        var args = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            args[_i - 2] = arguments[_i];
        }
        var self = this;
        var callback;
        var timer = setInterval(function () {
            var exit = !!func.apply(null, args);
            if (exit) {
                clearInterval(timer);
                self.funcInvoke.bind(self)(callback); // call with bind
            }
        }, wait || 300);
        return {
            done: function (done_callback) {
                callback = done_callback;
            }
        };
    };
    /**
     * Returns a function that will be executed at most one time, no matter how
     * often you call it. Useful for lazy initialization.
     */
    langClass.prototype.funcOnce = function (func) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        var self = this;
        var ran = false;
        var memo;
        return function () {
            if (ran)
                return memo;
            ran = true;
            memo = func.call.apply(func, [self].concat(args));
            return memo;
        };
    };
    /**
     * Returns a function, that, as long as it continues to be invoked, will not
     * be triggered. The function will be called after it stops being called for
     * N milliseconds.
     * If `immediate` is passed, trigger the function on the leading edge, instead of the trailing.
     */
    langClass.prototype.funcDebounce = function (context, func, wait, immediate) {
        if (immediate === void 0) { immediate = false; }
        var args = [];
        for (var _i = 4; _i < arguments.length; _i++) {
            args[_i - 4] = arguments[_i];
        }
        var timeout;
        //let context: any;
        var timestamp;
        var result;
        var later = function () {
            var last = __WEBPACK_IMPORTED_MODULE_0__random__["a" /* default */].now() - timestamp;
            var full_args = Array.prototype.slice.call(arguments).concat(args);
            if (last < wait && last > 0) {
                clearTimeout(timeout);
                timeout = setTimeout.apply(void 0, [later, wait - last].concat(full_args));
            }
            else {
                timeout = null;
                clearTimeout(timeout);
                if (!immediate) {
                    result = func.apply(context, full_args);
                }
            }
        };
        return function () {
            timestamp = __WEBPACK_IMPORTED_MODULE_0__random__["a" /* default */].now();
            var callNow = immediate && !timeout;
            var full_args = Array.prototype.slice.call(arguments).concat(args);
            if (!timeout) {
                timeout = setTimeout.apply(void 0, [later, wait].concat(full_args));
            }
            if (callNow) {
                result = func.apply(context, full_args);
            }
            return result;
        };
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    langClass.prototype._validateEmail = function (email) {
        try {
            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }
        catch (err) {
            return false;
        }
    };
    langClass.instance = function () {
        if (null == langClass.__instance) {
            langClass.__instance = new langClass();
        }
        return langClass.__instance;
    };
    return langClass;
}());
// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------
var lang = langClass.instance();
/* harmony default export */ __webpack_exports__["a"] = (lang);


/***/ }),
/* 2 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return Dictionary; });
var Dictionary = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function Dictionary(o) {
        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------
        this._items = {};
        this._count = 0;
        if (!!o) {
            for (var key in o) {
                if (o.hasOwnProperty(key)) {
                    this.put(key, o[key]);
                }
            }
        }
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    Dictionary.prototype.put = function (key, value) {
        this._items[key] = value;
        this._count++;
    };
    Dictionary.prototype.get = function (key) {
        return this._items[key];
    };
    Dictionary.prototype.containsKey = function (key) {
        return this._items.hasOwnProperty(key);
    };
    Dictionary.prototype.count = function () {
        return this._count;
    };
    Dictionary.prototype.isEmpty = function () {
        return this._count === 0;
    };
    Dictionary.prototype.keys = function () {
        var Keys = [];
        // tslint:disable-next-line:forin
        for (var key in this._items) {
            Keys.push(key);
        }
        return Keys;
    };
    Dictionary.prototype.remove = function (key) {
        var val = this._items[key];
        delete this._items[key];
        this._count--;
        return val;
    };
    Dictionary.prototype.values = function () {
        var values = [];
        // tslint:disable-next-line:forin
        for (var key in this._items) {
            values.push(this._items[key]);
        }
        return values;
    };
    Dictionary.prototype.clear = function () {
        if (!this.isEmpty()) {
            this._items = {};
        }
    };
    return Dictionary;
}());



/***/ }),
/* 3 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__random__ = __webpack_require__(4);
/**
 * Extends standard console
 */

var console_ext = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function console_ext() {
        this._uid = __WEBPACK_IMPORTED_MODULE_0__random__["a" /* default */].guid();
    }
    Object.defineProperty(console_ext.prototype, "uid", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._uid;
        },
        set: function (value) {
            this._uid = value;
        },
        enumerable: true,
        configurable: true
    });
    console_ext.prototype.error = function (scope, error) {
        var args = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            args[_i - 2] = arguments[_i];
        }
        console.error.apply(console, ["[" + this.uid + "] " + scope, error].concat(args));
    };
    ;
    console_ext.prototype.warn = function (scope) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        console.warn.apply(console, ["[" + this.uid + "] " + scope].concat(args));
    };
    ;
    console_ext.prototype.log = function (scope) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        console.log.apply(console, ["[" + this.uid + "] " + scope].concat(args));
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    console_ext.prototype.init = function () {
        this.uid = __WEBPACK_IMPORTED_MODULE_0__random__["a" /* default */].guid();
    };
    console_ext.instance = function () {
        if (null == console_ext.__instance) {
            console_ext.__instance = new console_ext();
        }
        return console_ext.__instance;
    };
    return console_ext;
}());
// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (console_ext.instance());


/***/ }),
/* 4 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
var random = /** @class */ (function () {
    function random() {
    }
    // ------------------------------------------------------------------------
    //                      random and GUID
    // ------------------------------------------------------------------------
    // A (possibly faster) way to get the current timestamp as an integer.
    random.now = function () {
        return !!Date.now ? Date.now() : new Date().getTime();
    };
    random.rnd = function (arg1, arg2) {
        try {
            if (null === arg2) {
                return Math.random() * arg1;
            }
            else {
                return Math.floor(Math.random() * (arg2 || arg1)) + arg1;
            }
        }
        catch (err) {
        }
        return Math.random();
    };
    random.guid = function () {
        return random._s4() + random._s4() + '-' + random._s4() + '-' + random._s4() + '-' +
            random._s4() + '-' + random._s4() + random._s4() + random._s4();
    };
    random.s4 = function () {
        return random._s4();
    };
    random.id = function () {
        return random._s4() + random._s4();
    };
    random.uniqueId = function (prefix) {
        var id = ++random._id_counter + '';
        return prefix ? prefix + id : id;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    random._s4 = function () {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    };
    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    random._id_counter = 0;
    return random;
}());
/* harmony default export */ __webpack_exports__["a"] = (random);


/***/ }),
/* 5 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__Events__ = __webpack_require__(8);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__BaseObject__ = __webpack_require__(13);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__collections_Dictionary__ = __webpack_require__(2);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();



/**
 * Class that emit events with a scope.
 */
var EventEmitter = /** @class */ (function (_super) {
    __extends(EventEmitter, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function EventEmitter() {
        var _this = _super.call(this) || this;
        _this._listeners = new __WEBPACK_IMPORTED_MODULE_2__collections_Dictionary__["a" /* Dictionary */]();
        return _this;
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    EventEmitter.prototype.on = function (scope, eventName, listener) {
        var key = EventEmitter.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new __WEBPACK_IMPORTED_MODULE_0__Events__["a" /* default */]());
        }
        this._listeners.get(key).on(eventName, listener.bind(scope));
    };
    EventEmitter.prototype.once = function (scope, eventName, listener) {
        var key = EventEmitter.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new __WEBPACK_IMPORTED_MODULE_0__Events__["a" /* default */]());
        }
        this._listeners.get(key).once(eventName, listener.bind(scope));
    };
    EventEmitter.prototype.off = function (scope, eventName) {
        var key = EventEmitter.key(scope);
        if (this._listeners.containsKey(key)) {
            this._listeners.get(key).off(eventName);
        }
    };
    EventEmitter.prototype.clear = function () {
        if (!!this._listeners) {
            var keys = this._listeners.keys();
            for (var _i = 0, keys_1 = keys; _i < keys_1.length; _i++) {
                var key = keys_1[_i];
                if (this._listeners.containsKey(key)) {
                    this._listeners.get(key).clear();
                }
            }
        }
    };
    EventEmitter.prototype.emit = function (eventName) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        var _a;
        if (!!this._listeners) {
            var keys = this._listeners.keys();
            for (var _b = 0, keys_2 = keys; _b < keys_2.length; _b++) {
                var key = keys_2[_b];
                if (this._listeners.containsKey(key)) {
                    (_a = this._listeners.get(key)).emit.apply(_a, [eventName].concat(args));
                }
            }
        }
    };
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    EventEmitter.key = function (scope) {
        try {
            return scope.uid;
        }
        catch (err) {
            console.warn("ApplicationEvents.key()", "BINDING EVENT ON DEFAULT KEY!");
            return '_default';
        }
    };
    return EventEmitter;
}(__WEBPACK_IMPORTED_MODULE_1__BaseObject__["a" /* default */]));
/* harmony default export */ __webpack_exports__["a"] = (EventEmitter);


/***/ }),
/* 6 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__commons_lang__ = __webpack_require__(1);
/**
 * Browser Utility class
 */

var browser = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function browser() {
        this.init();
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    browser.prototype.isReady = function () {
        return !!document && !!navigator && !!window;
    };
    browser.prototype.language = function () {
        if (this.isReady()) {
            return navigator.language;
        }
        return '';
    };
    browser.prototype.lang = function () {
        return this.language().split('-')[0];
    };
    browser.prototype.location = function () {
        return window.location.href;
    };
    browser.prototype.hasStorage = function () {
        return (typeof (Storage) !== "undefined");
    };
    browser.prototype.isMobile = function () {
        if (this.isReady()) {
            var check_1 = false;
            (function (a) {
                if (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino|android|ipad|playbook|silk/i.test(a) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0, 4)))
                    check_1 = true;
            })(navigator.userAgent || navigator.vendor);
            return check_1;
        }
        return false;
    };
    browser.prototype.isPushStateAvailable = function () {
        return !!(typeof window !== 'undefined' &&
            window.history &&
            window.history.pushState);
    };
    browser.prototype.isHashChangeAvailable = function () {
        return !!(typeof window !== 'undefined' &&
            ('onhashchange' in window));
    };
    browser.prototype.getParameterByName = function (name, url) {
        url = url || location.search;
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(url);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    };
    browser.prototype.getParameters = function (query) {
        if (query === void 0) { query = ''; }
        query = query || location.search;
        query = query.split('?').length > 1 ? query.split('?')[1] : query;
        var vars = query.split("&");
        var query_string = {};
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split("=");
            // If first entry with this name
            if (typeof query_string[pair[0]] === "undefined") {
                query_string[pair[0]] = decodeURIComponent(pair[1]);
                // If second entry with this name
            }
            else if (typeof query_string[pair[0]] === "string") {
                var arr = [query_string[pair[0]], decodeURIComponent(pair[1])];
                query_string[pair[0]] = arr;
                // If third or later entry with this name
            }
            else {
                query_string[pair[0]].push(decodeURIComponent(pair[1]));
            }
        }
        return query_string;
    };
    // ------------------------------------------------------------------------
    //                      e v e n t s
    // ------------------------------------------------------------------------
    browser.prototype.onResize = function (callback, debounce) {
        if (debounce === void 0) { debounce = 200; }
        this._on_resize_callback = callback;
        this._debounce_wait = debounce;
        //-- event hooks --//
        if (!!window) {
            if (!!this._debounce_func) {
                window.removeEventListener("resize", this._debounce_func);
            }
            this._debounce_func = __WEBPACK_IMPORTED_MODULE_0__commons_lang__["a" /* default */].funcDebounce(this, this._resize, this._debounce_wait);
            window.addEventListener("resize", this._debounce_func);
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    browser.prototype.init = function () {
    };
    browser.prototype.doResize = function (w, h) {
        if (!!this._on_resize_callback) {
            this._on_resize_callback(w, h);
        }
    };
    browser.prototype._resize = function () {
        this.doResize(window.innerWidth, window.innerHeight);
    };
    browser.instance = function () {
        if (null == browser.__instance) {
            browser.__instance = new browser();
        }
        return browser.__instance;
    };
    return browser;
}());
// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (browser.instance());


/***/ }),
/* 7 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* unused harmony export SelectorType */
/* unused harmony export SelectorParser */
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__browser__ = __webpack_require__(6);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__commons_strings__ = __webpack_require__(11);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__commons_lang__ = __webpack_require__(1);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__ly__ = __webpack_require__(0);




var SelectorType;
(function (SelectorType) {
    SelectorType[SelectorType["ID"] = 0] = "ID";
    SelectorType[SelectorType["CLASS"] = 1] = "CLASS";
    SelectorType[SelectorType["TAG"] = 2] = "TAG";
    SelectorType[SelectorType["ATTR"] = 3] = "ATTR";
})(SelectorType || (SelectorType = {}));
/**
 * Parser for CSS selectors.
 */
var SelectorParser = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function SelectorParser(selector) {
        this._selector = selector;
        this.parse();
    }
    Object.defineProperty(SelectorParser.prototype, "raw", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._selector;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(SelectorParser.prototype, "type", {
        get: function () {
            return this._type;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(SelectorParser.prototype, "name", {
        get: function () {
            return this._name;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(SelectorParser.prototype, "value", {
        get: function () {
            return this._value;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(SelectorParser.prototype, "operator", {
        get: function () {
            return this._operator;
        },
        enumerable: true,
        configurable: true
    });
    SelectorParser.prototype.match = function (elem) {
        try {
            if (!!elem) {
                return this.matchElement(elem);
            }
        }
        catch (err) {
        }
        return false;
    };
    SelectorParser.prototype.matchElement = function (elem) {
        if (this.type === SelectorType.TAG) {
            return elem.tagName === this.value;
        }
        else if (this.type === SelectorType.ID) {
            return elem.id == this.value;
        }
        else if (this.type === SelectorType.CLASS) {
            var classes = elem.className.split(" ") || [];
            return classes.indexOf(this.value) > -1;
        }
        else if (this.type === SelectorType.ATTR) {
            if (!!this.operator) {
                if (elem.hasAttribute(this.name)) {
                    var attr_value = elem.getAttribute(this.name) || '';
                    if (this.operator === "=") {
                        return attr_value === this.value;
                    }
                    else if (this.operator === "~=") {
                        // attribute value is a whitespace-separated list of words, one of which is exactly "val" (attribute="val1 val2")
                        return attr_value.split(" ").indexOf(this.value) > -1;
                    }
                    else if (this.operator === "|=") {
                        // attribute value equals "val" or starts with
                        return __WEBPACK_IMPORTED_MODULE_1__commons_strings__["a" /* default */].startWith(attr_value, this.value);
                    }
                }
            }
            else {
                // check only attribute
                return elem.hasAttribute(this.value);
            }
        }
        return false;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    SelectorParser.prototype.parse = function () {
        if (!!this._selector) {
            // defaults
            this._operator = '';
            // check type and parse values
            if (SelectorParser.isSelectorID(this._selector)) {
                // selector id
                this._type = SelectorType.ID;
                this._name = "id";
                this._value = this._selector.substring(1);
            }
            else if (SelectorParser.isSelectorCLASS(this._selector)) {
                // selector class
                this._type = SelectorType.CLASS;
                this._name = "class";
                this._value = this._selector.substring(1);
            }
            else if (SelectorParser.isSelectorATTR(this._selector)) {
                // selector attribute [attribute]='value'
                this._type = SelectorType.ATTR;
                var selector = __WEBPACK_IMPORTED_MODULE_1__commons_strings__["a" /* default */].replaceAll(["'", "[", "]"], "", this._selector);
                // [att=val] [att~=val] [att|=val]
                if (selector.indexOf("~=") > -1) {
                    // [att~=val] - Represents an element with the att attribute whose value is a whitespace-separated list of words, one of which is exactly "val"
                    var tokens = selector.split("~=");
                    this._name = tokens[0];
                    this._value = tokens[1];
                    this._operator = '~=';
                }
                else if (selector.indexOf("|=") > -1) {
                    // [att|=val] - Represents an element with the att attribute, its value either being exactly "val" or beginning with "val" immediately followed by "-" (U+002D).
                    var tokens = selector.split("|=");
                    this._name = tokens[0];
                    this._value = tokens[1];
                    this._operator = '|=';
                }
                else if (selector.indexOf("=") > -1) {
                    // [att=val]
                    var tokens = selector.split("=");
                    this._name = tokens[0];
                    this._value = tokens[1];
                    this._operator = '=';
                }
                else {
                    // [attr]
                    this._name = selector;
                    this._value = selector;
                    this._operator = '';
                }
            }
            else {
                // selector tag name
                this._type = SelectorType.TAG;
                this._name = this._selector;
                this._value = this._selector;
            }
        }
    };
    SelectorParser.isSelectorID = function (selector) {
        return !!selector ? __WEBPACK_IMPORTED_MODULE_1__commons_strings__["a" /* default */].startWith(selector, "#") : false;
    };
    SelectorParser.isSelectorCLASS = function (selector) {
        return !!selector ? __WEBPACK_IMPORTED_MODULE_1__commons_strings__["a" /* default */].startWith(selector, ".") : false;
    };
    SelectorParser.isSelectorATTR = function (selector) {
        return !!selector ? __WEBPACK_IMPORTED_MODULE_1__commons_strings__["a" /* default */].startWith(selector, "[") && selector.indexOf("]") > -1 : false;
    };
    return SelectorParser;
}());

/**
 * Default Export class.
 */
var doc = document;
var win = window;
var domClass = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function domClass() {
        this._readyList = [];
        this._ready = (doc.readyState === "complete" || (!doc.attachEvent && doc.readyState === "interactive"));
        if (!this._ready) {
            if (doc.addEventListener) {
                // first choice is DOMContentLoaded event
                doc.addEventListener("DOMContentLoaded", this.onDocumentReady.bind(this), false);
                // backup is window load event
                win.addEventListener("load", this.onDocumentReady.bind(this), false);
            }
            else {
                // must be IE
                doc.attachEvent("onreadystatechange", this.onDocumentReadyStateChange.bind(this));
                win.attachEvent("onload", this.onDocumentReady.bind(this));
            }
        }
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    domClass.prototype.ready = function (callback, bind_context) {
        if (__WEBPACK_IMPORTED_MODULE_3__ly__["a" /* default */].lang.isFunction(callback)) {
            var callback_to_invoke = callback.bind(bind_context || this);
            if (dom._ready) {
                __WEBPACK_IMPORTED_MODULE_3__ly__["a" /* default */].lang.funcDelay(callback_to_invoke, 1);
            }
            else {
                dom._readyList.push(callback_to_invoke);
            }
        }
    };
    domClass.prototype.parse = function (text) {
        var parser = new DOMParser();
        var doc = parser.parseFromString(text, "text/html");
        return doc;
    };
    domClass.prototype.getElementById = function (id) {
        return doc.getElementById(id);
    };
    domClass.prototype.getElementsByTagName = function (tag_name) {
        var response = [];
        var list = doc.getElementsByTagName(tag_name);
        var count = list.length;
        for (var i = 0; i < count; i++) {
            response.push(list.item(i));
        }
        return response;
    };
    domClass.prototype.getElementsByClassName = function (class_name) {
        var response = [];
        var list = doc.getElementsByClassName(class_name);
        var count = list.length;
        for (var i = 0; i < count; i++) {
            response.push(list.item(i));
        }
        return response;
    };
    domClass.prototype.createElement = function (tag, target) {
        if (tag === void 0) { tag = 'div'; }
        if (target === void 0) { target = 'body'; }
        var parent = null;
        if (target instanceof HTMLElement) {
            parent = target;
        }
        else if (__WEBPACK_IMPORTED_MODULE_3__ly__["a" /* default */].lang.isString(target)) {
            parent = doc[target] || doc.getElementsByTagName(target)[0];
        }
        var element = doc.createElement(tag);
        if (!!parent) {
            parent.appendChild(element);
        }
        return element;
    };
    domClass.prototype.injectStyle = function (css, target) {
        if (target === void 0) { target = 'head'; }
        var head = doc[target] || doc.getElementsByTagName(target)[0];
        var style = doc.createElement('style');
        style.type = 'text/css';
        if (style.styleSheet) {
            style.styleSheet.cssText = css;
        }
        else {
            style.appendChild(doc.createTextNode(css));
        }
        head.appendChild(style);
    };
    domClass.prototype.createAttribute = function (name) {
        return doc.createAttribute(name);
    };
    domClass.prototype.newElement = function (inner_html, append_to_selector) {
        if (inner_html === void 0) { inner_html = ''; }
        var elem;
        if (!!inner_html) {
            var wrapper = doc.createElement("div");
            wrapper.innerHTML = inner_html;
            if (wrapper.childElementCount > 1) {
                elem = wrapper;
            }
            else {
                elem = wrapper.firstChild;
            }
        }
        elem = elem || doc.createElement("div");
        if (!!append_to_selector) {
            var parent_1 = this.get(append_to_selector);
            if (parent_1.length > 0) {
                parent_1[0].appendChild(elem);
            }
        }
        return elem;
    };
    domClass.prototype.getFirst = function (selector, target) {
        if (selector === void 0) { selector = ''; }
        var response = this.get(selector, target);
        return response.length > 0 ? response[0] : null;
    };
    domClass.prototype.getLast = function (selector, target) {
        if (selector === void 0) { selector = ''; }
        var response = this.get(selector, target);
        return response.length > 0 ? response[response.length - 1] : null;
    };
    domClass.prototype.get = function (selector, target) {
        if (selector === void 0) { selector = ''; }
        if (!!selector) {
            var selector_parser = new SelectorParser(selector);
            if (!!target) {
                return this.getElementFromParent(target, selector_parser);
            }
            else {
                return this.getElementFromDocument(selector_parser);
            }
        }
        return [];
    };
    domClass.prototype.forEachChild = function (elem, func, deep) {
        if (deep === void 0) { deep = false; }
        if (__WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].isFunction(func) && !!elem && !!elem.children) {
            var count = elem.children.length;
            for (var i = 0; i < count; i++) {
                var child = elem.children.item(i);
                if (!!child) {
                    func(child);
                    if (deep && child.children.length > 0) {
                        // recursive
                        this.forEachChild(child, func, deep);
                    }
                }
            }
        }
    };
    domClass.prototype.map = function (elem, func, deep) {
        if (deep === void 0) { deep = false; }
        var response = new Array();
        if (__WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].isFunction(func) && !!elem) {
            var count = elem.children.length;
            for (var i = 0; i < count; i++) {
                var child = elem.children.item(i);
                if (!!child) {
                    if (func(child)) {
                        response.push(child);
                    }
                    if (deep && child.children.length > 0) {
                        // recursive
                        response.push.apply(response, this.map(child, func, deep));
                    }
                }
            }
        }
        return response;
    };
    domClass.prototype.isInput = function (elem) {
        return !!elem
            ? elem.tagName.toLowerCase() === "input"
            : false;
    };
    domClass.prototype.isInputButton = function (elem) {
        return !!elem
            ? this.isInput(elem) && elem.getAttribute("type") === "button"
            : false;
    };
    domClass.prototype.isInputText = function (elem) {
        return !!elem
            ? this.isInput(elem) && elem.getAttribute("type") === "text"
            : false;
    };
    domClass.prototype.isInputCheck = function (elem) {
        return !!elem
            ? this.isInput(elem) && elem.getAttribute("type") === "checkbox"
            : false;
    };
    domClass.prototype.isTextArea = function (elem) {
        return !!elem
            ? elem.tagName.toLowerCase() === "textarea"
            : false;
    };
    domClass.prototype.getValue = function (elem) {
        if (!!elem) {
            if (this.isInput(elem)) {
                var e = elem;
                if (!!e) {
                    var type = e.getAttribute("type");
                    if (type === "checkbox") {
                        return e.checked;
                    }
                    else if (!!e.value) {
                        return e.value;
                    }
                }
            }
            else if (this.isTextArea(elem)) {
                var e = elem;
                return !!e ? e.value : null;
            }
        }
        return null;
    };
    domClass.prototype.setValue = function (elem, value) {
        if (!!elem) {
            if (this.isInput(elem)) {
                var e = elem;
                if (!!e) {
                    var type = e.getAttribute("type");
                    if (type === "checkbox") {
                        e.checked = value;
                    }
                    else {
                        e.value = value;
                    }
                }
            }
            else if (this.isTextArea(elem)) {
                var e = elem;
                e.value = value;
            }
            else {
                elem.innerHTML = value;
            }
        }
    };
    domClass.prototype.classAdd = function (elem, class_name) {
        if (!!elem && !!elem.classList) {
            var classes = __WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].toArray(class_name);
            for (var _i = 0, classes_1 = classes; _i < classes_1.length; _i++) {
                var aclass = classes_1[_i];
                if (!elem.classList.contains(aclass)) {
                    elem.classList.add(aclass);
                }
            }
            return true;
        }
        return false;
    };
    domClass.prototype.classRemove = function (elem, class_name) {
        if (!!elem && !!elem.classList) {
            var classes = __WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].toArray(class_name);
            for (var _i = 0, classes_2 = classes; _i < classes_2.length; _i++) {
                var aclass = classes_2[_i];
                if (elem.classList.contains(aclass)) {
                    elem.classList.remove(aclass);
                }
            }
            return true;
        }
        return false;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    domClass.prototype.onDocumentReadyStateChange = function () {
        if (doc.readyState === "complete") {
            this.onDocumentReady();
        }
    };
    domClass.prototype.onDocumentReady = function () {
        this._ready = true;
        this._readyList.forEach(function (callback) {
            __WEBPACK_IMPORTED_MODULE_3__ly__["a" /* default */].lang.funcDelay(callback, 1000);
        });
        this._readyList = [];
    };
    domClass.prototype.getElementFromDocument = function (selector) {
        var list = [];
        if (!!selector && __WEBPACK_IMPORTED_MODULE_0__browser__["a" /* default */].isReady()) {
            if (selector.type === SelectorType.ID) {
                var result = doc.getElementById(selector.value);
                if (!!result) {
                    list.push(result);
                }
            }
            else if (selector.type === SelectorType.CLASS) {
                var result = doc.getElementsByClassName(selector.value);
                var count = result.length;
                for (var i = 0; i < count; i++) {
                    list.push(result.item(i));
                }
            }
            else if (selector.type === SelectorType.TAG) {
                var result = doc.getElementsByTagName(selector.value);
                var count = result.length;
                for (var i = 0; i < count; i++) {
                    list.push(result.item(i));
                }
            }
            else if (selector.type === SelectorType.ATTR) {
                var children = doc.body.children;
                for (var i = 0; i < children.length; i++) {
                    var elem = children.item(i);
                    var found = this.getElementFromParent(elem, selector);
                    if (found.length > 0) {
                        list.push.apply(list, found);
                    }
                }
            }
        }
        return list;
    };
    domClass.prototype.getElementFromParent = function (elem, selector) {
        var list = new Array();
        if (!!selector && __WEBPACK_IMPORTED_MODULE_0__browser__["a" /* default */].isReady()) {
            list.push.apply(list, this.map(elem, function (child) {
                return selector.match(child);
            }, true));
        }
        return list;
    };
    domClass.instance = function () {
        if (null == domClass.__instance) {
            domClass.__instance = new domClass();
        }
        return domClass.__instance;
    };
    return domClass;
}());
var dom = domClass.instance();
/* harmony default export */ __webpack_exports__["a"] = (dom);


/***/ }),
/* 8 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__collections_Dictionary__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__lang__ = __webpack_require__(1);


/**
 * Events controller.
 *
 * <code>
 *
 * import {Events} from "./events/Events";
 *
 * class MyEmitter extends Events{}
 *
 * let myEmitter = new MyEmitter();
 * myEmitter.on('event', () => {
 *   console.log('event occured')
 * });
 *
 * myEmitter.emit('event');
 *
 * </code>
 *
 *
 */
var Events = /** @class */ (function () {
    function Events() {
        // ------------------------------------------------------------------------
        //                      C O N S T
        // ------------------------------------------------------------------------
        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------
        this._events = new __WEBPACK_IMPORTED_MODULE_0__collections_Dictionary__["a" /* Dictionary */]();
        this._maxListeners = 0;
    }
    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------
    Events.prototype.getMaxListeners = function () {
        return this._maxListeners === 0 ? Events.DEFAULT_MAX_LISTENERS : this._maxListeners;
    };
    Events.prototype.setMaxListeners = function (limit) {
        this._maxListeners = limit;
        return this;
    };
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    Events.prototype.addListener = function (eventName, listener) {
        return this.on(eventName, listener);
    };
    Events.prototype.on = function (eventName, listener) {
        this._registerEvent(eventName, listener, false);
        return this;
    };
    Events.prototype.once = function (eventName, listener) {
        this._registerEvent(eventName, listener, true);
        return this;
    };
    Events.prototype.off = function (event_names, listener) {
        var names = __WEBPACK_IMPORTED_MODULE_1__lang__["a" /* default */].isArray(event_names)
            ? event_names
            : !!event_names ? [event_names] : [];
        if (!!listener) {
            for (var _i = 0, names_1 = names; _i < names_1.length; _i++) {
                var name_1 = names_1[_i];
                this.removeListener(name_1, listener);
            }
        }
        else {
            if (names.length > 0) {
                this.removeAllListeners(names);
            }
            else {
                this.removeAllListeners();
            }
        }
        return this;
    };
    Events.prototype.emit = function (eventName) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        var listeners = this._events.get(eventName);
        var listenerCount = this.listenerCount(eventName);
        if (listeners) {
            listeners.map(function (listener) { return listener.apply(void 0, args); });
        }
        return listenerCount !== 0;
    };
    Events.prototype.eventNames = function () {
        return this._events.keys();
    };
    Events.prototype.listeners = function (eventName) {
        return this._events.get(eventName);
    };
    Events.prototype.listenerCount = function (eventName) {
        var listeners = this._events.get(eventName);
        return listeners === undefined ? 0 : listeners.length;
    };
    Events.prototype.removeAllListeners = function (eventNames) {
        var _this = this;
        if (!eventNames) {
            eventNames = this._events.keys();
        }
        eventNames.forEach(function (eventName) { return _this._events.remove(eventName); });
        return this;
    };
    Events.prototype.removeListener = function (eventName, listener) {
        var listeners = this.listeners(eventName);
        var filtered_listeners = !!listeners
            ? listeners.filter(function (item) { return item === listener; }) // filter only valid
            : [];
        this._events.put(eventName, filtered_listeners);
        return this;
    };
    Events.prototype.clear = function () {
        this._events.clear();
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    Events.prototype._registerEvent = function (eventName, listener, type) {
        if (this._listenerLimitReached(eventName)) {
            console.warn("Maximum listener reached, new Listener not added");
            return;
        }
        if (type === true) {
            listener = this._createOnceListener(listener, eventName);
        }
        var listeners = Events._createListeners(listener, this.listeners(eventName));
        this._events.put(eventName, listeners);
        return;
    };
    Events.prototype._createOnceListener = function (listener, eventName) {
        var _this = this;
        return function () {
            var args = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                args[_i] = arguments[_i];
            }
            _this.removeListener(eventName, listener);
            return listener.apply(void 0, args);
        };
    };
    Events.prototype._listenerLimitReached = function (eventName) {
        return this.listenerCount(eventName) >= this.getMaxListeners();
    };
    Events._createListeners = function (listener, listeners) {
        if (!listeners) {
            listeners = [];
        }
        listeners.push(listener);
        return listeners;
    };
    Events.DEFAULT_MAX_LISTENERS = 10; // max listener for each event name
    return Events;
}());
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (Events);


/***/ }),
/* 9 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__commons_events_Events__ = __webpack_require__(8);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__dom__ = __webpack_require__(7);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__commons_lang__ = __webpack_require__(1);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__commons_collections_Dictionary__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__commons_events_EventEmitter__ = __webpack_require__(5);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__i18n__ = __webpack_require__(12);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__ = __webpack_require__(22);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();







var Component = /** @class */ (function (_super) {
    __extends(Component, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function Component() {
        var _this = _super.call(this) || this;
        _this._native_events = new __WEBPACK_IMPORTED_MODULE_3__commons_collections_Dictionary__["a" /* Dictionary */]();
        _this._native_elements = new __WEBPACK_IMPORTED_MODULE_3__commons_collections_Dictionary__["a" /* Dictionary */]();
        _this._element = _this._createElement(_this.render());
        _this._element_wrapper = new __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__["a" /* default */](_this, _this._element);
        _this._data = {};
        _this._normalizeElements();
        _this.localize();
        // auto-localize
        __WEBPACK_IMPORTED_MODULE_5__i18n__["a" /* default */].on(_this, __WEBPACK_IMPORTED_MODULE_5__i18n__["a" /* default */].EVENT_CHANGE_LANG, _this.localize);
        return _this;
    }
    Component.prototype.remove = function () {
        this._free();
    };
    Object.defineProperty(Component.prototype, "data", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._data;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Component.prototype, "element", {
        get: function () {
            return this._element_wrapper;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Component.prototype, "outerHTML", {
        get: function () {
            return !!this._element ? this._element.outerHTML : "";
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Component.prototype, "innerHTML", {
        get: function () {
            return !!this._element ? this._element.innerHTML : "";
        },
        enumerable: true,
        configurable: true
    });
    Component.prototype.hashCode = function (selector) {
        if (!!selector) {
            return this._hash(this._getFirstElement(selector));
        }
        return this._hash(this._element);
    };
    Component.prototype.hide = function () {
        __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].classAdd(this._element, 'hidden');
    };
    Component.prototype.show = function () {
        __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].classRemove(this._element, 'hidden');
    };
    // ------------------------------------------------------------------------
    //                      d o m
    // ------------------------------------------------------------------------
    Component.prototype.localize = function () {
        __WEBPACK_IMPORTED_MODULE_5__i18n__["a" /* default */].localize(this._element);
    };
    Component.prototype.get = function (selector) {
        var result = [];
        var elements = __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].get(selector, this._element);
        for (var _i = 0, elements_1 = elements; _i < elements_1.length; _i++) {
            var elem = elements_1[_i];
            result.push(new __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__["a" /* default */](this, elem));
        }
        return result;
    };
    Component.prototype.getFirst = function (selector) {
        return new __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__["a" /* default */](this, __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].getFirst(selector, this._element));
    };
    Component.prototype.getLast = function (selector) {
        return new __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__["a" /* default */](this, __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].getLast(selector, this._element));
    };
    Component.prototype.appendTo = function (selector, clean_parent) {
        if (clean_parent === void 0) { clean_parent = false; }
        var elem = (selector instanceof __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__["a" /* default */])
            ? selector
            : new __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__["a" /* default */](this, __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].getFirst(selector));
        if (!!elem) {
            if (clean_parent) {
                elem.innerHTML = '';
            }
            elem.appendChild(this._element);
            //-- this is ready --//
            this.ready();
        }
    };
    Component.prototype.appendChild = function (child_html, opt_target_selector) {
        if (!!child_html) {
            var child = this._createElement(child_html);
            var target = this._resolveElement(opt_target_selector || null, this._element);
            if (!!target) {
                target.appendChild(child);
                // handle events for child
                this._normalizeElement(child);
            }
        }
    };
    /**
     * Return a class list.
     * @param {string} selector Element selector. Only first matched element is returned.
     * @return {string[]} Array of class names
     */
    Component.prototype.classList = function (selector) {
        var elem = this._getFirstElement(selector);
        if (!!elem) {
            return elem.className.split(" ");
        }
        return [];
    };
    /**
     * Check if selected element contains passed class. If passed class is an Array, check for all of them.
     * @param {string} selector Element selector. Only first matched element is returned.
     * @param {string | string[]} class_name Single class name or multiple class names.
     * @return {boolean} Match found or not.
     */
    Component.prototype.classHas = function (selector, class_name) {
        var elem = this._getFirstElement(selector);
        if (!!elem) {
            var classes = __WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].toArray(class_name);
            for (var _i = 0, classes_1 = classes; _i < classes_1.length; _i++) {
                var aclass = classes_1[_i];
                if (!elem.classList.contains(aclass)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    };
    /**
     * Check the array for at least one match.
     * @param {string} selector  Element selector. Only first matched element is returned.
     * @param {string[]} class_name Array of classes
     * @return {boolean} Match found or not.
     */
    Component.prototype.classHasOne = function (selector, class_name) {
        var elem = this._getFirstElement(selector);
        if (!!elem) {
            var classes = __WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].toArray(class_name);
            for (var _i = 0, classes_2 = classes; _i < classes_2.length; _i++) {
                var aclass = classes_2[_i];
                if (elem.classList.contains(aclass)) {
                    return true;
                }
            }
        }
        return false;
    };
    Component.prototype.classAdd = function (selector, class_name) {
        var elem = this._getFirstElement(selector);
        return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].classAdd(elem, class_name);
    };
    Component.prototype.classRemove = function (selector, class_name) {
        var elem = this._getFirstElement(selector);
        return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].classRemove(elem, class_name);
    };
    Component.prototype.classSet = function (selector, value) {
        return this.attrSet(selector, "class", name);
    };
    Component.prototype.attrValue = function (selector, attr_name) {
        var elem = this._getFirstElement(selector);
        if (!!elem) {
            return elem.getAttribute(attr_name) || '';
        }
        return '';
    };
    Component.prototype.attrHas = function (selector, attr_name) {
        var elem = this._getFirstElement(selector);
        if (!!elem) {
            return elem.hasAttribute(attr_name);
        }
        return false;
    };
    Component.prototype.attrSet = function (selector, name, value) {
        var elem = this._getFirstElement(selector);
        if (!!elem) {
            elem.setAttribute(name, value);
            return elem.getAttribute(name) || '';
        }
        return '';
    };
    Component.prototype.getValue = function (selector) {
        var elem = this._getFirstElement(selector);
        if (!!elem) {
            return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].getValue(elem);
        }
        return null;
    };
    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    /**
     * Add event listener to internal HTMLElement
     * @param {string} selector
     * @param {string} event_name
     * @param {Listener} listener
     */
    Component.prototype.addEventListener = function (selector, event_name, listener) {
        var elem = this._resolveElement(selector, this._element);
        if (!!elem) {
            this._addEventListener(elem, event_name, listener);
        }
        else {
            console.warn("Component.addEventListener()", "Unable to add event '" + event_name + "' to '" + selector + "': Element not found!");
        }
    };
    /**
     * Remove event listener from internal HTMLElement
     * @param {string} selector
     * @param {string | string[]} event_names
     */
    Component.prototype.removeEventListener = function (selector, event_names) {
        var elem = this._resolveElement(selector, this._element);
        if (!!elem) {
            this._removeEventListener(elem, __WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].toArray(event_names));
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    Component.prototype._free = function () {
        // remove ly events
        _super.prototype.off.call(this, this);
        // remove native events
        this._freeListeners();
        // remove element from dom
        if (!!this._element) {
            this._element.remove();
        }
        // clear list
        this._native_events.clear();
        this._native_elements.clear();
        __WEBPACK_IMPORTED_MODULE_5__i18n__["a" /* default */].off(this, __WEBPACK_IMPORTED_MODULE_5__i18n__["a" /* default */].EVENT_CHANGE_LANG);
        // call abstract free
        this.free();
    };
    Component.prototype._freeListeners = function () {
        var hash_codes = this._native_events.keys();
        for (var _i = 0, hash_codes_1 = hash_codes; _i < hash_codes_1.length; _i++) {
            var hash_code = hash_codes_1[_i];
            var elem = this._native_elements.get(hash_code);
            if (!!elem) {
                var names = this._native_events.get(hash_code).eventNames();
                var count = this._removeEventListener(elem, names);
                //console.log("Component._free()", hash_code, names, count);
            }
        }
    };
    Component.prototype._normalizeElements = function () {
        var _this = this;
        // events on root
        this._normalizeElement(this._element);
        // events on child
        __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].forEachChild(this._element, function (elem) {
            _this._normalizeElement(elem);
        }, true);
    };
    Component.prototype._resolveElement = function (elem_or_selector, defVal) {
        if (!!elem_or_selector) {
            if (__WEBPACK_IMPORTED_MODULE_2__commons_lang__["a" /* default */].isString(elem_or_selector)) {
                var found = this._getFirstElement(elem_or_selector);
                if (!!found) {
                    return this._normalizeElement(found);
                }
            }
            else {
                var found = elem_or_selector;
                if (!!found) {
                    return this._normalizeElement(found);
                }
            }
        }
        return !!defVal ? this._normalizeElement(defVal) : null;
    };
    Component.prototype._getElement = function (selector) {
        return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].get(selector, this._element);
    };
    Component.prototype._getFirstElement = function (selector) {
        return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].getFirst(selector, this._element);
    };
    Component.prototype._getLastElement = function (selector) {
        return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].getLast(selector, this._element);
    };
    Component.prototype._addEventListener = function (elem, event_name, listener) {
        var hash_code = this._hash(elem);
        if (!this._native_events.containsKey(hash_code)) {
            this._native_events.put(hash_code, new __WEBPACK_IMPORTED_MODULE_0__commons_events_Events__["a" /* default */]());
        }
        // get context binded listener
        var ctx_listener = listener.bind(this);
        // register reference for further removal
        this._native_events.get(hash_code).on(event_name, ctx_listener);
        // attach listener to native event
        elem.addEventListener(event_name, ctx_listener, false);
    };
    Component.prototype._removeEventListener = function (elem, event_names, listener) {
        var counter = 0;
        if (!!elem) {
            var hash_code = this._hash(elem);
            if (this._native_events.containsKey(hash_code)) {
                var events = this._native_events.get(hash_code);
                for (var _i = 0, event_names_1 = event_names; _i < event_names_1.length; _i++) {
                    var name_1 = event_names_1[_i];
                    if (!!listener) {
                        // remove reference
                        events.removeListener(name_1, listener);
                        // remove native
                        elem.removeEventListener(name_1, listener);
                        counter++;
                    }
                    else {
                        var all_listeners = events.listeners(name_1);
                        for (var i = 0; i < all_listeners.length; i++) {
                            var _listener = all_listeners[i];
                            // remove reference
                            events.removeListener(name_1, _listener);
                            // remove native
                            elem.removeEventListener(name_1, _listener);
                            counter++;
                        }
                    }
                }
            }
        }
        return counter;
    };
    Component.prototype._createElement = function (html) {
        html = html.trim();
        return this._normalizeElement(__WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].newElement(html));
    };
    Component.prototype._normalizeElement = function (elem) {
        // add hash
        this._hash(elem);
        //... do more stuff here
        return elem;
    };
    Component.prototype._hash = function (elem) {
        if (null != elem) {
            var hash_code = __WEBPACK_IMPORTED_MODULE_6__ElementWrapper__["a" /* default */].hash(elem);
            if (!!hash_code) {
                // add new element reference to internal hash dictionary
                this._native_elements.put(hash_code, elem);
            }
            return hash_code;
        }
        return '';
    };
    return Component;
}(__WEBPACK_IMPORTED_MODULE_4__commons_events_EventEmitter__["a" /* default */]));
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (Component);


/***/ }),
/* 10 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__ = __webpack_require__(0);

/**
 * GLOBAL VARIABLES EXPOSED FROM DASHBOARD
 */
var root = window;
var default_locales = [
    {
        code: "it-IT",
        lang: "it",
        country: "IT",
        label: "Italiano"
    },
    {
        code: "en-GB",
        lang: "en",
        country: "GB",
        label: "English"
    }
];
var default_company_uid = "test_company";
var global_locales = default_locales;
var global_application = __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].Application;
var globals = {
    // expone window
    root: root,
    Resumable: root['Resumable'],
    Materialize: root['M'],
    $: root['$'],
    // AuthController
    company_uid: default_company_uid,
    // i18n
    i18n: __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n,
    // supported locales
    locales: global_locales,
    // Main Application scope
    Application: global_application,
};
/* harmony default export */ __webpack_exports__["a"] = (globals);


/***/ }),
/* 11 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__lang__ = __webpack_require__(1);

var strings = /** @class */ (function () {
    function strings() {
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    /**
     * Replace all occurrences of 'find' parameter with 'replace' parameter in a string 'str'.
     * @param {string[] | string} find Parameter to find
     * @param {string} replace Replace value
     * @param {string} str Source string
     * @return {string} String with replaced values
     */
    strings.replaceAll = function (find, replace, str) {
        var rep_array = [];
        if (__WEBPACK_IMPORTED_MODULE_0__lang__["a" /* default */].isString(find)) {
            rep_array.push(find);
        }
        else {
            rep_array.push.apply(rep_array, find);
        }
        var result = str;
        for (var i = 0; i < rep_array.length; i++) {
            result = strings._replaceAll(rep_array[i], replace, result);
        }
        return result;
    };
    strings.endWith = function (str, suffix) {
        if (str === null || suffix === null)
            return false;
        return str.indexOf(suffix, str.length - suffix.length) !== -1;
    };
    strings.startWith = function (str, prefix) {
        if (str === null || prefix === null)
            return false;
        return str.indexOf(prefix) === 0;
    };
    strings.fillLeft = function (value, fill, size) {
        while (value.length < size) {
            value = fill + value;
        }
        return value;
    };
    strings.fillRight = function (value, fill, size) {
        while (value.length < size) {
            value = value + fill;
        }
        return value;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    strings._escapeRegExp = function (value) {
        return value.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
    };
    strings._replaceAll = function (find, replace, str) {
        return str.replace(new RegExp(strings._escapeRegExp(find), 'g'), replace);
    };
    return strings;
}());
/* harmony default export */ __webpack_exports__["a"] = (strings);


/***/ }),
/* 12 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__commons_collections_Dictionary__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__commons_events_EventEmitter__ = __webpack_require__(5);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__browser__ = __webpack_require__(6);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__dom__ = __webpack_require__(7);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();




/**
 * Localization singleton controller.
 * Add dictionary using i18n.register(lang, dictionary);
 *
 * WARN:
 *  Do not listen directly at EVENT_CHANGE_LANG, but use Application events propagation.
 *  Components automatically handle this event, so you do not need to do it by yourself.
 *
 */
var i18n = /** @class */ (function (_super) {
    __extends(i18n, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function i18n() {
        var _this = _super.call(this) || this;
        _this._dictionaries = new __WEBPACK_IMPORTED_MODULE_0__commons_collections_Dictionary__["a" /* Dictionary */]();
        // get lang from browser
        _this._browser_lang = __WEBPACK_IMPORTED_MODULE_2__browser__["a" /* default */].lang();
        _this.register("", { key: "" });
        return _this;
    }
    Object.defineProperty(i18n.prototype, "EVENT_CHANGE_LANG", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return i18n._EVENT_CHANGE_LANG;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(i18n.prototype, "EVENT_LOCALIZED", {
        get: function () {
            return i18n._EVENT_LOCALIZED;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(i18n.prototype, "lang", {
        get: function () {
            return this._lang || this._browser_lang;
        },
        set: function (lang) {
            this._changeLang(lang);
        },
        enumerable: true,
        configurable: true
    });
    i18n.prototype.register = function (lang, dictionary) {
        var dic = (dictionary instanceof __WEBPACK_IMPORTED_MODULE_0__commons_collections_Dictionary__["a" /* Dictionary */]) ? dictionary : new __WEBPACK_IMPORTED_MODULE_0__commons_collections_Dictionary__["a" /* Dictionary */](dictionary);
        this._dictionaries.put(lang, dic);
    };
    i18n.prototype.registerDefault = function (dictionary) {
        var dic = (dictionary instanceof __WEBPACK_IMPORTED_MODULE_0__commons_collections_Dictionary__["a" /* Dictionary */]) ? dictionary : new __WEBPACK_IMPORTED_MODULE_0__commons_collections_Dictionary__["a" /* Dictionary */](dictionary);
        this._dictionaries.put(i18n._DEF_LANG, dic);
    };
    i18n.prototype.get = function (label, def_val) {
        if (this._dictionaries.containsKey(this._lang)) {
            var dic = this._dictionaries.get(this._lang);
            return dic.get(label) || def_val || '';
        }
        else if (this._dictionaries.containsKey(i18n._DEF_LANG)) {
            var dic = this._dictionaries.get(i18n._DEF_LANG);
            return dic.get(label) || def_val || '';
        }
        return def_val || '';
    };
    i18n.prototype.localize = function (elem) {
        var _this = this;
        this._localize(elem);
        __WEBPACK_IMPORTED_MODULE_3__dom__["a" /* default */].forEachChild(elem, function (child) {
            _this._localize(child);
        }, true);
        var trigger_event = !!this._lang && this._dictionaries.count() > 0;
        if (trigger_event) {
            _super.prototype.emit.call(this, i18n._EVENT_LOCALIZED, this._lang, this._dictionaries.get(this._lang));
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    i18n.prototype._changeLang = function (value) {
        var new_lang = !!value ? value.split('-')[0] : '';
        if (!!new_lang) {
            var lang_changed = (this._dictionaries.count() > 0) && (new_lang !== this._lang);
            if (lang_changed) {
                this._lang = new_lang;
                _super.prototype.emit.call(this, i18n._EVENT_CHANGE_LANG, this._lang, this._dictionaries.get(this._lang));
            }
        }
    };
    i18n.prototype._localize = function (elem) {
        if (!!elem && !!elem.hasAttribute) {
            var data_i18n = elem.getAttribute(i18n._ATTR_DATA_I18N) || '';
            if (!!data_i18n) {
                var value = this.get(data_i18n);
                if (!!value) {
                    // console.log("i18n._localize", data_i18n, value);
                    // ready to set i18n text or placeholder
                    if (__WEBPACK_IMPORTED_MODULE_3__dom__["a" /* default */].isInput(elem)) {
                        if (__WEBPACK_IMPORTED_MODULE_3__dom__["a" /* default */].isInputButton(elem)) {
                            __WEBPACK_IMPORTED_MODULE_3__dom__["a" /* default */].setValue(elem, value);
                        }
                        else if (elem.hasAttribute("placeholder")) {
                            elem.setAttribute("placeholder", value);
                        }
                    }
                    else {
                        elem.innerHTML = value;
                    }
                }
            }
        }
    };
    i18n.instance = function () {
        if (null == i18n.__instance) {
            i18n.__instance = new i18n();
        }
        return i18n.__instance;
    };
    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------
    i18n._EVENT_CHANGE_LANG = "on_change_lang";
    i18n._EVENT_LOCALIZED = "on_localized";
    i18n._DEF_LANG = "base";
    i18n._ATTR_DATA_I18N = "data-i18n";
    return i18n;
}(__WEBPACK_IMPORTED_MODULE_1__commons_events_EventEmitter__["a" /* default */]));
// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (i18n.instance());


/***/ }),
/* 13 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__random__ = __webpack_require__(4);

var BaseObject = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function BaseObject() {
        this._uid = __WEBPACK_IMPORTED_MODULE_0__random__["a" /* default */].uniqueId(BaseObject.PREFIX);
    }
    Object.defineProperty(BaseObject.prototype, "uid", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._uid;
        },
        enumerable: true,
        configurable: true
    });
    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------
    BaseObject.PREFIX = "lyts_object_";
    return BaseObject;
}());
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (BaseObject);


/***/ }),
/* 14 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__lang__ = __webpack_require__(1);

var objects = /** @class */ (function () {
    function objects() {
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    /**
     * Recursively goes through an object trying to resolve a path.
     * <code>
     *      console.log("name.value", ly.lang.get({"name":{"value":1}}, "name.value"));
     *      console.log("[name,value]", ly.lang.get({"name":{"value":1}}, ["name","value"]));
     *      console.log("length", ly.lang.get([1,2], ["length"]));
     * </code>
     * @param {Object} scope - The object to traverse (in each recursive call we dig into this object)
     * @param {string[]} path - An array of property names to traverse one-by-one
     * @param {number} [pathIndex=0] - The current index in the path array
     */
    objects.get = function (scope, path, pathIndex) {
        if (pathIndex === void 0) { pathIndex = 0; }
        if (typeof scope !== 'object' || scope === null || scope === undefined) {
            return '';
        }
        path = __WEBPACK_IMPORTED_MODULE_0__lang__["a" /* default */].isArray(path) ? path : path.split('.');
        var varName = path[pathIndex];
        var value = scope[varName];
        if (pathIndex === path.length - 1) {
            // It's a leaf, return whatever it is
            return value;
        }
        return objects.get(value, path, ++pathIndex);
    };
    objects.clone = function (obj) {
        var target = {};
        for (var field in obj) {
            if (obj.hasOwnProperty(field)) {
                target[field] = obj[field];
            }
        }
        return target;
    };
    objects.isEmpty = function (value) {
        if (!!value) {
            if (value.hasOwnProperty("length")) {
                return value.length === 0;
            }
            else {
                for (var key in value) {
                    if (value.hasOwnProperty(key)) {
                        return false; // not empty
                    }
                }
            }
        }
        return true;
    };
    objects.keys = function (value) {
        var result = [];
        for (var key in value) {
            if (value.hasOwnProperty(key)) {
                result.push(key);
            }
        }
        return result;
    };
    objects.values = function (value) {
        var result = [];
        for (var key in value) {
            if (value.hasOwnProperty(key)) {
                result.push(value[key]);
            }
        }
        return result;
    };
    return objects;
}());
/* harmony default export */ __webpack_exports__["a"] = (objects);


/***/ }),
/* 15 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return Router; });
/* unused harmony export Route */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return EVENT_ON_ROUTE; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__ly__ = __webpack_require__(0);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__commons_collections_Dictionary__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__commons_events_EventEmitter__ = __webpack_require__(5);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__commons_console__ = __webpack_require__(3);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();




var WILDCHAR = '*';
/**
 * Route wrapper.
 * Utility class to wrap route properties.
 */
var Route = /** @class */ (function () {
    function Route(route, handler) {
        this.path = route;
        this.handler = handler;
        this.tokens = this.tokenize(route);
        this.params = false;
    }
    Route.prototype.uid = function () {
        try {
            return __WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].lang.className(this.handler) + "." + __WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].objects.values(this.params).join('.');
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_3__commons_console__["a" /* default */].error("Route.uid", err);
        }
        return __WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].random.guid();
    };
    Route.prototype.isEmpty = function () {
        return !this.handler;
    };
    Route.prototype.endsWithWildchar = function () {
        return this.tokens[this.tokens.length - 1] === WILDCHAR;
    };
    Route.prototype.match = function (url) {
        try {
            var url_tokens = this.tokenize(url);
            if (url_tokens.length === this.tokens.length || this.endsWithWildchar()) {
                var params = this.mapTokens(url_tokens);
                if (!!params) {
                    this.params = params;
                    return true;
                }
            }
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_3__commons_console__["a" /* default */].error("Route.match", err);
        }
        return false;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    Route.prototype.tokenize = function (s) {
        var response = [];
        if (!!s) {
            var tokens = s.split('/');
            for (var _i = 0, tokens_1 = tokens; _i < tokens_1.length; _i++) {
                var token = tokens_1[_i];
                if (!!token) {
                    response.push(token);
                }
            }
        }
        return response;
    };
    Route.prototype.mapTokens = function (url_tokens) {
        var params = {};
        var count = 0;
        var found_wildchar = false;
        for (var i = 0; i < this.tokens.length; i++) {
            var route_token = this.tokens[i];
            var url_token = url_tokens[i];
            var route_token_is_param = route_token.indexOf(':') === 0; // starts with :
            var route_token_is_wildchar = route_token === WILDCHAR;
            found_wildchar = found_wildchar || route_token_is_wildchar;
            if (route_token !== url_token && !route_token_is_param && !route_token_is_wildchar) {
                break;
            }
            count++; // match found
            if (found_wildchar && this.endsWithWildchar()) {
                break;
            }
            if (route_token_is_param) {
                params[route_token.substring(1)] = url_token;
            }
        }
        // returns params if all matches count
        return (count === this.tokens.length) || (found_wildchar && this.endsWithWildchar())
            ? params : false;
    };
    return Route;
}());
// ------------------------------------------------------------------------
//                      c o n s t
// ------------------------------------------------------------------------
var _EVENT_POP_STATE = 'popstate';
var _EVENT_HASH_CHANGE = 'hashchange';
var _EMPTY = 'empty';
var _DEF_HASH = '#!';
var EVENT_ON_ROUTE = 'on_route'; // route found
var EMPTY_ROUTE = new Route("", null);
/**
 * Handle a simple routing between pages.
 *
 */
var Router = /** @class */ (function (_super) {
    __extends(Router, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function Router(root, hash) {
        if (root === void 0) { root = ''; }
        if (hash === void 0) { hash = _DEF_HASH; }
        var _this = _super.call(this) || this;
        _this._routes = new __WEBPACK_IMPORTED_MODULE_1__commons_collections_Dictionary__["a" /* Dictionary */]();
        _this._mode = __WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].browser.isPushStateAvailable() ? _EVENT_POP_STATE : __WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].browser.isHashChangeAvailable() ? _EVENT_HASH_CHANGE : _EMPTY;
        _this._native_listener = _this.onLocationChange.bind(_this);
        _this._hash = !!hash ? hash : _DEF_HASH;
        _this._use_hash = true;
        _this._root = root;
        _this._last_solved = false;
        _this._paused = false;
        _this._debug_mode = false;
        _this._initialized = false;
        _this.initialize();
        return _this;
    }
    Router.prototype.toString = function () {
        return JSON.stringify({
            uid: this.uid,
            root: this.root,
            hash: this.hash,
            useHash: this.useHash,
            paused: this.paused,
            routes: this._routes
        });
    };
    Object.defineProperty(Router.prototype, "root", {
        // ------------------------------------------------------------------------
        //                      p r o p e r t i e s
        // ------------------------------------------------------------------------
        get: function () {
            return this._root;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Router.prototype, "hash", {
        get: function () {
            return this._hash;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Router.prototype, "isSolved", {
        get: function () {
            return this._last_solved;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Router.prototype, "useHash", {
        get: function () {
            return this._use_hash;
        },
        set: function (value) {
            this._use_hash = value;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Router.prototype, "paused", {
        get: function () {
            return this._paused;
        },
        set: function (value) {
            this._paused = value;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Router.prototype, "debugMode", {
        get: function () {
            return this._debug_mode;
        },
        set: function (value) {
            this._debug_mode = value;
        },
        enumerable: true,
        configurable: true
    });
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    /**
     * START ROUTER
     */
    Router.prototype.start = function (elem) {
        if (!this._initialized) {
            this._initialized = true;
            this.initialize();
            this.startListen();
            this.resolve();
            this.relink(elem);
            this.debug("start", this);
        }
    };
    /**
     * STOP ROUTER
     */
    Router.prototype.stop = function () {
        if (this._initialized) {
            this.clear();
            // remove listeners
            this.stopListen();
            this._initialized = false;
        }
    };
    Router.prototype.relink = function (elem) {
        if (null != elem) {
            // ready to replace relative links adding current root
            this.replaceLinks(elem);
        }
    };
    Router.prototype.clear = function () {
        this._routes.clear();
    };
    /**
     * Register a rout handler
     * @param {string} path Route url. "page1/*", "/page1", "/page2/:id/:name"
     * @param {Page, Function} handler Route handle
     * @return {Router} this
     */
    Router.prototype.register = function (path, handler) {
        path = path || '/';
        var route = new Route(path, handler);
        if (this._routes.count() === 0) {
            // first page is also home page
            this._home_route = route;
        }
        this._routes.put(path, route);
        return this;
    };
    Router.prototype.goto = function (path) {
        if (!!window) {
            window.location.href = this._hash + path;
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    Router.prototype.debug = function (method_name) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        if (this._debug_mode) {
            __WEBPACK_IMPORTED_MODULE_3__commons_console__["a" /* default */].log.apply(__WEBPACK_IMPORTED_MODULE_3__commons_console__["a" /* default */], ["[" + this.uid + "] Router." + method_name].concat(args));
        }
    };
    Router.prototype.initialize = function () {
        if (!!this._root) {
            this._root = this._use_hash
                ? this.root.replace(/\/$/, '/' + this._hash)
                : this.root.replace(/\/$/, '');
        }
        else if (this._use_hash) {
            this._root = __WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].browser.location().split(this._hash)[0].replace(/\/$/, '/' + this._hash);
        }
    };
    Router.prototype.startListen = function () {
        if (__WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].browser.isReady()) {
            var event_name = this._mode;
            window.addEventListener(event_name, this._native_listener);
        }
        else {
            __WEBPACK_IMPORTED_MODULE_3__commons_console__["a" /* default */].warn("startListen", "Browser not Ready!");
        }
    };
    Router.prototype.stopListen = function () {
        if (__WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].browser.isReady()) {
            var event_name = this._mode;
            window.removeEventListener(event_name, this._native_listener);
        }
    };
    Router.prototype.onLocationChange = function () {
        this.debug('onLocationChange');
        if (!this.paused) {
            this.resolve();
        }
    };
    Router.prototype.getRoute = function (url, fallback, trace) {
        if (fallback === void 0) { fallback = EMPTY_ROUTE; }
        if (trace === void 0) { trace = false; }
        this.debug('getRoute', url);
        if (trace) {
            this._last_solved = false;
        }
        if (!this._routes.isEmpty() && !this.paused) {
            var paths = this._routes.keys();
            for (var _i = 0, paths_1 = paths; _i < paths_1.length; _i++) {
                var path = paths_1[_i];
                var route = this._routes.get(path);
                if (route.match(url)) {
                    this.debug('getRoute#found', route);
                    if (trace) {
                        this._last_solved = true;
                    }
                    return route;
                }
            }
            // NOT FOUND
            return fallback; // fallback page is always home page
        }
        return EMPTY_ROUTE; // empty route
    };
    // https://github.com/krasimir/navigo/blob/master/src/index.js
    Router.prototype.resolve = function (raw_path) {
        raw_path = !!raw_path ? Router.normalize(raw_path) : Router.normalize(__WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].browser.location());
        // remove root from url
        var url = raw_path.replace(this.root, '').replace(this.hash, '');
        var last_uid = !!this._last_route ? this._last_route.uid() : '';
        var route = this.getRoute(url, this._home_route, true);
        if (!route.isEmpty()) {
            var curr_uid = route.uid();
            //console.log("resolve", last_uid, curr_uid);
            if (last_uid === curr_uid) {
                // alredy routed
                return;
            }
            this._last_route = route;
            _super.prototype.emit.call(this, EVENT_ON_ROUTE, route);
        }
    };
    Router.prototype.replaceLinks = function (elem) {
        var native = elem.htmlElement;
        if (!!native) {
            var childs = __WEBPACK_IMPORTED_MODULE_0__ly__["a" /* default */].dom.get('[data-router=relative]', native);
            for (var i = 0; i < childs.length; i++) {
                var child = childs[i];
                var path = child.getAttribute('href') || '';
                if (!!path) {
                    var route = this.getRoute(path);
                    //if(!route.isEmpty()){
                    // CAN REPLACE
                    var new_path = this.root + (this.useHash ? this.hash : '/') + path;
                    child.setAttribute('href', new_path);
                    child.setAttribute('data-router', 'absolute');
                    this.debug('replaceLinks', '"' + path + '"', '"' + new_path + '"');
                    //}
                }
            }
        }
    };
    Router.normalize = function (s) {
        return s.replace(/\/+$/, '').replace(/^\/+/, '^/');
    };
    Router.instance = function () {
        if (null == Router.__instance) {
            Router.__instance = new Router();
        }
        return Router.__instance;
    };
    return Router;
}(__WEBPACK_IMPORTED_MODULE_2__commons_events_EventEmitter__["a" /* default */]));



/***/ }),
/* 16 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return Animate; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return AnimateEffect; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__StyleManager__ = __webpack_require__(33);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__lyts_core_commons_lang__ = __webpack_require__(1);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__animate_css__ = __webpack_require__(34);



/**
 * Effects
 *
 */
var AnimateEffect;
(function (AnimateEffect) {
    // ATTENTION SEEKER
    AnimateEffect["bouce"] = "bounce";
    AnimateEffect["flash"] = "flash";
    AnimateEffect["pulse"] = "pulse";
    AnimateEffect["rubberBand"] = "rubberBand";
    AnimateEffect["shake"] = "shake";
    AnimateEffect["headShake"] = "headShake";
    AnimateEffect["swing"] = "swing";
    AnimateEffect["tada"] = "tada";
    AnimateEffect["wobble"] = "wobble";
    AnimateEffect["jello"] = "jello";
    // BOUNCING ENTRANCES
    AnimateEffect["bounceIn"] = "bounceIn";
    AnimateEffect["bounceInDown"] = "bounceInDown";
    AnimateEffect["bounceInLeft"] = "bounceInLeft";
    AnimateEffect["bounceInRight"] = "bounceInRight";
    AnimateEffect["bounceInUp"] = "bounceInUp";
    // BOUNCING EXIT
    AnimateEffect["bounceOut"] = "bounceOut";
    AnimateEffect["bounceOutDown"] = "bounceOutDown";
    AnimateEffect["bounceOutLeft"] = "bounceOutLeft";
    AnimateEffect["bounceOutRight"] = "bounceOutRight";
    AnimateEffect["bounceOutUp"] = "bounceOutUp";
    // FADE ENTRANCES
    AnimateEffect["fadeIn"] = "fadeIn";
    AnimateEffect["fadeInDown"] = "fadeInDown";
    AnimateEffect["fadeInDownBig"] = "fadeInDownBig";
    AnimateEffect["fadeInLeft"] = "fadeInLeft";
    AnimateEffect["fadeInLeftBig"] = "fadeInLeftBig";
    AnimateEffect["fadeInRight"] = "fadeInRight";
    AnimateEffect["fadeInRightBig"] = "fadeInRightBig";
    AnimateEffect["fadeInUp"] = "fadeInUp";
    AnimateEffect["fadeInUpBig"] = "fadeInUpBig";
    // FADE EXIT
    AnimateEffect["fadeOut"] = "fadeOut";
    AnimateEffect["fadeOutDown"] = "fadeOutDown";
    AnimateEffect["fadeOutDownBig"] = "fadeOutDownBig";
    AnimateEffect["fadeOutLeft"] = "fadeOutLeft";
    AnimateEffect["fadeOutLeftBig"] = "fadeOutLeftBig";
    AnimateEffect["fadeOutRight"] = "fadeOutRight";
    AnimateEffect["fadeOutRightBig"] = "fadeOutRightBig";
    AnimateEffect["fadeOutUp"] = "fadeOutUp";
    AnimateEffect["fadeOutUpBig"] = "fadeOutUpBig";
    // FLIPPERS
    AnimateEffect["flipInX"] = "flipInX";
    AnimateEffect["flipInY"] = "flipInY";
    AnimateEffect["flipOutX"] = "flipOutX";
    AnimateEffect["flipOutY"] = "flipOutY";
    // LIGHTSPEED
    AnimateEffect["lightSpeedIn"] = "lightSpeedIn";
    AnimateEffect["lightSpeedOut"] = "lightSpeedOut";
    // ROTATING ENTRANCES
    AnimateEffect["rotateIn"] = "rotateIn";
    AnimateEffect["rotateInDownLeft"] = "rotateInDownLeft";
    AnimateEffect["rotateInDownRight"] = "rotateInDownRight";
    AnimateEffect["rotateInUpLeft"] = "rotateInUpLeft";
    AnimateEffect["rotateInUpRight"] = "rotateInUpRight";
    // ROTATING EXIT
    AnimateEffect["rotateOut"] = "rotateOut";
    AnimateEffect["rotateOutDownLeft"] = "rotateOutDownLeft";
    AnimateEffect["rotateOutDownRight"] = "rotateOutDownRight";
    AnimateEffect["rotateOutUpLeft"] = "rotateOutUpLeft";
    AnimateEffect["rotateOutUpRight"] = "rotateOutUpRight";
    // SLIDING ENTRANCES
    AnimateEffect["slideInDown"] = "slideInDown";
    AnimateEffect["slideInLeft"] = "slideInLeft";
    AnimateEffect["slideInRight"] = "slideInRight";
    AnimateEffect["slideInUp"] = "slideInUp";
    // SLIDING EXITS
    AnimateEffect["slideOutDown"] = "slideOutDown";
    AnimateEffect["slideOutLeft"] = "slideOutLeft";
    AnimateEffect["slideOutRight"] = "slideOutRight";
    AnimateEffect["slideOutUp"] = "slideOutUp";
    // ZOOM ENTRANCES
    AnimateEffect["zoomIn"] = "zoomIn";
    AnimateEffect["zoomInDown"] = "zoomInDown";
    AnimateEffect["zoomInLeft"] = "zoomInLeft";
    AnimateEffect["zoomInRight"] = "zoomInRight";
    AnimateEffect["zoomInUp"] = "zoomInUp";
    // ZOOM EXIT
    AnimateEffect["zoomOut"] = "zoomOut";
    AnimateEffect["zoomOutDown"] = "zoomOutDown";
    AnimateEffect["zoomOutLeft"] = "zoomOutLeft";
    AnimateEffect["zoomOutRight"] = "zoomOutRight";
    AnimateEffect["zoomOutUp"] = "zoomOutUp";
    // SPECIAL
    AnimateEffect["hinge"] = "hinge";
    AnimateEffect["jackInTheBox"] = "jackInTheBox";
    AnimateEffect["rollIn"] = "rollIn";
    AnimateEffect["rollOut"] = "rollOut";
})(AnimateEffect || (AnimateEffect = {}));
var AnimateClass = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function AnimateClass() {
        __WEBPACK_IMPORTED_MODULE_0__StyleManager__["a" /* StyleManager */]
            .register(__WEBPACK_IMPORTED_MODULE_0__StyleManager__["b" /* StyleModule */].animate, __WEBPACK_IMPORTED_MODULE_2__animate_css__["a" /* default */])
            .inject({}, __WEBPACK_IMPORTED_MODULE_0__StyleManager__["b" /* StyleModule */].animate);
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    AnimateClass.prototype.apply = function (effect, elem, callback, is_infinite) {
        this.applyEffect(effect, elem, callback, is_infinite);
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    AnimateClass.prototype.applyEffect = function (effect, elem, callback, is_infinite) {
        this.animate(elem, function () {
            elem.classRemove(effect);
            if (!!is_infinite) {
                elem.classRemove('infinite');
            }
            if (!!callback && __WEBPACK_IMPORTED_MODULE_1__lyts_core_commons_lang__["a" /* default */].isFunction(callback)) {
                __WEBPACK_IMPORTED_MODULE_1__lyts_core_commons_lang__["a" /* default */].funcInvoke(callback);
            }
        });
        elem.classAdd(effect);
        if (!!is_infinite) {
            elem.classAdd('infinite');
        }
    };
    AnimateClass.prototype.animate = function (elem, callback) {
        this.animationend(elem, function () {
            elem.classRemove('animated');
            __WEBPACK_IMPORTED_MODULE_1__lyts_core_commons_lang__["a" /* default */].funcInvoke(callback);
        });
        elem.classAdd('animated');
    };
    AnimateClass.prototype.animationend = function (elem, callback) {
        elem.addEventListener('animationend', function (e) {
            e.preventDefault();
            // remove listeners
            elem.removeEventListener('animationend');
            __WEBPACK_IMPORTED_MODULE_1__lyts_core_commons_lang__["a" /* default */].funcInvoke(callback);
        });
    };
    AnimateClass.instance = function () {
        if (null == AnimateClass.__instance) {
            AnimateClass.__instance = new AnimateClass();
        }
        return AnimateClass.__instance;
    };
    return AnimateClass;
}());
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
var Animate = AnimateClass.instance();



/***/ }),
/* 17 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
var IS_LOCALE = true;
var HOST_LOCALE = 'https://localhost:4199';
var HOST = 'https://api.conversacon.com:4199';
var HOST_APPLICATIONS_LOCALE = "http://localhost:63342/conversacon_dashboard";
var HOST_APPLICATIONS = "https://api.conversacon.com:8080";
var WS_HOST_LOCALE = 'wss://localhost:8181/websocket';
var WS_HOST = 'wss://api.conversacon.com:8181/websocket';
var constants = {
    uid: "test_upload",
    version: "1.0.1",
    // APP IDENTIFIER
    APP_TOKEN: "iuhdiu87w23ruh897dfyc2w3r",
    //-- events --//
    //-- host --//
    host: IS_LOCALE ? HOST_LOCALE : HOST,
    hostApplications: IS_LOCALE ? HOST_APPLICATIONS_LOCALE : HOST_APPLICATIONS,
    ws_host: IS_LOCALE ? WS_HOST_LOCALE : WS_HOST,
    download_file_url: (IS_LOCALE ? HOST_LOCALE : HOST) + '/api/file/get_file?app_token={0}&file_id={1}',
    server_namespace: 'com.drillio.server',
    //-- supported locales --//
    LOCALES: [
        {
            code: "it-IT",
            lang: "it",
            country: "IT",
            label: "Italiano"
        },
        {
            code: "en-GB",
            lang: "en",
            country: "GB",
            label: "English"
        }
    ],
    //-- VAR NAMES --//
    //-- URL PARAMETERS --//
    URL_PARAM_COMPANY_ID: 'company_id',
    // global scope vars
    FLD_VERSION_SERVER: "version_server",
    FLD_VERSION_CLIENT: "version_client",
    FLD_NETWORK_STATUS: "network_status",
    //-- GLOBAL VARIABLES --//
    GLOBAL_I18N: "_i18n",
    GLOBAL_AUTH: "_auth",
    GLOBAL_RUNNING_APP: "_running_app",
    GLOBAL_LOCALES: "_locales",
    GLOBAL_APPLICATION: "_application",
    //-- COMPONENTS STYLES --//
    STYLE_CHATBOX: "chatbox_style",
    STYLE_CARDS: "cards_style",
    //-- DEFAULT UI VALUES --//
    DEFAULT_UI_NAME: '',
    DEFAULT_UI_PAYLOAD: '',
    DEFAULT_UI_LOGO_PATH: '',
    DEFAULT_UI_WEBSITE: '',
    DEFAULT_UI_EMAIL: '',
    DEFAULT_UI_FACEBOOK: '',
    DEFAULT_UI_TWITTER: '',
    DEFAULT_UI_INSTAGRAM: '',
    DEFAULT_WRONG_LABEL: 'wrong label',
    DEBOUNCE_TIME_MS: 1000,
    DELAY_TIME_MS: 400,
    APP_MODAL_SELECTOR: "#_app_modal",
    UPLOAD: 'upload',
};
/* harmony default export */ __webpack_exports__["a"] = (constants);


/***/ }),
/* 18 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__ = __webpack_require__(0);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_commons_BaseObject__ = __webpack_require__(13);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__view_Main__ = __webpack_require__(24);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__vendor_lyts_core_commons_console__ = __webpack_require__(3);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__constants__ = __webpack_require__(17);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__i18n_en__ = __webpack_require__(41);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__i18n_it__ = __webpack_require__(42);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();







var CONTAINER = "#_app_container";
var launcher = /** @class */ (function (_super) {
    __extends(launcher, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function launcher() {
        var _this = _super.call(this) || this;
        _this.init();
        return _this;
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    launcher.prototype.start = function () {
        __WEBPACK_IMPORTED_MODULE_3__vendor_lyts_core_commons_console__["a" /* default */].log('launcher.start', 'version: ' + __WEBPACK_IMPORTED_MODULE_4__constants__["a" /* default */].version);
        this.loadMain();
    };
    /**
     * This method is expected to remove running app
     */
    launcher.prototype.remove = function () {
        try {
            if (!!this._main) {
                this._main.remove();
                __WEBPACK_IMPORTED_MODULE_3__vendor_lyts_core_commons_console__["a" /* default */].log("launcher.remove()", __WEBPACK_IMPORTED_MODULE_4__constants__["a" /* default */].version);
            }
            // remove local listeners, too
            __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].Application.events.off(this);
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_3__vendor_lyts_core_commons_console__["a" /* default */].error("launcher.remove()", err);
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    launcher.prototype.init = function () {
        // init application scope
        __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_commons_BaseObject__["a" /* default */].PREFIX = __WEBPACK_IMPORTED_MODULE_4__constants__["a" /* default */].uid + "_"; // application uid become component prefix.
        // init console
        __WEBPACK_IMPORTED_MODULE_3__vendor_lyts_core_commons_console__["a" /* default */].uid = __WEBPACK_IMPORTED_MODULE_4__constants__["a" /* default */].uid;
        // local i18n
        __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].Application.events.on(this, __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n.EVENT_CHANGE_LANG, this.onLocalChangeLang);
        // log current language
        __WEBPACK_IMPORTED_MODULE_3__vendor_lyts_core_commons_console__["a" /* default */].log("init", "lang=" + __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n.lang);
        this.initI18n();
    };
    launcher.prototype.loadMain = function () {
        this._main = new __WEBPACK_IMPORTED_MODULE_2__view_Main__["a" /* default */]();
        this._main.appendTo(CONTAINER);
    };
    launcher.prototype.onLocalChangeLang = function (lang) {
        __WEBPACK_IMPORTED_MODULE_3__vendor_lyts_core_commons_console__["a" /* default */].log("onLocalChangeLang", "lang=" + lang);
    };
    launcher.prototype.initI18n = function () {
        //-- load i18n dictionaries --//
        __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n.registerDefault(__WEBPACK_IMPORTED_MODULE_5__i18n_en__["a" /* default */]);
        __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n.register("en", __WEBPACK_IMPORTED_MODULE_5__i18n_en__["a" /* default */]);
        __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n.register("it", __WEBPACK_IMPORTED_MODULE_6__i18n_it__["a" /* default */]);
        // get start language from browser or from cookies
        __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n.lang = __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_ly__["a" /* default */].i18n.lang;
    };
    launcher.instance = function () {
        if (null == launcher.__instance) {
            launcher.__instance = new launcher();
        }
        return launcher.__instance;
    };
    return launcher;
}(__WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_commons_BaseObject__["a" /* default */]));
// ------------------------------------------------------------------------
//                      S T A R T   A P P L I C A T I O N
// ------------------------------------------------------------------------
launcher.instance().start();


/***/ }),
/* 19 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__lang__ = __webpack_require__(1);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__strings__ = __webpack_require__(11);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__objects__ = __webpack_require__(14);



/**
 * Utility class
 */
var format = /** @class */ (function () {
    function format() {
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    format.template = function (template, model) {
        if (model === void 0) { model = {}; }
        var VAR_MATCH_REGEX = /\{\{\s*(.*?)\s*\}\}/g;
        // don't touch the template if it is not a string
        if (typeof template !== 'string') {
            return template;
        }
        return template.replace(VAR_MATCH_REGEX, function (match, varName) {
            try {
                // defaultResolver never throws
                return __WEBPACK_IMPORTED_MODULE_0__lang__["a" /* default */].toString(__WEBPACK_IMPORTED_MODULE_2__objects__["a" /* default */].get(model, varName));
            }
            catch (e) {
                // if your resolver throws, we proceed with the default resolver
                return '';
            }
        });
    };
    format.date = function (date, locales, options) {
        try {
            if (!!locales) {
                if (__WEBPACK_IMPORTED_MODULE_0__lang__["a" /* default */].isArray(locales) || locales.length < 6) {
                    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/toLocaleString
                    return date.toLocaleString(locales, options);
                }
                else if (__WEBPACK_IMPORTED_MODULE_0__lang__["a" /* default */].isString(locales)) {
                    // uses pattern
                    var nm = format.getMonthName(date);
                    var nd = format.getDayName(date);
                    var f = locales;
                    f = f.replace(/yyyy/g, date.getFullYear() + "");
                    f = f.replace(/yy/g, String(date.getFullYear()).substr(2, 2));
                    f = f.replace(/MMM/g, nm.substr(0, 3).toUpperCase());
                    f = f.replace(/Mmm/g, nm.substr(0, 3));
                    f = f.replace(/MM\*/g, nm.toUpperCase());
                    f = f.replace(/Mm\*/g, nm);
                    f = f.replace(/mm/g, __WEBPACK_IMPORTED_MODULE_1__strings__["a" /* default */].fillLeft(String(date.getMonth() + 1), '0', 2));
                    f = f.replace(/DDD/g, nd.substr(0, 3).toUpperCase());
                    f = f.replace(/Ddd/g, nd.substr(0, 3));
                    f = f.replace(/DD\*/g, nd.toUpperCase());
                    f = f.replace(/Dd\*/g, nd);
                    f = f.replace(/dd/g, __WEBPACK_IMPORTED_MODULE_1__strings__["a" /* default */].fillLeft(String(date.getDate()), '0', 2));
                    f = f.replace(/d\*/g, date.getDate() + "");
                    return f;
                }
            }
            return date.toLocaleString([], options);
        }
        catch (err) {
        }
        return '';
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    format.getMonthName = function (date) {
        return date.toLocaleString().replace(/[^a-z]/gi, '');
    };
    format.getDayName = function (date) {
        switch (date.getDay()) {
            case 0:
                return 'Sunday';
            case 1:
                return 'Monday';
            case 2:
                return 'Tuesday';
            case 3:
                return 'Wednesday';
            case 4:
                return 'Thursday';
            case 5:
                return 'Friday';
            case 6:
                return 'Saturday';
        }
        return '';
    };
    return format;
}());
/* harmony default export */ __webpack_exports__["a"] = (format);


/***/ }),
/* 20 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__browser__ = __webpack_require__(6);

/**
 * Cookies Helper class
 */
var cookies = /** @class */ (function () {
    function cookies() {
    }
    cookies.create = function (name, value, days) {
        if (__WEBPACK_IMPORTED_MODULE_0__browser__["a" /* default */].isReady()) {
            var expires = '';
            if (days) {
                var date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                expires = '; expires=' + date.toUTCString();
            }
            else
                expires = '';
            document.cookie = name + '=' + value + expires + '; path=/';
        }
    };
    cookies.read = function (name, default_value) {
        if (default_value === void 0) { default_value = ''; }
        var nameEQ = name + '=';
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ')
                c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0)
                return c.substring(nameEQ.length, c.length);
        }
        return default_value;
    };
    /**
     * Remove a cookie
     * @param name Cookie name
     */
    cookies.erase = function (name) {
        if (__WEBPACK_IMPORTED_MODULE_0__browser__["a" /* default */].isReady()) {
            cookies.create(name, '', -1);
        }
    };
    /**
     * Remove all cookies
     */
    cookies.clear = function () {
        if (__WEBPACK_IMPORTED_MODULE_0__browser__["a" /* default */].isReady()) {
            var cookies_1 = document.cookie.split(";");
            for (var i = 0; i < cookies_1.length; i++) {
                var cookie = cookies_1[i];
                var eqPos = cookie.indexOf("=");
                var name_1 = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
                document.cookie = name_1 + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
            }
        }
    };
    return cookies;
}());
/* harmony default export */ __webpack_exports__["a"] = (cookies);


/***/ }),
/* 21 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* unused harmony export DEFAULT_REQUEST_OPTIONS */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return HttpClient; });
var DEFAULT_REQUEST_OPTIONS = {
    ignoreCache: false,
    headers: {
        Accept: 'application/json, text/javascript, text/plain, */*; q=0.01',
    },
    // default max duration for a request
    timeout: 5000,
};
var HttpClient = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function HttpClient() {
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    HttpClient.prototype.send = function (method, url, queryParams, body, options) {
        var _this = this;
        if (queryParams === void 0) { queryParams = {}; }
        if (body === void 0) { body = null; }
        if (options === void 0) { options = DEFAULT_REQUEST_OPTIONS; }
        var ignoreCache = options.ignoreCache || DEFAULT_REQUEST_OPTIONS.ignoreCache;
        var headers = options.headers || DEFAULT_REQUEST_OPTIONS.headers;
        var timeout = options.timeout || DEFAULT_REQUEST_OPTIONS.timeout;
        return new Promise(function (resolve, reject) {
            var xhr = new XMLHttpRequest();
            xhr.open(method, _this.withQuery(url, queryParams)); // open sync
            if (headers) {
                Object.keys(headers).forEach(function (key) { return xhr.setRequestHeader(key, headers[key]); });
            }
            if (ignoreCache) {
                xhr.setRequestHeader('Cache-Control', 'no-cache');
            }
            xhr.timeout = timeout || 5000;
            xhr.onload = function (evt) {
                resolve(_this.parseXHRResult(xhr));
            };
            xhr.onerror = function (evt) {
                reject(_this.errorResponse(xhr, 'Failed to make request.'));
            };
            xhr.ontimeout = function (evt) {
                reject(_this.errorResponse(xhr, 'Request took longer than expected.'));
            };
            if (method === 'post' && body) {
                xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.send(JSON.stringify(body));
            }
            else {
                xhr.send();
            }
        });
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    HttpClient.prototype.queryParams = function (params) {
        if (params === void 0) { params = {}; }
        return Object.keys(params)
            .map(function (k) { return encodeURIComponent(k) + '=' + encodeURIComponent(params[k]); })
            .join('&');
    };
    HttpClient.prototype.withQuery = function (url, params) {
        if (params === void 0) { params = {}; }
        var queryString = this.queryParams(params);
        return queryString ? url + (url.indexOf('?') === -1 ? '?' : '&') + queryString : url;
    };
    HttpClient.prototype.parseXHRResult = function (xhr) {
        return {
            ok: xhr.status >= 200 && xhr.status < 300,
            status: xhr.status,
            statusText: xhr.statusText,
            headers: xhr.getAllResponseHeaders(),
            data: xhr.responseText,
            json: function () {
                try {
                    return JSON.parse(xhr.responseText);
                }
                catch (err) {
                    return {};
                }
            },
        };
    };
    HttpClient.prototype.errorResponse = function (xhr, message) {
        if (message === void 0) { message = null; }
        return {
            ok: false,
            status: xhr.status,
            statusText: xhr.statusText,
            headers: xhr.getAllResponseHeaders(),
            data: message || xhr.statusText,
            json: function () {
                try {
                    return JSON.parse(message || xhr.statusText);
                }
                catch (err) {
                    return {};
                }
            },
        };
    };
    return HttpClient;
}());



/***/ }),
/* 22 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__commons_random__ = __webpack_require__(4);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__dom__ = __webpack_require__(7);


/**
 * Wrap a native HTMLElement to expose at Component methods.
 */
var ElementWrapper = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function ElementWrapper(owner, elem) {
        this._owner = owner;
        this._element = elem;
        this._hash_all();
    }
    Object.defineProperty(ElementWrapper.prototype, "htmlElement", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        /**
         * Warning: do not attach events to this object.
         * Use instead "addEventListener" method.
         * @return {HTMLElement}
         */
        get: function () {
            return this._element;
        },
        enumerable: true,
        configurable: true
    });
    ElementWrapper.prototype.hasElement = function () {
        return !!this._element;
    };
    ElementWrapper.prototype.remove = function () {
        if (!!this._element) {
            this._element.remove();
        }
    };
    ElementWrapper.prototype.appendChild = function (child) {
        if (!!this._element) {
            if (child instanceof ElementWrapper) {
                var elem = child;
                if (!!elem._element) {
                    this._element.appendChild(elem._element);
                }
            }
            else {
                this._element.appendChild(child);
            }
        }
    };
    ElementWrapper.prototype.appendTo = function (parent) {
        if (!!this._element && !!parent) {
            if (parent instanceof ElementWrapper) {
                parent.appendChild(this._element);
            }
            else {
                parent.appendChild(this._element);
            }
        }
    };
    Object.defineProperty(ElementWrapper.prototype, "innerHTML", {
        get: function () {
            if (!!this._element) {
                return this._element.innerHTML;
            }
            return '';
        },
        set: function (value) {
            if (!!this._element) {
                this._element.innerHTML = value;
            }
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "scrollTop", {
        get: function () {
            if (!!this._element) {
                return this._element.scrollTop;
            }
            return 0;
        },
        set: function (value) {
            if (!!this._element) {
                this._element.scrollTop = value;
            }
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "scrollLeft", {
        get: function () {
            if (!!this._element) {
                return this._element.scrollLeft;
            }
            return 0;
        },
        set: function (value) {
            if (!!this._element) {
                this._element.scrollLeft = value;
            }
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "scrollWidth", {
        get: function () {
            if (!!this._element) {
                return this._element.scrollWidth;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "scrollHeight", {
        get: function () {
            if (!!this._element) {
                return this._element.scrollHeight;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    ElementWrapper.prototype.scrollBy = function (x, y) {
        if (!!this._element) {
            return this._element.scrollBy(x, y);
        }
    };
    ElementWrapper.prototype.scrollTo = function (x, y) {
        if (!!this._element) {
            return this._element.scrollTo(x, y);
        }
    };
    Object.defineProperty(ElementWrapper.prototype, "offsetWidth", {
        get: function () {
            if (!!this._element) {
                return this._element.offsetWidth;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "offsetHeight", {
        get: function () {
            if (!!this._element) {
                return this._element.offsetHeight;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "offsetTop", {
        get: function () {
            if (!!this._element) {
                return this._element.offsetTop;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "clientWidth", {
        get: function () {
            if (!!this._element) {
                return this._element.clientWidth;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "clientHeight", {
        get: function () {
            if (!!this._element) {
                return this._element.clientHeight;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "clientLeft", {
        get: function () {
            if (!!this._element) {
                return this._element.clientLeft;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "clientTop", {
        get: function () {
            if (!!this._element) {
                return this._element.clientTop;
            }
            return 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ElementWrapper.prototype, "children", {
        get: function () {
            var _this = this;
            var response = [];
            if (!!this._element) {
                __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].forEachChild(this._element, function (elem) {
                    response.push(new ElementWrapper(_this._owner, elem));
                });
            }
            return response;
        },
        enumerable: true,
        configurable: true
    });
    ElementWrapper.prototype.addEventListener = function (event_name, listener) {
        if (null != this._element && !!this._owner) {
            var hash_code = ElementWrapper.hash(this._element);
            if (!!hash_code) {
                var selector = "[" + ElementWrapper.HASH_ATTRIBUTE + "=" + hash_code + "]";
                this._owner.addEventListener(selector, event_name, listener);
            }
        }
        else {
            if (!this._element) {
                console.error("ElementWrapper.addEventListener()", "Missing HTML Element.", this);
            }
            else {
                console.error("ElementWrapper.addEventListener()", "Component Owner.", this._element);
            }
        }
    };
    ElementWrapper.prototype.removeEventListener = function (event_names) {
        if (null != this._element && !!this._owner) {
            var hash_code = ElementWrapper.hash(this._element);
            if (!!hash_code) {
                var selector = "[" + ElementWrapper.HASH_ATTRIBUTE + "=" + hash_code + "]";
                this._owner.removeEventListener(selector, event_names);
            }
        }
        else {
            console.error("ElementWrapper.removeEventListener()", "Missing HTML Element or Component Owner.");
        }
    };
    ElementWrapper.prototype.classAdd = function (class_name) {
        return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].classAdd(this._element, class_name);
    };
    ElementWrapper.prototype.classRemove = function (class_name) {
        return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].classRemove(this._element, class_name);
    };
    ElementWrapper.prototype.hasAttribute = function (name) {
        if (!!this._element) {
            return this._element.hasAttribute(name);
        }
        return false;
    };
    ElementWrapper.prototype.setAttribute = function (name, value) {
        if (!!this._element) {
            this._element.setAttribute(name, value);
        }
    };
    ElementWrapper.prototype.getAttribute = function (name) {
        if (!!this._element) {
            return this._element.getAttribute(name) || "";
        }
        return "";
    };
    ElementWrapper.prototype.removeAttribute = function (name) {
        if (!!this._element) {
            this._element.removeAttribute(name);
        }
    };
    ElementWrapper.prototype.createAttribute = function (name) {
        if (!!this._element) {
            if (!this._element.hasAttribute(name)) {
                this._element.setAttributeNode(__WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].createAttribute(name));
            }
        }
    };
    ElementWrapper.prototype.value = function (value) {
        try {
            if (!!this._element) {
                if (value != undefined) {
                    __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].setValue(this._element, value);
                }
                return __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].getValue(this._element);
            }
        }
        catch (err) {
            console.error("ElementWrapper.value()", err);
        }
        return '';
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    ElementWrapper.prototype._hash_all = function () {
        if (null != this._element) {
            // events on root
            ElementWrapper.hash(this._element);
            // events on child
            __WEBPACK_IMPORTED_MODULE_1__dom__["a" /* default */].forEachChild(this._element, function (elem) {
                ElementWrapper.hash(elem);
            }, true);
        }
    };
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    ElementWrapper.hash = function (elem) {
        if (!!elem && !!elem.hasAttribute) {
            if (!elem.hasAttribute(ElementWrapper.HASH_ATTRIBUTE)) {
                var hash_code = __WEBPACK_IMPORTED_MODULE_0__commons_random__["a" /* default */].id();
                elem.setAttribute(ElementWrapper.HASH_ATTRIBUTE, hash_code);
            }
            return elem.getAttribute(ElementWrapper.HASH_ATTRIBUTE) || '';
        }
        return '';
    };
    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------
    ElementWrapper.HASH_ATTRIBUTE = "__hash__";
    return ElementWrapper;
}());
/* harmony default export */ __webpack_exports__["a"] = (ElementWrapper);


/***/ }),
/* 23 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__commons_events_Events__ = __webpack_require__(8);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__commons_collections_Dictionary__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__commons_BaseObject__ = __webpack_require__(13);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__view_i18n__ = __webpack_require__(12);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__commons_events_EventEmitter__ = __webpack_require__(5);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__commons_lang__ = __webpack_require__(1);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();






var ApplicationEvents = /** @class */ (function () {
    function ApplicationEvents() {
        this._listeners = new __WEBPACK_IMPORTED_MODULE_1__commons_collections_Dictionary__["a" /* Dictionary */]();
    }
    ApplicationEvents.prototype.on = function (scope, eventName, listener) {
        var key = ApplicationEvents.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new __WEBPACK_IMPORTED_MODULE_0__commons_events_Events__["a" /* default */]());
        }
        this._listeners.get(key).on(eventName, listener.bind(scope));
    };
    ApplicationEvents.prototype.once = function (scope, eventName, listener) {
        var key = ApplicationEvents.key(scope);
        if (!this._listeners.containsKey(key)) {
            this._listeners.put(key, new __WEBPACK_IMPORTED_MODULE_0__commons_events_Events__["a" /* default */]());
        }
        this._listeners.get(key).once(eventName, listener.bind(scope));
    };
    ApplicationEvents.prototype.off = function (scope, eventName) {
        var key = ApplicationEvents.key(scope);
        if (this._listeners.containsKey(key)) {
            this._listeners.get(key).off(eventName);
        }
    };
    ApplicationEvents.prototype.emit = function (eventName) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        var _a;
        var keys = this._listeners.keys();
        for (var _b = 0, keys_1 = keys; _b < keys_1.length; _b++) {
            var key = keys_1[_b];
            if (this._listeners.containsKey(key)) {
                (_a = this._listeners.get(key)).emit.apply(_a, [eventName].concat(args));
            }
        }
    };
    ApplicationEvents.prototype.clear = function () {
        var keys = this._listeners.keys();
        for (var _i = 0, keys_2 = keys; _i < keys_2.length; _i++) {
            var key = keys_2[_i];
            if (this._listeners.containsKey(key)) {
                this._listeners.get(key).clear();
            }
        }
    };
    ApplicationEvents.key = function (scope) {
        try {
            return scope.uid;
        }
        catch (err) {
            console.warn("ApplicationEvents.key()", "BINDING EVENT ON DEFAULT KEY!");
            return '_default';
        }
    };
    return ApplicationEvents;
}());
/**
 * Main Application Controller.
 * This is a singleton
 *
 * Events:
 * i18n.EVENT_CHANGE_LANG: Application propagates i18n EVENT_CHANGE_LANG for a centralized and
 * managed access to this event.
 *
 */
var Application = /** @class */ (function (_super) {
    __extends(Application, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function Application() {
        var _this = _super.call(this) || this;
        _this._events = new __WEBPACK_IMPORTED_MODULE_4__commons_events_EventEmitter__["a" /* default */]();
        _this._scope = new __WEBPACK_IMPORTED_MODULE_1__commons_collections_Dictionary__["a" /* Dictionary */]();
        _this.init();
        return _this;
    }
    Object.defineProperty(Application.prototype, "EVENT_CHANGE_LANG", {
        // ------------------------------------------------------------------------
        //                      p r o p e r t i e s
        // ------------------------------------------------------------------------
        get: function () {
            return Application._EVENT_CHANGE_LANG;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Application.prototype, "EVENT_LOCALIZED", {
        get: function () {
            return Application._EVENT_LOCALIZED;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Application.prototype, "events", {
        get: function () {
            return this._events;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Application.prototype, "scope", {
        get: function () {
            return this._scope;
        },
        enumerable: true,
        configurable: true
    });
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    Application.prototype.clear = function () {
        if (!!this._scope) {
            this._scope.clear();
        }
        if (!!this._events) {
            this._events.clear();
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    Application.prototype.init = function () {
        // i18n event (debounced to avoid multiple events)
        __WEBPACK_IMPORTED_MODULE_3__view_i18n__["a" /* default */].on(this, this.EVENT_CHANGE_LANG, __WEBPACK_IMPORTED_MODULE_5__commons_lang__["a" /* default */].funcDebounce(this, this.oni18nLangChange, 400, true));
        __WEBPACK_IMPORTED_MODULE_3__view_i18n__["a" /* default */].on(this, this.EVENT_LOCALIZED, __WEBPACK_IMPORTED_MODULE_5__commons_lang__["a" /* default */].funcDebounce(this, this.oni18nLocalized, 400, true));
    };
    Application.prototype.oni18nLangChange = function (lang, dictionary) {
        this.events.emit(this.EVENT_CHANGE_LANG, lang, dictionary);
    };
    Application.prototype.oni18nLocalized = function (lang, dictionary) {
        this.events.emit(this.EVENT_LOCALIZED, lang, dictionary);
    };
    Application.instance = function () {
        if (null == Application.__instance) {
            Application.__instance = new Application();
        }
        return Application.__instance;
    };
    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------
    Application._EVENT_CHANGE_LANG = __WEBPACK_IMPORTED_MODULE_3__view_i18n__["a" /* default */].EVENT_CHANGE_LANG;
    Application._EVENT_LOCALIZED = __WEBPACK_IMPORTED_MODULE_3__view_i18n__["a" /* default */].EVENT_LOCALIZED;
    return Application;
}(__WEBPACK_IMPORTED_MODULE_2__commons_BaseObject__["a" /* default */]));
/* harmony default export */ __webpack_exports__["a"] = (Application.instance());


/***/ }),
/* 24 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_commons_console__ = __webpack_require__(3);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_view_screens_ScreenController__ = __webpack_require__(25);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_ly__ = __webpack_require__(0);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__controllers_ApplicationController__ = __webpack_require__(26);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__view__ = __webpack_require__(27);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__globals__ = __webpack_require__(10);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__screens_main_ScreenMain__ = __webpack_require__(29);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();







/**
 * Main Screen controller.
 * Register here all application Screens
 */
var Main = /** @class */ (function (_super) {
    __extends(Main, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function Main() {
        var _this = _super.call(this, "") || this;
        //-- main screen --//
        _super.prototype.register.call(_this, '/main', __WEBPACK_IMPORTED_MODULE_6__screens_main_ScreenMain__["a" /* default */]);
        _this._body = _super.prototype.getFirst.call(_this, "#" + _this.uid + "_content");
        _this._loader = __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_ly__["a" /* default */].dom.getFirst("#_app_loader");
        return _this;
    }
    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------
    Main.prototype.render = function () {
        return Object(__WEBPACK_IMPORTED_MODULE_4__view__["a" /* default */])(this.uid, {});
    };
    Main.prototype.free = function () {
        _super.prototype.free.call(this);
        // release memory
        __WEBPACK_IMPORTED_MODULE_3__controllers_ApplicationController__["a" /* default */].off(false, this);
        __WEBPACK_IMPORTED_MODULE_3__controllers_ApplicationController__["a" /* default */].off(true, this);
    };
    Main.prototype.ready = function () {
        _super.prototype.ready.call(this);
        try {
            this.init();
            if (!!this._loader) {
                this._loader.remove();
            }
            else {
                __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_commons_console__["a" /* default */].warn('ready', 'this._loader is null.');
            }
            // console.log("loader", this._loader);
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_commons_console__["a" /* default */].error("ready", err);
        }
    };
    Main.prototype.show = function () {
    };
    Main.prototype.hide = function () {
    };
    Main.prototype.route = function (screen) {
        screen.appendTo(this._body);
    };
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    Main.prototype.init = function () {
        try {
            // event handlers
            this.initHandlers();
            // remove this logs in real application
            __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_commons_console__["a" /* default */].log("LAUNCHED!");
            __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_commons_console__["a" /* default */].log("LOCALES: ", __WEBPACK_IMPORTED_MODULE_5__globals__["a" /* default */].locales);
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_commons_console__["a" /* default */].error("Main.init", err);
        }
    };
    Main.prototype.initHandlers = function () {
    };
    return Main;
}(__WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_view_screens_ScreenController__["a" /* default */]));
/* harmony default export */ __webpack_exports__["a"] = (Main);


/***/ }),
/* 25 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__Router__ = __webpack_require__(15);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__components_Component__ = __webpack_require__(9);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__ly__ = __webpack_require__(0);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();



/**
 * Control screens.
 * Screens can contain Pages.
 * Sample screen url: ./#!screen1
 * Sample screen url with page: ./#!screen1/page1 or ./#!screen1/page2
 */
var ScreenController = /** @class */ (function (_super) {
    __extends(ScreenController, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function ScreenController(root, hash) {
        if (hash === void 0) { hash = ''; }
        var _this = _super.call(this) || this;
        _this._router = new __WEBPACK_IMPORTED_MODULE_0__Router__["b" /* Router */](root, hash);
        _this._router.on(_this, __WEBPACK_IMPORTED_MODULE_0__Router__["a" /* EVENT_ON_ROUTE */], _this.onRoute);
        return _this;
    }
    ScreenController.prototype.free = function () {
        this._router.stop();
        if (!!this._last_screen) {
            this._last_screen.remove();
        }
    };
    ScreenController.prototype.ready = function () {
        this._router.start(this.element);
        this._init();
    };
    Object.defineProperty(ScreenController.prototype, "root", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._router.root;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ScreenController.prototype, "isSolved", {
        get: function () {
            return this._router.isSolved;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ScreenController.prototype, "paused", {
        get: function () {
            return this._router.paused;
        },
        set: function (value) {
            this._router.paused = value;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ScreenController.prototype, "debugMode", {
        get: function () {
            return this._router.debugMode;
        },
        set: function (value) {
            this._router.debugMode = value;
        },
        enumerable: true,
        configurable: true
    });
    ScreenController.prototype.register = function (route, handler) {
        this._router.register(route + '/*', handler);
    };
    ScreenController.prototype.current = function () {
        return this._last_screen;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    ScreenController.prototype._init = function () {
    };
    ScreenController.prototype.onRoute = function (route) {
        try {
            var params = route.params;
            var func = route.handler;
            if (__WEBPACK_IMPORTED_MODULE_2__ly__["a" /* default */].lang.isFunction(func)) {
                if (__WEBPACK_IMPORTED_MODULE_2__ly__["a" /* default */].lang.isConstructor(route.handler)) {
                    // close last page
                    var last_page_1 = this._last_screen;
                    if (!!last_page_1) {
                        last_page_1.hide();
                        __WEBPACK_IMPORTED_MODULE_2__ly__["a" /* default */].lang.funcDelay(function () {
                            last_page_1.remove();
                        }, 400);
                    }
                    this._last_route = route;
                    this._last_screen = new func(this.root, route); // screen ctr
                    if (!this._last_screen.isSolved) {
                        this._last_screen.show();
                        this.route(this._last_screen);
                    }
                }
                else {
                    // we have a callback
                    func(params);
                }
            }
        }
        catch (err) {
            console.error("ScreenController.onRoute", err);
        }
    };
    return ScreenController;
}(__WEBPACK_IMPORTED_MODULE_1__components_Component__["a" /* default */]));
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (ScreenController);


/***/ }),
/* 26 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__globals__ = __webpack_require__(10);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_ly__ = __webpack_require__(0);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__ = __webpack_require__(3);



/**
 * Main Application Controller.
 * Use "ready" method to get Application Context.
 */
var ApplicationController = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function ApplicationController() {
    }
    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    /**
     * Emit global or local Application event
     */
    ApplicationController.prototype.emit = function (global, event_name) {
        var args = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            args[_i - 2] = arguments[_i];
        }
        var _a, _b;
        try {
            if (global) {
                (_a = __WEBPACK_IMPORTED_MODULE_0__globals__["a" /* default */].Application.events).emit.apply(_a, [event_name].concat(args));
            }
            else {
                (_b = __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_ly__["a" /* default */].Application.events).emit.apply(_b, [event_name].concat(args));
            }
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].error('ApplicationController.emit()', err);
        }
    };
    ApplicationController.prototype.on = function (global, scope, event_name, listener) {
        try {
            if (global) {
                __WEBPACK_IMPORTED_MODULE_0__globals__["a" /* default */].Application.events.on(scope, event_name, listener);
            }
            else {
                __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_ly__["a" /* default */].Application.events.on(scope, event_name, listener);
            }
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].error('ApplicationController.on()', err);
        }
    };
    ApplicationController.prototype.off = function (global, scope, event_name) {
        try {
            if (global) {
                __WEBPACK_IMPORTED_MODULE_0__globals__["a" /* default */].Application.events.off(scope, event_name);
            }
            else {
                __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_ly__["a" /* default */].Application.events.off(scope, event_name);
            }
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].error('ApplicationController.on()', err);
        }
    };
    ApplicationController.instance = function () {
        if (null == ApplicationController.__instance) {
            ApplicationController.__instance = new ApplicationController();
        }
        return ApplicationController.__instance;
    };
    return ApplicationController;
}());
// ------------------------------------------------------------------------
//                      e x p o r t
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (ApplicationController.instance());


/***/ }),
/* 27 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = view;
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__style__ = __webpack_require__(28);

function view(uid, props) {
    props = props || {};
    return "\n            <section id=\"" + uid + "\" class=\"page --unscrollable\">\n                " + Object(__WEBPACK_IMPORTED_MODULE_0__style__["a" /* default */])(uid, props) + "\n   \n                <section id=\"" + uid + "_content\" class=\"--scrollable\">\n                    \n                \n                    <!--\n                        CompMain WILL LOAD HERE ITS CONTENTS\n                    -->\n                    \n                    <!--\n                        EVENTUAL SECONDARY PAGES WILL BE APPENDED HERE\n                    -->\n                \n                </section>\n         \n            </section>\n\n        ";
}


/***/ }),
/* 28 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = style;
function style(uid, props) {
    // SMALL
    var main = "\n        <style>\n            \n            :root{\n                --topnav-background:#ee6e73;\n                --topnav-color:#fff;\n                --topnav-height:4rem;     \n                --topnav-title-size:2.6rem;                                 \n                --topnav-title-size-smaller:2rem;\n                --topnav-box-shadow:0 2px 2px 0 rgba(0, 0, 0, 0.14), 0 3px 1px -2px rgba(0, 0, 0, 0.12), 0 1px 5px 0 rgba(0, 0, 0, 0.2);\n                \n                --sidenav-background-1:#212121;\n                --sidenav-background-2:#616161;  \n                \n                --color-grey-lighten-2:#e0e0e0; \n                --color-grey-lighten-1:#bdbdbd;\n                --color-grey:#9e9e9e;\n                --color-grey-darken-2:#616161;\n                \n                --color-yellow-lighten-3:#fff59d;\n                --color-yellow-lighten-4:#fff9c4;\n                --color-red-lighten-1:#ef5350;\n                --color-red-lighten-2:#e57373;\n                --color-red-lighten-3:#ef9a9a;\n                --color-red-lighten-4:#ffcdd2;\n                --color-red-darken-4:#b71c1c;\n                --color-green-lighten-1:#66bb6a;\n                --color-green-lighten-2:#81c784;\n                --color-green-lighten-3:#a5d6a7;\n                --color-green-lighten-4:#c8e6c9;\n                --color-green-darken-4:#1b5e20;\n                --color-blue-lighten-3:#90caf9;\n                                                \n                --opacity-not-hover:0.7;\n                --opacity-hover:1;                         \n            }\n            \n            h1{\n                margin: 0;\n            }\n                                                            \n            #" + uid + "{\n            \n            } \n            \n            /* materializecss overwrite */\n            .tabs-content.carousel.carousel-slider{\n                height: auto;\n            }                \n            \n            .tabs-content.carousel.carousel-slider .carousel-item.active{\n                position: relative;\n            }   \n            \n            \n            \n            .drillio-background-problem{            \n                background-color: var(--color-red-lighten-3);\n            }\n            \n            .drillio-background-ok{\n                background-color: var(--color-green-lighten-3);\n            }\n            \n            .drillio-cursor-pointer{\n                cursor: pointer;\n            }\n            \n            \n            /** PAGE **/\n            .drillio-page{\n                display: flex;\n                flex-direction: column;                     \n            }\n            \n            /** PAGE TOP NAVIGATION */\n            .drillio-page-topnav{\n                display: flex;      \n                justify-content: space-between;\n                align-items: center;                                          \n                height: var(--topnav-height);   \n            }\n            \n            .drillio-page-topnav-shadow{\n                display: flex;      \n                justify-content: space-between;\n                align-items: center;                                          \n                height: var(--topnav-height);   \n                box-shadow: var(--topnav-box-shadow);\n            }\n                                    \n            .drillio-page-topnav-logo{\n                margin-left: 1.5rem;\n                height: 3rem;\n                width: auto;\n                cursor: pointer;\n            }\n            \n            .drillio-page-topnav__title{\n                flex: 1 1;                            \n            }\n            \n            .drillio-page-topnav__title h1{\n                width: 100%;\n                text-align: center;      \n                font-size: var(--topnav-title-size);  \n            }\n            \n            .drillio-page-topnav__back{\n                margin-left: 1.5rem;\n            }\n            \n                                    \n            /** PAGE CONTENT **/\n            .drillio-page-content{\n                height: calc(100vh - var(--topnav-height));               \n                max-height: calc(100vh - var(--topnav-height));\n                padding-top: 1rem;\n                overflow-y: auto;     \n            }\n            \n            .drillio-page-content .container{\n                height: 100%;\n            }\n            \n            /** PAGE MAIN **/\n            .drillio-page-main{   \n                display: flex;                     \n                flex-direction: column;                 \n                min-height: 100%;      \n                width: 70%;  \n                margin: 0 auto;                                                                      \n            }\n            \n            .drillio-page-main__border{\n                border-radius: 3px 3px 0 0;\n                border-bottom: none;\n                box-shadow: 1px 1px 3px 0 rgba(0,0,0,0.2),-1px 0 3px 0 rgba(0,0,0,0.2);\n            }\n            \n            /** PAGE SECTION **/ \n            .drillio-page-section{\n                display: flex;\n                flex-direction: column;\n                padding: 1.5rem;\n            }         \n                                                            \n            .drillio-page-section-data{\n                padding: 1.2rem 0.5rem 0 0.5rem;\n            }  \n            \n            .drillio-page-section-data__item{\n                display: flex;   \n                align-items: center;\n                max-width: 100%;    \n                min-width: 0;                                                                        \n            }  \n            \n            .drillio-page-section-data__item:not(:last-child){\n                margin-bottom: 1.5rem;            \n            }\n                                    \n            .drillio-page-section-data__item-label{\n                font-size: 0.9rem;  \n                word-wrap: break-word;    \n                overflow: hidden;                      \n                flex: 0 0 100px;\n                margin-right: 1rem;        \n                /**align-self: flex-start;**/ \n                padding-bottom: 2px;       \n            }\n            \n            .drillio-page-section-data__item-value{\n                font-size: 1rem;  \n                word-wrap: break-word;  \n                overflow: hidden;              \n                margin: 0; \n                padding-bottom: 4px;\n                line-height: 1.5;\n                width: 100%;\n                min-width: 0;\n            }\n                        \n            .drillio-page-section-data__item-name{\n                font-size: 1.8rem;  \n                word-wrap: break-word;   \n                overflow: hidden;             \n                margin: 0; \n                padding-bottom: 4px;\n                width: 100%;\n                min-width: 0;\n            }\n            \n            /** MODAL EDIT **/\n            .drillio-modal-edit-header{\n                display: flex;\n                justify-content: space-between;\n                align-items: center;\n            }\n            \n            .drillio-modal-edit-header h2{\n                margin: 0;\n                font-size: 1.8rem;   \n                word-break: break-word;         \n            }\n            \n            .drillio-modal-edit-header i{\n                font-size: 1.8rem;\n            }\n            \n            .drillio-modal-edit-header i:hover,\n            .drillio-modal-edit-header i:active{\n                cursor: pointer;            \n            }\n            \n            .drillio-modal-edit-body{\n                 padding: 1rem;\n            }\n            \n            .drillio-modal-edit-body .input-field{\n                margin-top: 2.5rem;\n            }\n                                                                                                                    \n            /** NO ITEMS HELP **/\n            .drillio-no-items-help{\n                font-size: 1.1rem;\n                color: var(--color-grey-darken-2);\n                line-height: 1.5;\n                word-wrap: break-word;  \n                overflow: hidden;                                            \n            }\n            \n            \n            \n                        \n        </style>      \n    ";
    var ex_large = "\n        <style>\n            @media only screen and (min-width: 1201px) {\n                                           \n            }\n        </style>        \n    ";
    var large = "\n        <style>\n            @media only screen and (min-width: 993px) and (max-width: 1200px) {   \n                /** PAGE MAIN **/                                                                                                                                              \n                .drillio-page-main{\n                    width: 85%;\n                }                               \n            }\n        </style>        \n    ";
    var medium = "\n        <style>\n            @media only screen and (min-width: 601px) and (max-width: 992px) {     \n                \n                /** PAGE MAIN **/                                                                                                                                              \n                .drillio-page-main{\n                    width: 90%;\n                }   \n                                   \n            }\n        </style>        \n    ";
    var small_ex_small = "\n        <style>\n            @media only screen and (max-width: 600px) {\n                    \n                /** TOP NAVIGATION */    \n            \n                .drillio-page-topnav__title{\n                    margin-right: 0.5rem;\n                }                \n            \n                .drillio-page-topnav__title h1{\n                    text-align: right;      \n                    font-size: var(--topnav-title-size-smaller);       \n                }  \n                \n                .drillio-page-topnav__back{\n                    margin-left: 0.5rem;\n                } \n                \n                /** PAGE MAIN **/                                                                                                                                              \n                .drillio-page-main{\n                    width: 100%;\n                } \n                \n                .drillio-page-section-data__item{                    \n                    flex-direction: column; \n                    align-items: flex-start;                                                      \n                }      \n                \n                .drillio-page-section-data__item-label{\n                    align-self: auto;\n                    flex: 1 1 auto;\n                    margin-bottom: 0.5rem;\n                    margin-right: 0;\n                }                                                                                                             \n            }\n        </style>        \n    ";
    var small = "\n        <style>\n            @media only screen and (min-width: 481px) and (max-width: 600px) {\n                  \n            }\n        </style>        \n    ";
    var ex_small = "\n        <style>\n            @media only screen and (max-width: 480px) {                                                                   \n            }\n        </style>        \n    ";
    return "\n        " + main + "\n        " + ex_large + "           \n        " + large + "\n        " + medium + "\n        " + small_ex_small + "\n        " + small + "\n        " + ex_small + "\n           \n    ";
}
;


/***/ }),
/* 29 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_view_screens_screen_Screen__ = __webpack_require__(30);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_style_styles_animate_Animate__ = __webpack_require__(16);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__ = __webpack_require__(3);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__view__ = __webpack_require__(35);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__components_upload_CompUpload__ = __webpack_require__(37);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();





var ScreenMain = /** @class */ (function (_super) {
    __extends(ScreenMain, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function ScreenMain(root, route) {
        var _this = _super.call(this, root, route) || this;
        _this.debugMode = true;
        _this._upload_box = _super.prototype.getFirst.call(_this, "#" + _this.uid + "_upload_box");
        return _this;
    }
    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------
    ScreenMain.prototype.route = function (page) {
    };
    ScreenMain.prototype.render = function () {
        return Object(__WEBPACK_IMPORTED_MODULE_3__view__["a" /* default */])(this.uid, {});
    };
    ScreenMain.prototype.free = function () {
        // release memory
        this.removeUploadCmp();
        __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].log('ScreenMain.free()', this.uid);
    };
    ScreenMain.prototype.ready = function () {
        _super.prototype.ready.call(this);
        this.init();
    };
    ScreenMain.prototype.show = function () {
        _super.prototype.show.call(this);
        __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_style_styles_animate_Animate__["a" /* Animate */].apply(__WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_style_styles_animate_Animate__["b" /* AnimateEffect */].slideInDown, this.element, function () {
            __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].log('ScreenMain.show', __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_style_styles_animate_Animate__["b" /* AnimateEffect */].slideInDown + ' animation terminated');
        });
    };
    ScreenMain.prototype.hide = function () {
        _super.prototype.hide.call(this);
    };
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    ScreenMain.prototype.init = function () {
        try {
            this.createUploadCmp();
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].error("ScreenMain.init", err);
        }
    };
    ScreenMain.prototype.createUploadCmp = function () {
        try {
            if (!this._upload_cmp) {
                this._upload_cmp = new __WEBPACK_IMPORTED_MODULE_4__components_upload_CompUpload__["a" /* default */]('7cb4bdb399c44ee3b6be874041e4f14a');
                this._upload_cmp.appendTo(this._upload_box);
            }
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].error("ScreenMain.createUploadCmp", err);
        }
    };
    ScreenMain.prototype.removeUploadCmp = function () {
        try {
            if (!!this._upload_cmp) {
                this._upload_cmp.off(this);
                this._upload_cmp.remove();
            }
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_2__vendor_lyts_core_commons_console__["a" /* default */].error("ScreenMain.removeUploadCmp", err);
        }
    };
    return ScreenMain;
}(__WEBPACK_IMPORTED_MODULE_0__vendor_lyts_core_view_screens_screen_Screen__["a" /* default */]));
/* harmony default export */ __webpack_exports__["a"] = (ScreenMain);


/***/ }),
/* 30 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__commons_console__ = __webpack_require__(3);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__pages_PageController__ = __webpack_require__(31);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__commons_paths__ = __webpack_require__(32);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();



var Screen = /** @class */ (function (_super) {
    __extends(Screen, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function Screen(root, route) {
        var _this = _super.call(this, root) || this;
        _this._parent_route = route;
        try {
            _this._name = _this.uid;
            if (!!route) {
                _this._params = route.params;
                _this._name = route.uid();
            }
        }
        catch (err) {
            __WEBPACK_IMPORTED_MODULE_0__commons_console__["a" /* default */].error("Screen.constructor", err);
        }
        return _this;
    }
    Screen.prototype.ready = function () {
        this.start();
    };
    Screen.prototype.register = function (route, handler) {
        _super.prototype.register.call(this, this.concatParent(route), handler);
    };
    Screen.prototype.show = function () {
        this.paused = false;
        this.element.classRemove('hide');
    };
    Screen.prototype.hide = function () {
        this.paused = true; // pause page controller
        this.element.classAdd('hide');
    };
    Object.defineProperty(Screen.prototype, "name", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._name;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Screen.prototype, "params", {
        /**
         * Return url parameters if any
         */
        get: function () {
            return !!this._params ? this._params : false;
        },
        enumerable: true,
        configurable: true
    });
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    Screen.prototype.parentPath = function () {
        var parent_path = this._parent_route.path;
        if (parent_path.indexOf('*') > -1) {
            return parent_path.substring(0, parent_path.length - 2);
        }
        return parent_path;
    };
    Screen.prototype.concatParent = function (path) {
        var parent_path = this.parentPath();
        return __WEBPACK_IMPORTED_MODULE_2__commons_paths__["a" /* default */].concat(parent_path, path);
    };
    return Screen;
}(__WEBPACK_IMPORTED_MODULE_1__pages_PageController__["a" /* default */]));
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (Screen);


/***/ }),
/* 31 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__Router__ = __webpack_require__(15);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__components_Component__ = __webpack_require__(9);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__ly__ = __webpack_require__(0);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();



var PageController = /** @class */ (function (_super) {
    __extends(PageController, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function PageController(root, hash) {
        if (hash === void 0) { hash = ''; }
        var _this = _super.call(this) || this;
        _this._router = new __WEBPACK_IMPORTED_MODULE_0__Router__["b" /* Router */](root, hash);
        _this._router.on(_this, __WEBPACK_IMPORTED_MODULE_0__Router__["a" /* EVENT_ON_ROUTE */], _this.onRoute);
        return _this;
    }
    PageController.prototype.free = function () {
        this._router.stop();
        if (!!this._last_page) {
            this._last_page.remove();
        }
    };
    PageController.prototype.start = function () {
        this._router.start(this.element);
    };
    Object.defineProperty(PageController.prototype, "root", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._router.root;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(PageController.prototype, "isSolved", {
        get: function () {
            return this._router.isSolved;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(PageController.prototype, "paused", {
        get: function () {
            return this._router.paused;
        },
        set: function (value) {
            this._router.paused = value;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(PageController.prototype, "debugMode", {
        get: function () {
            return this._router.debugMode;
        },
        set: function (value) {
            this._router.debugMode = value;
        },
        enumerable: true,
        configurable: true
    });
    PageController.prototype.register = function (route, handler) {
        this._router.register(route, handler);
    };
    PageController.prototype.current = function () {
        return this._last_page;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    PageController.prototype.onRoute = function (route) {
        try {
            var params = route.params;
            var func = route.handler;
            if (__WEBPACK_IMPORTED_MODULE_2__ly__["a" /* default */].lang.isFunction(func)) {
                if (__WEBPACK_IMPORTED_MODULE_2__ly__["a" /* default */].lang.isConstructor(route.handler)) {
                    // close last page
                    var last_page_1 = this._last_page;
                    if (!!last_page_1) {
                        last_page_1.hide();
                        __WEBPACK_IMPORTED_MODULE_2__ly__["a" /* default */].lang.funcDelay(function () {
                            last_page_1.remove();
                        }, 400);
                    }
                    this._last_route = route;
                    this._last_page = new func(route);
                    this._last_page.show();
                    this._router.relink(this._last_page.element);
                    this.route(this._last_page);
                }
                else {
                    // we have a callback
                    func(params);
                }
            }
        }
        catch (err) {
            console.error("PageController.onRoute", err);
        }
    };
    return PageController;
}(__WEBPACK_IMPORTED_MODULE_1__components_Component__["a" /* default */]));
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
/* harmony default export */ __webpack_exports__["a"] = (PageController);


/***/ }),
/* 32 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
var paths = /** @class */ (function () {
    function paths() {
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    paths.concat = function () {
        var args = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            args[_i] = arguments[_i];
        }
        var separator = '/';
        var replace = new RegExp(separator + '{1,}', 'g');
        return args.join(separator).replace(replace, separator);
    };
    return paths;
}());
/* harmony default export */ __webpack_exports__["a"] = (paths);


/***/ }),
/* 33 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return StyleManager; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return StyleModule; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__lyts_core_ly__ = __webpack_require__(0);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__lyts_core_commons_collections_Dictionary__ = __webpack_require__(2);


/**
 * Supported modules
 */
var StyleModule;
(function (StyleModule) {
    StyleModule["animate"] = "animate";
})(StyleModule || (StyleModule = {}));
/**
 * Singleton manager for application styles.
 * StyleManager allow to inject styles into html head
 */
var StyleManagerClass = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function StyleManagerClass() {
        this._registered_modules = new __WEBPACK_IMPORTED_MODULE_1__lyts_core_commons_collections_Dictionary__["a" /* Dictionary */]();
        this._use_one_style_tag = false;
        this._hystory = [];
    }
    Object.defineProperty(StyleManagerClass.prototype, "useOneStyleTag", {
        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------
        get: function () {
            return this._use_one_style_tag;
        },
        set: function (value) {
            this._use_one_style_tag = value;
        },
        enumerable: true,
        configurable: true
    });
    /**
     * Register a style module content or proxy (function called when content is required)
     * @param {string} module Module name
     * @param {string | Function} proxy CSS Content or function reference
     */
    StyleManagerClass.prototype.register = function (module, proxy) {
        this._registered_modules.put(module, proxy);
        return this;
    };
    /**
     * Inject style directly to head
     * @param props
     * @param {StyleModule} modules
     */
    StyleManagerClass.prototype.inject = function (props) {
        var modules = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            modules[_i - 1] = arguments[_i];
        }
        if (this._use_one_style_tag) {
            this.injectOne.apply(this, [props].concat(modules));
        }
        else {
            this.injectAll.apply(this, [props].concat(modules));
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    StyleManagerClass.prototype.loadModule = function (props, module) {
        var proxy = this._registered_modules.get(module);
        var module_content = __WEBPACK_IMPORTED_MODULE_0__lyts_core_ly__["a" /* default */].lang.isFunction(proxy) ? proxy(props) : proxy;
        if (!!module_content) {
            return module_content
                .split('<style>').join('\n')
                .split('</style>').join('\n')
                .trim();
        }
        return '';
    };
    StyleManagerClass.prototype.injectOne = function (props) {
        var modules = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            modules[_i - 1] = arguments[_i];
        }
        // creates css directives
        var css = '';
        for (var _a = 0, modules_1 = modules; _a < modules_1.length; _a++) {
            var module = modules_1[_a];
            if (this._hystory.indexOf(module) === -1) {
                css += this.loadModule(props, module);
                this._hystory.push(module);
            }
        }
        // add line
        css = '\n' + css + '\n';
        __WEBPACK_IMPORTED_MODULE_0__lyts_core_ly__["a" /* default */].dom.injectStyle(css);
    };
    StyleManagerClass.prototype.injectAll = function (props) {
        var modules = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            modules[_i - 1] = arguments[_i];
        }
        // creates css directives
        for (var _a = 0, modules_2 = modules; _a < modules_2.length; _a++) {
            var module = modules_2[_a];
            if (this._hystory.indexOf(module) === -1) {
                var css = this.loadModule(props, module);
                this._hystory.push(module);
                __WEBPACK_IMPORTED_MODULE_0__lyts_core_ly__["a" /* default */].dom.injectStyle(css);
            }
        }
    };
    StyleManagerClass.instance = function () {
        if (null == StyleManagerClass.__instance) {
            StyleManagerClass.__instance = new StyleManagerClass();
        }
        return StyleManagerClass.__instance;
    };
    return StyleManagerClass;
}());
// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
var StyleManager = StyleManagerClass.instance();



/***/ }),
/* 34 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = animate_css;
/***
 * https://daneden.github.io/animate.css/
 **/
function animate_css(props) {
    var main = "\n        <style>\n        /*!\n         * animate.css -http://daneden.me/animate\n         * Version - 3.5.2\n         * Licensed under the MIT license - http://opensource.org/licenses/MIT\n         *\n         * Copyright (c) 2017 Daniel Eden\n         */\n        \n        .animated{animation-duration:1s;animation-fill-mode:both}.animated.infinite{animation-iteration-count:infinite}.animated.hinge{animation-duration:2s}.animated.bounceIn,.animated.bounceOut,.animated.flipOutX,.animated.flipOutY{animation-duration:.75s}@keyframes bounce{0%,20%,53%,80%,to{animation-timing-function:cubic-bezier(.215,.61,.355,1);transform:translateZ(0)}40%,43%{animation-timing-function:cubic-bezier(.755,.05,.855,.06);transform:translate3d(0,-30px,0)}70%{animation-timing-function:cubic-bezier(.755,.05,.855,.06);transform:translate3d(0,-15px,0)}90%{transform:translate3d(0,-4px,0)}}.bounce{animation-name:bounce;transform-origin:center bottom}@keyframes flash{0%,50%,to{opacity:1}25%,75%{opacity:0}}.flash{animation-name:flash}@keyframes pulse{0%{transform:scaleX(1)}50%{transform:scale3d(1.05,1.05,1.05)}to{transform:scaleX(1)}}.pulse{animation-name:pulse}@keyframes rubberBand{0%{transform:scaleX(1)}30%{transform:scale3d(1.25,.75,1)}40%{transform:scale3d(.75,1.25,1)}50%{transform:scale3d(1.15,.85,1)}65%{transform:scale3d(.95,1.05,1)}75%{transform:scale3d(1.05,.95,1)}to{transform:scaleX(1)}}.rubberBand{animation-name:rubberBand}@keyframes shake{0%,to{transform:translateZ(0)}10%,30%,50%,70%,90%{transform:translate3d(-10px,0,0)}20%,40%,60%,80%{transform:translate3d(10px,0,0)}}.shake{animation-name:shake}@keyframes headShake{0%{transform:translateX(0)}6.5%{transform:translateX(-6px) rotateY(-9deg)}18.5%{transform:translateX(5px) rotateY(7deg)}31.5%{transform:translateX(-3px) rotateY(-5deg)}43.5%{transform:translateX(2px) rotateY(3deg)}50%{transform:translateX(0)}}.headShake{animation-timing-function:ease-in-out;animation-name:headShake}@keyframes swing{20%{transform:rotate(15deg)}40%{transform:rotate(-10deg)}60%{transform:rotate(5deg)}80%{transform:rotate(-5deg)}to{transform:rotate(0deg)}}.swing{transform-origin:top center;animation-name:swing}@keyframes tada{0%{transform:scaleX(1)}10%,20%{transform:scale3d(.9,.9,.9) rotate(-3deg)}30%,50%,70%,90%{transform:scale3d(1.1,1.1,1.1) rotate(3deg)}40%,60%,80%{transform:scale3d(1.1,1.1,1.1) rotate(-3deg)}to{transform:scaleX(1)}}.tada{animation-name:tada}@keyframes wobble{0%{transform:none}15%{transform:translate3d(-25%,0,0) rotate(-5deg)}30%{transform:translate3d(20%,0,0) rotate(3deg)}45%{transform:translate3d(-15%,0,0) rotate(-3deg)}60%{transform:translate3d(10%,0,0) rotate(2deg)}75%{transform:translate3d(-5%,0,0) rotate(-1deg)}to{transform:none}}.wobble{animation-name:wobble}@keyframes jello{0%,11.1%,to{transform:none}22.2%{transform:skewX(-12.5deg) skewY(-12.5deg)}33.3%{transform:skewX(6.25deg) skewY(6.25deg)}44.4%{transform:skewX(-3.125deg) skewY(-3.125deg)}55.5%{transform:skewX(1.5625deg) skewY(1.5625deg)}66.6%{transform:skewX(-.78125deg) skewY(-.78125deg)}77.7%{transform:skewX(.390625deg) skewY(.390625deg)}88.8%{transform:skewX(-.1953125deg) skewY(-.1953125deg)}}.jello{animation-name:jello;transform-origin:center}@keyframes bounceIn{0%,20%,40%,60%,80%,to{animation-timing-function:cubic-bezier(.215,.61,.355,1)}0%{opacity:0;transform:scale3d(.3,.3,.3)}20%{transform:scale3d(1.1,1.1,1.1)}40%{transform:scale3d(.9,.9,.9)}60%{opacity:1;transform:scale3d(1.03,1.03,1.03)}80%{transform:scale3d(.97,.97,.97)}to{opacity:1;transform:scaleX(1)}}.bounceIn{animation-name:bounceIn}@keyframes bounceInDown{0%,60%,75%,90%,to{animation-timing-function:cubic-bezier(.215,.61,.355,1)}0%{opacity:0;transform:translate3d(0,-3000px,0)}60%{opacity:1;transform:translate3d(0,25px,0)}75%{transform:translate3d(0,-10px,0)}90%{transform:translate3d(0,5px,0)}to{transform:none}}.bounceInDown{animation-name:bounceInDown}@keyframes bounceInLeft{0%,60%,75%,90%,to{animation-timing-function:cubic-bezier(.215,.61,.355,1)}0%{opacity:0;transform:translate3d(-3000px,0,0)}60%{opacity:1;transform:translate3d(25px,0,0)}75%{transform:translate3d(-10px,0,0)}90%{transform:translate3d(5px,0,0)}to{transform:none}}.bounceInLeft{animation-name:bounceInLeft}@keyframes bounceInRight{0%,60%,75%,90%,to{animation-timing-function:cubic-bezier(.215,.61,.355,1)}0%{opacity:0;transform:translate3d(3000px,0,0)}60%{opacity:1;transform:translate3d(-25px,0,0)}75%{transform:translate3d(10px,0,0)}90%{transform:translate3d(-5px,0,0)}to{transform:none}}.bounceInRight{animation-name:bounceInRight}@keyframes bounceInUp{0%,60%,75%,90%,to{animation-timing-function:cubic-bezier(.215,.61,.355,1)}0%{opacity:0;transform:translate3d(0,3000px,0)}60%{opacity:1;transform:translate3d(0,-20px,0)}75%{transform:translate3d(0,10px,0)}90%{transform:translate3d(0,-5px,0)}to{transform:translateZ(0)}}.bounceInUp{animation-name:bounceInUp}@keyframes bounceOut{20%{transform:scale3d(.9,.9,.9)}50%,55%{opacity:1;transform:scale3d(1.1,1.1,1.1)}to{opacity:0;transform:scale3d(.3,.3,.3)}}.bounceOut{animation-name:bounceOut}@keyframes bounceOutDown{20%{transform:translate3d(0,10px,0)}40%,45%{opacity:1;transform:translate3d(0,-20px,0)}to{opacity:0;transform:translate3d(0,2000px,0)}}.bounceOutDown{animation-name:bounceOutDown}@keyframes bounceOutLeft{20%{opacity:1;transform:translate3d(20px,0,0)}to{opacity:0;transform:translate3d(-2000px,0,0)}}.bounceOutLeft{animation-name:bounceOutLeft}@keyframes bounceOutRight{20%{opacity:1;transform:translate3d(-20px,0,0)}to{opacity:0;transform:translate3d(2000px,0,0)}}.bounceOutRight{animation-name:bounceOutRight}@keyframes bounceOutUp{20%{transform:translate3d(0,-10px,0)}40%,45%{opacity:1;transform:translate3d(0,20px,0)}to{opacity:0;transform:translate3d(0,-2000px,0)}}.bounceOutUp{animation-name:bounceOutUp}@keyframes fadeIn{0%{opacity:0}to{opacity:1}}.fadeIn{animation-name:fadeIn}@keyframes fadeInDown{0%{opacity:0;transform:translate3d(0,-100%,0)}to{opacity:1;transform:none}}.fadeInDown{animation-name:fadeInDown}@keyframes fadeInDownBig{0%{opacity:0;transform:translate3d(0,-2000px,0)}to{opacity:1;transform:none}}.fadeInDownBig{animation-name:fadeInDownBig}@keyframes fadeInLeft{0%{opacity:0;transform:translate3d(-100%,0,0)}to{opacity:1;transform:none}}.fadeInLeft{animation-name:fadeInLeft}@keyframes fadeInLeftBig{0%{opacity:0;transform:translate3d(-2000px,0,0)}to{opacity:1;transform:none}}.fadeInLeftBig{animation-name:fadeInLeftBig}@keyframes fadeInRight{0%{opacity:0;transform:translate3d(100%,0,0)}to{opacity:1;transform:none}}.fadeInRight{animation-name:fadeInRight}@keyframes fadeInRightBig{0%{opacity:0;transform:translate3d(2000px,0,0)}to{opacity:1;transform:none}}.fadeInRightBig{animation-name:fadeInRightBig}@keyframes fadeInUp{0%{opacity:0;transform:translate3d(0,100%,0)}to{opacity:1;transform:none}}.fadeInUp{animation-name:fadeInUp}@keyframes fadeInUpBig{0%{opacity:0;transform:translate3d(0,2000px,0)}to{opacity:1;transform:none}}.fadeInUpBig{animation-name:fadeInUpBig}@keyframes fadeOut{0%{opacity:1}to{opacity:0}}.fadeOut{animation-name:fadeOut}@keyframes fadeOutDown{0%{opacity:1}to{opacity:0;transform:translate3d(0,100%,0)}}.fadeOutDown{animation-name:fadeOutDown}@keyframes fadeOutDownBig{0%{opacity:1}to{opacity:0;transform:translate3d(0,2000px,0)}}.fadeOutDownBig{animation-name:fadeOutDownBig}@keyframes fadeOutLeft{0%{opacity:1}to{opacity:0;transform:translate3d(-100%,0,0)}}.fadeOutLeft{animation-name:fadeOutLeft}@keyframes fadeOutLeftBig{0%{opacity:1}to{opacity:0;transform:translate3d(-2000px,0,0)}}.fadeOutLeftBig{animation-name:fadeOutLeftBig}@keyframes fadeOutRight{0%{opacity:1}to{opacity:0;transform:translate3d(100%,0,0)}}.fadeOutRight{animation-name:fadeOutRight}@keyframes fadeOutRightBig{0%{opacity:1}to{opacity:0;transform:translate3d(2000px,0,0)}}.fadeOutRightBig{animation-name:fadeOutRightBig}@keyframes fadeOutUp{0%{opacity:1}to{opacity:0;transform:translate3d(0,-100%,0)}}.fadeOutUp{animation-name:fadeOutUp}@keyframes fadeOutUpBig{0%{opacity:1}to{opacity:0;transform:translate3d(0,-2000px,0)}}.fadeOutUpBig{animation-name:fadeOutUpBig}@keyframes flip{0%{transform:perspective(400px) rotateY(-1turn);animation-timing-function:ease-out}40%{transform:perspective(400px) translateZ(150px) rotateY(-190deg);animation-timing-function:ease-out}50%{transform:perspective(400px) translateZ(150px) rotateY(-170deg);animation-timing-function:ease-in}80%{transform:perspective(400px) scale3d(.95,.95,.95);animation-timing-function:ease-in}to{transform:perspective(400px);animation-timing-function:ease-in}}.animated.flip{-webkit-backface-visibility:visible;backface-visibility:visible;animation-name:flip}@keyframes flipInX{0%{transform:perspective(400px) rotateX(90deg);animation-timing-function:ease-in;opacity:0}40%{transform:perspective(400px) rotateX(-20deg);animation-timing-function:ease-in}60%{transform:perspective(400px) rotateX(10deg);opacity:1}80%{transform:perspective(400px) rotateX(-5deg)}to{transform:perspective(400px)}}.flipInX{-webkit-backface-visibility:visible!important;backface-visibility:visible!important;animation-name:flipInX}@keyframes flipInY{0%{transform:perspective(400px) rotateY(90deg);animation-timing-function:ease-in;opacity:0}40%{transform:perspective(400px) rotateY(-20deg);animation-timing-function:ease-in}60%{transform:perspective(400px) rotateY(10deg);opacity:1}80%{transform:perspective(400px) rotateY(-5deg)}to{transform:perspective(400px)}}.flipInY{-webkit-backface-visibility:visible!important;backface-visibility:visible!important;animation-name:flipInY}@keyframes flipOutX{0%{transform:perspective(400px)}30%{transform:perspective(400px) rotateX(-20deg);opacity:1}to{transform:perspective(400px) rotateX(90deg);opacity:0}}.flipOutX{animation-name:flipOutX;-webkit-backface-visibility:visible!important;backface-visibility:visible!important}@keyframes flipOutY{0%{transform:perspective(400px)}30%{transform:perspective(400px) rotateY(-15deg);opacity:1}to{transform:perspective(400px) rotateY(90deg);opacity:0}}.flipOutY{-webkit-backface-visibility:visible!important;backface-visibility:visible!important;animation-name:flipOutY}@keyframes lightSpeedIn{0%{transform:translate3d(100%,0,0) skewX(-30deg);opacity:0}60%{transform:skewX(20deg);opacity:1}80%{transform:skewX(-5deg);opacity:1}to{transform:none;opacity:1}}.lightSpeedIn{animation-name:lightSpeedIn;animation-timing-function:ease-out}@keyframes lightSpeedOut{0%{opacity:1}to{transform:translate3d(100%,0,0) skewX(30deg);opacity:0}}.lightSpeedOut{animation-name:lightSpeedOut;animation-timing-function:ease-in}@keyframes rotateIn{0%{transform-origin:center;transform:rotate(-200deg);opacity:0}to{transform-origin:center;transform:none;opacity:1}}.rotateIn{animation-name:rotateIn}@keyframes rotateInDownLeft{0%{transform-origin:left bottom;transform:rotate(-45deg);opacity:0}to{transform-origin:left bottom;transform:none;opacity:1}}.rotateInDownLeft{animation-name:rotateInDownLeft}@keyframes rotateInDownRight{0%{transform-origin:right bottom;transform:rotate(45deg);opacity:0}to{transform-origin:right bottom;transform:none;opacity:1}}.rotateInDownRight{animation-name:rotateInDownRight}@keyframes rotateInUpLeft{0%{transform-origin:left bottom;transform:rotate(45deg);opacity:0}to{transform-origin:left bottom;transform:none;opacity:1}}.rotateInUpLeft{animation-name:rotateInUpLeft}@keyframes rotateInUpRight{0%{transform-origin:right bottom;transform:rotate(-90deg);opacity:0}to{transform-origin:right bottom;transform:none;opacity:1}}.rotateInUpRight{animation-name:rotateInUpRight}@keyframes rotateOut{0%{transform-origin:center;opacity:1}to{transform-origin:center;transform:rotate(200deg);opacity:0}}.rotateOut{animation-name:rotateOut}@keyframes rotateOutDownLeft{0%{transform-origin:left bottom;opacity:1}to{transform-origin:left bottom;transform:rotate(45deg);opacity:0}}.rotateOutDownLeft{animation-name:rotateOutDownLeft}@keyframes rotateOutDownRight{0%{transform-origin:right bottom;opacity:1}to{transform-origin:right bottom;transform:rotate(-45deg);opacity:0}}.rotateOutDownRight{animation-name:rotateOutDownRight}@keyframes rotateOutUpLeft{0%{transform-origin:left bottom;opacity:1}to{transform-origin:left bottom;transform:rotate(-45deg);opacity:0}}.rotateOutUpLeft{animation-name:rotateOutUpLeft}@keyframes rotateOutUpRight{0%{transform-origin:right bottom;opacity:1}to{transform-origin:right bottom;transform:rotate(90deg);opacity:0}}.rotateOutUpRight{animation-name:rotateOutUpRight}@keyframes hinge{0%{transform-origin:top left;animation-timing-function:ease-in-out}20%,60%{transform:rotate(80deg);transform-origin:top left;animation-timing-function:ease-in-out}40%,80%{transform:rotate(60deg);transform-origin:top left;animation-timing-function:ease-in-out;opacity:1}to{transform:translate3d(0,700px,0);opacity:0}}.hinge{animation-name:hinge}@keyframes jackInTheBox{0%{opacity:0;transform:scale(.1) rotate(30deg);transform-origin:center bottom}50%{transform:rotate(-10deg)}70%{transform:rotate(3deg)}to{opacity:1;transform:scale(1)}}.jackInTheBox{animation-name:jackInTheBox}@keyframes rollIn{0%{opacity:0;transform:translate3d(-100%,0,0) rotate(-120deg)}to{opacity:1;transform:none}}.rollIn{animation-name:rollIn}@keyframes rollOut{0%{opacity:1}to{opacity:0;transform:translate3d(100%,0,0) rotate(120deg)}}.rollOut{animation-name:rollOut}@keyframes zoomIn{0%{opacity:0;transform:scale3d(.3,.3,.3)}50%{opacity:1}}.zoomIn{animation-name:zoomIn}@keyframes zoomInDown{0%{opacity:0;transform:scale3d(.1,.1,.1) translate3d(0,-1000px,0);animation-timing-function:cubic-bezier(.55,.055,.675,.19)}60%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(0,60px,0);animation-timing-function:cubic-bezier(.175,.885,.32,1)}}.zoomInDown{animation-name:zoomInDown}@keyframes zoomInLeft{0%{opacity:0;transform:scale3d(.1,.1,.1) translate3d(-1000px,0,0);animation-timing-function:cubic-bezier(.55,.055,.675,.19)}60%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(10px,0,0);animation-timing-function:cubic-bezier(.175,.885,.32,1)}}.zoomInLeft{animation-name:zoomInLeft}@keyframes zoomInRight{0%{opacity:0;transform:scale3d(.1,.1,.1) translate3d(1000px,0,0);animation-timing-function:cubic-bezier(.55,.055,.675,.19)}60%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(-10px,0,0);animation-timing-function:cubic-bezier(.175,.885,.32,1)}}.zoomInRight{animation-name:zoomInRight}@keyframes zoomInUp{0%{opacity:0;transform:scale3d(.1,.1,.1) translate3d(0,1000px,0);animation-timing-function:cubic-bezier(.55,.055,.675,.19)}60%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(0,-60px,0);animation-timing-function:cubic-bezier(.175,.885,.32,1)}}.zoomInUp{animation-name:zoomInUp}@keyframes zoomOut{0%{opacity:1}50%{opacity:0;transform:scale3d(.3,.3,.3)}to{opacity:0}}.zoomOut{animation-name:zoomOut}@keyframes zoomOutDown{40%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(0,-60px,0);animation-timing-function:cubic-bezier(.55,.055,.675,.19)}to{opacity:0;transform:scale3d(.1,.1,.1) translate3d(0,2000px,0);transform-origin:center bottom;animation-timing-function:cubic-bezier(.175,.885,.32,1)}}.zoomOutDown{animation-name:zoomOutDown}@keyframes zoomOutLeft{40%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(42px,0,0)}to{opacity:0;transform:scale(.1) translate3d(-2000px,0,0);transform-origin:left center}}.zoomOutLeft{animation-name:zoomOutLeft}@keyframes zoomOutRight{40%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(-42px,0,0)}to{opacity:0;transform:scale(.1) translate3d(2000px,0,0);transform-origin:right center}}.zoomOutRight{animation-name:zoomOutRight}@keyframes zoomOutUp{40%{opacity:1;transform:scale3d(.475,.475,.475) translate3d(0,60px,0);animation-timing-function:cubic-bezier(.55,.055,.675,.19)}to{opacity:0;transform:scale3d(.1,.1,.1) translate3d(0,-2000px,0);transform-origin:center bottom;animation-timing-function:cubic-bezier(.175,.885,.32,1)}}.zoomOutUp{animation-name:zoomOutUp}@keyframes slideInDown{0%{transform:translate3d(0,-100%,0);visibility:visible}to{transform:translateZ(0)}}.slideInDown{animation-name:slideInDown}@keyframes slideInLeft{0%{transform:translate3d(-100%,0,0);visibility:visible}to{transform:translateZ(0)}}.slideInLeft{animation-name:slideInLeft}@keyframes slideInRight{0%{transform:translate3d(100%,0,0);visibility:visible}to{transform:translateZ(0)}}.slideInRight{animation-name:slideInRight}@keyframes slideInUp{0%{transform:translate3d(0,100%,0);visibility:visible}to{transform:translateZ(0)}}.slideInUp{animation-name:slideInUp}@keyframes slideOutDown{0%{transform:translateZ(0)}to{visibility:hidden;transform:translate3d(0,100%,0)}}.slideOutDown{animation-name:slideOutDown}@keyframes slideOutLeft{0%{transform:translateZ(0)}to{visibility:hidden;transform:translate3d(-100%,0,0)}}.slideOutLeft{animation-name:slideOutLeft}@keyframes slideOutRight{0%{transform:translateZ(0)}to{visibility:hidden;transform:translate3d(100%,0,0)}}.slideOutRight{animation-name:slideOutRight}@keyframes slideOutUp{0%{transform:translateZ(0)}to{visibility:hidden;transform:translate3d(0,-100%,0)}}.slideOutUp{animation-name:slideOutUp}\n        </style>\n    ";
    return "\n        " + main + "\n    ";
}
;


/***/ }),
/* 35 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = view;
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__style__ = __webpack_require__(36);

function view(uid, props) {
    props = props || {};
    return "\n            <div id=\"" + uid + "\">\n                " + Object(__WEBPACK_IMPORTED_MODULE_0__style__["a" /* default */])(uid, props) + "                                                 \n                <div id=\"" + uid + "_upload_box\"></div> \n            </div>\n\n        ";
}


/***/ }),
/* 36 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = style;
function style(uid, props) {
    // main
    var main = "\n        <style>\n            #" + uid + "_upload_box{\n                width: 60%;\n                margin: 3rem auto;\n            }\n        </style>      \n    ";
    return "\n        " + main + "\n           \n    ";
}
;


/***/ }),
/* 37 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__view__ = __webpack_require__(38);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_view_components_Component__ = __webpack_require__(9);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__globals__ = __webpack_require__(10);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__constants__ = __webpack_require__(17);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__vendor_lyts_core_ly__ = __webpack_require__(0);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__vendor_lyts_core_style_styles_animate_Animate__ = __webpack_require__(16);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__controllers_ToastController__ = __webpack_require__(40);
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();







var CompUpload = /** @class */ (function (_super) {
    __extends(CompUpload, _super);
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function CompUpload(company_id) {
        var _this = _super.call(this) || this;
        _this._company_id = company_id;
        _this._btn_select = _super.prototype.getFirst.call(_this, '#' + _this.uid + '_btn_select');
        _this._fld_name = _super.prototype.getFirst.call(_this, '#' + _this.uid + '_fld_name');
        _this._fld_progress_value = _super.prototype.getFirst.call(_this, '#' + _this.uid + '_fld_progress_value');
        _this._progress = _super.prototype.getFirst.call(_this, '#' + _this.uid + '_progress');
        _this._progress_indicator = _super.prototype.getFirst.call(_this, '#' + _this.uid + '_progress_indicator');
        return _this;
    }
    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------
    CompUpload.prototype.render = function () {
        return Object(__WEBPACK_IMPORTED_MODULE_0__view__["a" /* default */])(this.uid, {});
    };
    CompUpload.prototype.free = function () {
        this.removeListeners();
        this.clearResumable();
    };
    CompUpload.prototype.ready = function () {
        this.init();
    };
    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    CompUpload.prototype.init = function () {
        try {
            this.initResumable();
            this.initListeners();
        }
        catch (err) {
            console.error('CompUpload.init', err);
        }
    };
    CompUpload.prototype.initResumable = function () {
        try {
            this._resumable = new __WEBPACK_IMPORTED_MODULE_2__globals__["a" /* default */].Resumable({
                target: __WEBPACK_IMPORTED_MODULE_3__constants__["a" /* default */].host
                    + '/' + __WEBPACK_IMPORTED_MODULE_3__constants__["a" /* default */].UPLOAD
                    + '?' + __WEBPACK_IMPORTED_MODULE_3__constants__["a" /* default */].URL_PARAM_COMPANY_ID + '=' + this._company_id,
                query: {},
                maxChunkRetries: 2,
                maxFiles: 1,
                prioritizeFirstAndLastChunk: true,
                simultaneousUploads: 1,
                chunkSize: 1 * 1024 * 1024
            });
            this._resumable.assignBrowse(this._btn_select);
            this._resumable.on('fileAdded', this.onFileAdded.bind(this));
            this._resumable.on('fileProgress', this.onFileProgress.bind(this));
            this._resumable.on('fileSuccess', this.onFileSuccess.bind(this));
            this._resumable.on('fileError', this.onFileError.bind(this));
            // global events
            this._resumable.on('uploadStart', this.onUploadStart.bind(this));
            this._resumable.on('complete', this.onUploadComplete.bind(this));
        }
        catch (err) {
            console.error('CompUpload.initResumable', err);
        }
    };
    CompUpload.prototype.clearResumable = function () {
        try {
            if (!!this._resumable) {
                //this._resumable.off();
                this._resumable = null;
            }
            if (!!this._current_file) {
                this._current_file = null;
            }
        }
        catch (err) {
            console.error('CompUpload.clearResumable', err);
        }
    };
    CompUpload.prototype.fileIsCurrentFile = function (file) {
        return !!file && !!this._current_file && file === this._current_file;
    };
    CompUpload.prototype.initListeners = function () {
        try {
        }
        catch (err) {
            console.error('CompUpload.initListeners', err);
        }
    };
    CompUpload.prototype.removeListeners = function () {
        try {
        }
        catch (err) {
            console.error('CompUpload.removeListeners', err);
        }
    };
    CompUpload.prototype.hideProgress = function () {
        try {
            this._fld_name.value('');
            this._fld_progress_value.value('0%');
            this._progress_indicator.setAttribute('style', 'width: 0%');
            //Animate.apply(AnimateEffect.fadeOut, this._progress, () => {
            this._progress.classAdd('hidden');
            //});
        }
        catch (err) {
            console.error('CompUpload.resetProgress', err);
        }
    };
    CompUpload.prototype.showProgress = function () {
        try {
            if (!!this._current_file) {
                this._fld_name.value(this._current_file.fileName);
                this._progress_indicator.setAttribute('style', 'width: 0%');
                this._progress.classRemove('hidden');
                __WEBPACK_IMPORTED_MODULE_5__vendor_lyts_core_style_styles_animate_Animate__["a" /* Animate */].apply(__WEBPACK_IMPORTED_MODULE_5__vendor_lyts_core_style_styles_animate_Animate__["b" /* AnimateEffect */].fadeIn, this._progress, function () {
                });
            }
        }
        catch (err) {
            console.error('CompUpload.showProgress', err);
        }
    };
    CompUpload.prototype.setProgress = function (value) {
        try {
            if (!!this._current_file) {
                var progress_value = Math.floor(value * 100) + '%';
                this._progress_indicator.setAttribute('style', 'width: ' + progress_value);
                this._fld_progress_value.value(progress_value);
            }
        }
        catch (err) {
            console.error('CompUpload.setProgress', err);
        }
    };
    // events handler
    CompUpload.prototype.onFileAdded = function (file, event) {
        try {
            if (!!this._resumable && !!file) {
                this._current_file = file;
                if (this._resumable.files.length === 1 &&
                    this._resumable.files[0] === this._current_file) {
                    this._resumable.upload();
                }
            }
        }
        catch (err) {
            console.error('CompUpload.onFileAdded', err);
        }
    };
    CompUpload.prototype.onFileProgress = function (file) {
        try {
            if (this.fileIsCurrentFile(file)) {
                this.setProgress(this._current_file.progress());
            }
        }
        catch (err) {
            console.error('CompUpload.onFileProgress', err);
        }
    };
    CompUpload.prototype.onFileSuccess = function (file, message) {
        try {
            if (this.fileIsCurrentFile(file)) {
                __WEBPACK_IMPORTED_MODULE_6__controllers_ToastController__["a" /* default */].instance().showInfo('File Uploaded');
                console.log('CompUpload.onFileSuccess', 'File Uploaded: ' + message);
            }
        }
        catch (err) {
            console.error('CompUpload.onFileSuccess', err);
        }
    };
    CompUpload.prototype.onFileError = function (file, message) {
        try {
            if (this.fileIsCurrentFile(file)) {
                __WEBPACK_IMPORTED_MODULE_6__controllers_ToastController__["a" /* default */].instance().showError('File Not Uploaded!');
                console.error('CompUpload.onFileError', 'File Not Uploaded: ' + message);
            }
        }
        catch (err) {
            console.error('CompUpload.onFileError', err);
        }
    };
    CompUpload.prototype.onUploadStart = function () {
        try {
            if (!!this._resumable && !!this._current_file) {
                this.showProgress();
            }
        }
        catch (err) {
            console.error('CompUpload.onUploadStart', err);
        }
    };
    CompUpload.prototype.onUploadComplete = function () {
        var _this = this;
        try {
            if (!!this._resumable && !!this._current_file) {
                this._resumable.removeFile(this._current_file);
                __WEBPACK_IMPORTED_MODULE_4__vendor_lyts_core_ly__["a" /* default */].lang.funcDelay(function () {
                    _this.hideProgress();
                }, 1000);
            }
        }
        catch (err) {
            console.error('CompUpload.onUploadComplete', err);
        }
    };
    return CompUpload;
}(__WEBPACK_IMPORTED_MODULE_1__vendor_lyts_core_view_components_Component__["a" /* default */]));
/* harmony default export */ __webpack_exports__["a"] = (CompUpload);


/***/ }),
/* 38 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = view;
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__style__ = __webpack_require__(39);

function view(uid, props) {
    props = props || {};
    return "\n            <div id=\"" + uid + "\" >\n                " + Object(__WEBPACK_IMPORTED_MODULE_0__style__["a" /* default */])(uid, props) + "\n                <!-- select -->        \n                <div class=\"" + uid + "_select\">          \n                    <a id=\"" + uid + "_btn_select\" class=\"waves-effect waves-light btn\" >Seleziona File</a>\n                </div>                                           \n                <!-- progress -->\n                <div id=\"" + uid + "_progress\" class=\"" + uid + "_progress hidden\">\n                    <div class=\"" + uid + "_progress_name\">\n                        <p id=\"" + uid + "_fld_name\"></p>\n                        <p id=\"" + uid + "_fld_progress_value\">0%</p>\n                    </div>\n                    <div class=\"progress\">\n                        <div id=\"" + uid + "_progress_indicator\" class=\"determinate\" style=\"width: 100%\"></div>\n                    </div>                \n                </div>      \n                                       \n                                \n            </div>\n\n        ";
}


/***/ }),
/* 39 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = style;
function style(uid, props) {
    // main
    var main = "\n        <style>\n            \n            #" + uid + "{\n                width: 100%;\n                display: flex;   \n                align-items: center;                             \n            } \n            \n            ." + uid + "_select{\n                \n            }              \n            \n            ." + uid + "_progress{\n                flex: 1;\n                display: flex;\n                flex-direction: column;\n                justify-content: space-around;\n                margin-left: 0.8rem;\n            }      \n            \n            ." + uid + "_progress_name{\n                display: flex;\n                justify-content: space-between;\n                margin-bottom: 4px;\n            }\n            \n            ." + uid + "_progress p{\n                margin: 0;\n                font-size: 0.9rem;\n                color: var(--color-grey);                                             \n            }\n            \n            ." + uid + "_progress .progress{\n                margin: 0;\n                height: 6px;\n            }\n            \n            ." + uid + "_success{\n                flex: 1;\n                margin: 2px 0 2px 0.8rem;     \n                padding: 0.5rem;           \n                border-radius: 100px;\n                display: flex;\n                align-items: center;\n                justify-content: center;\n                background-color: #43a047;\n            }\n            \n            ." + uid + "_success p{\n                margin: 0;\n                font-size: 1rem;\n                color: white;\n            }\n                                                                               \n        </style>      \n    ";
    var ex_large = "\n        <style>\n            @media only screen and (min-width: 1201px) {\n                                           \n            }\n        </style>        \n    ";
    var large = "\n        <style>\n            @media only screen and (min-width: 993px) and (max-width: 1200px) {                                          \n            }\n        </style>        \n    ";
    var medium = "\n        <style>\n            @media only screen and (min-width: 601px) and (max-width: 992px) {     \n                               \n                                   \n            }\n        </style>        \n    ";
    var small_ex_small = "\n        <style>\n            @media only screen and (max-width: 600px) {                                         \n            }\n        </style>        \n    ";
    var small = "\n        <style>\n            @media only screen and (min-width: 481px) and (max-width: 600px) {\n                  \n            }\n        </style>        \n    ";
    var ex_small = "\n        <style>\n            @media only screen and (max-width: 480px) {                                                                   \n            }\n        </style>        \n    ";
    return "\n        " + main + "\n        " + ex_large + "           \n        " + large + "\n        " + medium + "\n        " + small_ex_small + "\n        " + small + "\n        " + ex_small + "\n           \n    ";
    return "\n        " + main + "       \n    ";
}
;


/***/ }),
/* 40 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__globals__ = __webpack_require__(10);

/**
 * Main message controller.
 * Use this singleton to show message.
 */
var ToastController = /** @class */ (function () {
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    function ToastController() {
    }
    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    ToastController.prototype.showError = function (msg, duration) {
        this.show(msg, 'red darken-2', duration);
    };
    ToastController.prototype.showWarning = function (msg, duration) {
        this.show(msg, 'orange darken-2', duration);
    };
    ToastController.prototype.showInfo = function (msg, duration) {
        this.show(msg, 'green darken-2', duration);
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    ToastController.prototype.show = function (msg, classes, duration) {
        if (!!__WEBPACK_IMPORTED_MODULE_0__globals__["a" /* default */] && !!__WEBPACK_IMPORTED_MODULE_0__globals__["a" /* default */].Materialize) {
            __WEBPACK_IMPORTED_MODULE_0__globals__["a" /* default */].Materialize.toast({
                html: '' + msg + '',
                classes: !!classes ? 'rounded ' + classes : 'rounded',
                displayLength: duration ? duration : ToastController.NORMAL
            });
        }
    };
    ToastController.instance = function () {
        if (null == ToastController.__instance) {
            ToastController.__instance = new ToastController();
        }
        return ToastController.__instance;
    };
    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    ToastController.SHORT = 3000;
    ToastController.NORMAL = 5000;
    ToastController.MEDIUM = 10000;
    ToastController.LONG = 20000;
    return ToastController;
}());
/* harmony default export */ __webpack_exports__["a"] = (ToastController);


/***/ }),
/* 41 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony default export */ __webpack_exports__["a"] = ({
    /* Funny Gain Data */
    funny_gain_company_name: 'Funny Gain S.r.l',
    funny_gain_iban: '******',
    funny_gain_address: 'Via Onesto Scavino, 10 47891 - Falciano di San Marino (RSM)',
    funny_gain_email_info: 'info@funnygain.com',
    funny_gain_phone: '0549 963423',
    msg_upload_file_success: 'File Uploaded!',
    msg_upload_file_error: 'File not Uploaded: {0}',
    msg_upload_file_abort: 'File not Uploaded: {0}',
});


/***/ }),
/* 42 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony default export */ __webpack_exports__["a"] = ({
    /* Funny Gain Data */
    funny_gain_company_name: 'Funny Gain S.r.l',
    funny_gain_iban: '******',
    funny_gain_address: 'Via Onesto Scavino, 10 47891 - Falciano di San Marino (RSM)',
    funny_gain_email_info: 'info@funnygain.com',
    funny_gain_phone: '0549 963423',
    msg_upload_file_success: 'File Caricato!',
    msg_upload_file_error: 'File non Caricato: {0}',
    msg_upload_file_abort: 'File non caricato: {0}',
});


/***/ })
/******/ ]);