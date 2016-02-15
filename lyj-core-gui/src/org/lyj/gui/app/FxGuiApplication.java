package org.lyj.gui.app;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lyj.commons.i18n.DictionaryController;
import org.lyj.commons.util.PathUtils;
import org.lyj.gui.config.ConfigScene;
import org.lyj.gui.utils.FXMLLoderUtils;
import org.lyj.launcher.LyjLauncher;
import org.lyj.launcher.impl.LyjBaseLauncher;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class FxGuiApplication
        extends Application {
    // ------------------------------------------------------------------------
    //                      fields
    // ------------------------------------------------------------------------

    private LyjLauncher _launcher;
    private Stage _primary_stage;
    private ConfigScene _configuration;
    private DictionaryController _i18n;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FxGuiApplication(final DictionaryController dictionary) {
        _i18n = dictionary;
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
        _configuration = new ConfigScene();
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

    public ConfigScene configuration() {
        return _configuration;
    }

    public Stage primaryStage() {
        return _primary_stage;
    }

    public DictionaryController i18n() {
        return null != _i18n ? _i18n : new DictionaryController();
    }

    public Parent loadChild(final Parent container, final String path) throws IOException {
        return FXMLLoderUtils.loadChild(this, container, path);
    }

    public Parent load(final String path) throws IOException {
        return FXMLLoderUtils.load(this, path);
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        // Save file
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String[] args() {
        final List<String> params = super.getParameters().getUnnamed();
        return null != params ? params.toArray(new String[params.size()]) : new String[0];
    }

    private void ready() {
        try {
            this.initStage();
            this.ready(_primary_stage);
        } catch (Throwable t) {

        }
    }

    private void initStage() throws IOException {
        if (this.configuration().hasStage()) {
            final Parent root = FXMLLoderUtils.load(this, this.configuration().stage());
            final Scene scene = new Scene(root, this.configuration().width(), this.configuration().height());
            _primary_stage.setTitle(this.configuration().title());
            _primary_stage.setScene(scene);
            final Image icon = this.getImage(this.configuration().icon());
            if (null != icon) {
                _primary_stage.getIcons().add(icon);
            }
            if (this.configuration().autoShow()) {
                _primary_stage.show();
            }
            // handle close in primary stage
           // _primary_stage.addEventHandler();
        }
    }

    private Image getImage(final String path) {
        if (PathUtils.isAbsolute(path)) {
            return new Image(path);
        } else {
            InputStream is = getClass().getResourceAsStream(path);
            if (null != is) {
                return new Image(is);
            }
        }
        return null;
    }


}
