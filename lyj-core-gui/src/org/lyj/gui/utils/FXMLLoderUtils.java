package org.lyj.gui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import org.lyj.gui.app.FxGuiApplication;
import org.lyj.gui.app.controller.ViewController;

import java.io.IOException;

/**
 * FXML Loader helper class
 */
public class FXMLLoderUtils {

    public static Parent load(final FxGuiApplication application, final String path) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(application.getClass().getResource(path));
        final Parent result = loader.load();
        final Object controller = loader.getController();
        if(controller instanceof ViewController) {
            ((ViewController)controller).setApplication(application);
        }
        return result;
    }

    public static Parent loadChild(final FxGuiApplication application, final Parent container, final String path) throws IOException {
        Parent child = FXMLLoderUtils.load(application, path);

        if (null != child) {
            if(container instanceof AnchorPane){
                AnchorPane.setTopAnchor(child, 0.0);
                AnchorPane.setBottomAnchor(child, 0.0);
                AnchorPane.setLeftAnchor(child, 0.0);
                AnchorPane.setRightAnchor(child, 0.0);
                ((AnchorPane)container).getChildren().add(child);
            } else if (container instanceof TitledPane) {
                ((TitledPane)container).setContent(child);
            }


        }
        return child;
    }

}
