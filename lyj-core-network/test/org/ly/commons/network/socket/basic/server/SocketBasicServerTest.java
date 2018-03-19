package org.ly.commons.network.socket.basic.server;

import org.junit.Test;
import org.ly.commons.network.socket.basic.client.SocketBasicClient;
import org.lyj.commons.async.Async;

public class SocketBasicServerTest {

    @Test
    public void nio() throws Exception {
        System.out.println("Start server");
        SocketBasicServer server = new SocketBasicServer();
        server.port(5000).open();



        int count = 0;
        while(count<20){
            Thread.sleep(1000);
            count++;
            
            SocketBasicClient client = new SocketBasicClient();
            client.host("127.0.0.1");
            client.port(server.port());
            client.open();
            client.send("Message: " + count + "\n");

            //client.close(); // java.io.IOException: Connection reset by peer
        }

        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Stop server");
    }
}