package org.ly.ose.server.application.programming.tools.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.async.Locker;
import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.MapConverter;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Internationalization Utility.
 * Sample usage:
 * <p>
 * $resource.load('/i18n/data/base.json').getString();
 */
public class Tool_resource
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "resource"; // used as $resource.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final Map<String, Resource> _cached_resources; // path, resource

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_resource(final OSEProgram program) {
        super(NAME, program);
        _root = program.root();
        _cached_resources = Collections.synchronizedMap(new HashMap<>());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        synchronized (_cached_resources) {
            _cached_resources.clear();
        }
    }

    public Resource load(final String partial_path) {
        synchronized (_cached_resources) {
            final String path = PathUtils.concat(_root, partial_path);
            if (!_cached_resources.containsKey(path)) {
                _cached_resources.put(path, new Resource(path));
            }
            return _cached_resources.get(path);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * Helper
     */
    public class Resource {
        // --------------------------------------------------------------------
        //                      f i e l d s
        // --------------------------------------------------------------------

        private final File _file;

        // --------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // --------------------------------------------------------------------

        private Resource(final String full_path) {
            _file = new File(full_path);
        }

        // --------------------------------------------------------------------
        //                      p u b l i c
        // --------------------------------------------------------------------

        public String getString() throws Exception {
            final byte[] bytes = this.read();
            return new String(bytes);
        }

        public byte[] getBytes() throws Exception {
            return this.read();
        }

        public Map<String, Object> getObject() throws Exception {
            final String text = this.getString();
            if (StringUtils.isJSONObject(text)) {
                return MapConverter.toMap(new JSONObject(text));
            }
            return new HashMap<>();
        }

        public Collection getList() throws Exception {
            final String text = this.getString();
            if (StringUtils.isJSONArray(text)) {
                return MapConverter.toList(new JSONArray(text));
            }
            return new ArrayList<>();
        }

        public Object[] getArray() throws Exception {
            return this.getList().toArray(new Object[0]);
        }

        // --------------------------------------------------------------------
        //                      p r i v a t e
        // --------------------------------------------------------------------

        private byte[] read() throws IOException {
            Locker.instance().lock(_file.getAbsolutePath());
            try {
                if (_file.exists()) {
                    return ByteUtils.getBytes(_file);
                }
                return new byte[0];
            } finally {
                Locker.instance().unlock(_file.getAbsolutePath());
            }
        }


    }
}
