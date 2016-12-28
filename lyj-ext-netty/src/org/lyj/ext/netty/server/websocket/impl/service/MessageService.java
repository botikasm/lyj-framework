package org.lyj.ext.netty.server.websocket.impl.service;

import org.lyj.ext.netty.server.websocket.Response;

public class MessageService {

    public static Response sendMessage(String id, String message) {
        Response res = new Response();
        res.getData().put("id", id);
        res.getData().put("message", message);
        res.getData().put("ts", System.currentTimeMillis());
        return res;
    }
}
