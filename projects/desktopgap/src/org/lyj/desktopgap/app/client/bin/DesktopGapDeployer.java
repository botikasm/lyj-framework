package org.lyj.desktopgap.app.client.bin;

import org.lyj.Lyj;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopgap.app.DesktopGap;
import org.lyj.gui.application.app.utils.FontRegistry;

/**
 * Deploy client framework
 */
public class DesktopGapDeployer extends FileDeployer {


    // ------------------------------------------------------------------------
    //                      c o n s
    // ------------------------------------------------------------------------

    public static final String PATH = "htdocs";

    private static final String[] FONTS = new String[]{"ttf"};

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DesktopGapDeployer(final boolean silent) {
        super("", Lyj.getAbsolutePath(PATH),
                silent, false, false, false);
        super.setOverwrite(true);
        super.settings().preprocessorFileExts().add(".json");
        super.settings().preprocessorModel().put("port", DesktopGap.instance().webConfig().port() + "");

        super.settings().callback((data, fileName) -> {
            final String ext = PathUtils.getFilenameExtension(fileName, false);
            if (CollectionUtils.contains(FONTS, ext)) {
                FontRegistry.instance().load(data, PathUtils.getFilename(fileName, false));
            }
            return data;
        });
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

    public static DesktopGapDeployer create() {
        return new DesktopGapDeployer(Lyj.isSilent());
    }


}