package org.lyj.desktopfences;



import javafx.stage.Stage;
import org.lyj.Lyj;
import org.lyj.desktopfences.app.client.ApiController;
import org.lyj.desktopfences.deploy.assets.AssetsDeployer;
import org.lyj.desktopfences.deploy.config.ConfigurationDeployer;
import org.lyj.desktopfences.deploy.htdocs.HtdocsDeployer;
import org.lyj.desktopgap.DesktopGapAppLauncher;


public class Application
        extends DesktopGapAppLauncher {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _test_mode;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Application() {

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

        //-- add custom client API --//
        ApiController.install(super.router());
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
