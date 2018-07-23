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

package org.ly.server.application.programming.deploy;


import org.ly.server.application.importer.PackageImporter;
import org.ly.server.loggers.MainLogger;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.ZipUtils;

import java.io.File;

public class ProgramsDeployer
        extends FileDeployer {

    private MainLogger _logger;

    public ProgramsDeployer(final String targetFolder, final boolean silent) {
        super("", targetFolder,
                silent, false, false, false);

        super.setOverwrite(true);

        _logger = new MainLogger();
    }

    @Override
    public byte[] compile(byte[] data, final String filename) {
        return data;
    }

    @Override
    public byte[] compress(byte[] data, final String filename) {
        return null;
    }

    @Override
    public void deploy() {
        super.deploy();
    }

    @Override
    public void finish() {
        // creates packages and move into importer folder
        _logger.info("finish", "CREATE PACKAGES FROM: " + this.getTargetFolder());

        final File[] dirs = FileUtils.listDirs(this.getTargetFolder());
        if (dirs.length > 0) {
            for (final File dir : dirs) {
                final String tmp = PathUtils.getTemporaryFile(RandomUtils.randomUUID(true) + ".zip");
                try {
                    // creates package
                    ZipUtils.zipDir(tmp, dir, false);

                    // move package
                    PackageImporter.instance().put(new File(tmp));

                    // remove dir
                    FileUtils.tryDelete(dir);
                } catch (Throwable t) {
                    _logger.error("finish", t);
                } finally {
                    FileUtils.tryDelete(tmp);
                }

            }
        }

        _logger.info("finish", "CREATED PACKAGES FROM: " + this.getTargetFolder());
        FileUtils.tryDelete(this.getTargetFolder());
    }
}
