package org.lyj.commons.i18n.countrycode;

import org.lyj.commons.csv.CSVReader;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility for CountryCodes
 */
public class CountryCodeUtils extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String FLD_NAME = "name";
    public static final String FLD_COUNTRY_CODE = "country_code";
    public static final String FLD_COUNTRY_CODE2 = "country_code2";
    public static final String FLD_COUNTRY_ID = "country_id";
    public static final String FLD_DIAL = "dial";
    public static final String FLD_CURRENCY_CODE = "currency_code";
    public static final String FLD_CURRENCY_COUNTRY = "currency_country";
    public static final String FLD_CURRENCY_NAME = "currency_name";
    public static final String FLD_CURRENCY_ID = "currency_id";

    private static final String ROOT = PathUtils.getParent(PathUtils.getClassPath(CountryCodeUtils.class));
    private static final String RESOURCE = "country_codes.csv";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _path;
    private final List<Map<String, String>> _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private CountryCodeUtils() {
        _path = PathUtils.combine(ROOT, RESOURCE).substring(1);
        _data = new ArrayList<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public CountryCodeUtils load() {
        this.init();
        return this;
    }

    public CountryCodeUtils clear() {
        _data.clear();
        return this;
    }

    public List<Map<String, String>> data() {
        if (_data.isEmpty()) {
            this.init();
        }
        return _data;
    }

    public Map<String, String> find(final String fieldName,
                                    final String fieldValue) {
        for (final Map<String, String> item : _data) {
            if (item.get(fieldName).equalsIgnoreCase(fieldValue)) {
                return item;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        try {
            _data.clear();
            final String data = ClassLoaderUtils.getResourceAsString(this.getClass().getClassLoader(), _path);
            if (StringUtils.hasText(data)) {
                // get list of maps
                final CSVReader reader = new CSVReader(new StringReader(data), ',');
                _data.addAll(reader.readAllAsMap(true));
            }
        } catch (Throwable t) {
            super.error("init", t);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static CountryCodeUtils __instance;

    public static CountryCodeUtils instance() {
        if (null == __instance) {
            __instance = new CountryCodeUtils();
        }
        return __instance;
    }


}
