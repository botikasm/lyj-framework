package org.ly.commons.network.socket.basic;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.commons.network.socket.basic.client.SocketBasicClient;
import org.ly.commons.network.socket.basic.message.SocketMessage;
import org.ly.commons.network.socket.basic.server.SocketBasicServer;
import org.lyj.TestInitializer;
import org.lyj.commons.util.RandomUtils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SocketTest {


    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void startTest() throws Exception {

        try (final SocketBasicServer server = this.getServer()) {

            SocketBasicClient client = this.getClient(server.port());

            // HANDSHAKE
            client.handShake();

            int count = 0;

            SocketMessage response = client.send(count + ": " + RandomUtils.randomAlphanumeric(6));
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

            count++;
            response = client.send(count + ": " + "This is a message");
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));
            
        } catch (Exception ex) {
            throw ex;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private SocketBasicServer getServer() {
        SocketBasicServer server = new SocketBasicServer();
        server.port(5000)
                .encrypt(true)
                .onChannelOpen(this::channelOpen)
                .onChannelClose(this::channelClose)
                .onChannelMessage(this::channelMessage)
                .open();
        return server;
    }

    private SocketBasicClient getClient(final int port) {
        final SocketBasicClient client = new SocketBasicClient();
        client.host("127.0.0.1");
        client.port(port);
        client.timeout(50000);

        return client;
    }

    private void channelMessage(SocketBasicServer.ChannelInfo channelInfo,
                                SocketMessage request,
                                SocketMessage response) {
        // echo
        response.body("echo: " + new String(request.body()));
    }

    private void channelOpen(SocketBasicServer.ChannelInfo channelInfo) {
        // System.out.println(channelInfo.localAddress() + " " + channelInfo.remoteAddress() + " " + channelInfo.signature() + " " + channelInfo.uid());
    }

    private void channelClose(SocketBasicServer.ChannelInfo channelInfo) {

    }
}
