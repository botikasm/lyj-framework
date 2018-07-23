package org.ly.server.application.endpoints.socket;


import org.ly.server.application.controllers.messaging.MessageManager;
import org.ly.server.application.model.messaging.OSERequest;
import org.ly.server.application.model.messaging.OSEResponse;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.JsonConverter;
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

    private final SocketServer _server;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketController(final SocketServer server) {
        _server = server;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void notifyRequest(final String session_id,
                              final Object data) {
        try {
            final OSERequest request = new OSERequest(JsonConverter.toObject(data));

            // only messages witha client uid are valid messages
            if (StringUtils.hasText(request.uid())) {
                // set responde channel id (session_id)
                // and address
                request.channel(session_id)
                        .address(SessionClientController.instance().addressOf(session_id));

                // ready to process message
                // if message has a response, it's immediately dispatched
                // note: socket messages can have async later responses
                final OSEResponse response = MessageManager.instance().handle(request);
                response.uid(_server.config().uri());

                // send only if response has a payload
                if (response.hasPayload()) {
                    sendResponse(response.request().channel(), response);
                }
            }

        } catch (Throwable t) {
            super.error("notifyRequest", t);
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static void sendResponse(final String session_id,
                                    final OSEResponse message) {
        // is client still connected?
        if (SessionClientController.instance().exists(session_id)) {
            SessionClientController.instance().open(session_id).writeRaw(message);
        }
    }


}
