package org.lyj.gui.application.app.utils;

import javafx.application.Platform;
import javafx.scene.control.*;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.i18n.DictionaryController;
import org.lyj.commons.util.BeanUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

/**
 *
 */
public class PlatformUtils {

    // ------------------------------------------------------------------------
    //                      m a i n   t h r e a d
    // ------------------------------------------------------------------------

    public static void synch(final Delegates.Handler callback) {
        Platform.runLater(() -> {
            try {
                Delegates.invoke(callback);
            } catch (Throwable ignored) {
            }
        });
    }

    public static void delay(final Delegates.VarArgsCallback handler, final int delay) {
        Async.delay((params) -> {
            synch(() -> {
                if (null != handler) {
                    handler.handle(params);
                }
            });
        }, delay);
    }

    public static void delay(final Delegates.VarArgsCallback handler, final int delay, final Object... args) {
        Async.delay((params) -> {
            synch(() -> {
                if (null != handler) {
                    handler.handle(params);
                }
            });
        }, delay, args);
    }

    // ------------------------------------------------------------------------
    //                      i 1 8 n
    // ------------------------------------------------------------------------

    public static String i18n(final DictionaryController dictionary, final String lang, final String key, final Object... args) {
        if (null != dictionary) {
            return dictionary.get(lang, key, args);
        }
        return "";
    }

    public static void i18n(final DictionaryController dictionary,
                            final String lang,
                            final Object[] controls,
                            final String prop,
                            final Object... args) {
        CollectionUtils.forEach(controls, (item, key, index)->{
            i18n(dictionary, lang, item, prop, args);
        });
    }
    public static void i18n(final DictionaryController dictionary,
                            final String lang,
                            final Object control,
                            final String prop,
                            final Object... args) {
        final String id = (String)BeanUtils.getValueIfAny(control, "id", "");
        if (StringUtils.hasText(id)) {
            final String text = StringUtils.hasText(prop)
                    ? i18n(dictionary, lang, id + "." + prop, args)
                    : i18n(dictionary, lang, id, args);
            if (StringUtils.hasText(text)) {
                if (control instanceof Label) {
                    // LABEL
                    ((Label) control).textProperty().setValue(text);
                } else if (control instanceof TextField) {
                    // TEXT FIELD
                    if (StringUtils.hasText(prop)) {
                        if (prop.equals("prompt")) {
                            ((TextField) control).promptTextProperty().setValue(text);
                        }
                    } else {
                        ((TextField) control).textProperty().setValue(text);
                    }
                } else if (control instanceof TextArea) {
                    // TEXT AREA
                    if (StringUtils.hasText(prop)) {
                        if (prop.equals("prompt")) {
                            ((TextArea) control).promptTextProperty().setValue(text);
                        }
                    } else {
                        ((TextArea) control).textProperty().setValue(text);
                    }
                } else if (control instanceof Button){
                    // BUTTON
                    ((Button) control).textProperty().setValue(text);
                } else if (control instanceof Tab){
                    // TAB
                    ((Tab) control).textProperty().setValue(text);
                }
            }
        }
    }
}
