package org.lyj.ext.script.program.engines.javascript.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;
import org.lyj.ext.db.model.MapDocument;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 01/09/16.
 */
public class JavascriptConverterTest {


    @Test
    public void toScriptObject() throws Exception {

        final MapDocument doc = new MapDocument();
        doc.put("field1", "value1");

        ScriptObjectMirror obj = (ScriptObjectMirror)JavascriptConverter.toScriptObject(doc);
        assertNotNull(obj);
        assertEquals(obj.get("field1"), "value1");

        obj = (ScriptObjectMirror)JavascriptConverter.toScriptObject(doc);
        assertNotNull(obj);
        assertEquals(obj.get("field1"), "value1");

        obj = (ScriptObjectMirror)JavascriptConverter.toScriptObject(doc);
        assertNotNull(obj);
        assertEquals(obj.get("field1"), "value1");

        obj = (ScriptObjectMirror)JavascriptConverter.toScriptObject(doc);
        assertNotNull(obj);
        assertEquals(obj.get("field1"), "value1");

        obj = (ScriptObjectMirror)JavascriptConverter.toScriptObject(doc);
        assertNotNull(obj);
        assertEquals(obj.get("field1"), "value1");

        obj = (ScriptObjectMirror)JavascriptConverter.toScriptObject(doc);
        assertNotNull(obj);
        assertEquals(obj.get("field1"), "value1");
    }

}