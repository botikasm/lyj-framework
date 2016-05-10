package org.lyj.desktopgap.app.view.web;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.StringUtils;

/**
 * Java Bridge to pass to webview
 */
public class JavaBridge  extends AbstractLogEmitter {


    public void log(final Object value) {
        super.info("log", StringUtils.toString(value));
    }

}
