package org.ly.licensemanager.app.server.listeners.api.routing.impl;

import org.ly.licensemanager.app.server.listeners.api.routing.AbstractApi;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.HttpServerContext;

/**
 * LICENSE MANAGER API
 */
public class ApiLicense
        extends AbstractApi {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    private static final String PARAM_MODE = "mode"; // global, bot, company
    private static final String PARAM_UID = "uid"; // bot_uid, company_uid or empty for global DB access
    private static final String PARAM_COLLECTION = "collection";

    private static final String PARAM_KEY = "key";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ApiLicense() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void register(final HttpServerContext context) {
        final String token = super.getParamToken(context);

        super.auth(token, (err, valid) -> {
            if (null == err) {
                // params
                final String mode = super.getParam(context, PARAM_MODE);
                final String uid = super.getParam(context, PARAM_UID);
                final String collection = super.getParam(context, PARAM_COLLECTION);

                final String key = super.getParam(context, PARAM_KEY);
                if (StringUtils.hasText(mode) && StringUtils.hasText(uid) && StringUtils.hasText(collection)) {
                    if (StringUtils.hasText(key)) {

                        try {

                            final String response = "";

                            super.writeJSON(context, response);
                        } catch (Throwable t) {
                            super.writeError(context, t, "register");
                        }

                    } else {
                        super.writeErroMissingParams(context, PARAM_KEY);
                    }
                } else {
                    super.writeErroMissingParams(context, PARAM_MODE, PARAM_UID, PARAM_COLLECTION);
                }
            } else {
                super.writeError(context, err);
            }
        });
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
