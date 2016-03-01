package org.lyj.automator;

import org.lyj.Lyj;
import org.lyj.automator.app.App;
import org.lyj.automator.app.controllers.projects.ProjectsController;
import org.lyj.automator.deploy.config.ConfigurationDeployer;
import org.lyj.automator.deploy.projects.ProjectsDeployer;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.launcher.LyjLauncher;

/**
 * Main server class.
 *
 */
public class CmdLauncher
        extends LyjLauncher {



    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private App _application;
    private boolean _test_mode;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CmdLauncher(final String[] args) {
        // run Lyj app framework
        super(args);
        _test_mode = super.getArgBoolean("t");
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void ready() {
        if (!_test_mode)  {
            try {

                _application = new App(this);
                _application.start();

            } catch (Exception e) {
                e.printStackTrace();
                LoggingUtils.getLogger(this).error("Error Initializing Application", e);
            }
        } else {
            // TEST MODE: ONLY FOR TEST UNIT
            try {
                _application = new App(this);
                _application.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static void main(final String[] args){
        final CmdLauncher main = new CmdLauncher(args);

        Lyj.registerDeployer(new ConfigurationDeployer(Lyj.getConfigurationPath(), Lyj.isSilent()));
        Lyj.registerDeployer(new ProjectsDeployer(ProjectsController.getInstance().path(), Lyj.isSilent()));

        main.run();
    }

}
