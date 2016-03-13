package org.lyj.ext.netty.server.web.handlers.impl;

import org.lyj.commons.util.RandomUtils;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Write a buffer with request data.
 * Useful for debug or request inspection
 */
public class RequestInspectorHandler
        extends AbstractRequestHandler {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private RequestInspectorHandler(final HttpServerConfig config) {
        super(config);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void handle(final HttpServerRequest request,
                       final HttpServerResponse response) {

        response.headers().put(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().put("Inspector-Token", RandomUtils.randomUUID());

        response.buffer().write("WELCOME TO THE WILD WILD WEB SERVER\r\n");
        response.buffer().write("===================================\r\n");

        response.buffer().write("UUID: ").write(request.uuid()).write("\r\n");
        response.buffer().write("VERSION: ").write(request.protocolVersion().toString()).write("\r\n");
        response.buffer().write("HOSTNAME: ").write(request.host()).write("\r\n");
        response.buffer().write("REQUEST_URI: ").write(request.uri()).write("\r\n\r\n");

        // headers
        final Set<String> headerNames = request.headerNames();
        for (final String name : headerNames) {
            String value = request.headerValue(name);
            response.buffer().write("HEADER: ").write(name).write(" = ").write(value).write("\r\n");
        }
        response.buffer().write("\r\n");

        // query params
        final Map<String, List<String>> params = request.queryParams();
        if (!params.isEmpty()) {
            for (Map.Entry<String, List<String>> p : params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    response.buffer().write("PARAM: ").write(key).write(" = ").write(val).write("\r\n");
                }
            }
            response.buffer().write("\r\n");
        }

        // body params
        final Map<String, String> body_params = request.bodyParams();
        for (Map.Entry<String, String> p : body_params.entrySet()) {
            String key = p.getKey();
            String val = p.getValue();
            response.buffer().write("BODY PARAM: ").write(key).write(" = ").write(val).write("\r\n");
        }
        response.buffer().write("\r\n");

        if (request.hasLastHttpContent()) {
            response.buffer().write("END OF CONTENT\r\n");

            if (!request.trailingHeaderNames().isEmpty()) {
                response.buffer().write("\r\n");
                for (String name : request.trailingHeaderNames()) {
                    for (String value : request.trailingHeaderAll(name)) {
                        response.buffer().write("TRAILING HEADER: ");
                        response.buffer().write(name).write(" = ").write(value).write("\r\n");
                    }
                }
                response.buffer().write("\r\n");
            }

            if (!request.statusIsSuccess()) {
                response.buffer().write(".. WITH DECODER FAILURE: ");
                response.buffer().write(request.statusFailureCauseMessage());
                response.buffer().write("\r\n");
            }
        }

        response.handled(true); // exit chain
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static RequestInspectorHandler create(final HttpServerConfig config) {
        return new RequestInspectorHandler(config);
    }


}
