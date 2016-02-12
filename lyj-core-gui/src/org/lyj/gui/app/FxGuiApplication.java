package org.lyj.gui.app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.lyj.commons.Delegates;
import org.lyj.gui.app.controller.ViewController;
import org.lyj.gui.config.GuiConfiguration;
import org.lyj.launcher.LyjLauncher;
import org.lyj.launcher.impl.LyjBaseLauncher;

import java.io.IOException;
import java.util.List;

public abstract class FxGuiApplication
        extends Application {
    // ------------------------------------------------------------------------
    //                      fields
    // ------------------------------------------------------------------------

    private LyjLauncher _launcher;
    private Stage _primary_stage;
    private GuiConfiguration _configuration;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FxGuiApplication() {

    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    public void start(final Stage primaryStage) {
        _primary_stage = primaryStage;
        // run lyj launcher
        _launcher.handle(this::ready).run();
    }

    @Override
    public final void init() {
        _launcher = new LyjBaseLauncher(this.args());
        this.init(this.args());

        // create configuration after deployer initialization.
        _configuration = new GuiConfiguration();
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    /**
     * The application initialization method. This method is called immediately
     * after the Application class is loaded and constructed. An application may
     * override this method to perform initialization prior to the actual starting
     * of the application.
     * <p>
     * <p>
     * The implementation of this method provided by the Application class does nothing.
     * </p>
     * <p>
     * <p>
     * NOTE: This method is not called on the JavaFX Application Thread. An
     * application must not construct a Scene or a Stage in this
     * method.
     * An application may construct other JavaFX objects in this method.
     * </p>
     */
    public abstract void init(final String[] args);

    public abstract void ready(final Stage primaryStage) throws Exception;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public LyjLauncher launcher() {
        return _launcher;
    }

    public GuiConfiguration configuration() {
        return _configuration;
    }

    public Stage primaryStage() {
        return _primary_stage;
    }

    public FXMLLoader loader(final String path) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        final Object controller = loader.getController();
        if(controller instanceof ViewController) {
            ((ViewController)controller).setApplication(this);
        }
        return loader;
    }

    public Parent loadChild(final Parent container, final String path) throws IOException {
        final FXMLLoader loader = this.loader(path);
        Parent child = loader.load();

        if (null != child) {
            container.getChildrenUnmodifiable().add(child);
        }
        return child;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String[] args() {
        final List<String> params = super.getParameters().getUnnamed();
        return null != params ? params.toArray(new String[params.size()]) : new String[0];
    }

    private void ready() throws Exception {
        this.initStage();
        this.ready(_primary_stage);
    }

    private void initStage() throws IOException {
        if (this.configuration().hasStage()) {
            final Parent root = this.loader(this.configuration().stage()).load();
            final Scene scene = new Scene(root, this.configuration().width(), this.configuration().height());
            _primary_stage.setTitle(this.configuration().title());
            _primary_stage.setScene(scene);
            if (this.configuration().autoShow()) {
                _primary_stage.show();
            }
        }
    }


}
