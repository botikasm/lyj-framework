package org.lyj.ext.db.arango;


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

import java.util.Collection;

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

}