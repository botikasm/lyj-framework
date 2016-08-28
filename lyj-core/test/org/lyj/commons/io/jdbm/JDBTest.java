package org.lyj.commons.io.jdbm;

import org.junit.Before;
import org.junit.Test;
import org.lyj.Lyj;
import org.lyj.TestInitializer;
import org.lyj.commons.io.db.jdbm.JDB;
import org.lyj.commons.io.db.jdbm.JDBCollection;
import org.lyj.commons.util.MapBuilder;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 24/03/16.
 */
public class JDBTest {

    @Before
    public void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void collection() throws Exception {
        JDB db = JDB.create(Lyj.getAbsolutePath("test-jdb")).open("testdb");
        JDBCollection collection = db.collection("sample");

        System.out.println("Initial Size: " + collection.count());

        // add item
        Map<String, Object> item = collection.insert(MapBuilder.create(String.class, Object.class).put("name", "mario").put("surname", "rossi").toMap());

        assertNotNull(item);
        System.out.println(item);

        System.out.println("Size after insert: " + collection.count());

        // try add existing
        Exception err = null;
        try {
            collection.insert(item);
        } catch (Exception t) {
            err = t;
        }
        assertNotNull(err);

        System.out.println("Size after insert error: " + collection.count());

        final boolean exist_mario = collection.exists(item.get("_id"));
        assertTrue(exist_mario);

        // search mario
        Map<String, Object> mario = collection.findOne(MapBuilder.create(String.class, Object.class).put("name", "mario").toMap());
        assertNotNull(mario);

        // update mario
        Map<String, Object> mario_refurbished = collection.updateOne(mario, MapBuilder
                .create(String.class, Object.class).put("surname", "refurbished").put("age", 35).toMap());
        assertNotNull(mario_refurbished);
        System.out.println("Mario Refurbished: " + mario_refurbished.toString());

        // add wario
        Map<String, Object> wario = collection.insert(MapBuilder.create(String.class, Object.class)
                .put("name", "wario").put("age", 35).toMap());
        assertNotNull(wario);

        System.out.println("Size after insert Wario: " + collection.count());

        // update all
        Collection<Map<String, Object>> updated = collection.update(
                MapBuilder.create(String.class, Object.class).put("age", 35).toMap(),
                MapBuilder.create(String.class, Object.class).put("age", 45).toMap());
        assertTrue(updated.size() > 0);
        System.out.println("Updated age: " + updated);

        // upsert wario
        wario.put("sister", "jessica");
        Map<String, Object> updated_wario = collection.upsert(wario);
        assertNotNull(updated_wario);
        System.out.println("Updated Wario: " + updated_wario);

        // remove mario
        Map<String, Object> removed_mario = collection.removeOne(mario);
        assertNotNull(removed_mario);

        assertEquals(mario.get("_id"), removed_mario.get("_id"));

        System.out.println("Size after remove one: " + collection.count());


        // count mario
        long count = collection.count(MapBuilder.create(String.class, Object.class).put("name", "mario").toMap());
        System.out.println("Count marios: " + count);

        if (count > 0) {
            // remove all marios
            Collection<Map<String, Object>> removed_list = collection.remove(MapBuilder.create(String.class, Object.class).put("name", "mario").toMap());
            int count_removed = removed_list.size();
            long count_remain = collection.count();

            assertTrue(count_removed > 0);
            assertTrue(count_remain > 0); // should remain wario
        }


        collection.clear();
        assertTrue(collection.count() == 0);

    }

    @Test
    public void indexes() throws Exception {
        JDB db = JDB.create(Lyj.getAbsolutePath("test-jdb")).open("testdb");
        JDBCollection collection = db.collection("sample_indexes");

        collection.indexAddUnique(new String[]{"name", "surname"});

        assertTrue(collection.indexCount() == 1);
        assertTrue(collection.indexIsUnique(new String[]{"name", "surname"}));

        // remove existing to avoid errors with unique keys
        collection.clear();

        // add items
        Map<String, Object> item1 = collection.insert(
                MapBuilder.create(String.class, Object.class)
                        .put("name", "mario")
                        .put("surname", "rossi").toMap()
        );
        Map<String, Object> item2 = collection.insert(
                MapBuilder.create(String.class, Object.class)
                        .put("name", "mario")
                        .put("surname", "verdi").toMap()
        );


        // try add existing
        Exception err = null;
        try {
            collection.insert(MapBuilder.create(String.class, Object.class)
                    .put("name", "mario")
                    .put("surname", "verdi").toMap()
            );
        } catch (Exception t) {
            err = t;
        }
        assertNotNull(err);

        // remove mario verdi and add new mario verdi
        collection.remove(MapBuilder.create(String.class, Object.class)
                .put("name", "mario")
                .put("surname", "verdi").toMap());
        collection.insert(MapBuilder.create(String.class, Object.class)
                .put("name", "mario")
                .put("surname", "verdi").toMap()
        );

        // find
        Map<String, Object> query = MapBuilder.createSO()
                .put("name", "mario")
                .put("surname", "rossi")
                .toMap();
        Collection<Map<String, Object>> items = collection.find(query);
        assertTrue(items.size()==1);
        query = MapBuilder.createSO()
                .put("name", "mario")
                .put("surname", "verdi")
                .toMap();
        items = collection.find(query);
        assertTrue(items.size()==1);

        // update mario verdi to mario bianchi
        Map<String, Object> update_item = collection.findOne(MapBuilder.createSO()
                .put("name", "mario")
                .put("surname", "verdi")
                .toMap());
        update_item.put("surname", "bianchi");
        collection.upsert(update_item);

        // add new mario verdi
        collection.insert(MapBuilder.create(String.class, Object.class)
                .put("name", "mario")
                .put("surname", "verdi").toMap()
        );

        collection.clear();
        assertTrue(collection.count() == 0);


    }

}