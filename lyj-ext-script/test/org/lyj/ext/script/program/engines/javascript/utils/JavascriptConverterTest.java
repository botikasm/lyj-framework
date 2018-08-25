package org.lyj.ext.script.program.engines.javascript.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;
import org.lyj.commons.util.converters.MapConverter;
import org.lyj.ext.db.model.MapDocument;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 01/09/16.
 */
public class JavascriptConverterTest {


    @Test
    public void toScriptObject() throws Exception {

        final MapDocument doc = new MapDocument();
        doc.put("field1", "value1");
        doc.put("field2", true);
        doc.put("field3", false);

        ScriptObjectMirror obj = (ScriptObjectMirror)JavascriptConverter.toScriptObject(doc);
        assertNotNull(obj);
        assertEquals(obj.get("field1"), "value1");
        assertEquals(obj.get("field2"), true);
        assertEquals(obj.get("field3"), false);

        Map<String, Object> map = MapConverter.toMap(obj);
        map.put("field1", "value1");
        map.put("field2", true);
        map.put("field3", false);

    }

}