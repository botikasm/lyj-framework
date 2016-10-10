package org.ly.ext.mail;

import org.junit.Test;
import org.ly.ext.mail.reader.MailMessage;
import org.ly.ext.mail.reader.MailReader;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.Collection;

/**
 * CLIENT TEST
 */
public class MailReaderTest {

    private final static String TMP_ROOT = PathUtils.getTemporaryDirectory();

    @Test
    public void check() throws Exception {

        final MailReader reader = new MailReader(
                "SSL0.OVH.NET",
                110,
                "install@botfarmy.com",
                "!xxx"
        );
        final Collection<MailMessage> emails = reader.check();
        if (emails.size() > 0) {
            System.out.println("EMAILS: " + emails.size());
            for (final MailMessage msg : emails) {
                if (msg.body().hasAttachments()) {
                    // found email with attachments
                    Collection<File> files = msg.body().saveAttachmentsTo(TMP_ROOT);
                    for (final File file : files) {
                        System.out.println(file);
                        file.delete();
                    }
                }
            }
        } else {
            System.out.println("NO EMAILS");
        }
    }

}