package org.lyj.desktopgap;

import javafx.stage.Stage;
import org.lyj.Lyj;
import org.lyj.commons.event.bus.MessageBus;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopgap.app.Application;
import org.lyj.desktopgap.app.client.out.DesktopGapDeployer;
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

    private Application _application;
    private boolean _test_mode;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopGapAppLauncher() {
        super(stageFxml(), Dictionary.getInstance());
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------


    @Override
    public void init(final String[] args) {

        Lyj.registerDeployer(new ConfigurationDeployer(Lyj.isSilent()));
        Lyj.registerDeployer(new HtdocsDeployer(Lyj.isSilent()));
        Lyj.registerDeployer(new AssetsDeployer(Lyj.isSilent()));

        // deploy desktopgap framework
        Lyj.registerDeployer(new DesktopGapDeployer(Lyj.isSilent()));
    }

    @Override
    public void ready(final Stage primaryStage) throws Exception {

        _test_mode = super.launcher().getArgBoolean("t");

        // init message bus
        MessageBus.getInstance().listeners().setInterval(300);

        _application = Application.instance();
        _application.start();
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

    public static void main(String[] args) {
        launch(args);
    }

    private static String stageFxml() {
        final String root = PathUtils.getParent(PathUtils.getClassPath(DesktopGapAppLauncher.class));
        final String path = PathUtils.concat(root, "/app/view/web/web.fxml");
        return ClassLoaderUtils.getResourceAsString(DesktopGapAppLauncher.class.getClassLoader(), path);
    }

}
