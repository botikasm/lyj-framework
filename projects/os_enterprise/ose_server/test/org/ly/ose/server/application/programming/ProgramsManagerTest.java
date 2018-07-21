package org.ly.ose.server.application.programming;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.server.TestInitializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProgramsManagerTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    @Test
    public void getNew() {
        final OSEProgram program = ProgramsManager.instance().getNew("system.utils");
        assertNotNull(program);

        final Object init_response = program.open();
        System.out.println(init_response);

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

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

}