package org.lyj.commons.util;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 27/02/17.
 */
public class URLWrapperTest {

    private static final String URL = "http://www.google.com/path1/path2/path3?param=123&param2=eddcr#fragment=qywy&yyyh";
    private static final String URL_NO_HASH = "http://www.google.com/path1/path2/path3?param=123&param2=eddcr";

    @Test
    public void test() throws Exception {
        URL url = new URL(URL);

        System.out.println("protocol: " + url.getProtocol());
        System.out.println("port: " + url.getPort());
        System.out.println("domain: " + url.getAuthority());
        System.out.println("path: " + url.getPath());
        System.out.println("query: " + url.getQuery());
        System.out.println("hash: " + url.toURI().getFragment());

        URLWrapper wrapper = new URLWrapper(url.toString());
        System.out.println(wrapper.toString());
        assertTrue(wrapper.toString().equalsIgnoreCase(URL));
        assertTrue(wrapper.hash("").toString().equalsIgnoreCase(URL_NO_HASH));
    }

}