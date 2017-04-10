package org.lyj.ext.db.arango.serialization;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lyj.commons.util.JsonItem;
import org.lyj.ext.db.IDatabaseCollection;
import org.lyj.ext.db.arango.TestHelper;

import static org.junit.Assert.*;

/**
 *
 */
public class JsonDocumentTest {


    @Test
    public void testDatabase() throws Exception {

        IDatabaseCollection<JsonItem> collection = TestHelper.collection("sample", JsonItem.class);

        final JsonItem item = new JsonItem();
        item.put("text", "HELLO");
        item.put("int", 123);
        item.put("long", 123L);
        item.put("double", 123.0);
        item.put("boolean", true);
        item.put("int", 123);
        item.put("object", new JsonItem().put("attr1", "VALUE 1"));
        final JSONArray array = new JSONArray();
        final JSONObject obj1 = new JSONObject();
        obj1.put("attr2", "VALUE 2");
        array.put(obj1);
        item.put("array", array);

        JsonItem entity = collection.upsert(item);
        System.out.println(entity.toString());
        assertTrue(entity.getString("text").equalsIgnoreCase("HELLO"));

        entity.put("text", "hello 2");
        entity = collection.upsert(entity);
        assertTrue(entity.getString("text").equalsIgnoreCase("HELLO 2"));

        entity = collection.get(entity);
        assertNotNull(entity);
        System.out.println(entity.toString());
        
        collection.remove(entity);

        entity = collection.get(entity);
        assertNull(entity);
    }

}