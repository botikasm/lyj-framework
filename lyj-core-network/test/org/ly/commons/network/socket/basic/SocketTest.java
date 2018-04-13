package org.ly.commons.network.socket.basic;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.commons.network.socket.basic.client.SocketBasicClient;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.basic.server.SocketBasicServer;
import org.lyj.TestInitializer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;

import java.io.File;

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
            File file = new File(PathUtils.getAbsolutePath("./sample_small_file.txt"));
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
    public void startTestSSL() throws Exception {

        try (final SocketBasicServer server = this.getServer()) {

            SocketBasicClient client_ssl = this.getClient(server.port());

            // HANDSHAKE
            client_ssl.handShake();

            int count = 0;

            SocketMessage response = client_ssl.send(count + ": " + "This is a message");
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
    public void startTestFileSingle() throws Exception {

        SocketSettings.CHUNK_SIZE = 2048;

        try (final SocketBasicServer server = this.getServer()) {

            SocketBasicClient client = this.getClient(server.port());

            client.handShake();

            File small_file = new File(PathUtils.getAbsolutePath("./sample_small_file.txt"));
            System.out.println("SENDING BYTES: " + small_file.length());
            for (int i = 0; i < 1; i++) {
                SocketMessage response = client.send(small_file, MapBuilder.createSO()
                        .put("file_size", small_file.length())
                        .put("file_name", small_file.getName())
                        .toMap());
                assertNotNull(response);
                assertTrue(response.isValid());
                System.out.println("END SENT: " + (i + 1));
                System.out.println(response);
                System.out.println(new String(response.body()));
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

        return client;
    }

    private void channelMessage(SocketBasicServer.ChannelInfo channelInfo,
                                SocketMessage request,
                                SocketMessage response) {
        try {
            final SocketMessage.MessageType type = request.type();
            if (type.equals(SocketMessage.MessageType.File)) {
                // file
                System.out.println("Receiving file: " + request.headers().getString("file_name")
                        + " (" + request.headers().getString("file_size") + " bytes)");
                response.type(SocketMessage.MessageType.Text);
                final String temp_file_name = request.headers().fileName();
                System.out.println(temp_file_name);
                if (PathUtils.exists(temp_file_name)) {
                    final String text = FileUtils.readFileToString(new File(temp_file_name));
                    response.body(text);
                    FileUtils.delete(temp_file_name);
                } else {
                    response.body("FILE NOT FOUND: " + temp_file_name);
                }
            } else if (response.isChunk()) {
                final String body = new String(request.body());
                final String file_name = PathUtils.getFilename(response.headers().fileName());
                System.out.println("CHUNK: " + response.hashCode() + " " + response.ownerId() + " " + response.headers());
            } else {
                // echo
                final String echo = "echo: " + new String(request.body());
                response.body(echo);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void channelOpen(SocketBasicServer.ChannelInfo channelInfo) {
        // System.out.println(channelInfo.localAddress() + " " + channelInfo.remoteAddress() + " " + channelInfo.signature() + " " + channelInfo.uid());
    }

    private void channelClose(SocketBasicServer.ChannelInfo channelInfo) {

    }
}
