package org.ly.licensemanager.app.server.controllers;

import org.ly.licensemanager.IConstants;
import org.lyj.commons.Delegates;
import org.lyj.commons.logging.AbstractLogEmitter;

import java.util.HashMap;
import java.util.Map;

/**
 * Authorize tokens.
 * TOKENS are stored into internal cache to avoid database access at each request.
 */
public class TokenController
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Boolean> _token_responses;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private TokenController() {
        _token_responses = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Clear tokens cache.
     * Call this method when new application is added to database at runtime.
     */
    public void clear() {
        synchronized (_token_responses) {
            _token_responses.clear();
        }
    }

    public boolean auth(final String token) {
        try {
            if (_token_responses.containsKey(token)) {
                return _token_responses.get(token);
            } else {
                final boolean exists = token.equals(IConstants.APP_TOKEN_COINMULE_API)
                        || token.equals(IConstants.APP_TOKEN_CATCHUP_API);
                synchronized (_token_responses) {
                    _token_responses.put(token, exists);
                }
                return exists;
            }
        } catch (Throwable t) {
            super.error("auth", t);
        }
        return false;
    }

    public void auth(final String token, final Delegates.SingleResultCallback<Boolean> callback) {
        try {
            Delegates.invoke(callback, null, this.auth(token));
        } catch (Throwable t) {
            super.error("auth", t);
            Delegates.invoke(callback, t, false);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static TokenController __instance;

    public static synchronized TokenController instance() {
        if (null == __instance) {
            __instance = new TokenController();
        }
        return __instance;
    }
}
