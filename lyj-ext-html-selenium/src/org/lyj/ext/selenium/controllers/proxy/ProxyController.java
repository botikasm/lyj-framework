package org.lyj.ext.selenium.controllers.proxy;

import org.json.JSONArray;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.network.NetworkUtils;
import org.lyj.commons.util.*;
import org.lyj.ext.selenium.IConstants;
import org.lyj.ext.selenium.controllers.SeleniumManager;
import org.lyj.ext.selenium.controllers.proxy.model.ModelProxy;

import java.io.File;
import java.util.*;

/**
 * https://www.seleniumhq.org/docs/04_webdriver_advanced.jsp
 */
public class ProxyController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH = "./proxies.json";

    private static final String TYPE_JSON = "json";
    private static final String TYPE_TXT = "txt";
    private static final String TYPE_CSV = "csv";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _path; // file path
    private final Map<String, Randomizer> _randomizers;

    private boolean _test_connection;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ProxyController(final SeleniumManager parent) {
        _path = PathUtils.combine(parent.root(), PATH);
        _randomizers = Collections.synchronizedMap(new HashMap<>());

        _test_connection = false;

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public boolean testConnection() {
        return _test_connection;
    }

    public void testConnection(final boolean value) {
        _test_connection = value;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        try {
            if (this.isEmpty()) {
                this.importFromDir(IConstants.PATH_PROXYLIST);
            }
        } catch (Throwable t) {
            super.error("open", t);
        }
    }

    public void close() {
        _randomizers.clear();
    }

    public void importFromDir(final String dir) throws Exception {
        this.importFromDir(new File(dir), false);
    }

    public void importFromDir(final String dir,
                              final boolean append_only) throws Exception {
        this.importFromDir(new File(dir), append_only);
    }

    public void importFromDir(final File dir,
                              final boolean append_only) throws Exception {
        if (!append_only) {
            // reset array
            this.array(new JSONArray());
        }
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, dir);
        for (final File file : files) {
            this.importFromFile(file, true);
        }
    }

    public void importFromFile(final File file,
                               final boolean append_only) throws Exception {
        if (!append_only) {
            // reset array
            this.array(new JSONArray());
        }
        final String content = FileUtils.readFileToString(file);
        if (StringUtils.hasText(content)) {
            final String file_name = PathUtils.getFilename(file.getName(), false);
            final String ext = PathUtils.getFilenameExtension(file.getName(), false).toLowerCase();
            this.importContent(file_name, ext, content, _test_connection);
        }
    }

    public int count() {
        try {
            return this.array().length();
        } catch (Throwable ignored) {
            // ignored
        }
        return 0;
    }

    public boolean isEmpty() {
        try {
            return this.array().length() == 0;
        } catch (Throwable ignored) {
            // ignored
        }
        return true;
    }

    public ModelProxy getOneRnd() {
        return this.getOneRndByProtocol("");
    }

    public ModelProxy getOneRndByProtocol(final String protocol) {
        if (StringUtils.hasText(protocol)) {
            final Map<String, Object> filter = MapBuilder.createSO().put("protocol", protocol).toMap();
            final JSONArray array = this.array(filter);
            return this.rnd(protocol, array);
        } else {
            final JSONArray array = this.array();
            return this.rnd("*", array);
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        try {
            FileUtils.tryMkdirs(_path);

            if (PathUtils.exists(_path)) {
                // proxy list is ready
            } else {
                this.array(new JSONArray());
            }
        } catch (Throwable t) {
            super.error("init", t);
        }
    }

    private JSONArray array() {
        try {
            final String data = FileUtils.readFileToString(new File(_path));
            return new JSONArray(data);
        } catch (Throwable ignored) {
            // ignored
        }
        return new JSONArray();
    }

    private JSONArray array(final Map<String, Object> filter) {
        try {
            final String data = FileUtils.readFileToString(new File(_path));
            return new JSONArray(data);
        } catch (Throwable ignored) {
            // ignored
        }
        return new JSONArray();
    }

    private void array(final JSONArray array) {
        try {
            final String text = array.toString();
            FileUtils.writeStringToFile(new File(_path), text, CharEncoding.UTF_8);
        } catch (Throwable ignored) {
            // ignored
        }
    }

    private ModelProxy rnd(final String protocol,
                           final JSONArray array) {
        final int len = array.length();
        if (len > 0) {
            final int min = 0;
            final int max = len-1;
            int count = 0;
            while(count<len){
                count++;

                final int index = this.rnd(protocol, min, max);
                final ModelProxy proxy = new ModelProxy(array.get(index));
                if(!_test_connection || this.test(proxy)){
                    return proxy;
                }
            }

        }
        return null;
    }

    private int rnd(final String protocol,
                    final int min,
                    final int max) {
        synchronized (_randomizers) {
            final String key = "rnd_" + protocol + "_" + min + "_" + max;
            if (!_randomizers.containsKey(key)) {
                _randomizers.put(key, new Randomizer(2, min, max));
            }
            return _randomizers.get(key).next();
        }
    }

    private void importContent(final String file_name,
                               final String type,
                               final String text,
                               final boolean test_connection) {
        if (type.equalsIgnoreCase(TYPE_JSON)) {
            this.importJSON(text, test_connection);
        } else if (type.equalsIgnoreCase(TYPE_TXT)) {
            this.importTXT(file_name, text, test_connection);
        } else if (type.equalsIgnoreCase(TYPE_CSV)) {
            this.importCSV(text);
        }
    }

    private void importJSON(final String text,
                            final boolean test_connection) {
        final JSONArray source = new JSONArray(text);
        final JSONArray array = this.array();
        CollectionUtils.forEach(source, (item) -> {
            final ModelProxy model = new ModelProxy(source);
            if (!test_connection || test(model)) {
                array.put(model.json());
            }
        });
        this.array(array);
    }

    private void importCSV(final String text) {

    }

    private void importTXT(final String protocol,
                           final String text,
                           final boolean test_connection) {
        final String[] rows = StringUtils.split(text, "\n", true);
        final JSONArray array = this.array();
        for (final String row : rows) {
            final String[] tokens = StringUtils.split(row, ":");
            if (tokens.length == 2) {
                final String ip = tokens[0];
                final int port = ConversionUtils.toInteger(tokens[1]);
                final ModelProxy model = new ModelProxy();
                model.protocol(protocol);
                model.ip(ip);
                if (port > -1) {
                    model.port(port);
                }
                if (!test_connection || test(model)) {
                    array.put(model.json());
                }
            }
        }
        this.array(array);
    }

    private boolean test(final ModelProxy proxy) {
        return NetworkUtils.ping("https://api.myip.com/", 5000,
                proxy.ip(),
                proxy.port(), "", "");
    }

    private void logProxyList() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("-------------------------").append("\n");
        sb.append("\t").append(FormatUtils.format("PROXY READY FOR USE: %s", this.array().length())).append("\n");
        sb.append("-------------------------");
        super.info("PROXY LIST", sb.toString());
    }

}
