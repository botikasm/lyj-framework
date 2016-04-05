/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lyj.desktopfences.deploy.htdocs;


import org.lyj.Lyj;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.desktopgap.app.DesktopGap;
import org.lyj.gui.application.app.utils.FontRegistry;

public class HtdocsDeployer extends FileDeployer {


    // ------------------------------------------------------------------------
    //                      c o n s
    // ------------------------------------------------------------------------

    public static final String PATH = "htdocs";

    private static final String[] FONTS = new String[]{"ttf", "woff"};

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HtdocsDeployer(final boolean silent) {
        super("",
                Lyj.getAbsolutePath(PATH),
                silent,
                false,
                false,
                false);
        super.setOverwrite(true); // overwrite default desktopgap index page
        super.settings().excludeFileOrExts().add("/src/*"); // TODO: implement directory exclusion
        super.settings().preprocessorFileExts().add(".css");
        super.settings().preprocessorModel().put("port", DesktopGap.instance().webConfig().port()+"");

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
}
