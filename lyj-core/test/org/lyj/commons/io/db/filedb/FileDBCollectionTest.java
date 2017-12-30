package org.lyj.commons.io.db.filedb;

import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.async.Async;
import org.lyj.commons.io.db.filedb.exporter.FileDBExporter;
import org.lyj.commons.timewatching.TimeWatcher;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class FileDBCollectionTest {

    @BeforeClass
    public static void init() {
        TestInitializer.init();
    }


    @Test
    public void db() throws Exception {
        final FileDB db = new FileDB("test");

        FileDBCollection collection = db.collection("test_collection");

        System.out.println(StringUtils.toString(db.collectionNames()));
        System.out.println(db.dbPath());
    }

    @Test
    public void insert() throws Exception {
        final FileDB db = new FileDB("test");

        FileDBCollection collection = db.collection("test_collection");

        final FileDBEntity item = new FileDBEntity();
        item.key("123");
        item.put("tick", DateUtils.timestamp());

        collection.upsert(item);

        final FileDBEntity item2 = new FileDBEntity();
        item2.put("tick", "item2_ " + DateUtils.timestamp());
        collection.insert(item2);

        System.out.println("FIELDS: " + StringUtils.toString(collection.fields()));
        System.out.println("COUNT: " + collection.count());

    }

    @Test
    public void remove() throws Exception {
        final FileDB db = new FileDB("test");

        FileDBCollection collection = db.collection("test_collection");

        final FileDBEntity item = new FileDBEntity();
        item.key("123");
        item.put("tick", DateUtils.timestamp());

        final long count = collection.count();

        System.out.println("COUNT BEFORE REMOVE: " + collection.count());
        collection.remove(item);
        System.out.println("COUNT AFTER REMOVE: " + collection.count());

    }

    @Test
    public void export() throws Exception {
        final FileDB db = new FileDB("test");

        FileDBCollection collection = db.collection("test_collection");

        final FileDBEntity item = new FileDBEntity();
        item.put("text", "This is a text \"quoted\" with a CSV quote char");
        collection.upsert(item);

        String path = FileDBExporter.instance().exporter(".json").export(collection);
        String json_content = FileUtils.readFileToString(new File(path));
        assertTrue(StringUtils.isJSONArray(json_content));

        JSONArray array = new JSONArray(json_content);

        path = FileDBExporter.instance().exporter(".csv").export(collection);
    }

    @Test
    public void export_plus() throws Exception {
        final FileDB db = new FileDB("test");

        FileDBCollection collection = db.collection("test_big_collection");

        for (int i = 0; i < 100000; i++) {
            final FileDBEntity item = new FileDBEntity();
            item.put("text", "This is a text \"quoted\" with a CSV quote char");
            collection.upsert(item);
        }

        TimeWatcher t = new TimeWatcher();
        t.start();

        String path = FileDBExporter.instance().exporter(".json").export(collection);
        String json_content = FileUtils.readFileToString(new File(path));
        assertTrue(StringUtils.isJSONArray(json_content));

        JSONArray array = new JSONArray(json_content);

        path = FileDBExporter.instance().exporter(".csv").export(collection);

        t.stop();
        System.out.println("EXPORT ELAPSED: " + t.elapsed());
        System.out.println("ROWS: " + collection.count());
    }

    @Test
    public void multi_thread() throws Exception {
        final Collection<Thread> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(new Task());
        }

        Async.startAllThreads(tasks);
        Async.joinAllThreads(tasks);

    }

    private static class Task
            extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep((long) RandomUtils.rnd(10d, 500d));

                final FileDB db = new FileDB("test");
                final FileDBCollection collection = db.collection("test_big_collection");

                for (int i = 0; i < 100; i++) {
                    final FileDBEntity item = new FileDBEntity();
                    item.put("text", "This is a text \"quoted\" with a CSV quote char");
                    collection.upsert(item);

                }

                Thread t = Async.debounce("test", this::delayed, 1000, collection);
                t.join();

            } catch (Throwable t) {
                System.out.println(t);
            }

        }

        private void delayed(final Object[] args) {
            try {
                FileDBExporter.instance().exporter("csv").export((FileDBCollection) args[0]);
            } catch (Throwable t) {
                System.out.println(t);
            }
        }
    }

}