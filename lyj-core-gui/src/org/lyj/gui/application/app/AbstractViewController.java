package org.lyj.gui.application.app;

import javafx.fxml.Initializable;
import org.lyj.commons.async.future.Loop;
import org.lyj.commons.i18n.DictionaryController;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.gui.application.app.components.ComponentFactory;
import org.lyj.gui.application.app.utils.GuiObject;
import org.lyj.gui.application.app.utils.PlatformUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Base Controller
 */
public abstract class AbstractViewController
        extends AbstractLogEmitter
        implements Initializable {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _id;
    private FxGuiApplication _application;
    private boolean _fxml_initialized;
    private GuiObject _gui;

    private final List<AbstractViewController> _children;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractViewController() {
        this(null, "");
    }

    public AbstractViewController(final FxGuiApplication application, final String fxml) {
        _id = RandomUtils.randomUUID(false);
        _children = new ArrayList<>();
        _application = application;
        try {
            if (null != application && StringUtils.hasText(fxml)) {
                _gui = application.factory().loadFXML(this, fxml);
            }
        } catch (Throwable t) {
            super.error("AbstractViewController#loadFXML", ExceptionUtils.getMessage(t));
        }
    }

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
                PlatformUtils.synch(this::enter);
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    protected abstract void enter();

    protected abstract void exit();

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String id() {
        return _id;
    }

    public String lang() {
        return LocaleUtils.getCurrent().getLanguage();
    }

    public String i18n(final String key) {
        return i18n().get(lang(), key);
    }

    public String i18nFmt(final String key, final Object...args) {
        return i18n().get(lang(), key, args);
    }

    public String i18n(final String id, final String prop) {
        return this.i18n(id + "." + prop);
    }

    public void i18n(final Object control, final String prop){
        PlatformUtils.i18n(i18n(), lang(), control, prop);
    }

    public void i18n(final Object[] controls, final String prop){
        PlatformUtils.i18n(i18n(), lang(), controls, prop);
    }

    public void i18n(final Object...controls){
        PlatformUtils.i18n(i18n(), lang(), controls, null);
    }

    public GuiObject getGui() {
        return _gui;
    }

    public void setGui(final GuiObject gui) {
        _gui = gui;
    }

    public boolean hasView() {
        return null != _gui && null != _gui.view();
    }

    public boolean hasApplication(){
        return null!=_application;
    }

    public boolean hasFactory(){
        return this.hasApplication() && null!=_application.factory();
    }

    public void setApplication(final FxGuiApplication application) {
        _application = application;
    }

    public FxGuiApplication getApplication() {
        return _application;
    }

    public ComponentFactory getFactory(){
        if(this.hasFactory()) {
            return  _application.factory();
        }
        return null;
    }

    public boolean addChildController(final AbstractViewController child) {
        if (null != child && !child.getClass().equals(this.getClass()) && !_children.contains(child)) {
            _children.add(child);
            return true;
        }
        return false;
    }

    public boolean removeChildController(final AbstractViewController child) {
        return _children.remove(child);
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------


    void doExit() {
        for (final AbstractViewController child : _children) {
            try {
                child.doExit();
            } catch (Throwable ignored) {
            }
        }
        try {
            this.exit();
        } catch (Throwable ignored) {
        }

        _gui = null;
        _application = null;
        _children.clear();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private DictionaryController i18n() {
        return null != _application ? _application.i18n() : new DictionaryController();
    }

}
