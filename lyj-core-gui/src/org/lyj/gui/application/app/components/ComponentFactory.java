package org.lyj.gui.application.app.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.gui.application.app.AbstractViewController;
import org.lyj.gui.application.app.FxGuiApplication;
import org.lyj.gui.application.app.utils.GuiObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper lo load components
 */
public class ComponentFactory
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final FxGuiApplication _application;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ComponentFactory(final FxGuiApplication application) {
        _application = application;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public GuiObject root() throws IOException {
        return loadFXMLFromPath(_application.configuration().stage());
    }

    public GuiObject guiFromPath(final String path) throws IOException {
        return loadFXMLFromPath(path);
    }

    public GuiObject guiFromString(final String fxml) throws IOException {
        return loadFXMLFromString(fxml);
    }

    public void addChild(final AbstractViewController container,
                         final String childPath) throws IOException {
        if (null != container && container.hasView()) {
            this.addChild(container, container.getGui().view(), childPath);
        }
    }

    public void addChild(final AbstractViewController master,
                         final Parent container,
                         final String childPath) throws IOException {
        if (null != master && null != container) {
            final GuiObject gui = this.loadFXML(null, childPath);
            if (gui.hasController()) {
                this.addChild(master, container, gui.controller());
            } else {
                this.dock(container, gui.view());
            }
        }
    }

    public void addChild(final AbstractViewController container,
                         final Class<? extends AbstractViewController> childClass) throws IOException {
        this.addChild(container, this.create(childClass));
    }

    public void addChild(final AbstractViewController container,
                         final AbstractViewController child) throws IOException {
        if (null != child && null != container) {
            if (container.hasView()) {
                this.addChild(container, container.getGui().view(), child);
            }
        }
    }

    public void addChild(final AbstractViewController master,
                         final Parent container,
                         final Class<? extends AbstractViewController> childClass,
                         final Object... args) throws IOException {
        this.addChild(master, container, this.create(childClass, args));
    }

    public void addChild(final AbstractViewController master,
                         final Parent container,
                         final AbstractViewController child) throws IOException {
        if (null != child && null != master && null != container) {
            if (child.hasView()) {
                this.dock(container, child.getGui().view());
            }
            master.addChildController(child);
        }
    }

    public void dock(final Parent container,
                     final AbstractViewController child) throws IOException {
        if (null != child && child.hasView()) {
            this.dock(container, child.getGui().view());
        }
    }

    public void dock(final Parent container,
                     final Parent child) throws IOException {
        if (null != child) {
            if (container instanceof AnchorPane) {
                AnchorPane.setTopAnchor(child, 0.0);
                AnchorPane.setBottomAnchor(child, 0.0);
                AnchorPane.setLeftAnchor(child, 0.0);
                AnchorPane.setRightAnchor(child, 0.0);
                ((AnchorPane) container).getChildren().add(child);
            } else if (container instanceof TitledPane) {
                ((TitledPane) container).setContent(child);
            } else if (container instanceof ScrollPane) {
                ((ScrollPane) container).setContent(child);
            }
        }
    }

    public GuiObject loadFXML(final AbstractViewController acontroller,
                              final String path) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getResource(path));
        if (null != acontroller) {
            loader.setController(acontroller);
        }
        final Parent view = loader.load();
        final Object controller = loader.getController();
        if (null != controller) {
            if (controller instanceof AbstractViewController) {
                ((AbstractViewController) controller).setApplication(_application);
                return new GuiObject(view, ((AbstractViewController) controller));
            } else {
                throw new IOException(FormatUtils.format("Bad controller in %s", path));
            }
        } else {
            return new GuiObject(view, null);
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private URL getResource(final String path) {
        return _application.getClass().getResource(path);
    }

    private GuiObject loadFXMLFromPath(final String path) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getResource(path));

        final Parent result = loader.load();
        final Object controller = loader.getController();
        return getGuiObject(result, controller);
    }

    private GuiObject loadFXMLFromString(final String fxml) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        final Parent result = loader.load(new ByteArrayInputStream(fxml.getBytes()));
        final Object controller = loader.getController();
        return getGuiObject(result, controller);
    }

    private GuiObject getGuiObject(final Parent parent,
                                   final Object controller) {
        if (controller instanceof AbstractViewController) {
            ((AbstractViewController) controller).setApplication(_application);
        }
        return new GuiObject(parent, controller);
    }

    private AbstractViewController create(final Class<? extends AbstractViewController> aclass,
                                          final Object... args) {
        try {
            if (!CollectionUtils.isEmpty(args)) {
                final Object[] params = args[0] instanceof FxGuiApplication
                        ? args
                        : CollectionUtils.insertToArray(args, _application);
                return (AbstractViewController) ClassLoaderUtils.newInstance(aclass, params);
            } else {
                try {
                    return (AbstractViewController) ClassLoaderUtils.newInstance(aclass, new Object[]{_application});
                } catch (NoSuchMethodException t) {
                    return aclass.getConstructor().newInstance();
                }
            }
        } catch (Throwable err) {
            super.error("create", ExceptionUtils.getMessage(err));
        }
        return null;
    }

    private static Class[] getTypes(final Object[] objects) {
        final List<Class> result = new LinkedList<Class>();
        for (final Object object : objects) {
            result.add(object.getClass());
        }
        return result.toArray(new Class[result.size()]);
    }

}
