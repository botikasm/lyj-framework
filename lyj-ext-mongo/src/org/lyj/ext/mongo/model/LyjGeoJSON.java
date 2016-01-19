package org.lyj.ext.mongo.model;

import org.bson.Document;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.ext.mongo.ILyjMongoConstants;
import org.lyj.ext.mongo.utils.LyjMongoObjects;

import java.util.LinkedList;
import java.util.List;

/**
 * The coordinate order is longitude, then latitude.
 * <p>
 * Type: "Point", "MultiPoint", "LineString", "MultiLineString", "Polygon", "MultiPolygon", "GeometryCollection"
 * <p>
 * 2.1.2. Point
 * For type "Point", the "coordinates" member must be a single position.
 * <p>
 * 2.1.3. MultiPoint
 * For type "MultiPoint", the "coordinates" member must be an array of positions.
 * <p>
 * 2.1.4. LineString
 * For type "LineString", the "coordinates" member must be an array of two or more positions.
 * <p>
 * A LinearRing is closed LineString with 4 or more positions.
 * The first and last positions are equivalent (they represent equivalent points).
 * Though a LinearRing is not explicitly represented as a GeoJSON geometry type, it is referred
 * to in the Polygon geometry type definition.
 * <p>
 * 2.1.5. MultiLineString
 * For type "MultiLineString", the "coordinates" member must be an array of LineString coordinate arrays.
 * <p>
 * 2.1.6. Polygon
 * For type "Polygon", the "coordinates" member must be an array of LinearRing coordinate arrays.
 * For Polygons with multiple rings, the first must be the exterior ring and any others must be interior rings or holes.
 * <p>
 * 2.1.7. MultiPolygon
 * For type "MultiPolygon", the "coordinates" member must be an array of Polygon coordinate arrays.
 * <p>
 * 2.1.8 Geometry Collection
 * A GeoJSON object with type "GeometryCollection" is a geometry object which represents a collection
 * of geometry objects.
 * A geometry collection must have a member with the name "geometries".
 * The value corresponding to "geometries" is an array. Each element in this array is a GeoJSON geometry object.
 * <p>
 * <p>
 * Sample:
 * <p>
 * { type: "Point", coordinates: [ -73.97, 40.77 ] }
 * { type: "LineString", coordinates: [ [ 40, 5 ], [ 41, 6 ] ] }
 */
public class LyjGeoJSON
        extends Document
        implements ILyjMongoConstants {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------



    private static final String TYPE = "type";
    private static final String COORDINATES = "coordinates";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LyjGeoJSON() {
        this.setType(POINT);
        this.setCoordinates(CollectionUtils.asList(0.0, 0.0));
    }

    public LyjGeoJSON(final Object document) {
        if(document instanceof String){
            LyjMongoObjects.extend(this, Document.parse((String)document));
        } else if (document instanceof Document){
            LyjMongoObjects.extend(this, (Document) document);
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public String getType(){
        return this.getString(TYPE);
    }

    public LyjGeoJSON setType(final String value){
        this.put(TYPE, value);
        return this;
    }

    public List getCoordinates(){
        return (List)this.get(COORDINATES);
    }

    public LyjGeoJSON setCoordinates(final List value){
        this.put(COORDINATES, value);
        return this;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static LyjGeoJSON buildPoint(final double longitude,
                                        final double latidude){
        List<Double> coordinates = new LinkedList<Double>();
        coordinates.add( new Double(longitude) );
        coordinates.add( new Double(latidude) );

        LyjGeoJSON result = new LyjGeoJSON();
        result.setCoordinates(coordinates);

        return result;
    }

}
