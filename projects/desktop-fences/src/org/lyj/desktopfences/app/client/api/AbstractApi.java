package org.lyj.desktopfences.app.client.api;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.controllers.routing.RoutingContext;

import java.util.Map;

public abstract class AbstractApi extends AbstractLogEmitter{

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------
    private final static String PARAM_APP_TOKEN = "app_token";


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractApi(){}



    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------


    protected String getParam(final RoutingContext context, final String key){

        String result = "";

        final Map<String,Object> params = context.params();

        if(!params.isEmpty()){
            if(StringUtils.hasText(key)){
                result = (String) params.get(key);
            }
        }

        return result;
    }

    protected String getParamToken(final RoutingContext context){
        return getParam(context,PARAM_APP_TOKEN);
    }


    protected void writeError(final RoutingContext context, final Throwable t) {
        this.writeError(context, t, "");
    }

    protected void writeError(final RoutingContext context, final Throwable t, final String methodName) {

        if(StringUtils.hasText(methodName)){
            super.error(methodName, t);
        }
    }

    protected void writeErroMissingParams(final RoutingContext context, final String... names) {
        writeError(context, new Exception("Bad Request, missing some parameters: " + CollectionUtils.toCommaDelimitedString(names)));
    }



}
