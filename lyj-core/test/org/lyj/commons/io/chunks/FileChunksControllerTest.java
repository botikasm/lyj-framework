package org.lyj.commons.io.chunks;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.tokenizers.files.FileTokenizer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileChunksControllerTest {

    @BeforeClass
    public static void setUp() throws Exception {

        TestInitializer.init();

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Test
    public void contains() throws Exception {
        final FileChunksController chunks = FileChunksController.instance().open();
        final String chunk_id = "chunk_test_01";

        chunks.add(chunk_id, 1, 2, new byte[0]);
        chunks.add(chunk_id, 1, 2, new byte[0]);
        chunks.add(chunk_id, 1, 2, new byte[0]);

        int count = chunks.count(chunk_id);

        assertTrue(count==1);
    }

    @Test
    public void open() throws Exception {

        final FileChunksController chunks = FileChunksController.instance().open();
        final String chunk_id = "chunk_test_01";

        // split a file
        final String filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO.zip");
        FileTokenizer.split(new File(filename), 1024 * 1024, (index, count, progress, bytes) -> {
            try {
                System.out.println(FormatUtils.format("Chunking... {0}/{1} {2}%", index, count, progress * 100));

                chunks.add(chunk_id, index, count, bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        final String out_filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO_out.zip");
        if(FileUtils.exists(out_filename)){
            System.out.println(out_filename + ": " + FileUtils.getSize(out_filename) );
            FileUtils.delete(out_filename);
        }

        // compose chunks
        chunks.compose(chunk_id, out_filename);

        assertTrue(FileUtils.exists(out_filename));
        final long size_original = FileUtils.getSize(filename);
        final long size_out = FileUtils.getSize(out_filename);
        assertEquals(size_out, size_original);

        // remove file
        FileUtils.delete(out_filename);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}