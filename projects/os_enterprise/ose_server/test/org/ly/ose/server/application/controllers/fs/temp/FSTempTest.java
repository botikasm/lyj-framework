package org.ly.ose.server.application.controllers.fs.temp;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.ose.server.TestInitializer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FSTempTest {


    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Test
    public void put() throws Exception {

        final File file = this.getFile();
        System.out.println("PUT file into cache: " + file.getAbsolutePath());

        final String key = RandomUtils.randomUUID();
        FSTemp.instance().put(key, file, 5000);
        Assert.assertTrue(FSTemp.instance().has(key));

        final String text = FSTemp.instance().getString(key);
        Assert.assertTrue(StringUtils.isJSON(text));

        // wait a while
        Thread.sleep(35000);

        Assert.assertFalse(FSTemp.instance().has(key));
    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private File getFile(){
        // get a file
        final List<File> files = new ArrayList<>();
        final String folder = PathUtils.getAbsolutePath("");
        FileUtils.listFiles(files, new File(folder), "*.json");
        Assert.assertFalse(files.isEmpty());

        return files.get(0);
    }

}