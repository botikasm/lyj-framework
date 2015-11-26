package org.ly.commons.network.socket.server;

import junit.framework.TestCase;
import org.ly.commons.network.socket.client.Client;
import org.ly.commons.network.socket.server.handlers.impl.FilterEcho;
import org.ly.commons.network.socket.server.handlers.impl.FilterEchoDate;
import org.ly.commons.network.socket.server.handlers.impl.FilterEchoNull;

import java.util.Date;
import java.util.ResourceBundle;

public class TestObjects extends TestCase {

    static private int port;
    static private String host;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("org.smartly.commons.network.socket.server.TestServer");
        port = 10 + Integer.parseInt(resources.getString("server.port"));
        host = resources.getString("server.host");
    }

    public void testEcho() throws Exception {
        Server simpleSocketServer = Server.startServer(port, new Class[]{FilterEcho.class});
        String[] strings = {"Hello", "World"};
        String[] response = (String[]) Client.send(host, port, strings);
        assertTrue(response[0].equals("Hello"));
        simpleSocketServer.stopServer();
    }

    public void testDate() throws Exception {
        Server simpleSocketServer = Server.startServer(port, new Class[]{FilterEchoDate.class});
        Date serverDate = (Date) Client.send(host, port, (Object) null);
        long halfTripTimeMsec = (new Date()).getTime() - serverDate.getTime();
        System.out.println("Half Trip Time: " + halfTripTimeMsec);
        assertTrue(halfTripTimeMsec >= 0 && halfTripTimeMsec < 1000);
        simpleSocketServer.stopServer();
    }

    public void testNull() throws Exception {
        Server simpleSocketServer = Server.startServer(port, new Class[]{FilterEchoNull.class});
        Date serverDate = (Date) Client.send(host, port, (Object) null);
        assertTrue(serverDate == null);
        simpleSocketServer.stopServer();
    }

}
