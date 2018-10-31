package org.lyj.commons.nlp.entities;

import org.json.JSONArray;
import org.lyj.commons.nlp.elements.IKeywordConstants;
import org.lyj.commons.nlp.entities.macro.Macros;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;

public class NEntitySchema
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String PREFIX_MACRO = Macros.PREFIX_MACRO; // macro command prefix
    public static final String PREFIX_EXPRESSION = IKeywordConstants.PREFIX_CUSTOM; // @
    public static final String PREFIX_REGEX = "/";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public NEntitySchema() {
        super();
    }

    public NEntitySchema(final Object item) {
        super(item);
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Rule rule(final String name) {
        if (super.has(name)) {
            return new Rule(super.get(name));
        }
        return new Rule();
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * Rule wrapper
     * "start": [
     * "(nome|sono|io)"
     * ],
     * "rules": [
     * "/^[A-Z][a-z0-9_-]{3,19}$/"
     * ]
     */
    public class Rule
            extends JsonItem {

        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_START = "start";
        private static final String FLD_RULES = "rules";

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public Rule() {
            super();
        }

        public Rule(final Object item) {
            super(item);
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        @Override
        public boolean isEmpty() {
            return this.startArray().length() == 0 && this.rulesArray().length() == 0;
        }

        public JSONArray startArray() {
            if (!super.has(FLD_START)) {
                super.put(FLD_START, new JSONArray());
            }
            return super.getJSONArray(FLD_START);
        }

        public JSONArray rulesArray() {
            if (!super.has(FLD_RULES)) {
                super.put(FLD_RULES, new JSONArray());
            }
            return super.getJSONArray(FLD_RULES);
        }

        public String[] start() {
            return JsonWrapper.toArrayOfString(this.startArray());
        }

        public String[] rules() {
            return JsonWrapper.toArrayOfString(this.rulesArray());
        }

    }

}
