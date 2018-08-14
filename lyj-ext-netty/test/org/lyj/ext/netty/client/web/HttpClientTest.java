package org.lyj.ext.netty.client.web;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.ext.netty.TestInitializer;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class HttpClientTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void getTest() throws Exception {

        String url = "http://httpbin.org/get";

        System.out.println("GET: " + url);
        Task<HttpClientResponse> task = new Task<>((t) -> {
            final HttpClient client = this.client(url);
            client.fail(t::fail);
            client.success(t::success);

            client.execute();
        });
        task.run();
        final HttpClientResponse response = task.get();
        assertNotNull(response);
        System.out.println(response);


    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private HttpClient client(final String url) throws URISyntaxException {
        final HttpClient client = new HttpClient();
        client.encoding(CharEncoding.UTF_8);
        client.url(url);

        return client;
    }

}