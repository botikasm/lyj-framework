package org.lyj.commons.io.db.filedb;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.util.DateUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

public class FileDBCollectionTest {

    @BeforeClass
    public static void init() {
        TestInitializer.init();
    }


    @Test
    public void insert() throws Exception {
        final String path = PathUtils.getAbsolutePath("test_file_db.txt");
        FileDBCollection collection = new FileDBCollection(path);

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
        final String path = PathUtils.getAbsolutePath("test_file_db.txt");
        FileDBCollection collection = new FileDBCollection(path);

        final FileDBEntity item = new FileDBEntity();
        item.key("123");
        item.put("tick", DateUtils.timestamp());

        final long count = collection.count();

        System.out.println("COUNT BEFORE REMOVE: " + collection.count());
        collection.remove(item);
        System.out.println("COUNT AFTER REMOVE: " + collection.count());


    }

}