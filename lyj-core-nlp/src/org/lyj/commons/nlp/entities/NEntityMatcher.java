package org.lyj.commons.nlp.entities;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.nlp.elements.IKeywordConstants;
import org.lyj.commons.nlp.elements.KeywordList;
import org.lyj.commons.nlp.elements.KeywordsSolver;
import org.lyj.commons.nlp.elements.custom.CustomExpression;
import org.lyj.commons.nlp.entities.macro.Macros;
import org.lyj.commons.nlp.entities.regex.RegExHelper;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * Detect entities using a schema like this:
 * {
 * "names": {
 * "start": [
 * "(nome|sono|io)"
 * ],
 * "rules": [
 * "/^[A-Z][a-z0-9_-]{3,19}$/"
 * ]
 * },
 * "emails": {
 * "start": [],
 * "rules": [
 * "#email"
 * ]
 * },
 * "numbers": {
 * "start": [],
 * "rules": [
 * "#number"
 * ]
 * },
 * "dates": {
 * "start": [],
 * "rules": [
 * "#date"
 * ]
 * },
 * "phones": {
 * "start": [
 * "(numero|cell|cellulare|telefono|chiama|richiamarmi|chiama**|chiama***)"
 * ],
 * "rules": [
 * "#phone"
 * ]
 * }
 * }
 */
public class NEntityMatcher {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PREFIX_MACRO = NEntitySchema.PREFIX_MACRO; // macro command prefix
    private static final String PREFIX_EXPRESSION = NEntitySchema.PREFIX_EXPRESSION; // @
    private static final String PREFIX_REGEX = NEntitySchema.PREFIX_REGEX;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private NEntityMatcher() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Entity[] match(final String lang,
                          final String phrase,
                          final NEntitySchema schema,
                          final IKeywordConstants.Callback callback) {
        return match(lang, KeywordList.split(phrase), schema, callback);
    }

    public Entity[] match(final String lang,
                          final String[] phrase,
                          final NEntitySchema schema,
                          final IKeywordConstants.Callback callback) {
        final Collection<Entity> response = new LinkedList<>();
        final Set<String> entity_names = schema.keys();
        for (final String entity_name : entity_names) {
            final NEntitySchema.Rule rule = schema.rule(entity_name);
            if (!rule.isEmpty()) {
                final String[] start = rule.start(); // keywords to match for a starting point into phrase
                final String[] rules = rule.rules(); // expressions, regexp or macro
                final String opt_intent = rule.optIntent();
                final int start_index = KeywordsSolver.instance().matchIndex(phrase, start);
                final Entity entity = parse(lang, entity_name, Math.max(0, start_index), phrase, rules, callback);
                if (null != entity) {
                    entity.optIntent(opt_intent); // intent to infer if any
                    response.add(entity);
                }
            }
        }
        return response.toArray(new Entity[0]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {


    }

    private Entity parse(final String lang,
                         final String name,
                         final int start_index,
                         final String[] phrase,
                         final String[] rules,
                         final IKeywordConstants.Callback callback) {
        Entity response = null;
        for (final String rule : rules) {
            // check match between word and rule
            final String[] match_response = match(lang, start_index, phrase, rule, callback);
            if (match_response.length > 0) {
                if (null == response) {
                    response = new Entity();
                    response.name(name);
                }
                for (final String entity : match_response) {
                    response.valueArray().put(entity); // add entity value found from matching
                }
            }
        }
        return response;
    }

    private String[] match(final String lang,
                           final int start_index,
                           final String[] phrase,
                           final String rule,
                           final IKeywordConstants.Callback callback) {
        final Collection<String> response = new LinkedList<>();
        if (isExpression(rule)) {

            //-- EXPRESSION --//
            final CustomExpression expression = new CustomExpression(rule);
            if (null != callback) {
                callback.handle(expression, phrase, new JSONObject());
            }

        } else if (isMacro(rule)) {

            //-- MACRO --//
            final String name = rule.substring(1);
            final String[] match_response = Macros.instance().parse(name, lang, start_index, phrase);
            CollectionUtils.addAllNoDuplicates(response, match_response);

        } else if (isRegEx(rule)) {

            //-- REGEX --//
            final String pattern = rule.substring(1, rule.length() - 1);
            final String[] match_response = RegExHelper.instance().parse(start_index, phrase, pattern);
            CollectionUtils.addAllNoDuplicates(response, match_response);

        }
        return response.toArray(new String[0]);
    }

    private static boolean isMacro(final String name) {
        return name.startsWith(PREFIX_MACRO);
    }

    private static boolean isExpression(final String name) {
        return name.startsWith(PREFIX_EXPRESSION);
    }

    private static boolean isRegEx(final String name) {
        return name.startsWith(PREFIX_REGEX);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static NEntityMatcher __instance;

    public static synchronized NEntityMatcher instance() {
        if (null == __instance) {
            __instance = new NEntityMatcher();
        }
        return __instance;
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * Entity Wrapper
     */
    public static class Entity
            extends JsonItem {

        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_NAME = "name";  // name of entity
        private static final String FLD_VALUE = "value"; // detected values for this entity
        private static final String FLD_OPT_INTENT = "opt_intent";  // (optional intent to infer)

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public Entity() {
            super();
        }

        public Entity(final Object item) {
            super(item);
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public String name() {
            return super.getString(FLD_NAME);
        }

        public Entity name(final String name) {
            super.put(FLD_NAME, name);
            return this;
        }

        public JSONArray valueArray() {
            if (!super.has(FLD_VALUE)) {
                super.put(FLD_VALUE, new JSONArray());
            }
            return super.getJSONArray(FLD_VALUE);
        }

        public String[] value() {
            return JsonWrapper.toArrayOfString(this.valueArray());
        }

        public String optIntent() {
            return super.getString(FLD_OPT_INTENT);
        }

        public Entity optIntent(final String name) {
            super.put(FLD_OPT_INTENT, name);
            return this;
        }


    }


}
