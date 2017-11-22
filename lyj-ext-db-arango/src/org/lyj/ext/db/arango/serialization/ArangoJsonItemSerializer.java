package org.lyj.ext.db.arango.serialization;

import com.arangodb.velocypack.*;
import com.arangodb.velocypack.exception.VPackException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.commons.util.StringUtils;

import java.util.Set;

/**
 * Serialize/Deserialize JsonItems
 */
public class ArangoJsonItemSerializer
        implements VPackModule {

    @Override
    public <C extends VPackSetupContext<C>> void setup(final C context) {
        context.registerSerializer(JsonItem.class, new VPackSerializer<JsonItem>() {
            @Override
            public void serialize(VPackBuilder builder,
                                  String attribute,
                                  JsonItem o,
                                  VPackSerializationContext context)
                    throws VPackException {
                if (null != o) {
                    builder.add(attribute, ValueType.OBJECT);

                    final Set<String> keys = o.keys();
                    for (final String key : keys) {
                        final Object value = o.get(key);
                        if (value instanceof String) {
                            builder.add(key, (String) value);
                        } else if (value instanceof JSONObject) {
                            builder.add(key, value.toString());
                        } else if (value instanceof JSONArray) {
                            builder.add(key, value.toString());
                        }
                    }

                    builder.close();
                }
            }
        });
        context.registerDeserializer(JsonItem.class, new VPackDeserializer<JsonItem>() {
            @Override
            public JsonItem deserialize(VPackSlice parent,
                                        VPackSlice vpack,
                                        VPackDeserializationContext context) throws VPackException {
                final JsonItem response = new JsonItem();
                vpack.objectIterator().forEachRemaining((slice) -> {
                    final String key = slice.getKey();
                    final VPackSlice value = slice.getValue();
                    if (value.isString()) {
                        final String s_value = value.getAsString();
                        if (StringUtils.isJSONObject(s_value)) {
                            response.put(key, new JSONObject(s_value));
                        } else if (StringUtils.isJSONArray(s_value)) {
                            response.put(key, new JSONArray(s_value));
                        } else {
                            response.put(key, value.getAsString());
                        }
                    }
                });
                return response;
            }
        });
    }

}
