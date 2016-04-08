package org.lyj.desktopgap;

import javafx.stage.Stage;
import org.lyj.Lyj;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopgap.app.DesktopGap;
import org.lyj.desktopgap.app.client.bin.DesktopGapDeployer;
import org.lyj.desktopgap.deploy.assets.AssetsDeployer;
import org.lyj.desktopgap.deploy.config.ConfigurationDeployer;
import org.lyj.desktopgap.deploy.htdocs.HtdocsDeployer;
import org.lyj.desktopgap.i18n.Dictionary;
import org.lyj.ext.netty.server.web.controllers.routing.IRouter;
import org.lyj.gui.application.app.FxGuiApplication;

/**
 * Main server class.
 */
public class DesktopGapAppLauncher
        extends FxGuiApplication {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private DesktopGap _application;
    private boolean _test_mode;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopGapAppLauncher() {
        super(stageFxml(), Dictionary.instance());
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------


    @Override
    public void init(final String[] args) {

        Lyj.registerDeployer(new ConfigurationDeployer(Lyj.isSilent()));
        Lyj.registerDeployer(new HtdocsDeployer(Lyj.isSilent()));
        Lyj.registerDeployer(new AssetsDeployer(Lyj.isSilent()));

    }

    @Override
    public void ready(final Stage primaryStage) throws Exception {

        _test_mode = super.launcher().getArgBoolean("t");

        // init message bus
        MessageBus.getInstance().listeners().setInterval(300);

        // start application and web server
        _application = DesktopGap.instance().primaryStage(primaryStage);
        _application.start();

        // deploy desktopgap framework (at least, but befor web view navigation)
        DesktopGapDeployer.create().deploy();

    }

    @Override
    public void stop() {
        // close app server
        _application.stop();

        super.stop();
    }

    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------

    public boolean isTestMode() {
        return _test_mode;
    }

    public IRouter router() {
        if (null != _application) {
            return _application.router();
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


    // launcher
    public static void main(String[] args) {
        launch(args);
    }

    private static String stageFxml() {
        final String root = PathUtils.getParent(PathUtils.getClassPath(DesktopGapAppLauncher.class));
        final String path = PathUtils.concat(root, "/app/view/web/web.fxml");
        return ClassLoaderUtils.getResourceAsString(DesktopGapAppLauncher.class.getClassLoader(), path);
    }

}
