package org.lyj.commons.io.jsondb;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.lyj.Lyj;
import org.lyj.TestInitializer;

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
}