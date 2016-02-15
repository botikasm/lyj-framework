package org.lyj.commons.network.api.google.maps;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;



/**
 *
 */
public class MapStaticUrlTest {


    private static final String API_KEY = "put-here-api-key";


    @Test
    public void testToString() throws Exception {

        final MapStaticUrl url = new MapStaticUrl(API_KEY);

        String result = url.toString();
        System.out.println(result);

        assertNotNull(result);

        // add marker
        final MapMarker marker = new MapMarker();
        marker.location("Viale Veneto, 33\n47838 Riccione\n RN");

        url.addMarker(marker);
        result = url.toString();
        System.out.println(result);

        String base64 = url.toBase64();
        System.out.println("");
        System.out.println(base64);
    }
}