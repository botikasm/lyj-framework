package org.lyj.ext.selenium.controllers.proxy.model;

import org.lyj.commons.util.json.JsonItem;

public class ModelProxy
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_HTTPS = "https";
    public static final String PROTOCOL_SOCKS4 = "socks4";
    public static final String PROTOCOL_SOCKS5 = "socks5";

    private static final String FLD_IP = "ip";
    private static final String FLD_PORT = "port";
    private static final String FLD_PROTOCOL = "protocol";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModelProxy() {
        super();
        this.init();
    }

    public ModelProxy(final Object item) {
        super(item);
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

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

    public String protocol() {
        return super.getString(FLD_PROTOCOL);
    }

    public void protocol(final String value) {
        super.put(FLD_PROTOCOL, value);
    }

    public boolean isHttp(){
        return PROTOCOL_HTTP.equalsIgnoreCase(this.protocol());
    }

    public boolean isHttps(){
        return PROTOCOL_HTTPS.equalsIgnoreCase(this.protocol());
    }

    public boolean isSock4(){
        return PROTOCOL_SOCKS4.equalsIgnoreCase(this.protocol());
    }

    public boolean isSock5(){
        return PROTOCOL_SOCKS5.equalsIgnoreCase(this.protocol());
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
         super.initializeFromResource();
    }

}
