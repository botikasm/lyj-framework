package org.lyj.gui.application.app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.lyj.commons.Delegates;

/**
 * Utility for dialogs
 */
public class DialogUtils {

    public static void confirmation(final String title, final String message,
                                    final Delegates.Callback<ButtonType> callback) {
        alert(Alert.AlertType.CONFIRMATION, title, message, callback);
    }

    public static void error(final String title, final String message,
                             final Delegates.Callback<ButtonType> callback) {
        alert(Alert.AlertType.ERROR, title, message, callback);
    }

    public static void error(final String title, final String message) {
        alert(Alert.AlertType.ERROR, title, message, null);
    }

    public static void error(final String title, final Throwable err) {
        alert(Alert.AlertType.ERROR, title, err.toString(), null);
    }

    public static void error(final Throwable err) {
        alert(Alert.AlertType.ERROR, "", err.toString(), null);
    }

    public static void alert(final Alert.AlertType type, final String title, final String message,
                             final Delegates.Callback<ButtonType> callback) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        if (null != callback) {
            alert.showAndWait().ifPresent((button) -> {
                Delegates.invoke(callback, button);
            });
        } else {
            alert.show();
        }
    }

}
