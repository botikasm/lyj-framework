package org.lyj.ext.netty.server.web;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.ext.netty.TestInitializer;

/**
 * Created by angelogeminiani on 09/03/16.
 */
public class WebServerTest {


    @BeforeClass
    public static void setUpClass() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void testStart() throws Exception {

        final WebServer server = new WebServer();
        server.handler(new HttpStaticFileServerInitializer(server.ssl()));
        server.start();

        Thread.sleep(3000);

        server.stop();
        System.out.println("CLOSED");

        Thread.sleep(3000);
    }


    @Test
    public void testJoin() throws Exception {

        final WebServer server = new WebServer();
        server.handler(new HttpStaticFileServerInitializer(server.ssl()));
        server.start().join();
    }

}