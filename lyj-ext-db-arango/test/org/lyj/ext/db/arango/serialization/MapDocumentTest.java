package org.lyj.ext.db.arango.serialization;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.MapBuilder;
import org.lyj.ext.db.IDatabaseCollection;
import org.lyj.ext.db.arango.TestHelper;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 08/04/17.
 */
public class MapDocumentTest {

    @Test
    public void json() throws Exception {

        final ArangoMapDocument doc = new ArangoMapDocument();
        doc.put("obj1", new JSONObject("{\"attr1\":\"val1\"}"));
        doc.put("map1", MapBuilder.createSO().put("map1", "val_map1").toMap());
        
        final JSONArray array = new JSONArray();
        array.put(new JSONObject("{\"attr2\":\"val2\"}"));
        array.put("TEXT IN ARRAY");
        array.put(1234);
        doc.put("array", array);

        Collection list = doc.getList("array");
        assertTrue(list.size()==3);
        for(final Object value:list){
            System.out.println(value.getClass().getSimpleName());
        }


        System.out.println(doc);
    }

    @Test
    public void testDatabase() throws Exception {

        IDatabaseCollection<ArangoMapDocument> collection = TestHelper.collection("sample", ArangoMapDocument.class);

        final ArangoMapDocument item = new ArangoMapDocument();
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

        ArangoMapDocument entity = collection.upsert(item);
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