package org.lyj.ext.db.arango;


import org.json.JSONObject;
import org.junit.Test;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.db.IDatabase;
import org.lyj.ext.db.IDatabaseCollection;
import org.lyj.ext.db.IDatabaseConnection;
import org.lyj.ext.db.configuration.DatabaseConfiguration;
import org.lyj.ext.db.configuration.DatabaseConfigurationCredential;
import org.lyj.ext.db.configuration.DatabaseConfigurationHost;
import org.lyj.ext.db.exceptions.DatabaseDoesNotExists;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *
 */
public class ArangoConnectorTest {

    @Test
    public void instance() throws Exception {
        DatabaseConfiguration configuration = new DatabaseConfiguration();
        configuration.add(new DatabaseConfigurationHost().host("localhost").port(8529),
                new DatabaseConfigurationCredential().username("root").password("!qaz2WSX098"));

        System.out.println("Configuration: " + configuration.toString());

        ArnConnector.instance().add("sample", configuration);
        IDatabaseConnection connection = ArnConnector.instance().connection("sample");
        assertNotNull(connection);
        System.out.println("Connection: " + connection.toString());

        final String[] names = connection.databaseNames();
        System.out.println("Databases: " + StringUtils.toString(names));

        IDatabase db = connection.database("test");
        assertNotNull(db);
        assertEquals("test", db.name());

        String[] collections = db.collectionNames();
        System.out.println("Collections: " + StringUtils.toString(collections));

        IDatabaseCollection<String> collection = db.collection("sample", String.class);
        System.out.println("Items in collection: " + collection.count());
        collection.forEach((item) -> {
            System.out.println(item);
            return null;
        });

        //-- upsert --//

        JsonItem item = new JsonItem();
        item.put("name", "Zumboo");
        item.put("_key", "96973");

        collection.upsert(item.toString());
        System.out.println("Insert: " + collection.get(item));

        item.put("name", "Zumboo 2");

        collection.upsert(item.toString());
        System.out.println("Updated: " + collection.get(item));

        System.out.println("Count after upsert: " + collection.count());

        //-- remove --//

        assertTrue(collection.remove("96973"));

        System.out.println("Count after remove: " + collection.count());

        connection.close();

        collection.forEach((item2) -> {
            System.out.println(item2);
            return null;
        });

        assertFalse(collection.remove("fake_key"));

        //-- remove equal --//
        JsonItem item1 = new JsonItem();
        item1.put("name", "Zumboo");
        item1.put("tag", "remove_me");
        JsonItem item2 = new JsonItem();
        item2.put("name", "Zumboo");
        item2.put("tag", "remove_me");

        collection.insert(item1.toString());
        collection.insert(item2.toString());

        System.out.println("Count after insert of 2: " + collection.count());

        Collection<String> items = collection.removeEqual(MapBuilder.createSO().put("tag", "remove_me").toMap());
        assertTrue(items.size() == 2);

        System.out.println("Removed: " + items);

        System.out.println("Count after remove of 2: " + collection.count());
    }

    @Test
    public void nonASCII() throws Exception {

        IDatabaseCollection<String> collection = this.collection("sample");

        final Set<String> keys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            keys.add("key_" + i);
        }
        final String value = "[CTO]\n Ha supervisionato e gestito il reparto di R&D per il ééé €€€€€ °°° ####  settore software, formulando una visione di lungo periodo con la Direzione dell'Azienda.";

        for(final String key:keys){
            collection.remove(key);
        }

        for(final String key:keys){
            JsonItem item = new JsonItem();
            item.put("_key", key);
            item.put("value01", value);
            item.put("value02", value);
            item.put("value03", value);
            item.put("value04", value);
            item.put("value05", value);
            item.put("value06", value);
            item.put("value07", value);
            item.put("value08", value);
            item.put("value09", value);
            item.put("value10", value);
            item.put("value11", value);
            item.put("value12", value);
            item.put("value13", value);
            item.put("value14", value);
            item.put("value15", value);
            item.put("value16", value);
            item.put("sub_item", new JSONObject(item.toString()));

            collection.insert(item.toString());
        }

        System.out.println("inserted: " + keys.size());

        for(final String key:keys){
            collection.remove(key);
        }

        System.out.println("removed: " + keys.size());
    }

    @Test
    public void testNonASCII2() throws DatabaseDoesNotExists {
        final String no_working_item = "{\"name\":\"job_04_detail_1\",\"seven__\":\"123456789\",\"_key\":\"191d936d-1eb9-4094-9c1c-9e0ba1d01867\",\"lang\":\"it\",\"value\":\"[CTO]\\n Ha supervisionato e gestito il reparto di R&D per il software, 1234567 formulando una visione di lungo periodo con la Direzione dell'Azienda.\"}";
        final String working_item = "{\"name1\":\"job_04_detail_1\",\"seven__\":\"123456789\",\"_key\":\"191d936d-1eb9-4094-9c1c-9e0ba1d01867\",\"lang\":\"it\",\"value\":\"[CTO]\\n Ha supervisionato e gestito il reparto di R&D per il software, 1234567 formulando una visione di lungo periodo con la Direzione dell'Azienda.\"}";

        IDatabaseCollection<String> collection = this.collection("sample");

        final String new_item = collection.insert(working_item);

        assertTrue(collection.remove(new_item));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private IDatabaseCollection<String> collection(final String coll_name) throws DatabaseDoesNotExists {
        DatabaseConfiguration configuration = new DatabaseConfiguration();
        configuration.add(new DatabaseConfigurationHost().host("localhost").port(8529),
                new DatabaseConfigurationCredential().username("root").password("!qaz2WSX098"));

        ArnConnector.instance().add("sample", configuration);
        IDatabaseConnection connection = ArnConnector.instance().connection("sample");

        IDatabase db = connection.database("test");

        return db.collection(coll_name, String.class);
    }
}