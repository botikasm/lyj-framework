package org.lyj.ext.netty.server.web.handlers.impl;


import org.lyj.commons.Delegates;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.IHttpConstants;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

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
                final String path = this.sanitizeUri(request.uri());
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

    private String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                IHttpConstants.INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        // get htdocs path
        uri = PathUtils.concat(super.config().root(), uri);
        try {
            FileUtils.mkdirs(uri); // ensure directory exists
        } catch (Throwable ignore) {
        }

        return uri;
    }

    private File lookupFile(final String path) {
        if (PathUtils.isFile(path)) {
            final File file = new File(path);
            return file.exists() ? file : null;
        } else {
            final String[] index_files = super.config().indexFiles();
            for (final String name : index_files) {
                final String index_path = PathUtils.concat(path, name);
                final File file = new File(index_path);
                if (file.exists()) {
                    return file;
                }
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
