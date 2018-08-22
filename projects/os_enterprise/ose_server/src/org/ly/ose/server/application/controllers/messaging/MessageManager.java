package org.ly.ose.server.application.controllers.messaging;

import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.controllers.messaging.handlers.AbstractMessageHandler;
import org.ly.ose.server.application.controllers.messaging.handlers.DatabaseMessageHandler;
import org.ly.ose.server.application.controllers.messaging.handlers.ProgramMessageHandler;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Main message manager.
 * Process OSERequests and returns OSEResponse
 */
public class MessageManager {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String TYPE_PROGRAM = OSERequest.TYPE_PROGRAM;
    private static final String TYPE_DATABASE = OSERequest.TYPE_DATABASE;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, AbstractMessageHandler> _handlers;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private MessageManager() {
        _handlers = new HashMap<>();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object handle(final OSERequest request) {
        // remove "null" values
        removeNulls(request);

        // validate request localization
        if (!StringUtils.hasText(request.lang())) {
            request.lang(IConstants.DEF_LANG);
        }

        final AbstractMessageHandler handler = this.getHandler(request.type());
        if (null != handler) {
            return handler.handle(request);
        } else {
            final OSEResponse response = OSERequest.generateResponse(request);
            response.error("Unhandled request type: " + request.type() + ". value=" + request.toString());
            return response;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        _handlers.put(TYPE_PROGRAM, new ProgramMessageHandler());
        _handlers.put(TYPE_DATABASE, new DatabaseMessageHandler());
    }

    private AbstractMessageHandler getHandler(final String type) {
        if (_handlers.containsKey(type)) {
            return _handlers.get(type);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static void removeNulls(final Map<String, Object> map) {
        if (null != map) {
            final Set<String> keys = map.keySet();
            for (final String key : keys) {
                final Object value = map.get(key);
                if (null != value) {
                    if (value instanceof String && value.toString().equalsIgnoreCase(IConstants.STR_NULL)) {
                        map.put(key, "");
                    } else if (value instanceof Map) {
                        removeNulls((Map) value);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static MessageManager __instance;

    public static synchronized MessageManager instance() {
        if (null == __instance) {
            __instance = new MessageManager();
        }
        return __instance;
    }

}
