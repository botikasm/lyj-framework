package org.lyj.ext.netty.server.web;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.Lyj;
import org.lyj.ext.netty.TestInitializer;
import org.lyj.ext.netty.server.web.handlers.impl.RequestInspectorHandler;
import org.lyj.ext.netty.server.web.handlers.impl.ResourceHandler;
import org.lyj.ext.netty.server.web.handlers.impl.RoutingHandler;

/**
 * Created by angelogeminiani on 09/03/16.
 */
public class HttpServerTest {


    @BeforeClass
    public static void setUpClass() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void testStart() throws Exception {

        final HttpServer server = new HttpServer();
        //server.handler(new HttpStaticFileServerInitializer(server));
        server.start();

        Thread.sleep(3000);

        server.stop();
        System.out.println("CLOSED");

        Thread.sleep(3000);
    }

    @Test
    public void testInspect() throws Exception {

        final HttpServer server = new HttpServer();
        server.config().port(4000).portAutodetect(true).root(Lyj.getAbsolutePath("./htdocs"));
        server.handler(RequestInspectorHandler.create(server.config()));

        server.start().join();
    }

    @Test
    public void testJoin() throws Exception {


        final HttpServer server = new HttpServer();
        server.config().port(4000).portAutodetect(true).root(Lyj.getAbsolutePath("./htdocs"));

        RoutingHandler routing = RoutingHandler.create(server.config());
        server.handler(routing);
        server.handler(ResourceHandler.create(server.config()));

        // add routing
        routing.get("/version/*").handler(this::sampleHandler);
        routing.get("/params/:user_id/:user_name").handler(this::sampleHandler);
        routing.post("/params/:user_id/:user_name").handler(this::sampleHandler);
        routing.post("/upload/*").handler(this::upload);

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

    private void upload(final HttpServerContext context) {
        final HttpParams params = context.params();
        System.out.println("PARAMS: " + params.toQueryString());

        context.write("THIS IS PLAIN TEXT");
    }
}