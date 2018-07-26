package org.ly.ose.server.application.programming;

import org.json.JSONObject;
import org.ly.ose.server.IConstants;
import org.lyj.commons.util.json.JsonWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Program descriptor
 */
public class OSEProgramInfo {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // custom data fields
    public static final String FLD_SESSION_ID = "session_id";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _namespace;
    private String _name;
    private String _description;
    private String _version;
    private String _author;
    private int _session_timeout;
    private int _loop_interval;

    private String _installation_root;

    private final Map<File, String> _files;

    private final Map<String, Object> _custom_data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgramInfo() {
        _files = new HashMap<>();
        _custom_data = new HashMap<>();
        this.init();
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject toJson() {
        final JSONObject sb = new JSONObject();
        sb.put("namespace", _namespace);
        sb.put("name", _name);
        sb.put("version", _version);
        sb.put("description", _description);
        sb.put("author", _author);
        sb.put("session_timeout", _session_timeout);
        sb.put("loop_interval", _loop_interval);
        sb.put("files", _files.size());
        return sb;
    }

    public Map<String, Object> toMap() {
        return JsonWrapper.toMap(this.toJson());
    }

    public String uid() {
        return _namespace.concat(".").concat(_name);
    }

    public Map<File, String> files() {
        return _files;
    }

    public Map<String, Object> data() {
        return _custom_data;
    }

    public String namespace() {
        return _namespace;
    }

    public OSEProgramInfo namespace(final String value) {
        _namespace = value;
        return this;
    }

    public String name() {
        return _name;
    }

    public OSEProgramInfo name(final String value) {
        _name = value;
        return this;
    }

    public String description() {
        return _description;
    }

    public OSEProgramInfo description(final String value) {
        _description = value;
        return this;
    }

    public String version() {
        return _version;
    }

    public OSEProgramInfo version(final String value) {
        _version = value;
        return this;
    }

    public String author() {
        return _author;
    }

    public OSEProgramInfo author(final String value) {
        _author = value;
        return this;
    }

    public String installationRoot() {
        return _installation_root;
    }

    public OSEProgramInfo installationRoot(final String value) {
        _installation_root = value;
        return this;
    }

    public int loopInterval() {
        return _loop_interval;
    }

    public OSEProgramInfo loopInterval(final int value) {
        _loop_interval = value;
        return this;
    }

    public int sessionTimeout() {
        return _session_timeout;
    }

    public OSEProgramInfo sessionTimeout(final int value) {
        _session_timeout = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.loopInterval(IConstants.LOOP_INTERVAL_MS);
        this.sessionTimeout(IConstants.SESSION_TIMEOUT_MS);
    }

}
