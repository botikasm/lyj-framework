package org.ly.ose.server.application.programming.tools.remote;

import jdk.nashorn.api.scripting.JSObject;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.network.http.client.HttpClient;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.utils.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP client
 */
public class Tool_http
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "http"; // used as $http.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _encoding;
    private long _timeout;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_http(final OSEProgram program) {
        super(NAME, program);
        _encoding = CharEncoding.UTF_8;
        _timeout = 10 * 1000;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    public String getEncoding() {
        return _encoding;
    }

    public void setEncoding(final String encoding) {
        _encoding = CharEncoding.isSupported(encoding) ? encoding : _encoding;
    }

    // ------------------------------------------------------------------------
    //                      G E T
    // ------------------------------------------------------------------------

    public Object get(final String url,
                      final JSObject callback) throws Exception {
        return this.get(url, callback, null);
    }

    public Object get(final String url,
                      final Object body,
                      final JSObject callback) throws Exception {
        if (null != callback) {
            if (StringUtils.hasText(url)) {
                try {
                    final Map<String, Object> params = Converter.toMap(body);
                    final Task<String> task = this.doGet(url, params);
                    final String response = task.get();
                    this.response(callback, response);
                } catch (Throwable t) {
                    this.error(callback, t);
                }
            }
        } else {
            return this.get(url, body);
        }
        return false;
    }

    public String get(final String url) throws Exception {
        return this.get(url, new HashMap<>());
    }

    public String get(final String url,
                      final Object body) throws Exception {
        if (StringUtils.hasText(url)) {
            final Map<String, Object> params = Converter.toMap(body);
            final Task<String> task = this.doGet(url, params);
            return task.get();
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      P O S T
    // ------------------------------------------------------------------------

    public Object post(final String url,
                       final Object body,
                       final JSObject callback) throws Exception {
        if (null != callback) {
            if (StringUtils.hasText(url)) {
                try {
                    final Map<String, Object> params = Converter.toMap(body);
                    final Task<String> task = this.doPost(url, params);
                    final String response = task.get();
                    this.response(callback, response);
                } catch (Throwable t) {
                    this.error(callback, t);
                }
            }
        } else {
            return this.post(url, body);
        }
        return false;
    }

    public String post(final String url,
                       final Object body) throws Exception {
        if (StringUtils.hasText(url)) {
            final Map<String, Object> params = Converter.toMap(body);
            final Task<String> task = this.doPost(url, params);
            return task.get();
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Task<String> doPost(final String url,
                                final Map<String, Object> params) {
        return new Task<String>(t -> {

            HttpClient client = new HttpClient();
            client.post(url, params, (err, result) -> {
                if (null != err) {
                    t.fail(err);
                } else {
                    t.success(result);
                }
            });

        }).setTimeout(_timeout).run();
    }

    private Task<String> doGet(final String url,
                               final Map<String, Object> params) {
        return new Task<String>(t -> {

            HttpClient client = new HttpClient();
            client.get(url, params, (err, result) -> {
                if (null != err) {
                    t.fail(err);
                } else {
                    t.success(result);
                }
            });

        }).setTimeout(_timeout).run();
    }

    private void error(final JSObject callback, final Throwable t) {
        if (null != callback) {
            callback.call(null, ExceptionUtils.getMessage(t), null);
        }
    }

    private void response(final JSObject callback, final String response) {
        if (null != callback) {
            callback.call(null, null, response);
        }
    }


}
