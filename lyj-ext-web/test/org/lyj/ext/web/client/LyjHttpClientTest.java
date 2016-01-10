package org.lyj.ext.web.client;

import org.junit.Before;
import org.junit.Test;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.util.JsonWrapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 09/01/16.
 */
public class LyjHttpClientTest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------




    // ------------------------------------------------------------------------
    //                      b e f o r e
    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {

    }

    // ------------------------------------------------------------------------
    //                      t e s t
    // ------------------------------------------------------------------------

    //@Test
    public void testPost() throws Exception {
        final String host = "localhost";
        final int port = 4000;
        final String url = "/api/users/find_by_id";
        final String app_token = "funny_gain_68j21";
        final String id = "0af72d73e5742f053e29287e0cc9e26b";

        JsonWrapper json = new JsonWrapper("{}");
        json.put("app_token", app_token);
        json.put("id", id);

        List<Task<String>> tasks = new ArrayList<>();

        for(int i=0;i<1;i++){
            tasks.add(new Task<String>( t->{
                LyjHttpClient.create((err, client)->{
                    if(null!=err) {
                        t.fail(err);
                    } else {
                        client.setChunkBody(true); // force chunks
                        client.setChunkSize(1);
                        client.setConnectTimeout(1000);
                        client.setDefaultHost(host).setDefaultPort(port);

                        client.post(url, json.getJSONObject(), (err2, response)->{
                            if(null!=err2) {
                                t.fail(err2);
                            } else {
                                t.success(response);
                                System.out.println(response);
                            }
                        } );
                    }
                });
            }));
        }

        Async.joinAll(tasks);
    }

    @Test
    public void testGet() throws Exception {
        final String host = "http://www.funnygain.com";
        final int port = 80;
        final String url = "/";

        List<Task<String>> tasks = new ArrayList<>();

        for(int i=0;i<3;i++){
            tasks.add(new Task<String>( t->{
                LyjHttpClient.create((err, client)->{
                    if(null!=err) {
                        t.fail(err);
                    } else {
                        client.setChunkBody(true); // force chunks
                        client.setChunkSize(1);
                        client.setConnectTimeout(1000);
                        client.setDefaultHost(host).setDefaultPort(port);

                        client.get(url, (err2, response)->{
                            if(null!=err2) {
                                t.fail(err2);
                                System.out.println(err2.toString());
                            } else {
                                System.out.println(response.length());
                                t.success(response);
                            }
                        } );
                    }
                });
            }));
        }

        Async.joinAll(tasks);



    }


}