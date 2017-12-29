package org.lyj.commons.io.db.filedb;

import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.io.db.filedb.exporter.FileDBExporter;
import org.lyj.commons.timewatching.TimeWatcher;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;

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

        for(int i=0;i<100000;i++){
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

}