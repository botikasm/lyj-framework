package org.lyj.desktopgap.app.client.bin;

import org.lyj.Lyj;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.desktopgap.app.Application;

/**
 * Deploy client framework
 */
public class DesktopGapDeployer extends FileDeployer {


    // ------------------------------------------------------------------------
    //                      c o n s
    // ------------------------------------------------------------------------

    public static final String PATH = "htdocs";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopGapDeployer(final boolean silent) {
        super("", Lyj.getAbsolutePath(PATH),
                silent, false, false, false);
        super.setOverwrite(true);
        super.settings().preprocessorFileExts().add(".json");
        super.settings().preprocessorModel().put("port", Application.instance().webConfig().port()+"");
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    @Override
    public void deploy() {
        super.deploy();
    }

    @Override
    public byte[] compile(byte[] data, final String filename) {
        return data;
    }

    @Override
    public byte[] compress(byte[] data, final String filename) {
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static DesktopGapDeployer create(){
        return new DesktopGapDeployer(Lyj.isSilent());
    }

}