package org.lyj.ext.netty.server.web.handlers.impl;


import org.lyj.commons.Delegates;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import java.io.File;

/**
 * Manage resource requests returning files to browsers
 */
public class ResourceHandler
        extends AbstractRequestHandler {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ResourceHandler(final HttpServerConfig config) {
        super(config);
        _root = config.root();
    }

    @Override
    public void handle(final HttpServerRequest request, final HttpServerResponse response) {
        this.validate((err, valid) -> {
            if (null != err) {
                response.writeErrorINTERNAL_SERVER_ERROR();
                response.handled(true);
                super.error("handle", err);
            } else {
                final String uri = request.uri();
                final String path = super.config().filePath(uri);
                if (StringUtils.hasText(path)) {

                    final File file = this.lookupFile(path);
                    if (null == file) {
                        response.writeErrorNOT_FOUND();
                        response.handled(true);
                    } else {
                        // got the file
                        if (!request.isModifiedSince(file)) {
                            // file is the same
                            response.writeNotModified();
                            response.handled(true);
                        } else {
                            response.writeFile(file);
                            response.handled(false); // allow other chain handlers to modify response content
                        }
                    }
                } else {
                    response.writeErrorFORBIDDEN();
                    response.handled(true);
                }
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void validate(final Delegates.SingleResultCallback<Boolean> callback) {
        try {
            if (!PathUtils.exists(_root)) {
                Delegates.invoke(callback, new Exception("Bad configuration: missing HTDOCS root"), false);
            } else {
                Delegates.invoke(callback, null, true);
            }
        } catch (Throwable t) {
            Delegates.invoke(callback, t, false);
        }
    }

    private File lookupFile(final String path) {
        try {
            if (PathUtils.isFile(path)) {
                final File file = new File(path);
                return file.exists() ? file : null;
            } else {
                return this.fetchIndex(path);
            }
        } catch (Throwable ignored) {
            return this.fetchIndex(path);
        }
    }

    private File fetchIndex(final String path) {
        final String[] index_files = super.config().indexFiles();
        for (final String name : index_files) {
            final String index_path = PathUtils.concat(path, name);
            final File file = new File(index_path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static ResourceHandler create(final HttpServerConfig config) {
        return new ResourceHandler(config);
    }
}
