package org.ly.ose.server.application.programming;

import org.junit.Test;

import static org.junit.Assert.*;

public class OSEProgramInfoTest {

    @Test
    public void toStringTest() {

        OSEProgramInfo info = new OSEProgramInfo();
        info.namespace("org.botika");
        String text = info.toString();
        System.out.println(text);
        
        assertEquals("{\"singleton\":false,\"microservice\":false,\"namespace\":\"org.botika\",\"logging\":\"SEVERE\",\"files\":0,\"autostart\":false,\"loop_interval\":1000,\"session_timeout\":30000}", text);


    }
}