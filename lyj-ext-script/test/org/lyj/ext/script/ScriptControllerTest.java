package org.lyj.ext.script;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.script.program.Program;

import static org.junit.Assert.assertEquals;

/**
 * Script test
 */
public class ScriptControllerTest {

    @BeforeClass
    public static void start() {

        TestInitializer.init();

        ScriptController.instance().context().put("foo", "THIS IS A VALUE");
    }

    @Test
    public void simple() throws Exception {

        Program prg = ScriptController.instance().create();
        prg.engine().eval("console.log('this is a test')");
        prg.engine().eval("console.error('value1', 'ERROR MESSAGE')");

        final int val = (Integer) prg.engine().eval("3 + 2");
        System.out.println(val);

        prg.engine().eval("print('test')");

        prg.engine().eval("print('foo = ' + foo)");

    }

    @Test
    public void specialFormula() throws Exception {

        String formula = "(L/1000 * H/1000)";

        Program prg = ScriptController.instance().create();
        double result = (double) prg.engine().eval(formula, MapBuilder.createSO()
                .put("L", 123).put("H", 676)
                .toMap());
        System.out.println("FORMULA=" + result);
    }

    @Test
    public void require() throws Exception {

        Program prg = ScriptController.instance().create();
        prg.engine().eval("console.log( require('test_require.js').testvar )");

    }

    @Test
    public void load() throws Exception {

        Program prg = ScriptController.instance().create();
        prg.engine().eval("load('https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.8.3/underscore-min.js')");

        prg.engine().eval("console.info( _.first([5, 4, 3, 2, 1]) )");


        final int val = (Integer) prg.engine().eval("_.first([5, 4, 3, 2, 1])");
        assertEquals(5, val);
        System.out.println(val);
    }

    @Test
    public void sysVars() throws Exception {
        String command = "print(__FILE__, __LINE__, __DIR__);";

        Program prg = ScriptController.instance().create();
        prg.engine().eval(command);

    }

    @Test
    public void fileProgram() throws Exception {

        Program prg = ScriptController.instance().create();
        prg.root(PathUtils.getAbsolutePath(""));
        prg.context().put("SIMPLE", new SimpleObject());
        prg.engine().eval();

    }

}