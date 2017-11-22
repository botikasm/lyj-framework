package org.lyj.commons.network.api.google.geocode;

import org.junit.Test;
import org.lyj.commons.util.json.JsonItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by angelogeminiani on 07/07/17.
 */
public class GeocoderTest {

    // https://developers.google.com/maps/documentation/geocoding/get-api-key
    final String API_KEY = "YOUR_KEY";

    @Test
    public void geocode() throws Exception {


        final String address = "via tre settembre, 99 - dogana -san marino";
        final Geocoder.Response response = Geocoder.instance().geocode(API_KEY, address);
        System.out.println(response);
        final JsonItem result = response.result();
        System.out.println(result);
        final String formatted_address = response.formattedAddress();
        assertNotNull(formatted_address);
        System.out.println(formatted_address);
        final JsonItem location = response.location();
        System.out.println(location);

        // reverse
        final double lat = location.getDouble("lat");
        final double lon = location.getDouble("lng");
        final Geocoder.Response response_rev = Geocoder.instance().reverse(API_KEY, lat, lon);
        final String formatted_address_rev = response_rev.formattedAddress();
        assertNotNull(formatted_address_rev);
        System.out.println(formatted_address_rev);

        assertEquals(formatted_address, formatted_address_rev);
    }

    @Test
    public void reverse() throws Exception {
        final double lat = 37.4224082;
        final double lon = -122.0856086;
        final Geocoder.Response response = Geocoder.instance().reverse(API_KEY, lat, lon);
        System.out.println(response);
        final JsonItem result = response.result();
        System.out.println(result);
        final String formatted_address = response.formattedAddress();
        assertNotNull(formatted_address);
        System.out.println(formatted_address);
        final JsonItem location = response.location();
        System.out.println(location);
    }

    @Test
    public void geocodeMinimal() throws Exception {


        final String address = "Milano";
        final Geocoder.Response response = Geocoder.instance().geocode(API_KEY, address);
        System.out.println(response);
        final JsonItem result = response.result();
        System.out.println(result);
        final String formatted_address = response.formattedAddress();
        assertNotNull(formatted_address);
        System.out.println(formatted_address);
        final JsonItem location = response.location();
        System.out.println(location);

        // reverse
        final double lat = location.getDouble("lat");
        final double lon = location.getDouble("lng");
        final Geocoder.Response response_rev = Geocoder.instance().reverse(API_KEY, lat, lon);
        final String formatted_address_rev = response_rev.formattedAddress();
        assertNotNull(formatted_address_rev);
        System.out.println(formatted_address_rev);

        assertEquals(formatted_address, formatted_address_rev);
    }


}