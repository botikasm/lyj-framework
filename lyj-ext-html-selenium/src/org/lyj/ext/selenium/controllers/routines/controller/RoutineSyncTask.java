package org.lyj.ext.selenium.controllers.routines.controller;

import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.selenium.controllers.routines.controller.scripts.ScriptProgram;
import org.lyj.ext.selenium.controllers.routines.model.ModelPackage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.concurrent.TimeUnit;

/**
 * Run a selenium task
 * <p>
 * ---------------------
 * <p>
 * https://www.seleniumhq.org/docs/04_webdriver_advanced.jsp
 */
public class RoutineSyncTask {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final ModelPackage _info;
    private final RoutineLogger _logger;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private RoutineSyncTask(final String root,
                            final ModelPackage info) {
        _root = root;
        _info = info;
        _logger = new RoutineLogger(root, info.name()); // one logger with custom path for each class

        _logger.info("STARTING", info.toString());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void run() {
        try {
            final ScriptProgram program = this.createProgram();
            
            // try get driver
            final WebDriver driver = DriverBuilder.build(_info);
            if (null != driver) {
                try {
                    final String url = _info.url();

                    // execute commands
                    driver.manage().deleteAllCookies(); // remove all cookies
                    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

                    driver.get(url);

                } finally {
                    driver.close();
                }
                System.out.println(_info);
            } else {
                throw new Exception(FormatUtils.format("Driver not found: '%s'", _info.browser()));
            }
        } catch (WebDriverException werr) {
            final String message = werr.getMessage();
            final String more_info = werr.getAdditionalInformation();
        } catch (Throwable t) {
            _logger.error("run", FormatUtils.format(
                    "Error running '%s' from '%s': '%s'\nINFO: %s",
                    _info.name(), _root, t, _info));
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private ScriptProgram createProgram(){
        final String script_root = PathUtils.concat(_root, _info.name());
        final String program_root = PathUtils.concat(script_root, "program");
        final ScriptProgram program = new ScriptProgram(program_root, _info, _logger);

        return program;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static void run(final String root,
                           final ModelPackage info) {
        RoutineSyncTask t = new RoutineSyncTask(root, info);
        t.run();
    }

}
