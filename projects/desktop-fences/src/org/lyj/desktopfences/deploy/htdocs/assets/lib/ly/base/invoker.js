(function(){

    'use strict';

    // ---------------------------------------------
    //          imports
    // ---------------------------------------------

    var ly = require('../ly');

    // ---------------------------------------------
    //          public
    // ---------------------------------------------

    var Invoker = {

        invoke: function () {
            var args = ly.toArray(arguments);
            if (ly.isFunction(args[0])) {
                if (args.length === 1) {
                    return ly.bind(args[0], this)();
                } else {
                    return ly.bind(args[0], this).apply(this, args.splice(1));
                }
            }
            return null;
        }

    };

    // ---------------------------------------------
    //          exports
    // ---------------------------------------------


    module.exports = Invoker;

}).call(this);