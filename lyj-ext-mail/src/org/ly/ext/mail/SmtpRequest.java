package org.ly.ext.mail;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.MimeTypeUtils;
import org.lyj.commons.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Email Sender helper
 */
public class SmtpRequest
        extends AbstractLogEmitter {

    private static final String MIME_TEXT = MimeTypeUtils.getMimeType(".txt");
    private static final String MIME_HTML = MimeTypeUtils.MIME_HTML;



    /**
     * The primary subtype for multipart, "mixed", is intended for use when the body parts are independent and intended
     * to be displayed serially. Any multipart subtypes that an implementation does not recognize should be
     * treated as being of subtype "mixed".
     */
    private static final String SUBTYPE_MIXED = "mixed";

    /**
     * The multipart/alternative type is syntactically identical to multipart/mixed, but the semantics are different.
     * In particular, each of the parts is an "alternative" version of the same information.
     * User agents should recognize that the content of the various parts are interchangeable.
     * The user agent should either choose the "best" type based on the user's environment and preferences,
     * or offer the user the available alternatives. In general, choosing the best type means displaying only
     * the LAST part that can be displayed. This may be used, for example, to send mail in a fancy text format
     * in such a way that it can easily be displayed anywhere
     */
    private static final String SUBTYPE_ALTERNATIVE = "alternative";

    /**
     * This document defines a "digest" subtype of the multipart Content-Type. This type is syntactically identical
     * to multipart/mixed, but the semantics are different.
     * In particular, in a digest, the default Content-Type value for a body part is changed from "text/plain" to
     * "message/rfc822". This is done to allow a more readable digest format that is largely compatible
     * (except for the quoting convention) with RFC 934
     */
    private static final String SUBTYPE_DIGEST = "digest";


    /**
     *This document defines a "parallel" subtype of the multipart Content-Type. This type is syntactically identical
     * to multipart/mixed, but the semantics are different. In particular, in a parallel entity, all of the parts
     * are intended to be presented in parallel, i.e., simultaneously, on hardware and software that are capable of
     * doing so. Composing agents should be aware that many mail readers will lack this capability and will show
     * the parts serially in any event.
     */
    private static final String SUBTYPE_PARALLEL = "parallel";

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String _host;
    private int _port = 25;
    private String _user = null;
    private String _password = null;
    private boolean _TLS = false;
    private boolean _SSL = false;
    private boolean _debug = false;

    private String _from;
    private String _to;
    private String _subject;
    private String _message_txt;
    private String _message_html;

    private final List<String> _addresses;
    private final List<File> _fileAttachments;
    private final Map<String, InputStream> _streamAttachments;

    private Exception _exception;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SmtpRequest() {
        _addresses = new ArrayList<String>();
        _fileAttachments = new ArrayList<File>();
        _streamAttachments = new HashMap<String, InputStream>();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _addresses.clear();
            _fileAttachments.clear();
            _streamAttachments.clear();
        } catch (Exception e) {
        }
        super.finalize();
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    //--  p r o p e r t i e s  --//

    public Exception exception() {
        return _exception;
    }

    public boolean tls() {
        return _TLS;
    }

    public SmtpRequest tls(boolean TLS) {
        this._TLS = TLS;
        return this;
    }

    public boolean ssl() {
        return _SSL;
    }

    public SmtpRequest ssl(boolean value) {
        this._SSL = value;
        return this;
    }

    public boolean debug() {
        return _debug;
    }

    public SmtpRequest debug(boolean debug) {
        this._debug = debug;
        return this;
    }

    public String host() {
        return _host;
    }

    public SmtpRequest host(String value) {
        _host = value;
        return this;
    }

    public int port() {
        return _port;
    }

    public SmtpRequest port(int value) {
        _port = value;
        return this;
    }

    public String from() {
        return _from;
    }

    public SmtpRequest from(String value) {
        _from = this.checkAddress(value);
        return this;
    }

    public String to() {
        return _to;
    }

    public SmtpRequest to(String value) {
        _to = this.checkAddress(value);
        return this;
    }

    public String subject() {
        return _subject;
    }

    public SmtpRequest subject(String value) {
        _subject = value;
        return this;
    }

    public String bodyText() {
        return _message_txt;
    }

    public SmtpRequest bodyText(String value) {
        _message_txt = value;
        return this;
    }

    public String bodyHtml() {
        return _message_html;
    }

    public SmtpRequest bodyHtml(String value) {
        _message_html = value;
        return this;
    }

    public String user() {
        return _user;
    }

    public SmtpRequest user(String value) {
        _user = value;
        return this;
    }

    public String password() {
        return _password;
    }

    public SmtpRequest password(String value) {
        _password = value;
        return this;
    }

    public SmtpRequest address(final String address) {
        this.addresses(StringUtils.split(address, new String[]{";", ","}));
        return this;
    }

    public SmtpRequest addresses(final String[] addresses) {
        for (final String address : addresses) {
            if (null != address && address.length() > 0) {
                _addresses.add(checkAddress(address));
            }
        }
        return this;
    }

    public String[] addresses() {
        return _addresses.toArray(new String[_addresses.size()]);
    }

    public SmtpRequest clearAddresses() {
        _addresses.clear();
        return this;
    }

    public SmtpRequest attachment(final String path) {
        _fileAttachments.add(new File(path));
        return this;
    }

    public SmtpRequest attachment(final File file) {
        _fileAttachments.add(file);
        return this;
    }

    /**
     * Add an attachment as output stream.<br>
     * In most cases you can take "other content types" to mean file attachments,
     * such as Word documents, but for something a bit more interesting,
     * for example sending a Java serialized object.<br>
     *
     * @param name   Name
     * @param stream InputStream
     */
    public SmtpRequest attachment(final String name, final InputStream stream) {
        try {
            _streamAttachments.put(name, stream);
        } catch (Throwable ignored) {
        }
        return this;
    }

    //--  m e t h o d s  --//

    public boolean send() {
        try {
            // create addresses
            if (null != _to && _to.length() > 0) {
                final String[] addresses = _to.split(";");
                this.addresses(addresses);
            }

            // Create a mail session
            final Properties props = this.getProperties();
            final Authenticator auth = new SMTPAuthenticator(_user, _password);
            final Session session = Session.getInstance(props, auth);
            session.setDebug(debug());

            final Transport transport = session.getTransport("smtp");
            try {
                transport.connect();
                int count = _addresses.size();
                for (int i = 0; i < count; i++) {
                    final String mailAddress = _addresses.get(i);
                    final InternetAddress[] addressTo = new InternetAddress[1];
                    addressTo[0] = new InternetAddress(mailAddress);
                    final InternetAddress addressFrom = new InternetAddress(_from);

                    // Construct the message
                    final Message message = new MimeMessage(session);
                    message.setSentDate(new Date());
                    message.setDescription("this is a lyj message");
                    message.setFrom(addressFrom);
                    message.setReplyTo(new InternetAddress[]{addressFrom});
                    message.setRecipients(Message.RecipientType.TO, addressTo);

                    message.setSubject(_subject);

                    //msg.setContent(_message, _mailFormat.toString());
                    final Multipart mp = this.getMailMultiPart();
                    message.setContent(mp);

                    // Send the message
                    transport.sendMessage(message, message.getAllRecipients());
                }
                return true;
            } finally {
                try {
                    transport.close();
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable t) {
            final String msg = FormatUtils.format("SEND EMAIL ERROR: " +
                            "'%s' error sending mail: \n%s.",
                    t.getClass().getSimpleName(),
                    t);
            _exception = new Exception(msg, t);
            super.error("send", msg);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Properties getProperties() {
        final Properties props = new Properties();

        props.put("mail.smtp.host", _host);
        props.put("mail.smtp.port", "" + _port);

        if (_TLS) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.startssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "SSLv3 TLSv1");
        }
        if (_SSL) {
            props.put("mail.smtp.startssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "SSLv3 TLSv1");

            props.put("mail.smtp.socketFactory.port", "" + _port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        if (StringUtils.hasText(_user)) {
            props.put("mail.smtp.auth", "true");
        }

        // close connection upon quit being sent
        props.put("mail.smtp.quitwait", "false");

        return props;
    }

    private Multipart getMailMultiPart() throws MessagingException {
        final Multipart multipart = new MimeMultipart(SUBTYPE_ALTERNATIVE);

        // add multipart message: order is important, first must be text
        if (StringUtils.hasText(_message_txt)) {
            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(_message_txt, CharEncoding.UTF_8);//"utf-8"
            multipart.addBodyPart(textPart);
        }

        if (StringUtils.hasText(_message_html)) {
            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(_message_html, MIME_HTML);//"text/html; charset=utf-8"
            multipart.addBodyPart(htmlPart);
        }

        // attachments
        final MimeBodyPart[] attachments = this.getAttachments();
        // add attachments
        for (MimeBodyPart part : attachments) {
            multipart.addBodyPart(part);
        }

        return multipart;
    }

    private MimeBodyPart[] getAttachments() throws MessagingException {
        final List<MimeBodyPart> result = new ArrayList<MimeBodyPart>();

        // file attachments
        for (final File file : _fileAttachments) {
            final DataHandler dh = new DataHandler(new FileDataSource(file));
            final MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(dh);
            part.setFileName(file.getName());
            result.add(part);
        }

        // stream attachments
        final Set<Map.Entry<String, InputStream>> entries = _streamAttachments.entrySet();
        for (final Map.Entry<String, InputStream> entry : entries) {
            try {
                final String name = entry.getKey();
                final InputStream stream = entry.getValue();
                final DataHandler dh = new DataHandler(new ByteArrayDataSource(stream, name));
                final MimeBodyPart body = new MimeBodyPart();
                body.setDataHandler(dh);
                result.add(body);
            } catch (Throwable ignored) {
            }
        }

        return result.toArray(new MimeBodyPart[result.size()]);
    }

    private String checkAddress(final String address) {
        final String result = address.replace("[", "<").replace("]", ">");
        return result;
    }

    // ------------------------------------------------------------------------
    //                      e m b e d d e d
    // ------------------------------------------------------------------------

    /**
     * SimpleAuthenticator is used to do simple authentication
     * when the SMTP server requires it.
     */
    private class SMTPAuthenticator
            extends Authenticator {

        private String _usr = null;
        private String _psw = null;

        public SMTPAuthenticator(final String user,
                                 final String password) {
            _usr = user;
            _psw = password;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(_usr, _psw);
        }
    }

}
