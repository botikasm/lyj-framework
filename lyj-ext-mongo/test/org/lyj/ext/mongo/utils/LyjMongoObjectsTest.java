package org.lyj.ext.mongo.utils;

import org.bson.Document;
import org.junit.Test;
import org.lyj.commons.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

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
        doc.put("long", DateUtils.now().getTime());
        doc.put("double", 0d);
        list.add(doc);

        final String json = LyjMongoObjects.toJson(list);
        System.out.println(json);




    }
}