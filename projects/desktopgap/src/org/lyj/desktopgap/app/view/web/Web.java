package org.lyj.desktopgap.app.view.web;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.lyj.commons.async.Async;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopgap.app.DesktopGap;
import org.lyj.desktopgap.app.IConstants;
import org.lyj.desktopgap.deploy.ResourceLoader;
import org.lyj.gui.application.app.AbstractViewController;
import org.lyj.gui.application.app.utils.PlatformUtils;
import org.lyj.gui.components.AnimatedGif;

import java.net.CookieHandler;
import java.net.CookieManager;

public class Web
        extends AbstractViewController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f x   f i e l d s
    // ------------------------------------------------------------------------

    @FXML
    private WebView _web_view;
    @FXML
    private StackPane _loader;
    @FXML
    private StackPane _loader_pane;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _uri;
    private boolean _enable_debug;
    private AnimatedGif _loader_gif;
    private CookieManager _cookie_manager;
    private JavaBridge _java_bridge;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Web() {
        _java_bridge = new JavaBridge();
        _cookie_manager = new java.net.CookieManager();
        CookieHandler.setDefault(_cookie_manager);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void clearCookies() {
        _cookie_manager.getCookieStore().removeAll();
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void enter() {

        _uri = super.getApplication().configuration().getString(IConstants.CONFIG_PATH_MAIN, "");
        _enable_debug = super.getApplication().configuration().getBoolean(IConstants.CONFIG_PATH_ENABLE_DEBUG, false);
        if (!PathUtils.hasProtocol(_uri)) {
            _uri = DesktopGap.instance().webUri(_uri);
        }

        this.initGui();
    }

    protected void exit() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initGui() {
        try {

            this.loading(true);

            final WebEngine engine = _web_view.getEngine();

            engine.load(_uri);

            addJavaBridge(engine, _java_bridge);

            engine.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<Worker.State>() {
                        public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {

                            addJavaBridge(engine, _java_bridge);

                            if (newState == Worker.State.SUCCEEDED) {

                                loading(false);

                                if (_enable_debug) {
                                    enableFirebug(engine);
                                }
                            }
                        }
                    });
            engine.setOnAlert(new EventHandler<WebEvent<String>>() {
                @Override
                public void handle(WebEvent<String> event) {
                    if (_enable_debug) {
                        info("WEBVIEW", event.getData());
                    }
                }
            });
        } catch (Exception e) {
            super.error("initGui", "Error '%s'", ExceptionUtils.getMessage(e));
        }
    }

    private void loading(final boolean active) {
        Async.invoke((args) -> {
            PlatformUtils.synch(() -> {
                try {
                    if (active) {
                        // ON
                        if (null == _loader_gif) {
                            final String loader = super.getApplication().configuration().getString(IConstants.CONFIG_PATH_LOADER, "plus");
                            final int loader_time = super.getApplication().configuration().getInteger(IConstants.CONFIG_PATH_LOADER_TIME, 2000);
                            _loader_gif = new AnimatedGif(ResourceLoader.instance().read("assets/gif/loaders/" + loader), loader_time);
                            _loader_gif.setCycleCount(20);
                            _loader_pane.getChildren().addAll(_loader_gif.getView());
                        }
                        _loader_gif.play();
                        _loader.setVisible(true);
                        _web_view.setVisible(false);
                    } else {
                        // OFF
                        _loader_gif.stop();
                        _loader_gif = null;
                        _loader.setVisible(false);
                        _web_view.setVisible(true);
                    }
                } catch (Throwable ignored) {
                }
            });
        });
    }


    private void handle(final org.lyj.commons.event.Event event) {
        PlatformUtils.synch(() -> {
            try {
                final String name = event.getName();
                final String tag = event.getTag();


            } catch (Throwable t) {
                super.error("handle", t);
            }
        });
    }

    /**
     * Enables Firebug Lite for debugging a webEngine.
     *
     * @param engine the webEngine for which debugging is to be enabled.
     */
    private static void enableFirebug(final WebEngine engine) {
        engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
    }

    private void addJavaBridge(final WebEngine engine, final JavaBridge bridge) {
        try {
            Object window = engine.executeScript("window");
            if (null != window) {
                if (((JSObject) window).getMember("java").equals("undefined")) {
                    ((JSObject) window).setMember("java", bridge);
                    engine.executeScript("console.log = function(message)\n" +
                            "{\n" +
                            "    java.log(message);\n" +
                            "};");
                }
            }
        } catch (Throwable t) {
            super.error("addJavaBridge", t);
        }
    }
}
