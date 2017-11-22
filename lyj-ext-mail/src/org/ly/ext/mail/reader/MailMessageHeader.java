package org.ly.ext.mail.reader;

import org.json.JSONArray;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.StringUtils;

import javax.mail.Address;
import javax.mail.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * FROM, TO, SUBJECT
 */
public class MailMessageHeader
        extends JsonItem {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private static final String FLD_PARSE_ERROR = "parse_error";

    private static final String FLD_FROM = "from";
    private static final String FLD_TO = "to";
    private static final String FLD_SUBJECT = "subject";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MailReader _reader;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MailMessageHeader(final MailReader reader, final Message m) {
        super();
        _reader = reader;

        this.parse(m);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String error() {
        return super.getString(FLD_PARSE_ERROR);
    }

    public JSONArray from() {
        return super.getJSONArray(FLD_FROM);
    }

    public MailMessageHeader from(final JSONArray value) {
        super.put(FLD_FROM, value);
        return this;
    }

    public JSONArray to() {
        return super.getJSONArray(FLD_TO);
    }

    public MailMessageHeader to(final JSONArray value) {
        super.put(FLD_TO, value);
        return this;
    }

    public String subject() {
        return super.getString(FLD_SUBJECT);
    }

    public MailMessageHeader subject(final String value) {
        super.put(FLD_SUBJECT, value);
        return this;
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String[] fromAsArray() {
        return JsonWrapper.toArrayOfString(this.from());
    }

    public String[] fromAsEmailArray() {
        final String[] data = this.fromAsArray();
        final List<String> response = new ArrayList<>();
        for (final String item : data) {
            final String email = this.getEmail(item);
            if (StringUtils.hasText(email)) {
                response.add(email);
            }
        }
        return response.toArray(new String[response.size()]);
    }

    public String[] toAsArray() {
        return JsonWrapper.toArrayOfString(this.to());
    }

    public boolean isFrom(final String address) {
        final String email = this.getEmail(address);
        if (StringUtils.hasText(email)) {
            final String[] from = this.fromAsArray();
            for (final String item : from) {
                if (email.equalsIgnoreCase(this.getEmail(item))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void parse(final Message m) {
        try {
            Address[] a;

            // FROM
            if ((a = m.getFrom()) != null) {
                final JSONArray array_from = new JSONArray();
                for (int j = 0; j < a.length; j++) {
                    //System.out.println("FROM: " + a[j].toString());
                    array_from.put(a[j].toString());
                }
                this.from(array_from);
            }

            // TO
            if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
                final JSONArray array_to = new JSONArray();
                for (int j = 0; j < a.length; j++) {
                    //System.out.println("TO: " + a[j].toString());
                    array_to.put(a[j].toString());
                }
                this.to(array_to);
            }

            // SUBJECT
            if (m.getSubject() != null) {
                this.subject(m.getSubject());
            }
        } catch (Throwable t) {
            super.error("parse", t);
            super.put(FLD_PARSE_ERROR, t.toString());
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String getEmail(final String address) {
        final String[] tokens = StringUtils.split(address, " ", true);
        for (final String token : tokens) {
            final String clean = token.replace("<", "").replace(">", "");
            if (RegExpUtils.isValidEmail(clean)) {
                return clean;
            }
        }
        return "";
    }

}
