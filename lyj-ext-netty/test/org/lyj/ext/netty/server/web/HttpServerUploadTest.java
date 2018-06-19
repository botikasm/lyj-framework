package org.lyj.ext.netty.server.web;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.Lyj;
import org.lyj.ext.netty.TestInitializer;
import org.lyj.ext.netty.server.web.handlers.impl.ResourceHandler;
import org.lyj.ext.netty.server.web.handlers.impl.RoutingHandler;
import org.lyj.ext.netty.server.web.handlers.impl.UploadHandler;


public class HttpServerUploadTest {


    @BeforeClass
    public static void setUpClass() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void testUpload() throws Exception {


        final HttpServer server = new HttpServer();
        server.config().port(4000)
                .portAutodetect(true)
                .corsAllowOrigin("*")
                .root(Lyj.getAbsolutePath("./htdocs"));

        RoutingHandler routing = RoutingHandler.create(server.config());
        server.handler(routing);
        // add routing
        routing.get("/version/*").handler(this::sampleHandler);

        // upload handler has default internal upload route "/upload/*"
        UploadHandler upload = UploadHandler.create(server.config());
        server.handler(upload);
        upload.onFileUpload(this::onUpload);

        // RESOURCE HANDLER ALWAYS AT THE END OF CHAIN
        server.handler(ResourceHandler.create(server.config()));

        System.out.println("To test REST handler try: " + server.config().uri("version"));

        server.start().join();
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void sampleHandler(final HttpServerContext context) {
        System.out.println("HANDLED REST REQUEST: method=" + context.method() + ", " + context.uri() + " - params: " + context.params().toString());
        final HttpParams params = context.params();

        final String paramType = (String) params.get("type");  // test with POST

        // write response
        if ("json".equals(paramType)) {
            context.writeJson("THIS IS JSON");
        } else if ("html".equals(paramType)) {
            context.writeHtml("<div>THIS IS HTML</div>");
        } else if ("xml".equals(paramType)) {
            context.writeXml("<tag prop='attribute'>THIS IS XML</tag>");
        } else {
            context.write("THIS IS PLAIN TEXT");
        }
    }

    private void onUpload(final UploadHandler.FileInfo fileInfo) {
        System.out.println("UPLOADED: " + fileInfo.toString());
    }
}