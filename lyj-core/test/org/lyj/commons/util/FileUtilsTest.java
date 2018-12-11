package org.lyj.commons.util;

import org.junit.Test;
import org.lyj.commons.async.Async;
import org.lyj.commons.lang.Counter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtilsTest {

    @Test
    public void readFileToString() throws Exception {

        final String root = PathUtils.getAbsolutePath("");
        System.out.println(root);

        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(root));

        System.out.println("FOUND FILES: " + files.size());

        final long start = System.currentTimeMillis();
        Counter count = new Counter();
        final List<Thread> thlist = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            //thlist.add(Async.invoke((args)->{
            for (final File file : files) {
                try {
                    final String ext = PathUtils.getFilenameExtension(file.getAbsolutePath(), true);
                    if (null != ext && ext.equalsIgnoreCase(".java")) {
                        final String content = FileUtils.readFileToString(file);
                        if (StringUtils.hasText(content)) {
                            count.inc();
                        }
                    }
                } catch (Throwable t) {
                }
            }
            //}));
        }

        Async.joinAllThreads(thlist);

        System.out.println("READ FILES: " + count.value());
        final long elapsed_ms = System.currentTimeMillis() - start;
        System.out.println("ELAPSED SECONDS:" + (elapsed_ms / 1000d));

    }

    @Test
    public void copy() throws Exception {
        final String root = "/users/angelogeminiani/test";
        FileUtils.mkdirs(root);
        final String source = PathUtils.concat(root, "geckodriver");
        final String target = PathUtils.concat(root, "copy_of_geckodriver");

        final File source_file = new File(source);
        final File target_file = new File(target);
        if(target_file.exists()){
            FileUtils.delete(target_file);
        }

        // copy
        FileUtils.copy(source_file, target_file);

        System.out.println("SOURCE: " + source_file.isFile() + ", " + source_file.length() + ", " + source_file.canExecute());
        System.out.println("TARGET: " + target_file.isFile() + ", " + target_file.length() + ", " + target_file.canExecute());

    }

}