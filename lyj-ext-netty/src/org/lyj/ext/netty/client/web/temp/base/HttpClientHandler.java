package org.lyj.ext.netty.client.web.temp.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.client.web.temp.HttpTempClient;
import org.lyj.ext.netty.client.web.temp.HttpTempClientRequest;
import org.lyj.ext.netty.client.web.temp.HttpTempClientResponse;

import java.util.Set;

public class HttpClientHandler
        extends SimpleChannelInboundHandler<HttpObject> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpTempClientRequest _request;
    private final Delegates.Callback<HttpTempClientResponse> _callback_success;
    private final Delegates.Callback<Throwable> _callback_fail;

    private HttpTempClientResponse _response;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClientHandler(final HttpTempClientRequest request,
                             final Delegates.Callback<HttpTempClientResponse> callback_success,
                             final Delegates.Callback<Throwable> callback_fail) {
        _request = request;
        _callback_success = callback_success;
        _callback_fail = callback_fail;

        _response = new HttpTempClientResponse(_request.contentFile(), _request.encoding());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final HttpObject msg) {

        _response.handle(msg);
        if (_response.handled()) {
            ctx.close();

            this.onResponse(_response);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {

        ctx.close();

        this.onError(cause);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void onError(final Throwable error) {
        if (null != _callback_fail) {
            _callback_fail.handle(error);
        }
    }

    private void onResponse(final HttpTempClientResponse response) {
        if (response.isRedirect() && StringUtils.hasText(response.getRedirectUrl())) {
            this.redirect(response);
        } else {
            if (null != _callback_success) {
                _callback_success.handle(response);
            }
        }
    }

    private void redirect(final HttpTempClientResponse response) {
        try {
            final String location = response.getRedirectUrl();
            if (StringUtils.hasText(location)) {

                final HttpTempClient client = new HttpTempClient();

                /**
                Set<String> header_names = response.headers().keys();
                for(final String name:header_names){
                    //if(name.toLowerCase().startsWith("x-") || name.equalsIgnoreCase(HttpHeaderNames.COOKIE.toString())){
                        client.headers().add(name, response.headers().get(name));
                    //}
                }  **/

                Set<String> header_names = _request.headers().keys();
                for(final String name:header_names){
                    //if(name.toLowerCase().startsWith("x-") || name.equalsIgnoreCase(HttpHeaderNames.COOKIE.toString())){
                    client.headers().add(name, _request.headers().get(name));
                    //}
                }

                client.get().url(location).send(_callback_success);

            } else {
                // unhandled redirect
                if (null != _callback_success) {
                    _callback_success.handle(response);
                }
            }
        } catch (Throwable t) {
            this.onError(t);
        }
    }

}
