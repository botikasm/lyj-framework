package org.ly.ose.server.application.programming;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.server.TestInitializer;
import org.ly.ose.server.application.importer.PackageImporter;
import org.ly.ose.server.application.programming.deploy_test.ProgramsTestDeployer;
import org.lyj.Lyj;
import org.lyj.commons.util.PathUtils;
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

        // find
        method = "find";
        response = database.callMember(method);
        assertNotNull(response);
        Collection list = (Collection) response;
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));

        // findEqual
        method = "findEqual";
        response = database.callMember(method);
        assertNotNull(response);
        list = (Collection) response;
        System.out.println(method + " (" + list.size() + ") : " + Converter.toJsonCompatible(response));

        // findEqual
        method = "findEqualAsc";
        response = database.callMember(method);
        assertNotNull(response);
        list = (Collection) response;
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
}