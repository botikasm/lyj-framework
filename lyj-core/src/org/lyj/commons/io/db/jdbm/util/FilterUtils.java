package org.lyj.commons.io.db.jdbm.util;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Utility methods for filtering
 */
public class FilterUtils {

    public static boolean match(final Map<String, Object> item,
                                final Map<String, Object> condition) {
        if (null != item && null != condition) {
            final Set<String> cond_keys = condition.keySet();
            if (cond_keys.size() > 0) {
                boolean response = true;
                for (final String key : cond_keys) {
                    // TODO: implement logic operators

                    // AND operator for equal match
                    final Object item_val = item.get(key);
                    final Object cond_val = condition.get(key);
                    if (null == item_val && null == cond_val) {
                        response = true;
                    } else if (null == item_val || null == cond_val) {
                        response =  false;
                    } else {
                        response =  item_val.equals(cond_val);
                    }
                }

                return response;
            }
            return true;
        }
        return false;
    }

}
