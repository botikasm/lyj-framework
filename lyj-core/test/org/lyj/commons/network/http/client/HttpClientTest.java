package org.lyj.commons.network.http.client;

import org.junit.Test;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.JsonWrapper;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelogeminiani on 12/01/16.
 */
public class HttpClientTest {

    @Test
    public void testPost() throws Exception {

        final String url = "http://localhost:4000/api/users/find_by_id";
        final String app_token = "funny_gain_68j21";
        final String id = "0af72d73e5742f053e29287e0cc9e26b";

        JsonWrapper json = new JsonWrapper("{}");
        json.put("app_token", app_token);
        json.put("id", id);

        Runtime.getRuntime().gc();

        //final String  large_string = ClassLoaderUtils.getResourceAsString(this.getClass().getClassLoader(), this.getClass(), "test.json");
        //if(StringUtils.hasText(large_string)){
        //    json.put("large", large_string);
        //}

        System.out.println("INITIAL MEMORY");
        System.out.println(SystemUtils.printSystemStatus());

        // 5 threads
        for (int x = 0; x < 5; x++) {
            List<Task<String>> tasks = new ArrayList<>();

            // 3 concurrent requests
            for (int i = 0; i < 3; i++) {
                tasks.add(new Task<String>(t -> {

                    HttpClient client = new HttpClient();
                    client.post(url, json.getJSONObject(), (err, result)->{
                        if(null!=err){
                            t.fail(err);
                            System.out.println(err);
                        } else {
                            t.success(result);
                            System.out.println(result);
                        }
                    });

                }));
            }

            Async.joinAll(tasks);

            System.out.println("BEFORE GC");
            System.out.println(SystemUtils.printSystemStatus());

            Runtime.getRuntime().gc();

            System.out.println("AFTER GC");
            System.out.println(SystemUtils.printSystemStatus());
        }



    }
}