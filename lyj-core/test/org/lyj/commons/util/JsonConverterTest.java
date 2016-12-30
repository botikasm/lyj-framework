package org.lyj.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 30/12/16.
 */
public class JsonConverterTest {



    @Test
    public void toJsonCompatible() throws Exception {

        final Map<String, Object> map = new HashMap<>();
        map.put("key1", new byte[]{1,2,3,4,5,6}); // base64
        map.put("key2", new Byte[]{1,2,3,4,5,6}); // array

        Object converted = JsonConverter.toJsonCompatible(map);
        System.out.println(converted);

        assertTrue(converted instanceof JSONObject);
        assertTrue(((JSONObject)converted).opt("key1") instanceof String);
        assertTrue(((JSONObject)converted).opt("key2") instanceof JSONArray);

    }

}