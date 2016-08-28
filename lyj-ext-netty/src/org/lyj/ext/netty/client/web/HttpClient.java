package org.lyj.ext.netty.client.web;

import org.lyj.commons.Delegates;
import org.lyj.commons.network.http.IHttpConstants;
import org.lyj.ext.netty.client.web.executor.HttpClientExecutor;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Client for Http requests.
 * Wrapper of AsyncHttpClient:
 * https://github.com/AsyncHttpClient/async-http-client
 */
public class HttpClient {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpClientInfo _info;

    private Delegates.Callback<Throwable> _fail_callback;
    private Delegates.Callback<HttpClientResponse> _success_callback;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpClient() {
        _info = new HttpClientInfo();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public HttpClientInfo info() {
        return _info;
    }

    public String encoding() {
        return this.info().encoding();
    }

    public HttpClient encoding(final String value) {
        this.info().encoding(value);
        return this;
    }

    public String method() {
        return this.info().method();
    }

    public HttpClient method(final String value) {
        this.info().method(value);
        return this;
    }

    public HttpClient get() {
        this.info().method(IHttpConstants.METHOD_GET);
        return this;
    }

    public HttpClient post() {
        this.info().method(IHttpConstants.METHOD_POST);
        return this;
    }

    public HttpClient put() {
        this.info().method(IHttpConstants.METHOD_PUT);
        return this;
    }

    public HttpClient delete() {
        this.info().method(IHttpConstants.METHOD_DELETE);
        return this;
    }

    public HttpClient url(final String url) throws URISyntaxException {
        this.info().url(url);
        return this;
    }

    public HttpClient url(final URL url) throws URISyntaxException {
        this.info().url(url.toString());
        return this;
    }

    public HttpClient url(final URI url) throws URISyntaxException {
        this.info().url(url.toString());
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpClient fail(final Delegates.Callback<Throwable> fail_callback) {
        _fail_callback = fail_callback;
        return this;
    }

    public HttpClient success(final Delegates.Callback<HttpClientResponse> success_callback) {
        _success_callback = success_callback;
        return this;
    }

    public void execute() {
        try {
            final HttpClientResponse response = this.executeRequest();
            this.doSuccess(response);
        } catch (Throwable t) {
            this.doFail(t);
        }
    }

    public HttpClientResponse send() throws Exception {
        return this.executeRequest();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private HttpClientResponse executeRequest() throws Exception {
        return new HttpClientExecutor(this).execute();
    }

    private boolean doFail(final Throwable t) {
        if (null != _fail_callback) {
            _fail_callback.handle(t);
            return true;
        }
        return false;
    }

    private boolean doSuccess(final HttpClientResponse response) {
        if (null != _success_callback) {
            _success_callback.handle(response);
            return true;
        }
        return false;
    }

}
