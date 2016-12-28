package org.lyj.ext.netty.server.websocket;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private int error_code;
    private String error_msg;
    private Map<String,Object> data;

    public Response() {
        data = new HashMap<String,Object>();
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Response(int error_code, String error_msg) {
        this.error_code = error_code;
        this.error_msg = error_msg;
    }
}
