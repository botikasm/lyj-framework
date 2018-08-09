package org.ly.ose.server.application.programming.tools.i18n;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.MapConverter;
import org.lyj.ext.script.utils.Converter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Internationalization Utility.
 * Sample usage:
 * response.dic_phrase_base = $i18n.get('hello');
 * response.dic_phrase_it = $i18n.get('it', 'hello');
 * <p>
 * response.content_base = $i18n.load('/i18n').content();
 * response.content_it = $i18n.load('/i18n').content('it');
 * <p>
 * response.data_base = $i18n.load('/i18n/data').content();
 * response.data_it = $i18n.load('/i18n/data').content('it');
 * response.data_len_it = $i18n.load('/i18n/data').content('it').length;
 * response.data_len_base = $i18n.load('/i18n/data').content().length;
 */
public class Tool_i18n
        extends OSEProgramToolRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "i18n"; // used as $i18n.

    private static final String BASE_LANG = IConstants.BASE_LANG;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final String _root_i18n;
    private final Map<String, Resource> _cached_resources;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_i18n(final OSEProgram program) {
        super(NAME, program);
        _root = program.root();
        _root_i18n = PathUtils.concat(program.root(), "i18n");
        _cached_resources = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        _cached_resources.clear();
    }

    //-- dictionary --//

    public ContentHelper load() {
        return new ContentHelper(this, _root_i18n);
    }

    public ContentHelper load(final String partial_path) {
        return new ContentHelper(this, PathUtils.concat(_root, partial_path));
    }

    public Object get(final String key) throws Exception {
        return this.load().get(key);
    }

    public Object get(final String lang,
                      final String key) throws Exception {
        return this.load().get(lang, key);
    }

    //-- data --//

    public Object content() throws Exception {
        return this.load().content();
    }

    public Object content(final String lang) throws Exception {
        return this.load().content(lang);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Resource getCachedResource(final String root) {
        if (!_cached_resources.containsKey(root)) {
            _cached_resources.put(root, new Resource(root));
        }
        return _cached_resources.get(root);
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * Manage Resources
     */
    private class Resource {
        // --------------------------------------------------------------------
        //                      f i e l d s
        // --------------------------------------------------------------------

        private final String _root_dir;
        private final Map<String, Object> _resources;

        // --------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // --------------------------------------------------------------------

        private Resource(final String root_dir) {
            _root_dir = root_dir;
            _resources = new HashMap<>();
        }

        // --------------------------------------------------------------------
        //                      p u b l i c
        // --------------------------------------------------------------------

        public Object get(final String lang) throws Exception {
            Object response = this.getData(lang);
            if (null == response) {
                response = this.getData(BASE_LANG);
            }
            return Converter.toJsonCompatible(response);
        }


        // --------------------------------------------------------------------
        //                      p r i v a t e
        // --------------------------------------------------------------------

        private Object getData(final String lang) throws Exception {
            if (!_resources.containsKey(lang)) {
                // lookup for resource
                final String content = this.read(PathUtils.concat(_root_dir, lang.concat(".json")));
                if (StringUtils.hasText(content)) {
                    if (StringUtils.isJSONArray(content)) {
                        _resources.put(lang, new JSONArray(content));
                    } else if (StringUtils.isJSONObject(content)) {
                        _resources.put(lang, new JSONObject(content));
                    } else {
                        _resources.put(lang, content);
                    }
                }
            }
            return _resources.get(lang); // may be null
        }

        private String read(final String path) throws IOException {
            final File file = new File(path);
            if (file.exists()) {
                return FileUtils.readFileToString(file);
            }
            return "";
        }


    }

    public class ContentHelper {

        // --------------------------------------------------------------------
        //                      f i e l d s
        // --------------------------------------------------------------------

        private final Tool_i18n _parent;
        private final String _root_dir;

        // --------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // --------------------------------------------------------------------

        private ContentHelper(final Tool_i18n parent,
                              final String root_dir) {
            _parent = parent;
            _root_dir = root_dir;
        }

        // --------------------------------------------------------------------
        //                      p u b l i c
        // --------------------------------------------------------------------

        public Object content() throws Exception {
            return this.content(_parent.getLang());
        }

        public Object content(final String lang) throws Exception {
            return this.getContent(lang, _root_dir);
        }

        public Object get(final String key) throws Exception {
            return this.get(_parent.getLang(), key);
        }

        public Object get(final String lang,
                          final String key) throws Exception {
            final Object content = this.getContent(lang, _root_dir);
            if (null != content) {
                if (content instanceof Map) {
                    final Map map = (Map) content;
                    return map.containsKey(key) ? map.get(key) : false;
                }
            }
            return false;
        }

        // --------------------------------------------------------------------
        //                      p r i v a t e
        // --------------------------------------------------------------------

        private Object getContent(final String lang,
                                  final String root) throws Exception {
            final Resource resource = _parent.getCachedResource(root);
            if (null != resource) {
                final Object obj = resource.get(lang);
                if (null != obj) {
                    if (obj instanceof JSONObject) {
                        //  map
                        return MapConverter.toMap(obj);
                    } else if (obj instanceof JSONArray) {
                        // array of map
                        final Collection list = MapConverter.toList(obj);
                        return null != list ? list.toArray(new Object[0]) : new Object[0];
                    } else {
                        // native data
                        return obj;
                    }
                }
            }
            return null;
        }


    }
}
