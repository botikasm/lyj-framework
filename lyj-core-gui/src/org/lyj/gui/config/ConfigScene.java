package org.lyj.gui.config;

import org.lyj.Lyj;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;
import org.lyj.gui.app.utils.Size;

/**
 * Helper class to wrap scene configuration
 */
public class ConfigScene {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH = "scene";
    private static final String PATH_TITLE = "title";
    private static final String PATH_STAGE = "stage";
    private static final String PATH_SIZE_WIDTH = "size.width";
    private static final String PATH_SIZE_HEIGHT = "size.height";
    private static final String PATH_ICON = "icon";
    private static final String PATH_AUTO_SHOW = "auto-show";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private JsonWrapper __configuration;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ConfigScene() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean hasStage(){
        return StringUtils.hasText(this.stage());
    }

    public String title() {
        return this.configuration().getString(PATH_TITLE);
    }

    public String stage() {
        return this.configuration().getString(PATH_STAGE);
    }

    public Size size(){
        return new Size(this.width(), this.height());
    }

    public double width(){
        return this.configuration().deepDouble(PATH_SIZE_WIDTH, 300);
    }

    public double height(){
        return this.configuration().deepDouble(PATH_SIZE_HEIGHT, 300);
    }

    public boolean autoShow(){
        return this.configuration().deepBoolean(PATH_AUTO_SHOW, true);
    }

    public String icon() {
        return this.configuration().deepString(PATH_ICON);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JsonWrapper configuration(){
        if(null==__configuration) {
            __configuration = new JsonWrapper(Lyj.getConfiguration().getJSONObject(PATH));
        }
        return __configuration;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private ConfigScene __instance;

    public ConfigScene instance() {
        if (null == __instance) {
            __instance = new ConfigScene();
        }
        return __instance;
    }

}
