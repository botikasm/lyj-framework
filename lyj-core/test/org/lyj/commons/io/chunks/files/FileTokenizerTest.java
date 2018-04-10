package org.lyj.commons.io.chunks.files;

import org.junit.Test;
import org.lyj.commons.tokenizers.IProgressCallback;
import org.lyj.commons.tokenizers.files.FileTokenizer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FileTokenizerTest {

    @Test
    public void testSplitFromChunkSize() throws Exception {

        final String filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO.zip");
        final String[] chunks = FileTokenizer.splitFile(filename, 1024 * 5 * 1000, new IProgressCallback() {
            @Override
            public void onProgress(int index, int count, double progress) {
                System.out.println(FormatUtils.format("Chunking... {0}/{1} {2}%", index, count, progress * 100));
            }
        });

        final String out_filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO_out.zip");
        FileTokenizer.join(chunks, out_filename, new IProgressCallback() {
            @Override
            public void onProgress(int index, int count, double progress) {
                System.out.println(FormatUtils.format("Composing... {0}/{1} {2}%", index, count, progress * 100));
            }
        });

        assertTrue(FileUtils.exists(out_filename));
        FileUtils.delete(out_filename);
        FileUtils.delete(PathUtils.concat(PathUtils.getTemporaryDirectory(), "TOKENIZER/"));
    }

    @Test
    public void testSplitFile() throws Exception {

        final String filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO.zip");
        final String out_filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO_out.zip");

        // split stream and re-create output
        FileTokenizer.split(new File(filename), 1000, (index, count, progress, bytes) -> {
            try {
                FileTokenizer.append(bytes, out_filename);
                System.out.println(FormatUtils.format("Composing... {0}/{1} {2}%", index, count, progress * 100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertTrue(FileUtils.exists(out_filename));
        FileUtils.delete(out_filename);
        FileUtils.delete(PathUtils.concat(PathUtils.getTemporaryDirectory(), "TOKENIZER/"));
    }


}
