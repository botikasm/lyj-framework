package org.lyj.commons.network;

import org.junit.Test;

public class NetworkUtilsTest {

    @Test
    public void getFreePort() {
        int port = NetworkUtils.getFreePort(80);
        System.out.println(port);

        port = NetworkUtils.getFreePort(port+1000);
        System.out.println(port);
    }

}