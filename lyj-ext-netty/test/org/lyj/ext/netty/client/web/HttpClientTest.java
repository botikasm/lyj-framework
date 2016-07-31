package org.lyj.ext.netty.client.web;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.netty.TestInitializer;

import java.io.File;
import java.io.IOException;

/**
 * Created by angelogeminiani on 29/07/16.
 * Simple Test for HttpClient Class.
 */
public class HttpClientTest {


    private static final String HTTPS_URL = "https://www.google.it/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=bot";
    private static final String HTTP_URL = "http://www.smartfeeling.org";
    private static final String HTTPS_IMAGE = "https://www.nasa.gov/sites/default/files/styles/image_card_4x3_ratio/public/thumbnails/image/leisa_christmas_false_color.png?itok=Jxf0IlS4";


    @BeforeClass
    public static void setUpClass() throws Exception {
        TestInitializer.init();
    }

    /**
     * Test GET method
     */
    @Test
    public void getImage() throws Exception {
        this.get(HTTPS_IMAGE, "image_https");
    }

    @Test
    public void getHttp() throws Exception {
        this.get(HTTP_URL, "file_http");
    }

    @Test
    public void getHttps() throws Exception {
        this.get(HTTPS_URL, "file_https");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void get(final String url, final String outname) throws Exception {
        HttpClient client = new HttpClient();

        Task<HttpClientResponse> task = new Task<>((t) -> {
            client.get().url(url).send((response) -> {
                try {
                    t.success(response);
                } catch (Throwable err) {
                    t.fail(err);
                }
            });
        });
        task.run();
        final HttpClientResponse response = task.get();
        Assert.assertNotNull(response);
        System.out.println(response);

        this.handleResponse(response, outname);
    }

    private void handleResponse(final HttpClientResponse response, final String outname) throws IOException {
        byte[] content = response.content();
        Assert.assertFalse(content.length == 0);

        // write file
        String filename = PathUtils.getAbsolutePath("test/" + outname + "." + response.headers().ContentTypeExtension());
        FileUtils.mkdirs(filename);
        FileUtils.copy(content, new File(filename));

        System.out.println("OUTPUT: " + filename);
        System.out.println("OUTPUT CHARSET: " + response.headers().ContentTypeCharset());
    }

}