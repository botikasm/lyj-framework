package org.lyj.gui.app.controller;

import javafx.fxml.Initializable;
import org.lyj.commons.async.future.Loop;
import org.lyj.commons.i18n.DictionaryController;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.gui.app.FxGuiApplication;
import org.lyj.gui.utils.PlatformUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base Controller
 */
public abstract class ViewController
        extends AbstractLogEmitter
        implements IViewController, Initializable {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private FxGuiApplication _application;
    private boolean _fxml_initialized;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      Initializable
    // ------------------------------------------------------------------------

    @Override
    public final void initialize(URL location, ResourceBundle resources) {
        _fxml_initialized = true;
        Loop l = new Loop(100, 500);
        l.start((i) -> {
            if (_fxml_initialized && null != _application) {
                i.stop();
                PlatformUtils.synch(this::initialize);
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      IViewController
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
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    protected abstract void initialize();

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String lang(){
        return LocaleUtils.getCurrent().getLanguage();
    }

    public String i18n(final String key){
        return i18n().get(lang(), key);
    }

    public String i18n(final String id, final String prop){
        return this.i18n(id + "." + prop);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private DictionaryController i18n() {
        return null != _application ? _application.i18n() : new DictionaryController();
    }

}
