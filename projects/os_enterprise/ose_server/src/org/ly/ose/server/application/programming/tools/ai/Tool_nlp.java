package org.ly.ose.server.application.programming.tools.ai;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;
import org.lyj.commons.nlp.elements.KeywordsSolver;
import org.lyj.commons.util.converters.MapConverter;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.ext.script.utils.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * NLP engine
 */
public class Tool_nlp
        extends OSEProgramToolRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "nlp"; // used as $nlp.

    private static final String FLD_KEYWORDS = "keywords";
    private static final String FLD_VALUE = "value";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_nlp(final OSEProgram program) {
        super(NAME, program);
        _root = program.root();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    /**
     * Detect intent and does not allow custom expressions (delegation to callback in javascript)
     */
    public Object[] getIntents(final String text,
                               final Object keys_intent) {
        return this.getIntents(text, keys_intent, null);
    }

    /**
     * Detect intent.
     *
     * @param text        Phrase to analyze
     * @param keys_intent Object containing intent definition
     * @param callback    Javascript callback to handle custom expressions
     * @return
     */
    public Object[] getIntents(final String text,
                               final Object keys_intent,
                               final JSObject callback) {

        final Collection<Map> list = this.toList(keys_intent);
        final Collection<Object> response = new LinkedList<>();
        for (final Map map : list)
            try {
                final String value = KeywordsSolver.getValue(map);
                final String[] keywords = JsonWrapper.toArrayOfString(KeywordsSolver.getKeys(map));

                // get intents
                final Object intent = KeywordsSolver.instance().matchKeywords(text, keywords, value, new JSONObject(map),
                        (expression, keys, node) -> {
                            try {
                                if (null != callback) {
                                    return callback.call(null, expression.toMap(), keys);
                                }
                            } catch (Throwable t) {
                               super.program().logger().error("getIntents", t);
                            }

                            return null;
                        });
                if (null != intent) {
                    response.add(intent);
                }

            } catch (Throwable ignored) {
                // ignored
            }
        return response.toArray(new Object[0]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    public Collection<Map> toList(final Object values) {
        if (null != values) {
            final JSONArray array = Converter.toJsonArray(values);
            if (null != array && array.length() > 0) {
                return MapConverter.toList(array);
            }
        }
        return new ArrayList<>();
    }


}
