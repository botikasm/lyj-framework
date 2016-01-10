package org.lyj.commons.util;

import org.junit.Test;

/**
 * User: angelo.geminiani
 */
public class SystemUtilsTest {

    public SystemUtilsTest() {

    }

    @Test
    public void testGetOperatingSystem() throws Exception {
        String name = SystemUtils.getOperatingSystem();
        String version = SystemUtils.getOSVersion();
        String arch = SystemUtils.getOSAchitecture();

        System.out.println(name);
        System.out.println(version);
        System.out.println(arch);
    }

    @Test
    public void testReport() throws Exception {
        String status = SystemUtils.printSystemStatus();

        System.out.println(status);

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
