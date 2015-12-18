package org.lyj.ext.mongo;

import org.bson.*;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by angelogeminiani on 18/12/15.
 */
public class LyjMongoTest {



    @Test
    public void testCodec() throws Exception {

        final Document doc = new Document();

        final JSONArray array = new JSONArray();
        array.put(new Document("doc1", "hello"));

        final Collection<Document> list = new ArrayList<>();
        list.add(new Document("doc1", "hello"));

        doc.put("list", list);

        final String json = doc.toJson();
        System.out.println(json);

        Assert.assertTrue(StringUtils.isJSONObject(json));

        final JsonWrapper jw = new JsonWrapper(json);

        Assert.assertTrue(jw.isJSONObject());
        Assert.assertNotNull(jw.getJSONArray("list"));
        Assert.assertTrue(jw.getJSONArray("list").getJSONObject(0).getString("doc1").equals("hello"));
    }

    @Test
    public void testDocument() throws Exception {

        BsonArray array = new BsonArray();
        array.add(new BsonDocument("id", new BsonInt32(0)));
        Assert.assertNotNull(array);
        System.out.println(array.getValues().toString());
    }
}