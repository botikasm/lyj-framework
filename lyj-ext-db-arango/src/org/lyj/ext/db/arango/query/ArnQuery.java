package org.lyj.ext.db.arango.query;

import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility to build Arango QUERY
 * <p>
 * FOR user1 IN users
 * ...FOR user2 IN users
 * ...FILTER user1 != user2
 * ...LET sumOfAges = user1.age + user2.age
 * ...FILTER sumOfAges < 100
 * ...RETURN {
 * ......pair: [user1.name, user2.name],
 * ......sumOfAges: sumOfAges
 * ...}
 */
public class ArnQuery {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FOR = "FOR %s IN %s";
    private static final String FILTER = "FILTER";
    private static final String RETURN = "RETURN";
    private static final String LET = "LET";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final StringBuilder _sb;
    private final Map<String, String> _collections;
    private int _indent;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ArnQuery() {
        _sb = new StringBuilder();
        _collections = new LinkedHashMap<>();
        _indent = 0;
    }

    @Override
    public String toString() {
        return _sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String[] tables() {
        return _collections.keySet().toArray(new String[_collections.size()]);
    }

    public ArnQuery FOR(final String collection_name) {
        final String t = this.addCollection(collection_name);
        this.addLine(FormatUtils.format(FOR, t, collection_name));
        return this;
    }

    public ArnQuery FILTER(final String filter) {
        if (StringUtils.hasText(filter)) {
            this.addLine(FILTER).append(" ").append(filter);
        }
        return this;
    }

    public ArnQuery LET(final String name, final String value) {
        if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
            this.addLine(LET).append(" ").append(name).append("=").append(value);
        }
        return this;
    }


    public ArnQuery RETURN(final Object ret) {
        if (null != ret) {
            this.addLine(RETURN).append(" ").append(ret.toString());
        }
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private StringBuilder addLine(final String line) {
        if (StringUtils.hasText(line)) {
            if (_sb.length() > 0) {
                _sb.append("\n");
                if (line.startsWith("FOR")) {
                    _indent++;
                }
            }
            for(int i=0;i<_indent;i++){
                _sb.append("\t");
            }

            _sb.append(line);
        }
        return _sb;
    }

    private String addCollection(final String collection_name) {
        final String name = "t" + (_collections.size() + 1);
        _collections.put(name, collection_name);
        return name;
    }

}
