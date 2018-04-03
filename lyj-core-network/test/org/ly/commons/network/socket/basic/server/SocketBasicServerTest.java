package org.ly.commons.network.socket.basic.server;

import org.junit.Test;
import org.ly.commons.network.socket.basic.client.SocketBasicClient;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.util.RandomUtils;

import static org.junit.Assert.assertNotNull;

public class SocketBasicServerTest {

    @Test
    public void nio() throws Exception {
        System.out.println("Start server");
        SocketBasicServer server = new SocketBasicServer();
        server.port(5000)
                .onChannelOpen(this::channelOpen)
                .onChannelClose(this::channelClose)
                .onChannelMessage(this::channelMessage)
                .open();


        int count = 0;
        while (count < 20) {
            Thread.sleep(1000);
            count++;

            SocketBasicClient client = new SocketBasicClient();
            client.host("127.0.0.1");
            client.port(server.port());

            SocketMessage response = client.send(count + ": " + RandomUtils.randomAlphanumeric(10000));
            assertNotNull(response);
            System.out.println(response.toString());

            count++;
            response = client.send(count + ": " + RandomUtils.randomAlphanumeric(10000));
            assertNotNull(response);
            System.out.println(response.toString());
        }

        System.out.println("LOOP: "+ count);
        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.close();
        System.out.println("Stop server");
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