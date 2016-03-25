package org.lyj.desktopfences;


import javafx.stage.Stage;
import org.lyj.Lyj;
import org.lyj.desktopfences.app.DesktopFences;
import org.lyj.desktopfences.deploy.assets.AssetsDeployer;
import org.lyj.desktopfences.deploy.config.ConfigurationDeployer;
import org.lyj.desktopfences.deploy.htdocs.HtdocsDeployer;
import org.lyj.desktopgap.DesktopGapAppLauncher;


public class DesktopFencesLauncher
        extends DesktopGapAppLauncher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopFencesLauncher() {

    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------


    @Override
    public void init(final String[] args) {
        super.init(args);

        Lyj.registerDeployer(new ConfigurationDeployer(Lyj.isSilent()));
        Lyj.registerDeployer(new AssetsDeployer(Lyj.isSilent()));
        Lyj.registerDeployer(new HtdocsDeployer(Lyj.isSilent()));
    }

    @Override
    public void ready(final Stage primaryStage) throws Exception {
        super.ready(primaryStage);

        // bootstrap main application controller
        DesktopFences.init(super.router(), super.isTestMode()).start();
    }

    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }


}
