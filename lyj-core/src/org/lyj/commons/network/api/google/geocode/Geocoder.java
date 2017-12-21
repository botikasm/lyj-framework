package org.lyj.commons.network.api.google.geocode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.network.URLUtils;
import org.lyj.commons.util.*;
import org.lyj.commons.util.converters.MapConverter;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.json.JsonWrapper;

import java.util.Map;

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

    public static synchronized Geocoder instance() {
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

        public Map<String, Object> itemAsMap() {
            return MapConverter.toMap(_item);
        }

        public JsonItem result() {
            return _main_result;
        }

        public Map<String, Object> resultAsMap() {
            return MapConverter.toMap(_main_result);
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

        public Map<String, Object> locationAsMap() {
            return MapConverter.toMap(this.location());
        }

        public JsonItem simplify() {
            final JsonItem response = new JsonItem();
            try {
                // address
                response.put("address", this.formattedAddress());
                // location
                final JsonItem location = this.location();
                response.put("lng", location.getString("lng"));
                response.put("lat", location.getString("lat"));
                // address_components array
                final JSONArray addres_components = _main_result.getJSONArray("address_components");
                CollectionUtils.forEach(addres_components, (item) -> {
                    if (item instanceof JSONObject) {
                        final JsonItem jitem = new JsonItem(item);
                        final String[] types = JsonWrapper.toArrayOfString(jitem.getJSONArray("types"));
                        final String short_name = jitem.getString("short_name");
                        if (CollectionUtils.contains(types, "administrative_area_level_3")) {
                            // city
                            response.put("city", short_name);
                        } else if (CollectionUtils.contains(types, "administrative_area_level_2")) {
                            // province
                            response.put("province", short_name);
                        } else if (CollectionUtils.contains(types, "administrative_area_level_1")) {
                            // region
                            response.put("region", short_name);
                        } else if (CollectionUtils.contains(types, "country")) {
                            // country
                            response.put("country", short_name);
                        } else if (CollectionUtils.contains(types, "postal_code")) {
                            // postal_code
                            response.put("postal_code", short_name);
                        }
                    }
                });
            } catch (Throwable ignored) {
                // unhandled error
            }
            return response;
        }

        public Map<String, Object> simplifyAsMap() {
            return MapConverter.toMap(this.simplify());
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void init() {
            _main_result = new JsonItem(_item.getJSONObject("results.[0]"));

        }

    }

}
