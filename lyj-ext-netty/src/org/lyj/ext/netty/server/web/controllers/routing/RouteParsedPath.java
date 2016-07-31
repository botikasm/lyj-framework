package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.StringUtils;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class RouteParsedPath {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private RouteParsedPath _template;
    private String _encoding;
    private String _path;
    private String[] _tokens;
    private boolean _jolly;
    private String[] _param_names; // param names
    private boolean _match_template;
    private final Map<String, String> _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private RouteParsedPath() {
        _param_names = new String[0];
        _tokens = new String[0];
        _template = null;
        _encoding = CharEncoding.getDefault();
        _params = new LinkedHashMap<>();
    }

    public RouteParsedPath(final String path,
                           final String encoding) {
        this();
        _template = null; // this is the template
        _encoding = encoding;

        this.parse(validatePath(path));
    }

    public RouteParsedPath(final String path,
                           final RouteParsedPath template) {
        this();
        _template = template;
        _encoding = template.encoding();

        this.parse(validatePath(path));
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String encoding() {
        return _encoding;
    }

    public String path() {
        return _path;
    }

    public boolean jolly() {
        return _jolly;
    }

    public String[] tokens() {
        return _tokens;
    }

    public Map<String, String> params() {
        return _params;
    }

    public boolean matchTemplate() {
        return _match_template;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void parse(final String path) {
        if (null == _template) {
            // TEMPLATE
            if (path.contains("/:")) {
                // /api/name/:param1/:param2
                final int idx = path.indexOf("/:");
                _path = path.substring(0, idx);
                _param_names = StringUtils.split(path.substring(idx), "/", (value) -> {
                    return StringUtils.replace(value.trim(), ":", "");
                });
            } else if (path.contains("/*")) {
                // /api/*
                _path = path.substring(0, path.indexOf("/*"));
                _jolly = true;
            } else {
                _path = path;
            }
            _tokens = StringUtils.split(_path, "/", true);
            _match_template = false;
            // params
            final String[] param_names = _param_names;
            for (String param_name : param_names) {
                _params.put(param_name, "");
            }
        } else {
            // URL
            final String[] query_tokens = this.splitQueryParams(path);
            _path = query_tokens.length > 0 ? query_tokens[0] : path;
            _tokens = StringUtils.split(_path, "/", true);
            _match_template = this.match();
            if (_match_template) {
                // params
                if (_template._param_names.length > 0) {
                    final String[] param_names = _template._param_names;
                    final String[] param_values = paramValues(_tokens, param_names, _encoding);
                    if (param_values.length == param_names.length) {
                        for (int i = 0; i < param_names.length; i++) {
                            _params.put(param_names[i], param_values[i]);
                        }
                    }
                }
            }
        }
    }

    private String[] splitQueryParams(final String url) {
        if (url.contains("/?") || url.contains("?")) {
            return StringUtils.split(StringUtils.replace(url, "/?", "?"), "?");
        }
        return new String[]{url};
    }

    private boolean match() {
        if (null != _template) {
            // match
            if (this.path().equals(_template.path())) {
                return true;
            } else if (_template.jolly()) {
                return this.path().startsWith(_template.path().concat("/")) || this.path().startsWith(_template.path().concat("?"));
            } else if (_template._param_names.length > 0) {
                return this.path().startsWith(_template.path().concat("/"))
                        && _tokens.length == _template._tokens.length + _template._param_names.length;
            }
        }
        return false;
    }

    private static String validatePath(final String path) {
        String response = path;
        if (!response.equals("/")) {
            if (response.endsWith("/")) {
                response = response.substring(0, response.lastIndexOf("/"));
            }
        }

        return response;
    }


    private static String[] paramValues(final String[] tokens,
                                        final String[] names,
                                        final String encoding) {
        // prepare array
        final String[] values = new String[names.length];
        if (values.length > 0) {
            Arrays.fill(values, "");

            int count = 0;
            if (tokens.length >= names.length) {
                for (int i = names.length - 1; i > -1; i--) {
                    int ivalue = values.length - (count + 1);
                    int itoken = tokens.length - (count + 1);
                    values[ivalue] = decode(tokens[itoken], encoding);
                    count++;
                }
            }
        }

        return values;
    }

    private static String decode(final String value, final String encoding) {
        try {
            return URLDecoder.decode(value, encoding);
        } catch (Throwable ignored) {
            return value;
        }
    }

}
