package org.lyj.ext.netty.server.websocket;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.Lyj;
import org.lyj.ext.netty.TestInitializer;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.web.HttpServerContext;
import org.lyj.ext.netty.server.web.handlers.impl.RequestInspectorHandler;
import org.lyj.ext.netty.server.web.handlers.impl.ResourceHandler;
import org.lyj.ext.netty.server.web.handlers.impl.RoutingHandler;

/**
 *
 */
public class HttpWebSocketServerTest {


    @BeforeClass
    public static void setUpClass() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void testJoin() throws Exception {



        final WebSocketServer server = new WebSocketServer();
        server.config().port(8083).portAutodetect(true);

        System.out.println("open client.html in browser");

        server.start().join();
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------



}