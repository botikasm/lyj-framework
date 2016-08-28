/**
 * Extends javascript engine.
 */

"use strict";


(function () {

    function extension(filename) {
        return filename.indexOf(".") > -1 ? filename.split('.').pop() : "";
    }

    function parseJSON(text) {
        try {
            return JSON.parse(text);
        } catch (err) {
            return false;
        }
    }

    // ------------------------------------
    //  require('<PATH>');
    // ------------------------------------
    __engine__.addAttribute("require", function (path) {
        if (!!requirer) {
            var text = requirer.require(path);
            if (!!text) {
                var ext = extension(path) || "js";
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
                        return text;
                    }
                } else {
                    var json = parseJSON(text);
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
    //  _new('<class>');
    //  returns new instance of passed class.
    // ------------------------------------
    __engine__.addAttribute("_new", function (aclass) {

        if(typeof aclass === 'function'){
            return new aclass();
        } else {
            return aclass;
        }

    });
})();