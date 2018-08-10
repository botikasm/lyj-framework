package org.ly.ose.server.application.programming.tools.ai;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;
import org.lyj.commons.nlp.elements.KeywordsSolver;
import org.lyj.commons.nlp.entities.NEntityMatcher;
import org.lyj.commons.nlp.entities.NEntitySchema;
import org.lyj.commons.util.converters.MapConverter;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.ext.script.utils.Converter;

import java.util.*;

/**
 * NLP engine
 */
public class Tool_nlp
        extends OSEProgramToolRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "nlp"; // used as $nlp.

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
    public Object[] getIntents(final String lang,
                               final String text,
                               final Object keys_intent) {
        return this.getIntents(lang, text, keys_intent, null);
    }

    /**
     * Detect intent.
     *
     * @param text        Phrase to analyze
     * @param keys_intent Object containing intent definition
     * @param callback    Javascript callback to handle custom expressions
     * @return
     */
    public Object[] getIntents(final String lang,
                               final String text,
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

    public Map getEntities(final String lang,
                           final String text,
                           final Object keys_intent) {
        return this.getEntities(lang, text, keys_intent, null);
    }

    /**
     * Returns a map of identified entities.
     *
     * @param lang          Language of request
     * @param text          Message to analyze
     * @param keys_entities Entity Rules
     * @param callback      Optional callback used from CustomExpressions
     * @return Map of entities
     */
    public Map getEntities(final String lang,
                           final String text,
                           final Object keys_entities,
                           final JSObject callback) {
        final Map response = new HashMap();
        final NEntitySchema schema = new NEntitySchema(this.toMap(keys_entities));
        final NEntityMatcher.Entity[] detected_entities = NEntityMatcher.instance().match(lang, text, schema,
                (expression, keys, node) -> {
                    try {
                        if (null != callback) {
                            return callback.call(null, expression.toMap(), keys);
                        }
                    } catch (Throwable t) {
                        super.program().logger().error("getEntities", t);
                    }

                    return null;
                });

        for (final NEntityMatcher.Entity entity : detected_entities) {
            response.put(entity.name(), this.toList(entity.valueArray()));
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Collection<Map> toList(final Object values) {
        if (null != values) {
            final JSONArray array = Converter.toJsonArray(values);
            if (null != array && array.length() > 0) {
                return MapConverter.toList(array);
            }
        }
        return new ArrayList<>();
    }

    private Map toMap(final Object values) {
        if (null != values) {
            final JSONObject item = Converter.toJsonObject(values);
            if (null != item && item.length() > 0) {
                return MapConverter.toMap(item);
            }
        }
        return new HashMap();
    }

}
