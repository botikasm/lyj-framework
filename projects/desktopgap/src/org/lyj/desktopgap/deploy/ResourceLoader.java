package org.lyj.desktopgap.deploy;

import javafx.scene.image.Image;
import org.lyj.commons.util.PathUtils;

import java.io.InputStream;

/**
 *
 */
public final class ResourceLoader {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ResourceLoader() {
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Image getImage(final String path) {
        if (PathUtils.isAbsolute(path)) {
            return new Image(path);
        } else {
            InputStream is = this.getClass().getResourceAsStream(path);
            if (null != is) {
                return new Image(is);
            }
        }
        return null;
    }


    public InputStream read(final String path){
        return this.getClass().getResourceAsStream(path);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ResourceLoader __instance;

    public static ResourceLoader instance() {
        if (null == __instance) {
            __instance = new ResourceLoader();
        }
        return __instance;
    }


}
