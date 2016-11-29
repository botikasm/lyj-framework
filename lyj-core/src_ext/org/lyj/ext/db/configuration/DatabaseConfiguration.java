package org.lyj.ext.db.configuration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.JsonItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * configuration helper
 */
public class DatabaseConfiguration
        extends JsonItem {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ENABLED = "enabled";
    private static final String AUTOCREATE = "autocreate";
    private static final String HOSTS = "hosts";
    private static final String CREDENTIALS = "credentials";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DatabaseConfiguration() {
        super();
    }

    public DatabaseConfiguration(final JSONObject item) {
        super(item);
    }

    public DatabaseConfiguration(final JsonItem item) {
        super(item);
    }

    public DatabaseConfiguration(final Map<String, Object> item) {
        super(item);
    }

    public DatabaseConfiguration(final Properties item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enabled() {
        return super.getBoolean(ENABLED);
    }

    public DatabaseConfiguration enabled(final boolean value) {
        super.put(ENABLED, value);
        return this;
    }

    public boolean autocreate() {
        return super.getBoolean(AUTOCREATE, true);
    }

    public DatabaseConfiguration autocreate(final boolean value) {
        super.put(AUTOCREATE, value);
        return this;
    }

    public DatabaseConfigurationHost[] hosts() {
        final List<DatabaseConfigurationHost> response = new LinkedList<>();
        final JSONArray items = super.getJSONArray(HOSTS);
        CollectionUtils.forEach(items, (item) -> {
            response.add(new DatabaseConfigurationHost(item));
        });
        return response.toArray(new DatabaseConfigurationHost[response.size()]);
    }

    public DatabaseConfigurationCredential[] credentials() {
        final List<DatabaseConfigurationCredential> response = new LinkedList<>();
        final JSONArray items = super.getJSONArray(CREDENTIALS);
        CollectionUtils.forEach(items, (item) -> {
            response.add(new DatabaseConfigurationCredential(item));
        });
        return response.toArray(new DatabaseConfigurationCredential[response.size()]);
    }

    public DatabaseConfiguration add(final DatabaseConfigurationHost host, final DatabaseConfigurationCredential credential) {
        if (!super.has(HOSTS)) {
            super.put(HOSTS, new JSONArray());
        }
        if (!super.has(CREDENTIALS)) {
            super.put(CREDENTIALS, new JSONArray());
        }
        super.getJSONArray(HOSTS).put(host.json());
        super.getJSONArray(CREDENTIALS).put(credential.json());

        return this;
    }

}
