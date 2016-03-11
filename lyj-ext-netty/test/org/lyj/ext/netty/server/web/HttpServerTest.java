package org.lyj.ext.netty.server.web;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.Lyj;
import org.lyj.ext.netty.TestInitializer;
import org.lyj.ext.netty.server.web.handlers.impl.RequestInspectorHandler;
import org.lyj.ext.netty.server.web.handlers.impl.ResourceHandler;

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
        server.handler(new RequestInspectorHandler(server.config()));

        server.start().join();
    }

    @Test
    public void testJoin() throws Exception {

        final HttpServer server = new HttpServer();
        server.config().port(4000).portAutodetect(true).root(Lyj.getAbsolutePath("./htdocs"));
        server.handler(new ResourceHandler(server.config()));

        server.start().join();
    }

}