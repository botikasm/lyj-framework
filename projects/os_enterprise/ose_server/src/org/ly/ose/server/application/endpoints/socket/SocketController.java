package org.ly.ose.server.application.endpoints.socket;


import org.json.JSONObject;
import org.lyj.IConstants;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.JsonConverter;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionClientController;

/**
 * Dispatch socket messages
 */
public class SocketController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String REQ_TYPE_TEXT = "text";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private SocketController() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void notifyRequest(final String session_id,
                              final Object data) {
        try {
            final JSONObject json = JsonConverter.toObject(data);

            final WebRequest request = new WebRequest(json);
            request.address(SessionClientController.instance().addressOf(session_id));
            if (StringUtils.hasText(request.address())) {

                if (this.exists(request.uid())) {

                    // get data special fields
                    final String data_bot_id = this.lookupData(request.data(), "bot_id");
                    final String data_company_uid = this.lookupData(request.data(), "company_uid");
                    final String data_contact_id = this.lookupData(request.data(), "contact_id");
                    final String sender_id = StringUtils.hasText(data_contact_id) ? data_contact_id : request.sender();

                    // TODO: handle request

                } else {
                    // request for a bot that does not exists

                }
            }

        } catch (Throwable t) {
            super.error("notifyRequest", t);
        }
    }

    public void sendResponse(final String uid, final JSONObject message) {
        SessionClientController.instance().open(uid).write(message);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean exists(final String channel_id) {
        // TODO: check channel or channel content exists

        return true;
    }

    private String lookupData(final Object data, final String field_name) {
        try {
            final JsonItem item = new JsonItem(data);
            return item.getString(field_name);
        } catch (Throwable t) {
            return "";
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static SocketController __instance;

    public static synchronized SocketController instance() {
        if (null == __instance) {
            __instance = new SocketController();
        }
        return __instance;
    }

    // ------------------------------------------------------------------------
    //                      e m b e d d e d
    // ------------------------------------------------------------------------

    public static class WebRequest
            extends JsonItem {


        // --------------------------------------------------------------------
        //                      c o n s t
        // --------------------------------------------------------------------

        private static final String FLD_ADDRESS = "address";

        private static final String FLD_UID = "uid";
        private static final String FLD_LANG = "lang";
        private static final String FLD_TYPE = "type";
        private static final String FLD_SENDER = "sender";
        private static final String FLD_DATA = "data";
        private static final String FLD_COMPANY_UID = "company_uid";
        private static final String FLD_MESSAGE = "message";

        private static final String SENDER_ANONYMOUS = "anonymous";

        // --------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // --------------------------------------------------------------------

        public WebRequest(final JSONObject item) {
            super(item);
            this.init();
        }

        // --------------------------------------------------------------------
        //                      p u b l i c
        // --------------------------------------------------------------------

        public WebRequest address(final String value) {
            super.put(FLD_ADDRESS, value);
            return this;
        }

        public String address() {
            return super.getString(FLD_ADDRESS);
        }

        public WebRequest uid(final String value) {
            super.put(FLD_UID, value);
            return this;
        }

        public String companyUid() {
            return super.getString(FLD_COMPANY_UID);
        }

        public WebRequest companyUid(final String value) {
            super.put(FLD_COMPANY_UID, value);
            return this;
        }

        public String uid() {
            return super.getString(FLD_UID);
        }

        public WebRequest lang(final String value) {
            super.put(FLD_LANG, value);
            return this;
        }

        public String lang() {
            return super.getString(FLD_LANG);
        }

        public WebRequest type(final String value) {
            super.put(FLD_TYPE, value);
            return this;
        }

        public String type() {
            return super.getString(FLD_TYPE);
        }

        public WebRequest sender(final String value) {
            super.put(FLD_SENDER, value);
            return this;
        }

        public String sender() {
            return super.getString(FLD_SENDER);
        }

        public WebRequest message(final Object value) {
            super.put(FLD_MESSAGE, value);
            return this;
        }

        public Object data() {
            return super.getString(FLD_DATA);
        }

        public WebRequest data(final Object value) {
            super.put(FLD_DATA, value);
            return this;
        }

        public Object message() {
            return super.get(FLD_MESSAGE);
        }

        // --------------------------------------------------------------------
        //                      p r i v a t e
        // --------------------------------------------------------------------

        private void init() {
            // check lang
            this.lang(LocaleUtils.getLanguage(this.lang()));
            if (!StringUtils.hasText(this.lang())) {
                this.lang(IConstants.DEF_LANG);
            }

            // check type
            if (!StringUtils.hasText(this.type())) {
                this.type(REQ_TYPE_TEXT);
            }

            // check sender
            if (!StringUtils.hasText(this.sender())) {
                this.sender(SENDER_ANONYMOUS);
            }
        }

    }

}
