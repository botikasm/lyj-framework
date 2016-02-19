package org.lyj.commons.network.api.google.maps;

import org.lyj.commons.image.ImageIOUtils;
import org.lyj.commons.lang.Base64;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.network.URLUtils;
import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper to use with google maps.
 * Supported platforms:<br>
 * <ol>
 * <li>Static API: https://developers.google.com/maps/documentation/static-maps/intro</li>
 * </ol>
 */
public class MapStaticUrl {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String FORMAT_PNG = "png"; // 8bit png
    public static final String FORMAT_PNG32 = "png32";
    public static final String FORMAT_GIF = "gif";
    public static final String FORMAT_JPG = "jpg";
    public static final String FORMAT_JPG_BASELINE = "jpg-baseline";

    public static final String TYPE_ROADMAP = "roadmap"; // default
    public static final String TYPE_SATELLITE = "satellite";
    public static final String TYPE_TERRAIN = "terrain";
    public static final String TYPE_HYBRID = "hybrid";

    private static final String STATIC_MAP_URL_TEMPLATE = "https://maps.googleapis.com/maps/api/staticmap?size={width}x{height}";
    private static final String PARAM_KEY = "&key={api_key}";
    private static final String PARAM_ZOOM = "&zoom={zoom}";
    private static final String PARAM_CENTER = "&center={center_lat},{center_lon}";
    private static final String PARAM_SCALE = "&scale={scale}";
    private static final String PARAM_FORMAT = "&format={format}";
    private static final String PARAM_MAPTYPE = "&maptype={maptype}";
    private static final String PARAM_MARKERS = "&markers={marker}";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _api_key;

    private int _width;
    private int _height;
    private int _scale;
    private int _zoom;
    private double _center_lat;
    private double _center_lon;
    private String _format;
    private String _maptype;

    private final List<MapMarker> _markers;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MapStaticUrl(final String apiKey) {
        _api_key = apiKey;
        _scale = 1;
        _zoom = 0;
        _width = 320;
        _height = 320;
        _format = FORMAT_PNG;

        _markers = new LinkedList<>();
    }

    @Override
    public String toString() {
        return buildUrl();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public MapStaticUrl addMarker(final MapMarker marker) {
        _markers.add(marker);
        return this;
    }

    public MapMarker[] markers(){
        return _markers.toArray(new MapMarker[_markers.size()]);
    }

    public MapStaticUrl width(final int value) {
        _width = value;
        return this;
    }

    public int width() {
        return _width;
    }

    public MapStaticUrl height(final int value) {
        _height = value;
        return this;
    }

    public int height() {
        return _height;
    }

    public MapStaticUrl scale(final int value) {
        _scale = value;
        return this;
    }

    public int scale() {
        return _scale;
    }

    public MapStaticUrl zoom(final int value) {
        _zoom = value;
        return this;
    }

    public int zoom() {
        return _zoom;
    }

    public MapStaticUrl center(final double lat, final double lon) {
        _center_lat = lat;
        _center_lon = lon;
        return this;
    }

    public double[] center() {
        return new double[]{_center_lat, _center_lon};
    }

    public MapStaticUrl format(final String value) {
        _format = value;
        return this;
    }

    public String format() {
        return _format;
    }

    public MapStaticUrl mapType(final String value) {
        _maptype = value;
        return this;
    }

    public String mapType() {
        return _maptype;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public BufferedImage toImage(){
        InputStream is = null;
        try{
            is = URLUtils.getInputStream(this.toString());
            if(null!=is){
                return ImageIOUtils.readBytes(ByteUtils.getBytes(is));
            }
        } catch(Throwable ignored) {

        } finally {
            if(null!=is) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    public String toBase64(){
        InputStream is = null;
        try{

            is = URLUtils.getInputStream(this.toString());
            if(null!=is){
                return Base64.encodeBytes(ByteUtils.getBytes(is));
            }
        } catch(Throwable ignored) {
            System.out.println(ignored);
        } finally {
            if(null!=is) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String format(final String template, final Object... args) {
        return this.format(true, template, args);
    }

    private String format(final boolean encode,
                          final String template,
                          final Object... args) {
        final Counter count = new Counter(-1);
        return FormatUtils.formatTemplate(template, "{", "}", (key) -> {
            try {
                count.inc();
                final Object value = args[count.value()];
                if (null != value) {
                    return encode
                            ? StringUtils.urlEncode(value.toString(), CharEncoding.getDefault(), true)
                            : value.toString();
                } else {
                    return "";
                }
            } catch (Throwable t) {
                return "";
            }
        });
    }

    private String buildUrl() {
        final StringBuilder sb = new StringBuilder();
        sb.append(format(STATIC_MAP_URL_TEMPLATE, _width, _height));
        sb.append(format(PARAM_SCALE, _scale));
        // api key
        if (StringUtils.hasText(_api_key)) {
            sb.append(format(PARAM_KEY, _api_key));
        }
        // zoom
        if (_zoom > 0) {
            sb.append(format(PARAM_ZOOM, _zoom));
        }
        // center
        if (_center_lat != 0 && _center_lon != 0) {
            sb.append(format(PARAM_CENTER, _center_lat, _center_lon));
        }
        // format
        if (StringUtils.hasText(_format)) {
            sb.append(format(PARAM_FORMAT, _format));
        }
        // maptype
        if (StringUtils.hasText(_maptype)) {
            sb.append(format(PARAM_MAPTYPE, _maptype));
        }

        // markers
        if(_markers.size()>0){
            for(final MapMarker marker:_markers) {
                sb.append(format(false, PARAM_MARKERS, marker.toString()));
            }
        }

        return sb.toString();
    }

}
