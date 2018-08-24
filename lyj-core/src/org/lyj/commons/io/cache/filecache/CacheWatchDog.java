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

package org.lyj.commons.io.cache.filecache;

import org.lyj.commons.io.FileObserver;
import org.lyj.commons.io.IFileObserverListener;
import org.lyj.commons.io.cache.filecache.registry.IRegistry;
import org.lyj.commons.util.FormatUtils;

/**
 * Repository with expiration time for its content.
 * Expired files are removed
 */
public class CacheWatchDog
        extends AbstractFileCache
        implements IFileObserverListener {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int MAX_ERRORS = 10;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private FileObserver _dirObserver;
    private int _countErrors;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CacheWatchDog(final String root) {
        super(root);
        _countErrors = 0;
    }

    public CacheWatchDog(final String root,
                         final long duration) {
        super(root, duration, duration, IRegistry.Mode.Memory);
        _countErrors = 0;
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    @Override
    public void onEvent(int event, final String path) {
        this.handle(event, path);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void open() {
        super.open();
        this.startObserver();
    }

    @Override
    public void close() {
        super.close();

        this.stopObserver();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void stopObserver() {
        try {
            _dirObserver.interrupt();
            _dirObserver = null;
        } catch (Throwable ignored) {
        }
    }

    private void startObserver() {
        //-- file observer initialization --//
        try {
            if (null != _dirObserver) {
                _dirObserver.interrupt();
                _dirObserver = null;
            }
            _dirObserver = new FileObserver(super.root(), true, false, FileObserver.ALL_EVENTS, this);
            _dirObserver.startWatching();
        } catch (Throwable t) {
            super.logger().error(t.toString());
        }
    }

    private void handle(final int event, final String path) {
        if (!super.pathLog().equalsIgnoreCase(path)) {
            String sevent = "UNDEFINED";
            try {
                if (event == FileObserver.EVENT_CREATE) {
                    sevent = "CREATE";
                    // CREATE
                    if (!super.pathData().equalsIgnoreCase(path)
                            && !super.pathSettings().equalsIgnoreCase(path)) {
                        super.registryAddItem(path, super.duration());
                        this.debug(FormatUtils.format("Action '{0}' on '{1}'", sevent, path));
                    }
                } else if (event == FileObserver.EVENT_MODIFY) {
                    sevent = "MODIFY";
                    // MODIFY
                    if (super.pathSettings().equalsIgnoreCase(path)) {
                        super.registryReloadSettings();
                        super.debug("Changed Settings: reload all settings from file.");
                    }
                } else if (event == FileObserver.EVENT_DELETE) {
                    sevent = "DELETE";
                    if (!super.pathData().equalsIgnoreCase(path)
                            && !super.pathSettings().equalsIgnoreCase(path)) {
                        super.registryRemoveItemByPath(path);
                        super.debug(FormatUtils.format("Action '{0}' on '{1}'", sevent, path));
                    } else {
                        super.registryClear();
                        super.registrySave();
                        super.debug("Removed DATA file: reset of registry.");
                    }
                }
            } catch (final Throwable t) {
                final String msg = FormatUtils.format("Error on '{0}' path '{1}' to temp repository: {2}",
                        sevent, path, t);
                this.handleError(msg, t);
            }
        }
    }

    protected void handleError(final String message,
                               final Throwable t) {
        if (_countErrors < MAX_ERRORS) {
            _countErrors++;
            super.error("handleError", message, t);
        } else {
            this.close();
        }
    }

}
