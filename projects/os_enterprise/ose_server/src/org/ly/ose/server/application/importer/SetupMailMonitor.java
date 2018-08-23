package org.ly.ose.server.application.importer;

import org.json.JSONArray;
import org.ly.ext.mail.reader.MailMessage;
import org.ly.ext.mail.reader.MailMessageAttachment;
import org.ly.ose.server.application.controllers.email.pop.AbstractMailMonitor;
import org.ly.ose.server.application.controllers.email.smtp.MailSender;
import org.ly.ose.server.deploy.config.ConfigHelper;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;

import java.io.File;
import java.io.IOException;

public class SetupMailMonitor
        extends AbstractMailMonitor {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private MailSender __mail_sender;
    private Delegates.Callback<EventData> _callback;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SetupMailMonitor() {
        super(ConfigHelper.instance().mailClient());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    @Override
    public void open() {
        super.open();
    }

    @Override
    public void close() {
        super.close();
    }

    public void handle(final Delegates.Callback<EventData> callback) {
        _callback = callback;
    }
    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    @Override
    protected boolean handle(final MailMessage message) {
        return this.checkMessage(message);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean checkMessage(final MailMessage message) {
        if (message.isReady()) {
            final String[] sender = message.info().fromAsEmailArray();

            // check for INSTALL or IMPORT
            if (message.body().hasAttachments()) {
                message.body().attachmentForEach((attachment) -> {
                    install(sender, attachment);
                });
            } else {
                // no attachment

            }
            return true; // remove this email

        } else {
            super.error("checkMessage",
                    FormatUtils.format("Message has errors: %s",
                            message.info().error()));
        }
        return false;
    }

    private void install(final String[] sender,
                         final MailMessageAttachment attachment) {
        // should install the bot moving file to new path
        // before install must check bot name is authorized getting name from configuration file
        try {
            // move file and unzip
            final String temp = PathUtils.getTemporaryDirectory(RandomUtils.randomUUID());
            try {
                final File file = attachment.saveFileTo(temp, true);
                this.handleFile(sender, file);
            } finally {
                FileUtils.delete(temp);
            }
        } catch (Throwable t) {
            super.error("install", t);
            this.sendEmailResponse(sender, "ERROR: " + t.toString());
        }
    }

    void handleFile(final String[] sender,
                    final File file) throws IOException {
        if (file.exists()) {
            // check if package contains an authorized bot

            // try to import this package
            if (null != _callback) {
                final EventData data = new EventData();
                data.fileName(file.getAbsolutePath());
                data.recipients(sender);
                data.action(EventData.ACTION_IMPORT);
                _callback.handle(data);
            } else {
                // MOVE (copy & remove) file into install folder
                PackageImporter.instance().put(file);
            }
            this.sendEmailResponse(sender, FormatUtils.format("PACKAGE '%s' successfully submitted for import.",
                    file.getName()));
        }


    }

    private MailSender sender() {
        try {
            if (null == __mail_sender) {
                __mail_sender = new MailSender(ConfigHelper.instance().mailStmp());
            }
        } catch (Throwable ignored) {
            // ignored
        }
        return __mail_sender;
    }

    private void sendEmailResponse(final String[] sender,
                                   final String message) {
        try {
            final MailSender mail_sender = this.sender();
            if(null!=sender){
                mail_sender.send(sender, "", "installer response", message, message);
            }
        } catch (Throwable ignored) {
            // ignored
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static SetupMailMonitor __instance;

    public static synchronized SetupMailMonitor instance() {
        if (null == __instance) {
            __instance = new SetupMailMonitor();
        }
        return __instance;
    }

// ------------------------------------------------------------------------
//                      M A I L   M O N I T O R   E V E N T  P A R A M
// ------------------------------------------------------------------------

    public static class EventData
            extends JsonItem {

        private static final String FLD_RECIPIENTS = "recipients";
        private static final String FLD_FILENAME = "filename";
        private static final String FLD_ACTION = "action";

        public static final String ACTION_IMPORT = "import";
        public static final String ACTION_INSTALL = "install";

        public boolean isImport() {
            return this.action().equalsIgnoreCase(ACTION_IMPORT);
        }

        public boolean isInstall() {
            return this.action().equalsIgnoreCase(ACTION_INSTALL);
        }

        public JSONArray recipientsArray() {
            if (!super.has(FLD_RECIPIENTS)) {
                super.put(FLD_RECIPIENTS, new JSONArray());
            }
            return super.getJSONArray(FLD_RECIPIENTS);
        }

        public String[] recipients() {
            return JsonWrapper.toArrayOfString(this.recipientsArray());
        }

        public EventData recipients(final String[] recipients) {
            final JSONArray array = new JSONArray();
            for (final String recipient : recipients) {
                array.put(recipient);
            }
            super.put(FLD_RECIPIENTS, array);
            return this;
        }

        public String fileName() {
            return super.getString(FLD_FILENAME);
        }

        public EventData fileName(final String value) {
            super.put(FLD_FILENAME, value);
            return this;
        }

        public String action() {
            return super.getString(FLD_ACTION);
        }

        public EventData action(final String value) {
            super.put(FLD_ACTION, value);
            return this;
        }

    }

}

