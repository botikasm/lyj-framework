package org.ly.ext.mail.reader;

import org.json.JSONObject;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.json.JsonWrapper;
import org.lyj.commons.util.StringUtils;

import javax.mail.Message;

/**
 * Wrap a message
 */
public class MailMessage
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MailReader _reader;
    private final MailMessageInfo _info;
    private final MailMessageHeaders _headers;
    private final MailMessageBody _body;
    private final int _id;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MailMessage(final MailReader reader,
                       final Message raw_message) {
        _reader = reader;
        _id = raw_message.getMessageNumber();
        _info = new MailMessageInfo(reader, raw_message);
        _headers = new MailMessageHeaders(raw_message);
        _body = new MailMessageBody(reader, raw_message);
    }

    @Override
    public String toString() {
        final JSONObject obj = new JSONObject(_info.toString());
        JsonWrapper.extend(obj, _body.json());

        return obj.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int id() {
        return _id;
    }

    public boolean isReady() {
        return !StringUtils.hasText(_info.error()) && !StringUtils.hasText(_body.error());
    }

    public MailMessageInfo info() {
        return _info;
    }

    public MailMessageBody body() {
        return _body;
    }

}
