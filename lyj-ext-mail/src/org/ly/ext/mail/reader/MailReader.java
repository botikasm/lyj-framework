package org.ly.ext.mail.reader;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ConversionUtils;

import javax.mail.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Read emails
 * <p>
 * String host = "pop.gmail.com";// change accordingly
 * String mailStoreType = "pop3";
 * String username = "yourmail@gmail.com";// change accordingly
 * String password = "*****";// change accordingly
 * <p>
 * check(host, mailStoreType, username, password);
 * <p>
 * https://www.tutorialspoint.com/javamail_api/javamail_api_fetching_emails.htm
 */
public class MailReader extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String TYPE_POP3 = "pop3";
    private static final int MAX_MEM_QUOTA = (int) (40 * ConversionUtils.KBYTE);        // max attachment loaded in memory
    private static final int MAX_ATTACHMENT_SIZE = (int) (10 * ConversionUtils.MBYTE);   // max attachment size: about 10Mb

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _host;
    private int _port;
    private String _type;
    private String _username;
    private String _password;
    private boolean _tls;
    private boolean _auth;
    private boolean _remove_after_read;
    private int _attachment_memo_quota; // exceeding quota will be downloaded into file
    private int _attachment_size;
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MailReader() {
        _port = 110;
        _type = TYPE_POP3;
        _tls = true;
        _auth = true;
        _remove_after_read = false;
        _attachment_memo_quota = MAX_MEM_QUOTA;
        _attachment_size = MAX_ATTACHMENT_SIZE;
    }

    public MailReader(final String host, final int port, final String username, final String password) {
        this();
        _host = host;
        _port = port;
        _username = username;
        _password = password;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String type() {
        return _type;
    }

    public MailReader type(final String value) {
        _type = value;
        return this;
    }

    public String host() {
        return _host;
    }

    public MailReader host(final String value) {
        _host = value;
        return this;
    }

    public int port() {
        return _port;
    }

    public MailReader port(final int value) {
        _port = value;
        return this;
    }

    public boolean removeAfterRead() {
        return _remove_after_read;
    }

    public MailReader removeAfterRead(final boolean value) {
        _remove_after_read = value;
        return this;
    }

    public boolean tls() {
        return _tls;
    }

    public MailReader tls(final boolean value) {
        _tls = value;
        return this;
    }

    public boolean auth() {
        return _auth;
    }

    public MailReader auth(final boolean value) {
        _auth = value;
        return this;
    }

    public String username() {
        return _username;
    }

    public MailReader username(final String value) {
        _username = value;
        return this;
    }

    public String password() {
        return _password;
    }

    public MailReader password(final String value) {
        _password = value;
        return this;
    }

    public int attachmentMemoryQuota() {
        return _attachment_memo_quota;
    }

    public MailReader attachmentMemoryQuota(final int value) {
        _attachment_memo_quota = value;
        return this;
    }

    public int attachmentSize() {
        return _attachment_size;
    }

    public MailReader attachmentSize(final int value) {
        _attachment_size = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void delete(final MailMessage message) {
        this.delete(message.id());
    }

    public void delete(final int message_id) {
        try {

            //create the POP3 store object and connect with the pop server
            final Store store = this.getStore();

            //create the folder object and open it
            final Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            // retrieve the messages from the folder in an array and print it
            final Message[] messages = emailFolder.getMessages();

            for (int i = 0; i < messages.length; i++) {
                if (message_id == messages[i].getMessageNumber()) {
                    this.deleteMessage(messages[i]);
                }
            }

            //close the store and folder objects
            emailFolder.close(true); // expunges=true
            store.close();

        } catch (Exception e) {
            super.error("delete", e);
        }
    }

    public Collection<MailMessage> check() {
        final List<MailMessage> list = new LinkedList<>();
        try {

            //create the POP3 store object and connect with the pop server
            final Store store = this.getStore();

            //create the folder object and open it
            final Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(_remove_after_read ? Folder.READ_WRITE : Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            final Message[] messages = emailFolder.getMessages();

            for (int i = 0; i < messages.length; i++) {
                list.add(new MailMessage(this, messages[i]));
                if (_remove_after_read) {
                    this.deleteMessage(messages[i]);
                }
            }

            //close the store and folder objects
            emailFolder.close(_remove_after_read);
            store.close();

        } catch (NoSuchProviderException e) {
            super.error("check", e);
        } catch (MessagingException e) {
            super.error("check", e);
        } catch (Exception e) {
            super.error("check", e);
        }
        return list;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Properties config() {
        final Properties properties = new Properties();

        properties.put("mail.pop3.host", _host);
        properties.put("mail.pop3.port", _port);
        properties.put("mail.pop3.starttls.enable", _tls);
        //properties.put("mail.pop3.disabletop", true);
        properties.put("mail.pop3.auth", true);
        //properties.put("mail.debug", "true");
        //properties.put("mail.debug.quote", "true");

        return properties;
    }

    private Session getSession() {
        return Session.getDefaultInstance(this.config());
    }

    private Store getStore() throws MessagingException {
        final Store store = this.getSession().getStore(_type);
        store.connect(_host, _username, _password);

        return store;
    }

    private void deleteMessage(final Message message) throws MessagingException {
        message.setFlag(Flags.Flag.DELETED, true);
    }

}
