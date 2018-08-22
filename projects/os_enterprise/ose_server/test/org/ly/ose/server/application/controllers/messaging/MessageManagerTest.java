package org.ly.ose.server.application.controllers.messaging;

import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadDatabase;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.TestInitializer;
import org.ly.ose.server.application.importer.PackageImporter;
import org.ly.ose.server.application.programming.deploy_test.ProgramsTestDeployer;
import org.lyj.Lyj;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.util.*;

import static org.junit.Assert.assertNotNull;

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

        List<OSEResponse> responses = this.testDatabase();
        assertNotNull(responses);
        System.out.println("DATABASE:");
        System.out.println(responses);

        for (final OSEResponse response:responses) {
            final JSONArray payload = response.payload();
            System.out.println("\t------");
            CollectionUtils.forEach(payload, (item)->{
                System.out.println("\t\t" + item);
            });
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private List<OSEResponse> testDatabase() {
        final List<OSEResponse> result = new LinkedList<>();

        List<OSEResponse> upsert = this.insertUsers();
        result.addAll(upsert);

        result.addAll(this.findUsers());

        return result;
    }

    private List<OSEResponse> insertUsers() {
        final List<OSEResponse> result = new LinkedList<>();

        final OSERequest request = new OSERequest();
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

        final OSEPayloadDatabase payload = new OSEPayloadDatabase();

        payload.appToken(IConstants.APP_TOKEN_CLIENT_API);
        payload.database("test");
        payload.collection("users");

        // Mario
        payload.query("#upsert");
        payload.params().put("_key", "1234");
        payload.params().put("name", "Mario");
        payload.params().put("surname", "Rossi");
        // add payload
        request.payload().putAll(payload.map());
        // invoke handler
        result.add((OSEResponse) MessageManager.instance().handle(request));

        // Sergio
        payload.query("#upsert");
        payload.params().put("_key", "1qaz");
        payload.params().put("name", "Sergio");
        payload.params().put("surname", "Rossi");
        // add payload
        request.payload().putAll(payload.map());
        // invoke handler
        result.add((OSEResponse)MessageManager.instance().handle(request));

        return result;
    }


    private List<OSEResponse>  findUsers(){
        final List<OSEResponse> result = new LinkedList<>();

        final OSERequest request = new OSERequest();
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

        final OSEPayloadDatabase payload = new OSEPayloadDatabase();

        payload.appToken(IConstants.APP_TOKEN_CLIENT_API);
        payload.database("test");
        payload.collection("users");

        payload.query("#findEqualAsc");
        final Map<String, Object> params = new HashMap<>();
        params.put("surname", "Rossi");
        payload.params().put("params", params);
        payload.params().put("sort", Arrays.asList("name"));

        // add payload
        request.payload().putAll(payload.map());
        // invoke handler
        result.add((OSEResponse)MessageManager.instance().handle(request));

        return result;
    }
}