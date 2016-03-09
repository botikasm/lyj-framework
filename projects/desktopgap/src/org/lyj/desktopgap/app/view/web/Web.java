package org.lyj.desktopgap.app.view.web;


import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.gui.app.AbstractViewController;
import org.lyj.gui.app.utils.PlatformUtils;

public class Web
        extends AbstractViewController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      g u i   f i e l d s
    // ------------------------------------------------------------------------

    @FXML
    private WebView _web_view;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


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
        _web_view.getEngine().load("https://opentokrtc.com/foo");
    }

    protected void exit() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initGui() {
        try {

        } catch (Exception e) {
            super.error("initGui", "Error '%s'", ExceptionUtils.getMessage(e));
        }
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
