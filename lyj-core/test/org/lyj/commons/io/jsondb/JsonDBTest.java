package org.lyj.commons.io.jsondb;

import net.kotek.jdbm.ConcurrentSortedMap;
import net.kotek.jdbm.DB;
import net.kotek.jdbm.DBMaker;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.lyj.Lyj;
import org.lyj.TestInitializer;
import org.lyj.commons.io.db.jsondb.JsonDB;
import org.lyj.commons.io.db.jsondb.JsonDBCollection;
import org.lyj.commons.util.MapBuilder;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 13/03/16.
 */
public class JsonDBTest {

    @Before
    public void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void testOpen() throws Exception {

        JsonDB db = new JsonDB(Lyj.getAbsolutePath("db"));
        db.open("test-db");
        assertTrue(db.isOpen());

        JsonDBCollection collection = db.collection("sample");

        // insert or update
        JSONObject obj = new JSONObject();
        obj.put("_id", "1");
        obj.put("name", "test");
        collection.upsert(obj);

        // find
        JSONObject existing = collection.findOne("_id", "1");
        assertNotNull(existing);
        assertEquals(existing.optString("name"), "test");

        // update
        existing.put("name", "changed name");
        collection.upsert(existing);

        // find
        existing = collection.findOne("_id", "1");
        assertNotNull(existing);
        assertEquals(existing.optString("name"), "changed name");

    }

    @Test
    public void testJdbm(){
        final String path = Lyj.getAbsolutePath("testdb");
        //Configure and open database using builder pattern.
        //All options are available with code auto-completion.
        DB db = DBMaker.newFileDB(new File(path))
                .closeOnJvmShutdown()
                .encryptionEnable("password")
                .make();

        //open an collection, TreeMap has better performance then HashMap
        ConcurrentSortedMap<Integer,String> map = db.getTreeMap("collectionName");

        map.put(1,"one");
        map.put(2,"two");
        //map.keySet() is now [1,2] even before commit

        db.commit();  //persist changes into disk

        map.put(3,"three");
        //map.keySet() is now [1,2,3]
        db.rollback(); //revert recent changes
        //map.keySet() is now [1,2]

        Set<Map<Integer,String>> users = db.getHashSet("users");

        users.add(MapBuilder.create(Integer.class, String.class).put(1, "mario").toMap());

        ConcurrentSortedMap<Integer,Map<Integer,String>> collections = db.getTreeMap("collections");

        collections.put(1, MapBuilder.create(Integer.class, String.class).put(1, "sample").toMap());

        db.commit();

        db.close();

    }
}