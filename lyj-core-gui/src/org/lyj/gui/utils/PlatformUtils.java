package org.lyj.gui.utils;

import javafx.application.Platform;
import org.lyj.commons.Delegates;

/**
 *
 */
public class PlatformUtils {

    public static void synch(final Delegates.Handler callback){
        Platform.runLater(() -> {
            Delegates.invoke(callback);
        });
    }

}
