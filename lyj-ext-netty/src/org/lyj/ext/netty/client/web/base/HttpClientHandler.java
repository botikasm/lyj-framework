package org.lyj.ext.netty.client.web.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import org.lyj.commons.Delegates;
import org.lyj.ext.netty.client.web.HttpClientResponse;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Delegates.Callback<HttpClientResponse> _callback_success;
    private final Delegates.Callback<Throwable> _callback_fail;
    private final String _content_file_name;

    private HttpClientResponse _response;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClientHandler(final Delegates.Callback<HttpClientResponse> callback_success,
                             final Delegates.Callback<Throwable> callback_fail,
                             final String contentFileName) {
        _callback_success = callback_success;
        _callback_fail = callback_fail;
        _content_file_name = contentFileName;

        _response = new HttpClientResponse(_content_file_name);
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
        cause.printStackTrace();
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

    private void onResponse(final HttpClientResponse response) {
        if (null != _callback_success) {
            _callback_success.handle(response);
        }
    }



}
