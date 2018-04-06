package org.ly.commons.network.socket.basic;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.commons.network.socket.basic.client.SocketBasicClient;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.basic.server.SocketBasicServer;
import org.lyj.TestInitializer;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;

import java.io.File;

import static org.junit.Assert.assertFalse;
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

            SocketBasicClient client_ssl = this.getClient(server.port());
            SocketBasicClient client = this.getClient(server.port());

            // HANDSHAKE
            client_ssl.handShake();
            

            int count = 0;

            SocketMessage response = client_ssl.send(count + ": " + RandomUtils.randomAlphanumeric(6));
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

            count++;
            response = client_ssl.send(count + ": " + "This is a message");
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

            // send file encoded
            File file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));
            response = client_ssl.send(file, MapBuilder.createSO()
                    .put("file_size", file.length())
                    .put("file_name", file.getName())
                    .toMap());
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

            // error if server does not distinguish between clients
            response = client.send("This is a clear message");
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

            // send file clear
            response = client.send(file, MapBuilder.createSO()
                    .put("file_size", file.length())
                    .put("file_name", file.getName())
                    .toMap());
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));
            
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void startTestClear() throws Exception {

        try (final SocketBasicServer server = this.getServer()) {

            File file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));

            SocketBasicClient client = this.getClient(server.port());

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

            // send file clear
            response = client.send(file, MapBuilder.createSO()
                    .put("file_size", file.length())
                    .put("file_name", file.getName())
                    .toMap());
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));
            
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void startTestFileCrypto() throws Exception {

        try (final SocketBasicServer server = this.getServer()) {

            SocketBasicClient client_ssl = this.getClient(server.port());
            
            // HANDSHAKE
            client_ssl.handShake();

            // send file clear encoded
            File file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));
            SocketMessage response = client_ssl.send(file, MapBuilder.createSO()
                    .put("file_size", file.length())
                    .put("file_name", file.getName())
                    .toMap());
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void startTestFileClear() throws Exception {

        try (final SocketBasicServer server = this.getServer()) {

            SocketBasicClient client = this.getClient(server.port());

            File small_file = new File(PathUtils.getAbsolutePath("./sample_small_file.txt"));
            System.out.println("SENDING BYTES: " + small_file.length());
            SocketMessage response = client.send(small_file, MapBuilder.createSO()
                    .put("file_size", small_file.length())
                    .put("file_name", small_file.getName())
                    .toMap());
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

            File medium_file = new File(PathUtils.getAbsolutePath("./sample_medium_file.txt"));
            System.out.println("SENDING BYTES: " + medium_file.length());
            response = client.send(medium_file, MapBuilder.createSO()
                    .put("file_size", medium_file.length())
                    .put("file_name", medium_file.getName())
                    .toMap());
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

            File large_file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));
            System.out.println("SENDING BYTES: " + large_file.length());
            response = client.send(large_file, MapBuilder.createSO()
                    .put("file_size", large_file.length())
                    .put("file_name", large_file.getName())
                    .toMap());
            assertNotNull(response);
            assertTrue(response.isValid());
            System.out.println(response.toString());
            System.out.println(new String(response.body()));

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void startTestFileClearMultiple() throws Exception {

        try (final SocketBasicServer server = this.getServer()) {

            SocketBasicClient client = this.getClient(server.port());

            File small_file = new File(PathUtils.getAbsolutePath("./sample_small_file.txt"));

            System.out.println("SENDING BYTES: " + small_file.length());
            for(int i=0;i<100;i++){
                SocketMessage response = client.send(small_file, MapBuilder.createSO()
                        .put("file_size", small_file.length())
                        .put("file_name", small_file.getName())
                        .toMap());
                assertNotNull(response);
                assertTrue(response.isValid());
                System.out.println(i);
            }
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
        if(request.type().equals(SocketMessage.MessageType.File)){
             // file
            System.out.println("Receiving file: " + request.headers().getString("file_name")
                    + " (" + request.headers().getString("file_size") + " bytes)");
             response.body("FILE RECEIVED!");
        } else {
            // echo
            final String echo = "echo: " + new String(request.body());
            response.body(echo);
        }
    }

    private void channelOpen(SocketBasicServer.ChannelInfo channelInfo) {
        // System.out.println(channelInfo.localAddress() + " " + channelInfo.remoteAddress() + " " + channelInfo.signature() + " " + channelInfo.uid());
    }

    private void channelClose(SocketBasicServer.ChannelInfo channelInfo) {

    }
}
