package org.ly.server.application.programming;

import org.json.JSONObject;
import org.lyj.commons.util.json.JsonWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AppProgramInfo {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _namespace;
    private String _name;
    private String _description;
    private String _version;
    private String _author;

    private String _installation_root;

    private final Map<File, String> _files;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AppProgramInfo() {
        _files = new HashMap<>();
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

    public String namespace() {
        return _namespace;
    }

    public AppProgramInfo namespace(final String value) {
        _namespace = value;
        return this;
    }

    public String name() {
        return _name;
    }

    public AppProgramInfo name(final String value) {
        _name = value;
        return this;
    }

    public String description() {
        return _description;
    }

    public AppProgramInfo description(final String value) {
        _description = value;
        return this;
    }

    public String version() {
        return _version;
    }

    public AppProgramInfo version(final String value) {
        _version = value;
        return this;
    }

    public String author() {
        return _author;
    }

    public AppProgramInfo author(final String value) {
        _author = value;
        return this;
    }

    public String installationRoot() {
        return _installation_root;
    }

    public AppProgramInfo installationRoot(final String value) {
        _installation_root = value;
        return this;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
