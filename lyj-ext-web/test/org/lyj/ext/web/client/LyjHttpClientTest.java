package org.lyj.ext.web.client;

import org.junit.Before;
import org.junit.Test;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.util.JsonWrapper;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 09/01/16.
 */
public class LyjHttpClientTest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String HOST = "localhost";
    public static final int PORT = 4000;


    // ------------------------------------------------------------------------
    //                      b e f o r e
    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {

    }

    // ------------------------------------------------------------------------
    //                      t e s t
    // ------------------------------------------------------------------------

    @Test
    public void testPost() throws Exception {
        final String url = "/api/users/find_by_id";
        final String app_token = "funny_gain_68j21";
        final String id = "0af72d73e5742f053e29287e0cc9e26b";

        JsonWrapper json = new JsonWrapper("{}");
        json.put("app_token", app_token);
        json.put("id", id);

        final LyjHttpClient client = LyjHttpClient.create().run().get();
        client.setChunkBody(true); // force chunks
        client.setChunkSize(1);
        client.setDefaultHost(HOST)
                .setDefaultPort(PORT);
        final String response = client.post(url, json.getJSONObject()).run().get();
        System.out.println(response);


    }


}