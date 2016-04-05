package org.lyj.desktopgap.app.view.web;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import org.lyj.commons.async.Async;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopgap.app.DesktopGap;
import org.lyj.desktopgap.app.IConstants;
import org.lyj.desktopgap.deploy.ResourceLoader;
import org.lyj.gui.application.app.AbstractViewController;
import org.lyj.gui.application.app.utils.PlatformUtils;
import org.lyj.gui.components.AnimatedGif;

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
    private AnimatedGif _loader_gif;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Web() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void enter() {

        _uri = super.getApplication().configuration().getString(IConstants.CONFIG_PATH_MAIN, "");
        if(!PathUtils.hasProtocol(_uri)){
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

            _web_view.getEngine().load(_uri);

            _web_view.getEngine().getLoadWorker().stateProperty().addListener(
                    new ChangeListener<Worker.State>() {
                        public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                            if (newState == Worker.State.SUCCEEDED) {

                                loading(false);

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

}
