package org.lyj.gui.application.app;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lyj.Lyj;
import org.lyj.commons.i18n.DictionaryController;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.gui.application.app.components.ComponentFactory;
import org.lyj.gui.application.app.utils.GuiObject;
import org.lyj.gui.application.config.ConfigScene;
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
    private String _primary_stage_fxml;
    private Stage _primary_stage;
    private AbstractViewController _primary_controller;
    private ConfigScene _configuration;
    private DictionaryController _i18n;

    private final AbstractLogEmitter _logger;
    private final ComponentFactory _factory;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FxGuiApplication(final DictionaryController dictionary) {
        this("", dictionary);
    }

    public FxGuiApplication(final String primaryStageFXML,
                            final DictionaryController dictionary) {
        _primary_stage_fxml = primaryStageFXML;
        _i18n = dictionary;
        _logger = new AbstractLogEmitter(LoggingUtils.getLogger(this));
        _factory = new ComponentFactory(this);
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

    public ComponentFactory factory() {
        return _factory;
    }

    @Override
    public void stop() {
        this.logger().debug("stop", "Stage is closing");
        if (null != _primary_controller) {
            _primary_controller.doExit();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected AbstractLogEmitter logger() {
        return _logger;
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
            _logger.error("ready", t);
        }
    }

    private void initStage() throws IOException {
        if (this.configuration().hasStage() || StringUtils.hasText(_primary_stage_fxml)) {
            final GuiObject gui_root = StringUtils.hasText(_primary_stage_fxml)
                    ? this.factory().guiFromString(_primary_stage_fxml)
                    : this.factory().root();
            final Scene scene = new Scene(gui_root.view(), this.configuration().width(), this.configuration().height());
            _primary_stage.setTitle(this.configuration().title());
            _primary_stage.setScene(scene);
            final Image icon = this.getImage(this.configuration().icon());
            if (null != icon) {
                _primary_stage.getIcons().add(icon);
            }
            if (this.configuration().autoShow()) {
                _primary_stage.show();
            }
            _primary_controller = gui_root.controller();
        } else {
            logger().warning("initStage", "Missing a primary stage. This application has not a GUI.");
        }
    }

    private Image getImage(final String path) {
        String img_path = path;
        try {
            if (PathUtils.isAbsolute(img_path)) {
                return new Image(img_path);
            } else {
                InputStream is = getClass().getResourceAsStream(img_path);
                if (null != is) {
                    return new Image(is);
                } else {
                    img_path = Lyj.getAbsolutePath(path);
                    if (PathUtils.exists(img_path)) {
                        return new Image("file://" + img_path);
                    }
                }
            }
        } catch (Throwable t) {
            this.logger().error("getImage", FormatUtils.format("Error opening '%s': %s", img_path, t));
        }
        return null;
    }


}
