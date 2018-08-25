package org.ly.ose.server.application.programming;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadProgram;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.TestInitializer;
import org.ly.ose.server.application.importer.PackageImporter;
import org.ly.ose.server.application.programming.deploy_test.ProgramsTestDeployer;
import org.ly.ose.server.application.programming.exceptions.ImproperUseException;
import org.ly.ose.server.application.programming.exceptions.InfiniteLoopException;
import org.lyj.Lyj;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.ext.script.utils.Converter;

import java.util.Collection;

import static org.junit.Assert.*;

public class ProgramsManagerTest {


    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();

        final String deploy_root = PathUtils.getAbsolutePath("config/programs");
        (new ProgramsTestDeployer(deploy_root, Lyj.isSilent())).deploy();

        PackageImporter.instance().force();
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    @Test
    public void getNew() throws Exception {
        final OSEProgram program = this.get("system.utils");

        Object response = program.callMember("version");
        assertNotNull(response);
        assertTrue(response instanceof String);
        System.out.println("version: " + response);

        response = program.callMember("echo", "hello");
        assertNotNull(response);
        assertTrue(response instanceof String);
        assertEquals(response, "hello");
        System.out.println("echo: " + response);
    }

    @Test
    public void runTests() throws Exception {
        final OSEProgram program = this.get("tests.all");

        //-- database --//
        this.test_database(program);

        //-- session --//
        this.test_session(program);

        //-- object --//
        this.test_object(program);

        //-- i18n --//
        this.test_i18n(program);

        //-- resource --//
        this.test_resource(program);

        //-- http --//
        this.test_http(program);

        //-- ose --//
        this.test_ose(program);

        program.close();
    }

    @Test
    public void runTest_ose() throws Exception {
        final OSEProgram program = this.get("tests.all");

        //-- ose --//
        this.test_ose(program);

        program.close();

    }

    @Test
    public void runTest_database() throws Exception {
        final OSEProgram program = this.get("tests.all");

        //-- ose --//
        this.test_database(program);

        program.close();

    }

    @Test
    public void runTest_database2() throws Exception {
        final OSEProgram program = this.get("tests.all");

        //-- ose --//
        this.test_database_upsert2(program);

        program.close();

    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private OSEProgram get(final String class_name) throws Exception {
        final OSEProgramInfo info = ProgramsManager.instance().getInfo(class_name);
        final OSEProgram program = new OSEProgram(info);
        assertNotNull(program);

        final Object init_response = program.open();
        System.out.println("INIT RESPONSE: " + init_response);

        return program;
    }

    private void test_database(final OSEProgram program) throws Exception {
        ScriptObjectMirror database = (ScriptObjectMirror) program.callMember("database");

        // upsert
        String method = "upsert";
        Object response = database.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

        // upsert2
        method = "upsert2";
        response = database.callMember(method);
        assertNotNull(response);
        Collection list = Converter.toList(response);
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));

        // find
        method = "find";
        response = database.callMember(method);
        assertNotNull(response);
        list = Converter.toList(response);
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));

        // findEqual
        method = "findEqual";
        response = database.callMember(method);
        assertNotNull(response);
        list = Converter.toList(response);
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));

        // findEqual
        method = "findEqualAsc";
        response = database.callMember(method);
        assertNotNull(response);
        list = Converter.toList(response);
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));

        // findEqual
        method = "forEach";
        response = database.callMember(method);
        assertNotNull(response);
        list = Converter.toList(response);
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));
    }

    private void test_database_upsert2(final OSEProgram program) throws Exception {
        ScriptObjectMirror database = (ScriptObjectMirror) program.callMember("database");

        // upsert
        String method = "upsert2";
        Object response = database.callMember(method);
        assertNotNull(response);
        Collection list = Converter.toList(response);
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));
    }

    private void test_session(final OSEProgram program) throws Exception {
        ScriptObjectMirror session = (ScriptObjectMirror) program.callMember("session");

        String method = "id";
        Object response = session.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

        method = "put";
        response = session.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

        method = "keys";
        response = session.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

        method = "elapsed";
        response = session.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));
    }

    private void test_object(final OSEProgram program) throws Exception {

        String method = "object_name";
        Object response = program.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

        method = "array_len";
        response = program.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

    }

    private void test_i18n(final OSEProgram program) throws Exception {

        String method = "i18n";
        Object response = program.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));


    }

    private void test_resource(final OSEProgram program) throws Exception {

        String method = "resource";
        Object response = program.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));


    }

    private void test_http(final OSEProgram program) throws Exception {
        ScriptObjectMirror http = (ScriptObjectMirror) program.callMember("http");

        // get
        String method = "get";
        Object response = http.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

        // post
        method = "post";
        response = http.callMember(method);
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));
    }

    private void test_ose(final OSEProgram program) throws Exception {

        final OSERequest request = new OSERequest();
        request.type(OSERequest.TYPE_PROGRAM);
        request.lang("it");
        request.clientId(RandomUtils.randomUUID());
        final OSEPayloadProgram payload = new OSEPayloadProgram();
        payload.appToken(IConstants.APP_TOKEN_CLIENT_API);
        request.payload().putAll(payload.map());

        // callMember
        String method = "ose.callMember";
        Object response = OSEProgramInvoker.instance().callMember(request, program, method,
                new String[]{"tests.all", "version", "param1", "params2"});
        assertNotNull(response);
        System.out.println(method + ": " + Converter.toJsonCompatible(response));

        // callMember
        Throwable err = null;
        try {
            method = "ose.recursive";
            response = OSEProgramInvoker.instance().callMember(request, program, method,
                    new String[]{"tests.all", "recursive", "param1", "params2"});
            assertNotNull(response);
            System.out.println(method + ": " + Converter.toJsonCompatible(response));
        } catch (Throwable t) {
            err = t;
        }
        assertNotNull(err);
        assertTrue( (err instanceof InfiniteLoopException)  );

    }
}