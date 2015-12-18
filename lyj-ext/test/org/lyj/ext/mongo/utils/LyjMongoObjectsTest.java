package org.lyj.ext.mongo.utils;

import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 18/12/15.
 */
public class LyjMongoObjectsTest {

    @Test
    public void testToJson() throws Exception {

        final List<Document> list = new ArrayList<>();
        final Document doc = new Document("id",0);
        final List<Document> list2 = new ArrayList<>();
        list2.add(new Document("id", 1));
        doc.put("sub-list", list2);
        list.add(doc);

        final String json = LyjMongoObjects.toJson(list);
        System.out.println(json);




    }
}