package org.lyj.commons.util;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;

/**
 * User: angelo.geminiani
 */
public class SystemUtilsTest {

    @BeforeClass
    public static void setUp() {
        TestInitializer.init();
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

    @Test
    public void testOpenUrl() throws Exception {
        String url = "file:///Users/angelogeminiani/Desktop/002629086197.pdf";

        SystemUtils.openURL(url);

    }

    @Test
    public void testOpenFile() throws Exception {

        SystemUtils.selectFile("./sample_file.txt");

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
