package org.lyj.ext.netty.client.websocket;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.commons.async.Async;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.TestInitializer;
import org.lyj.ext.netty.server.websocket.WebSocketServer;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionClientController;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionMessage;

/**
 *
 */
public class HttpWebSocketClientTest {

    static final String URL = System.getProperty("url", "ws://127.0.0.1:8083/websocket");

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void testClient() throws Exception {
        // start the server
        this.runServer(8083);

        final WebSocketClient client = new WebSocketClient(URL);



        client.open()
                .onReceivePong(this::onPong)
                .onReceiveText(this::onText)
                .ping()
                .send("hello");


        Thread.sleep(2000);

        client.close();

        // wait before exit
        // Thread.sleep(200000);
    }




    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void runServer(final int port) throws Exception {
        Async.invoke((args) -> {
            try {
                final WebSocketServer server = new WebSocketServer();
                server.config()
                        .port(port)
                        .portAutodetect(true);

                server.start();

                System.out.println("SERVER ON PORT: " + server.config().port());

                // init socket listener
                SessionClientController.instance().listener(this::onSocketRequest);
            } catch (Throwable ignored) {
                // ignored
            }
        });
    }

    private void onSocketRequest(final String session_id, final Object data) {
        if (StringUtils.hasText(session_id) && null != data) {

            System.out.println("Session: " + session_id + ". DATA: " + data);

            System.out.println("Address: " + SessionClientController.instance().addressOf(session_id));

            SessionClientController.instance().open(session_id).write(data);

            SessionClientController.instance().open(session_id).write("PUSHED");


        }
    }

    private void onPong() {
        System.out.println("CLIENT: pong");
    }

    private void onText(final String text) {
        if(StringUtils.isJSONObject(text)){
            SessionMessage message = new SessionMessage(text);
            System.out.println("CLIENT: " + message.asText());
        } else {
            System.out.println("CLIENT: " + text);
        }
    }

}