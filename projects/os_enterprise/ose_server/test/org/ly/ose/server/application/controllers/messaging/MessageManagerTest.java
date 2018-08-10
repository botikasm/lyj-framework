package org.ly.ose.server.application.controllers.messaging;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadDatabase;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.Launcher;
import org.ly.ose.server.TestInitializer;
import org.ly.ose.server.application.endpoints.api.ApiHelper;
import org.ly.ose.server.application.importer.PackageImporter;
import org.ly.ose.server.application.programming.deploy_test.ProgramsTestDeployer;
import org.lyj.Lyj;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import static org.junit.Assert.*;

public class MessageManagerTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();

        // deploy tests
        final String deploy_root = PathUtils.getAbsolutePath("config/programs");
        (new ProgramsTestDeployer(deploy_root, Lyj.isSilent())).deploy();
        PackageImporter.instance().force();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Test
    public void mainTest() throws Exception {

        OSEResponse response = this.testDatabase();
        assertNotNull(response);
        System.out.println("DATABASE:");
        System.out.println(response);

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private OSEResponse testDatabase(){
        final OSERequest request = new OSERequest();
        final OSEPayloadDatabase payload = new OSEPayloadDatabase();

        payload.appToken(IConstants.APP_TOKEN_CLIENT_API);
        payload.database("test");
        payload.collection("users");
        payload.query("#upsert");
        payload.params().put("_key", "1234");
        payload.params().put("name", "Mario");
        payload.params().put("surname", "Rossi");

        // add payload
        request.payload().putAll(payload.map());

        // complete request
        request.source(IConstants.CHANNEL_API);
        if (!StringUtils.hasText(request.type())) {
            request.type(IConstants.TYPE_DATABASE);
        }
        if (!StringUtils.hasText(request.lang())) {
            request.lang("it");
        }
        request.clientId(RandomUtils.randomUUID());
        request.address("");

        // invoke handler
        return MessageManager.instance().handle(request);
    }




}