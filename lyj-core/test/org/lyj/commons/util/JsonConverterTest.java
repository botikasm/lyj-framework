package org.lyj.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lyj.commons.util.converters.JsonConverter;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 30/12/16.
 */
public class JsonConverterTest {


    @Test
    public void toJsonCompatible() throws Exception {

        final Map<String, Object> map = new HashMap<>();
        map.put("key1", new byte[]{1, 2, 3, 4, 5, 6}); // base64
        map.put("key2", new Byte[]{1, 2, 3, 4, 5, 6}); // array

        Object converted = JsonConverter.toJsonCompatible(map);
        System.out.println(converted);

        assertTrue(converted instanceof JSONObject);
        assertTrue(((JSONObject) converted).opt("key1") instanceof String);
        assertTrue(((JSONObject) converted).opt("key2") instanceof JSONArray);

        final String wrong_json = "{country=IT, address=Rimini, Province of Rimini, Italy, lng=12.5695158, province=RN, city=Rimini, region=Emilia-Romagna, lat=44.0678288}";
        converted = JsonConverter.toJson(wrong_json);
        assertTrue(converted instanceof JSONObject);
        System.out.println(converted);

    }

}