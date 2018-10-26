package org.lyj.commons.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.tokenizers.TokenInfo;

import java.io.File;

public class ByteUtilsTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void getBytes() throws Exception {

        final File file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));

        int count = 0;
        final TokenInfo ti = new TokenInfo(file.length(), (long) 10000);
        for (int i = 0; i < ti.getChunkCount(); i++) {
            final int off = (int) ti.getChunkOffsets()[i];
            byte[] data = ByteUtils.getBytes(file, off, 10000);
            count += data.length;
        }

        Assert.assertEquals(count, file.length());
    }
}