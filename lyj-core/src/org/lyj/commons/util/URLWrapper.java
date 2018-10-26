package org.lyj.commons.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * [protocol:][//domain][:port][path][?query][#fragment]
 */
public class URLWrapper {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String LOCALHOST = "localhost";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _protocol;
    private String _domain;
    private int _port;
    private String _path;
    private String _query;
    private String _hash;

    private final Map<String, Object> _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public URLWrapper() {
        _params = new HashMap<>();
        this.parse("");
    }

    public URLWrapper(final String url) {
        this();
        this.parse(url);
    }

    @Override
    public String toString() {
        return this.stringify();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Map<String, Object> params() {
        return _params;
    }

    public boolean hasParams() {
        return !this.params().isEmpty();
    }

    public String query() {
        _query = "";
        if (!_params.isEmpty()) {
            _query = CollectionUtils.mapToString(_params, "&");
        }
        return _query;
    }

    public String path() {
        return _path;
    }

    public String hash() {
        return _hash;
    }

    public URLWrapper hash(final String value) {
        _hash = value;
        return this;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void parse(final String surl) {
        if (StringUtils.hasText(surl)) {
            try {
                final URL url = new URL(surl);
                _protocol = url.getProtocol();
                _domain = url.getAuthority();
                _port = url.getPort() > -1 ? url.getPort() : url.getDefaultPort();
                _path = url.getPath();
                _query = url.getQuery();
                _hash = url.toURI().getFragment();

                this.setParams(_query);

            } catch (Throwable ignored) {
            }
        } else {
            _protocol = HTTP;
            _domain = LOCALHOST;
            _port = 80;
            _path = "";
            _query = "";
            _hash = "";
        }
    }

    private void setParams(final String query) {
        if (StringUtils.hasText(query)) {
            _params.putAll(CollectionUtils.toMap(query));
        }
    }

    // [protocol:][//domain][:port][path][?query][#fragment]
    private String stringify() {
        final StringBuilder sb = new StringBuilder();
        sb.append(_protocol).append("://");
        sb.append(_domain);
        if (!this.isDefaultPort()) {
            sb.append(":").append(_port);
        }

        sb.append(_path);

        if (!_params.isEmpty()) {
            sb.append("?");
            sb.append(this.query());
        }

        if (StringUtils.hasText(_hash)) {
            sb.append("#");
            sb.append(_hash);
        }

        return sb.toString();
    }

    private boolean isDefaultPort() {
        return (_protocol.equals(HTTP) && _port == 80) || (_protocol.equals(HTTPS) && _port == 443);
    }

}
