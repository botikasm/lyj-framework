(function(){

    var obj = require('test_require');

    console.log('testvar: ' + obj.testvar);

    var model = require('model/sample.json');
    console.log("var1: " + model.var1);
    console.log("var2: " + model.var2);

    var model2 = require('model/sample.js');
    console.log("var1: " + model2.var1);
    console.log("var2: " + model2.var2);

    print(__FILE__, __LINE__, __DIR__);

    print(SIMPLE.sayHello());

})();