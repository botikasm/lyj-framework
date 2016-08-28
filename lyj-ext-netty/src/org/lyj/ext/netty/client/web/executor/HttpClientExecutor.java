package org.lyj.ext.netty.client.web.executor;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.lyj.commons.network.http.IHttpConstants;
import org.lyj.commons.util.FormatUtils;
import org.lyj.ext.netty.client.web.HttpClient;
import org.lyj.ext.netty.client.web.HttpClientResponse;

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
        final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        if (method.equalsIgnoreCase(IHttpConstants.METHOD_GET)) {
            return asyncHttpClient.prepareGet(_client.info().url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_POST)) {
            return asyncHttpClient.preparePost(_client.info().url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_PUT)) {
            return asyncHttpClient.preparePut(_client.info().url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_OPTIONS)) {
            return asyncHttpClient.prepareOptions(_client.info().url());
        } else if (method.equalsIgnoreCase(IHttpConstants.METHOD_DELETE)) {
            return asyncHttpClient.prepareDelete(_client.info().url());
        }
        throw new Exception(FormatUtils.format("Method not supported: %s", method));
    }


}
