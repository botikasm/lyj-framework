package org.lyj.ext.netty.client.web.executor;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.lyj.commons.network.http.IHttpConstants;
import org.lyj.commons.util.FormatUtils;
import org.lyj.ext.netty.client.web.HttpClient;
import org.lyj.ext.netty.client.web.HttpClientInfo;
import org.lyj.ext.netty.client.web.HttpClientResponse;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Executor implementation.
 * This is the class containig all client implementation dependencies.
 */
public class HttpClientExecutor {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpClient _client;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClientExecutor(final HttpClient client) {
        _client = client;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpClientResponse execute() throws Exception {
        final HttpClientResponse response = new HttpClientResponse(_client.info());
        response.content(this.executeRequest());
        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    private String executeRequest() throws Exception {
        final BoundRequestBuilder request = this.prepareRequest();
        Future<Response> f = request.execute();
        Response r = f.get();

        return r.getResponseBody();
    }

    private BoundRequestBuilder prepareRequest() throws Exception {
        final String method = _client.method();
        final HttpClientInfo info = _client.info();

        //AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().build();
        final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        final BoundRequestBuilder request;
        if (method.equalsIgnoreCase(IHttpConstants.METHOD_GET)) {
            request = asyncHttpClient.prepareGet(info.url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_POST)) {
            request = asyncHttpClient.preparePost(info.url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_PUT)) {
            request = asyncHttpClient.preparePut(info.url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_OPTIONS)) {
            request = asyncHttpClient.prepareOptions(info.url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_DELETE)) {
            request = asyncHttpClient.prepareDelete(info.url());
        } else {
            request = null;
        }

        if (null == request) {
            throw new Exception(FormatUtils.format("Method not supported: %s", method));
        }

        if(null!=info.body()){
            request.setBody(info.body().toString());
        }

        if (info.headers().length() > 0) {
            final Set<String> keys = info.headers().keySet();
            for (final String key : keys) {
                request.addHeader(key, info.headers().get(key));
            }
        }

        return request;
    }


}
