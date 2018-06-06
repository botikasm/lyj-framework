package org.ly.ext.mail.reader;

import org.lyj.commons.util.json.JsonItem;

import javax.mail.Header;
import javax.mail.Message;
import java.util.Enumeration;

public class MailMessageHeaders
        extends JsonItem {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_PARSE_ERROR = "parse_error";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    public MailMessageHeaders(final Message m) {
        super();

        this.parse(m);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String error() {
        return super.getString(FLD_PARSE_ERROR);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void parse(final Message m) {
        try {
            final Enumeration e = m.getAllHeaders();
            while (e.hasMoreElements()) {
                final Object raw_header = e.nextElement();
                if (raw_header instanceof Header) {
                    final Header header = (Header) raw_header;
                    this.put(header.getName(), header.getValue());
                }
            }
        } catch (Throwable t) {
            super.error("parse", t);
            super.put(FLD_PARSE_ERROR, t.toString());
        }
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
