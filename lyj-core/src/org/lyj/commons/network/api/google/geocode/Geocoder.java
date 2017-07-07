package org.lyj.commons.network.api.google.geocode;

import org.lyj.commons.network.URLUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.JsonItem;
import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.StringUtils;

/**
 * Helper to use Google API for geocoding.
 * <p>
 * GET API KEY:
 * https://developers.google.com/maps/documentation/geocoding/get-api-key
 * GET DOC:
 * https://developers.google.com/maps/documentation/geocoding/start
 */
public class Geocoder {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PARAM_API_KEY = "API_KEY";
    private static final String PARAM_ADDRESS = "ADDRESS";
    private static final String PARAM_LAT_LNG = "LAT_LNG";

    //https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key={{API_KEY}}
    private static final String URL_GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json?address={{ADDRESS}}&key={{API_KEY}}";
    // https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=YOUR_API_KEY
    private static final String URL_GEOCODE_REV = "https://maps.googleapis.com/maps/api/geocode/json?latlng={{LAT_LNG}}&key={{API_KEY}}";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Geocoder() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Response geocode(final String api_key, final String address) {
        try {
            final String api_url = FormatUtils.formatTemplate(
                    URL_GEOCODE,
                    "{{", "}}",
                    MapBuilder.createSS()
                            .put(PARAM_API_KEY, api_key)
                            .put(PARAM_ADDRESS, this.normalizeAddress(address))
                            .toMap()
            );
            final String response = URLUtils.getUrlContent(api_url, 2000, URLUtils.TYPE_JSON);
            if (StringUtils.isJSONObject(response)) {
                return new Response(response);
            } else {
                throw new Exception("Empty Message");
            }
        } catch (Throwable t) {
            final JsonItem response = new JsonItem();
            response.put("status", "ERROR");
            response.put("error_message", t.toString());
            return new Response(response.toString());
        }
    }

    public Response reverse(final String api_key, final double lat, final double lon) {
        try {
            final String lat_lon = lat + "," + lon;
            final String api_url = FormatUtils.formatTemplate(
                    URL_GEOCODE_REV,
                    "{{", "}}",
                    MapBuilder.createSS()
                            .put(PARAM_API_KEY, api_key)
                            .put(PARAM_LAT_LNG, lat_lon)
                            .toMap()
            );
            final String response = URLUtils.getUrlContent(api_url, 2000, URLUtils.TYPE_JSON);
            if (StringUtils.isJSONObject(response)) {
                return new Response(response);
            } else {
                throw new Exception("Empty Message");
            }
        } catch (Throwable t) {
            final JsonItem response = new JsonItem();
            response.put("status", "ERROR");
            response.put("error_message", t.toString());
            return new Response(response.toString());
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String normalizeAddress(final String address) {
        return address.replaceAll(" ", "+");
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static Geocoder __instance;

    public static Geocoder instance() {
        if (null == __instance) {
            __instance = new Geocoder();
        }
        return __instance;
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Response {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private final JsonItem _item;
        private JsonItem _main_result;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        private Response(final String json) {
            _item = new JsonItem(json);
            this.init();
        }

        @Override
        public String toString() {
            return _item.toString();
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public JsonItem item() {
            return _item;
        }

        public JsonItem result() {
            return _main_result;
        }

        public boolean isError() {
            return !_item.getString("status").equalsIgnoreCase("OK");
        }

        public String error() {
            return _item.getString("error");
        }

        public String formattedAddress() {
            if (null != _main_result) {
                return _main_result.getString("formatted_address");
            }
            return "";
        }

        public JsonItem location() {
            if (null != _main_result) {
                return new JsonItem(_main_result.getJSONObject("geometry.location"));
            }
            return new JsonItem();
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void init() {
            _main_result = new JsonItem(_item.getJSONObject("results.[0]"));

        }

    }

}
