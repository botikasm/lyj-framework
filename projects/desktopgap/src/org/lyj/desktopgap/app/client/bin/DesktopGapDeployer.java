package org.lyj.desktopgap.app.client.bin;

import org.lyj.Lyj;
import org.lyj.commons.io.repository.deploy.FileDeployer;

/**
 * Deploy client framework
 *
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
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public byte[] compile(byte[] data, final String filename) {
        return data;
    }

    @Override
    public byte[] compress(byte[] data, final String filename) {
        return null;
    }

}