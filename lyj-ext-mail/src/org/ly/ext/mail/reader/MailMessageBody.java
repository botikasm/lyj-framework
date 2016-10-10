package org.ly.ext.mail.reader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.*;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Message body wrapper
 */
public class MailMessageBody
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String MIME_TEXT = "text/plain";
    private static final String MIME_HTML = "text/html";
    private static final String MIME_MULTIPART = "multipart/*";
    private static final String MIME_RFC828 = "message/rfc822";
    private static final String MIME_IMAGE = "image/jpeg";


    private static final String FLD_PARSE_ERROR = "parse_error";

    private static final String FLD_CONTENT_TYPE = "content_type";
    private static final String FLD_MIME_TYPE = "mime_type";
    private static final String FLD_MESSAGE_NUMBER = "message_number";
    private static final String FLD_BODY_TEXT = "body_text";
    private static final String FLD_BODY_HTML = "body_html";
    private static final String FLD_ATTACHMENTS = "attachments"; // json object with base64 values

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MailReader _reader;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MailMessageBody(final MailReader reader, final Message message) {
        _reader = reader;
        this.init(message);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String error() {
        return super.getString(FLD_PARSE_ERROR);
    }

    public String contentType() {
        return super.getString(FLD_CONTENT_TYPE);
    }

    public String mimeType() {
        return super.getString(FLD_MIME_TYPE);
    }

    public int messageNumber() {
        return super.getInt(FLD_MESSAGE_NUMBER);
    }

    public boolean isText() {
        return this.mimeType().equals(MIME_TEXT);
    }

    public boolean isMultipart() {
        return this.mimeType().equals(MIME_MULTIPART);
    }

    public String text() {
        return this.getString(FLD_BODY_TEXT);
    }

    public String html() {
        return this.getString(FLD_BODY_HTML);
    }

    public boolean hasAttachments() {
        return super.has(FLD_ATTACHMENTS) && this.attachments().length() > 0;
    }

    public int countAttachments() {
        return this.hasAttachments() ? this.attachments().length() : 0;
    }

    public JSONArray attachments() {
        if (!super.has(FLD_ATTACHMENTS)) {
            super.put(FLD_ATTACHMENTS, new JSONArray());
        }
        return super.getJSONArray(FLD_ATTACHMENTS);
    }

    public Collection<File> saveAttachmentsTo(final String root) {
        final Collection<File> list = new ArrayList<>();
        if (super.has(FLD_ATTACHMENTS) && StringUtils.hasText(root)) {
            final String absolute_path = PathUtils.getAbsolutePath(root);
            FileUtils.tryMkdirs(absolute_path);
            final JSONArray attachments = this.attachments();
            CollectionUtils.forEach(attachments, (item, key, index) -> {
                try {
                    if (item instanceof JSONObject) {
                        final MailMessageAttachment attachment = new MailMessageAttachment(_reader, (JSONObject) item);
                        final File f = attachment.saveFileTo(root, true);
                        if (null != f) {
                            list.add(f);
                        }
                    }
                } catch (Throwable t) {
                    super.error("saveAttachmentsTo", t);
                }
            });
        }
        return list;
    }

    public void attachmentForEach(final CollectionUtils.IterationItemCallback<MailMessageAttachment> callback) {
        if (null != callback && this.hasAttachments()) {
            final JSONArray attachments = this.attachments();
            CollectionUtils.forEach(attachments, (item, key, index) -> {
                try {
                    if (item instanceof JSONObject) {
                        final MailMessageAttachment attachment = new MailMessageAttachment(_reader, (JSONObject) item);
                        callback.handle(attachment);
                    }
                } catch (Throwable t) {
                    super.error("attachmentForEach", t);
                }
            });
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final Message message) {
        try {
            // content type
            super.put(FLD_CONTENT_TYPE, message.getContentType());
            if (message.isMimeType(MIME_TEXT)) {
                super.put(FLD_MIME_TYPE, MIME_TEXT);
            } else if (message.isMimeType(MIME_MULTIPART)) {
                super.put(FLD_MIME_TYPE, MIME_MULTIPART);
            } else if (message.isMimeType(MIME_RFC828)) {
                super.put(FLD_MIME_TYPE, MIME_RFC828);
            } else if (message.isMimeType(MIME_IMAGE)) {
                super.put(FLD_MIME_TYPE, MIME_IMAGE);
            }

            super.put(FLD_MESSAGE_NUMBER, message.getMessageNumber());

            // body
            if (isText(message)) {
                // TEXT
                super.put(FLD_BODY_TEXT, (String) message.getContent());
            } else if (isMultipart(message)) {
                // multipart
                this.parse((Multipart) message.getContent());
            } else {
                super.info("init", "Unhandled message type: " + message);
            }

        } catch (Throwable t) {
            super.error("init", t);
            super.put(FLD_PARSE_ERROR, t.toString());
        }
    }

    private void parse(final Multipart message) {
        try {

            final int count = message.getCount();
            for (int i = 0; i < count; i++) {
                final BodyPart item = message.getBodyPart(i);
                final Object content = item.getContent();
                if (isText(item)) {
                    // simple text
                    super.put(FLD_BODY_TEXT, this.text() + content);
                } else if (isHTML(item)) {
                    // HTML
                    super.put(FLD_BODY_HTML, this.html() + content);
                } else if (isNested(item)) {
                    // children
                    System.out.println("NESTED: " + item.getContent().getClass());
                } else if (isInlineImage(item)) {
                    // children
                    System.out.println("IMAGE: " + item.getContent().getClass());
                } else if (isMultipart(item)) {
                    // body
                    this.parse((Multipart) content);
                } else if (isAttachment(item)) {
                    // attachment
                    this.parseAttachment(item);
                } else {
                    super.info("parse", "Unhandled message type: " + message);
                }
            }
        } catch (Throwable t) {
            super.put(FLD_PARSE_ERROR, ExceptionUtils.getRealMessage(t)); // error
        }
    }

    private void parseAttachment(final BodyPart part) throws Exception {
        this.attachments().put(new MailMessageAttachment(_reader, part));
    }

    private void init(Part p) throws Exception {

        //check if the content is plain text
        if (p.isMimeType("text/plain")) {
            System.out.println("This is plain text");
            System.out.println("---------------------------");
            System.out.println((String) p.getContent());
        }
        //check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
            System.out.println("This is a Multipart");
            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            //Part pp= mp.getBodyPart(0);
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                //writePart(mp.getBodyPart(i));
            }
        }
        //check if the content is a nested message
        else if (p.isMimeType("message/rfc822")) {
            System.out.println("This is a Nested Message");
            System.out.println("---------------------------");

            //writePart((Part) p.getContent());
        }
        //check if the content is an inline image
        else if (p.isMimeType("image/jpeg")) {
            System.out.println("--------> image/jpeg");
            Object o = p.getContent();

            InputStream x = (InputStream) o;
            // Construct the required byte array
            System.out.println("x.length = " + x.available());
            int i = 0;
            byte[] bArray = new byte[x.available()];
            while ((i = (int) ((InputStream) x).available()) > 0) {
                int result = (int) (((InputStream) x).read(bArray));
                if (result == -1)
                    i = 0;
                //byte[] bArray = new byte[x.available()];

                break;
            }
            FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
            f2.write(bArray);
        } else if (p.getContentType().contains("image/")) {
            System.out.println("content type" + p.getContentType());
            File f = new File("image" + new Date().getTime() + ".jpg");
            DataOutputStream output = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
            com.sun.mail.util.BASE64DecoderStream test =
                    (com.sun.mail.util.BASE64DecoderStream) p.getContent();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = test.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } else {
            Object o = p.getContent();
            if (o instanceof String) {
                System.out.println("This is a string");
                System.out.println("---------------------------");
                System.out.println((String) o);
            } else if (o instanceof InputStream) {
                System.out.println("This is just an input stream");
                System.out.println("---------------------------");
                InputStream is = (InputStream) o;
                is = (InputStream) o;
                int c;
                while ((c = is.read()) != -1)
                    System.out.write(c);
            } else {
                System.out.println("This is an unknown type");
                System.out.println("---------------------------");
                System.out.println(o.toString());
            }
        }

    }

    private static boolean isInlineImage(final Part p) {
        try {
            return p.isMimeType(MIME_IMAGE);
        } catch (Throwable ignored) {
            // nothing to do
        }
        return false;
    }

    private static boolean isNested(final Part p) {
        try {
            return p.isMimeType(MIME_RFC828);
        } catch (Throwable ignored) {
            // nothing to do
        }
        return false;
    }

    private static boolean isText(final Part p) {
        try {
            return p.isMimeType(MIME_TEXT);
        } catch (Throwable ignored) {
            // nothing to do
        }
        return false;
    }

    private static boolean isHTML(final Part p) {
        try {
            return p.isMimeType(MIME_HTML);
        } catch (Throwable ignored) {
            // nothing to do
        }
        return false;
    }

    private static boolean isMultipart(final Part p) {
        try {
            return p.isMimeType(MIME_MULTIPART);
        } catch (Throwable ignored) {
            // nothing to do
        }
        return false;
    }

    private static boolean isAttachment(final Part p) {
        try {
            return Part.ATTACHMENT.equalsIgnoreCase(p.getDisposition()) && StringUtils.hasText(p.getFileName());
        } catch (Throwable ignored) {
            // nothing to do
        }
        return false;
    }

}
