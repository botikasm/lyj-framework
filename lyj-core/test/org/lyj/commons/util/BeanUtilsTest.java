package org.lyj.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lyj.ext.db.model.MapDocument;

import static org.junit.Assert.*;

/**
 * User: angelo.geminiani
 */
public class BeanUtilsTest {

    public BeanUtilsTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void getSuperclass() {

        final MapDocument instance = new MapDocument();
        Class c1 = BeanUtils.getRootclass(instance);
        Class c2 = instance.getClass().getSuperclass();
        System.out.println(c1);
        System.out.println(c2);
    }

    @Test
    public void testGetValueIfAny() throws Exception {

        JSONArray array = new JSONArray();
        array.put("test");
        array.put("foo");

        Object result = BeanUtils.getValueIfAny(array, "test");
        assertNotNull(result);

        result = BeanUtils.getValueIfAny(array, "undefined");
        assertNull(result);

        JSONObject json = new JSONObject();
        json.putOpt("test", "hello");
        result = BeanUtils.getValueIfAny(json, "test");
        assertEquals("hello", result);

    }

    @Test
    public void testGetDefault() {

        String s_def = BeanUtils.getDefault("hello");
        assertEquals("", s_def);

        Float f_def = BeanUtils.getDefault(123.5f);
        assertTrue(f_def.equals(0.0f));

        Integer i_def = BeanUtils.getDefault(1234);
        assertTrue(i_def.equals(0));

    }

}
