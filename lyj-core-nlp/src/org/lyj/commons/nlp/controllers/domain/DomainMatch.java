package org.lyj.commons.nlp.controllers.domain;

import org.lyj.commons.nlp.NUri;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Single ontology response
 */
public class DomainMatch {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final double VAL_MAX = 100;
    private static final double VAL_ROOT = 50;
    private static final double VAL_PARAM = 30;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final NUri _uri; // verb + params in URI format
    private final Map<String, String> _match_params; // params added from matching (NOT contained in URI VERB)
    private String _default_verb_value;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DomainMatch() {
        _uri = new NUri("");
        _match_params = new HashMap<>();
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("uri:").append(this.uri());
        sb.append(",");
        sb.append("rating:").append(this.rating());
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uri() {
        if (this.hasMatchValue()) {
            return _uri.toString();
        } else {
            final NUri output = new NUri(_default_verb_value);
            output.params().putAll(_uri.params());

            return output.toString();
        }
    }

    /**
     * If TRUE, this response is a valid response with a valid action root.
     *
     * @return TRUE if this is a valid response
     */
    public boolean hasMatchValue() {
        return StringUtils.hasText(_uri.root()); // has root
    }

    public boolean hasMatchParams() {
        return !_match_params.isEmpty();
    }

    public DomainMatch addVerb(final String value) {
        _uri.root(value);
        return this;
    }

    public DomainMatch addVerbDefault(final String value) {
        _default_verb_value = value;
        return this;
    }

    public DomainMatch addParams(final String value) {
        _uri.addParams(value);
        _match_params.putAll(CollectionUtils.stringToMapOfStrings(value, "&"));
        return this;
    }

    public double rating() {
        return this.calculateRating(_uri);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private double calculateRating(final NUri uri) {
        double count = 0;
        if (null != uri) {
            if (StringUtils.hasText(uri.root())) {
                count += VAL_ROOT;
            }
            count += (VAL_PARAM * uri.params().size());
        }
        return count / VAL_MAX;
    }

}
