package org.lyj.gui.app.controller;

import javafx.fxml.FXML;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.gui.app.FxGuiApplication;

/**
 * Base Controller
 */
public abstract class ViewController
        extends AbstractLogEmitter
        implements IViewController {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private FxGuiApplication _application;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    public void setApplication(final FxGuiApplication application) {
        _application = application;
    }

    @Override
    public FxGuiApplication getApplication() {
        return _application;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



}
