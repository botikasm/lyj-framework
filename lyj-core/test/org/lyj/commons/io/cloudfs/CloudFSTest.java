package org.lyj.commons.io.cloudfs;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.util.PathUtils;

import java.io.File;

public class CloudFSTest {

    @BeforeClass
    public static void setUp() throws Exception {

        TestInitializer.init();

    }

    @Test
    public void instance() throws Exception {

        final CloudFS fs = CloudFS.openTest();

        System.out.println("CLOUD_FS: " + fs.toString());

        final File file = new File(PathUtils.getAbsolutePath("sample_medium_file.txt")); // 300k file
        if (file.exists()) {
            System.out.println("START WITH FILE: " + file.getName() + ", size: " + file.length() + " bytes");
            for (int i = 0; i < 10; i++) {
                final String path = fs.copy(file);
                System.out.println(i + 1 + ") " + path);
            }
        }


        System.out.println("CLOUD_FS: " + fs.toString());
    }
}