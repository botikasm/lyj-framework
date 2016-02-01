package org.lyj.commons.network.api.google.maps;

import org.lyj.commons.util.StringUtils;

/**
 * marker
 */
public class MapMarker {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String SIZE_TINY = "tiny";
    public static final String SIZE_MID = "mid";
    public static final String SIZE_SMALL = "small";

    private static final String SEP = "|";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _size;
    private String _color;
    private String _label;
    private double _lat;
    private double _lon;
    private String _address;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MapMarker() {
        _size = SIZE_MID;
    }

    @Override
    public String toString() {
        return buildString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * size: (optional) specifies the size of marker from the set {tiny, mid, small}.
     * If no size parameter is set, the marker will appear in its default (normal) size.
     * @param value size of marker from the set {tiny, mid, small}
     * @return this instance
     */
    public MapMarker size(final String value) {
        _size = value;
        return this;
    }

    public String size() {
        return _size;
    }

    /**
     * color: (optional) specifies a 24-bit color (example: color=0xFFFFCC)
     * or a predefined color from the set
     * {black, brown, green, purple, yellow, blue, gray, orange, red, white}.
     *
     * @param value a 24-bit color (example: color=0xFFFFCC)
     *              or a predefined color from the set
     *              {black, brown, green, purple, yellow, blue, gray, orange, red, white}.
     * @return this instance.
     */
    public MapMarker color(final String value) {
        _color = value;
        return this;
    }

    public String color() {
        return _color;
    }

    /**
     * (optional) specifies a single uppercase alphanumeric character
     * from the set {A-Z, 0-9}. (The requirement for uppercase characters is new to this version of the API.)
     * Note that default and mid sized markers are the only markers capable of displaying
     * an alphanumeric-character parameter. tiny and small markers are not capable of
     * displaying an alphanumeric-character.
     * @param value Single uppercase character.
     * @return this instance
     */
    public MapMarker label(final String value) {
        _label = value;
        return this;
    }

    public String label() {
        return _label;
    }

    public MapMarker location(final double lat, final double lon) {
        _lat = lat;
        _lon = lon;
        return this;
    }

    public MapMarker location(final String address) {
        _address = address;
        return this;
    }

    public double[] locationCoords() {
        return new double[]{_lat, _lon};
    }

    public String locationAddress() {
        return _address;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void appendField(final StringBuilder sb,
                             final String prefix,
                             final Object value){
        if(null!=value){
            if(sb.length()>0) {
                sb.append(SEP);
            }
            sb.append(prefix).append(value);
        }
    }

    private String buildString(){
        final StringBuilder sb = new StringBuilder();
        this.appendField(sb, "color:", _color);
        this.appendField(sb, "size:", _size);
        this.appendField(sb, "label:", _label);
        if(sb.length()>0) {
            sb.append(SEP);
        }
        if(StringUtils.hasText(_address)) {
            // address
            sb.append(StringUtils.urlEncodeSpaces(_address));
        } else {
            // latitude,longitude
            sb.append(_lat).append(",").append(_lon);
        }

        return sb.toString();
    }

}
