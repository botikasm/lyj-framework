package org.ly.ose.server.application.endpoints.socket;

import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.TestInitializer;
import org.ly.ose.server.application.importer.PackageImporter;
import org.ly.ose.server.application.programming.deploy_test.ProgramsTestDeployer;
import org.lyj.Lyj;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.netty.client.websocket.WebSocketClient;

public class SocketControllerTest {

    private static final long sleep = 20 * 1000;

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.initAll();

        final String deploy_root = PathUtils.getAbsolutePath("config/programs");
        (new ProgramsTestDeployer(deploy_root, Lyj.isSilent())).deploy();

        PackageImporter.instance().force();
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------


    @Test
    public void notifyRequest() throws Exception {
        try (final WebSocketClient client = new WebSocketClient("wss://localhost:8181/websocket")) {
            final WebSocketClient.Channel channel = client.open();
            System.out.println("------> endpoint reached: " + client.endPoint());

            final OSERequest request = new OSERequest();
            request.uid("channel_" + channel.hashCode());
            request.lang(IConstants.DEF_LANG);
            request.type(OSERequest.TYPE_PROGRAM);

            final OSEPayloadProgram payload = new OSEPayloadProgram(request.payload());
            payload.namespace("system.utils");
            payload.function("version");
            payload.sessionTimeout(-1); // uses system timeout

            channel.send(request.toString());
            channel.onReceiveText(this::onReceive);

            // wait few seconds for execution
            Thread.sleep(sleep);
        }


    }

    @Test
    public void runTestsAll() throws Exception {
        try (final WebSocketClient client = new WebSocketClient("wss://localhost:8181/websocket")) {
            final WebSocketClient.Channel channel = client.open();
            System.out.println("------> endpoint reached: " + client.endPoint());

            final OSERequest request = new OSERequest();
            request.uid("channel_" + channel.hashCode());
            request.lang(IConstants.DEF_LANG);
            request.type(OSERequest.TYPE_PROGRAM);

            final OSEPayloadProgram payload = new OSEPayloadProgram(request.payload());
            payload.namespace("tests.all");
            payload.function("database.findEqualAsc");
            payload.params();
            payload.sessionTimeout(-1); // uses system timeout

            channel.send(request.toString());
            channel.onReceiveText(this::onReceive);

            // wait few seconds for execution
            Thread.sleep(sleep);
        }


    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private void onReceive(final String text) {
        System.out.println("Socket Response: " + text);
        final OSEResponse response = new OSEResponse(text);
        if(response.hasPayload()){
            final JSONArray array = response.payload();
            System.out.println("RESPONSE:");
            CollectionUtils.forEach(array, (item)->{
                System.out.println("\t" + item.toString());
            });

        }
    }


}