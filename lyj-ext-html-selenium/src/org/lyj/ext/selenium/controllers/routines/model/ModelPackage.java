package org.lyj.ext.selenium.controllers.routines.model;

import org.lyj.commons.util.json.JsonItem;

public class ModelPackage
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_ENABLED = "enabled";
    private static final String FLD_NAME = "name";
    private static final String FLD_BROWSER = "browser";
    private static final String FLD_VERSION = "version";
    private static final String FLD_URL = "url";
    private static final String FLD_PROXY = "proxy";
    private static final String FLD_LOG_LEVEL = "log_level";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModelPackage() {
        super();
        this.init();
    }

    public ModelPackage(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enabled() {
        return super.getBoolean(FLD_ENABLED);
    }

    public void enabled(final boolean value) {
        super.put(FLD_ENABLED, value);
    }

    public String browser() {
        return super.getString(FLD_BROWSER);
    }

    public void browser(final String value) {
        super.put(FLD_BROWSER, value);
    }

    public String name() {
        return super.getString(FLD_NAME);
    }

    public void name(final String value) {
        super.put(FLD_NAME, value);
    }

    public String version() {
        return super.getString(FLD_VERSION);
    }

    public void version(final String value) {
        super.put(FLD_VERSION, value);
    }

    public String url() {
        return super.getString(FLD_URL);
    }

    public void url(final String value) {
        super.put(FLD_URL, value);
    }

    public String logLevel() {
        return super.getString(FLD_LOG_LEVEL);
    }

    public void logLevel(final String value) {
        super.put(FLD_LOG_LEVEL, value);
    }

    public Proxy proxy() {
        return new Proxy(super.getJSONObject(FLD_PROXY));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        super.initializeFromResource();
    }

    // ------------------------------------------------------------------------
    //                      N E S T E D
    // ------------------------------------------------------------------------

    public static class Proxy
            extends JsonItem {

        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_ENABLED = "enabled";
        private static final String FLD_PROTOCOL = "protocol";
        private static final String FLD_IP = "ip";
        private static final String FLD_PORT = "port";

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        private Proxy(final Object item) {
            super(item);
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public boolean enabled() {
            return super.getBoolean(FLD_ENABLED);
        }

        public void enabled(final boolean value) {
            super.put(FLD_ENABLED, value);
        }

        public String protocol() {
            return super.getString(FLD_PROTOCOL);
        }

        public void protocol(final String value) {
            super.put(FLD_PROTOCOL, value);
        }

        public String ip() {
            return super.getString(FLD_IP);
        }

        public void ip(final String value) {
            super.put(FLD_IP, value);
        }

        public int port() {
            return super.getInt(FLD_PORT);
        }

        public void port(final int value) {
            super.put(FLD_PORT, value);
        }

    }

}
