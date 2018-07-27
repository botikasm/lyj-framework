package org.ly.ose.server.application.programming;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.server.TestInitializer;
import org.ly.ose.server.application.importer.PackageImporter;
import org.ly.ose.server.application.programming.deploy_test.ProgramsTestDeployer;
import org.lyj.Lyj;
import org.lyj.commons.util.PathUtils;

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
    public void getNew() {
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
    public void runTests() {
        final OSEProgram program = this.get("tests.all");

        //--  --//
        Object response = program.callMember("version");
        assertNotNull(response);
        assertTrue(response instanceof String);
        System.out.println("version: " + response);
    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private OSEProgram get(final String class_name) {
        final OSEProgram program = ProgramsManager.instance().getNew(class_name);
        assertNotNull(program);

        final Object init_response = program.open();
        System.out.println("INIT RESPONSE: " + init_response);

        return program;
    }

}