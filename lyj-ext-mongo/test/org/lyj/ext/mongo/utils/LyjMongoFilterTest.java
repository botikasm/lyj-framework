package org.lyj.ext.mongo.utils;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.lyj.ext.mongo.utils.filter.LyjMongoFilter;

import java.util.Arrays;

/**
 * Created by angelogeminiani on 27/12/15.
 */
public class LyjMongoFilterTest {

    @Test
    public void testGeoWithin() throws Exception {

        final String expected = "{ \"loc\" : { \"$geoWithin\" : { \"$geometry\" : { \"type\" : \"Polygon\", \"coordinates\" : [[0.0, 10.0]] } } } }";

        final Document filter = LyjMongoFilter.create().geoWithin("loc", Arrays.asList(0.0, 10.0)).asBson();
        System.out.println(filter.toJson());
        Assert.assertEquals(expected, filter.toJson());

    }

    @Test
    public void testGeoWithinSpere() throws Exception {

        final String expected = "{ \"loc\" : { \"$geoWithin\" : { \"$centerSphere\" : [[0.0, 10.0], 10.0] } } }";

        final Document filter = LyjMongoFilter.create().geoWithinSphereRadians("loc", 0.0, 10.0, 10.0).asBson();
        System.out.println(filter.toJson());
        Assert.assertEquals(expected, filter.toJson());

    }

    @Test
    public void testNear() throws Exception {

        final String expected = "{ \"loc\" : { \"$near\" : { \"$geometry\" : { \"type\" : \"Point\", \"coordinates\" : [0.0, 10.0] }, \"$maxDistance\" : 20 } } }";

        final Document filter = LyjMongoFilter.create().near("loc", 20, 0.0, 10.0).asBson();
        System.out.println(filter.toJson());
        Assert.assertEquals(expected, filter.toJson());

    }
}