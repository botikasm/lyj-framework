package org.lyj.gui.application.app.utils;

import javafx.scene.Parent;
import org.lyj.gui.application.app.AbstractViewController;

/**
 *
 */
public class GuiObject {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Parent _view;
    private AbstractViewController _controller;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public GuiObject(final Parent view, final Object controller) {
        this(view,
                controller instanceof AbstractViewController ? (AbstractViewController) controller : null);
    }

    public GuiObject(final Parent view, final AbstractViewController controller) {
        _view = view;
        _controller = controller;
        if (null != _controller) {
            _controller.setGui(this);
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Parent view() {
        return _view;
    }

    public AbstractViewController controller() {
        return _controller;
    }

    public boolean hasView() {
        return null != _view;
    }

    public boolean hasController() {
        return null != _controller;
    }

}
