package org.lyj.gui.application.app.network.http;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.network.http.client.HttpClient;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.config.network.ConfigNetwork;
import org.lyj.gui.application.app.utils.PlatformUtils;

import java.util.Map;

/**
 * Helper class to make http request
 */
public class AbstractHttpClient
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private ConfigNetwork _config;
    private final String _api_root;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractHttpClient(final ConfigNetwork config) {
        _config = config;
        _api_root = PathUtils.concat(_config.connectionString(), _config.pathRoot());
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected String path(final String endpoint, final String method) {
        return PathUtils.concat(endpoint, method);
    }

    protected void post(final String api_name, final Map<String, Object> params,
                        final Delegates.SingleResultCallback<JsonWrapper> callback) {
        Async.invoke((args) -> {
            final String surl = PathUtils.concat(_api_root, api_name);
            this.client().post(surl, params, (err, response) -> {
                PlatformUtils.synch(() -> {
                    if (null != err) {
                        Delegates.invoke(callback, err, null);
                    } else {
                        try {
                            Delegates.invoke(callback, null, this.parse(response));
                        } catch (Throwable t) {
                            Delegates.invoke(callback, t, null);
                        }
                    }
                });
            });
        });
    }

    protected void get(final String api_name, final Map<String, Object> params,
                       final Delegates.SingleResultCallback<JsonWrapper> callback) {
        Async.invoke((args) -> {
            final String surl = PathUtils.concat(_api_root, api_name);
            this.client().get(surl, params, (err, response) -> {
                PlatformUtils.synch(() -> {
                    if (null != err) {
                        Delegates.invoke(callback, err, null);
                    } else {
                        try {
                            Delegates.invoke(callback, null, this.parse(response));
                        } catch (Throwable t) {
                            Delegates.invoke(callback, t, null);
                        }
                    }
                });
            });
        });
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private HttpClient client() {
        HttpClient client = new HttpClient();

        return client;
    }

    private JsonWrapper parse(final String response) throws Exception {
        final JsonWrapper json = new JsonWrapper(response);
        final String error = json.isJSONObject() ? json.deepString("error"):"";
        if (StringUtils.hasText(error)) {
            throw new Exception(error);
        } else {
            return json;
        }
    }

}
