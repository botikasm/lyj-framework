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

package org.ly.commons.network.sftp.impl;

import com.jcraft.jsch.SftpProgressMonitor;

import javax.swing.*;

/**
 * User: angelo.geminiani
 */
public class SFTPProgressMonitor implements SftpProgressMonitor {

    private long _percent = -1;
    private ProgressMonitor _monitor;
    private long _count = 0;
    private long _max = 0;

    public SFTPProgressMonitor() {

    }

    public void init(int op, String src, String dest, long max) {
        _max = max;
        _monitor = new ProgressMonitor(null,
                ((op == SftpProgressMonitor.PUT) ?
                        "put" : "get") + ": " + src,
                "", 0, (int) max);
        _count = 0;
        _percent = -1;
        _monitor.setProgress((int) this._count);
        _monitor.setMillisToDecideToPopup(1000);
    }

    public boolean count(long count) {
        _count += count;

        if (_percent >= this._count * 100 / _max) {
            return true;
        }
        _percent = this._count * 100 / _max;

        _monitor.setNote("Completed " + this._count + "(" + _percent + "%) out of " + _max + ".");
        _monitor.setProgress((int) this._count);

        return !(_monitor.isCanceled());
    }

    public void end() {
        _monitor.close();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
