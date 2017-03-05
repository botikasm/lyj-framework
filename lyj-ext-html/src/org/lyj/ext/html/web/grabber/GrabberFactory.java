package org.lyj.ext.html.web.grabber;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.ext.html.IHtmlConstants;
import org.lyj.ext.html.web.grabber.html.HtmlGrabber;
import org.lyj.ext.html.web.grabber.rss.Rss2Grabber;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage document parser creation
 */
public class GrabberFactory
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String TYPE_HTML = IHtmlConstants.TYPE_HTML;
    private static final String TYPE_RSS_2 = IHtmlConstants.TYPE_RSS_2;


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Class<? extends AbstractGrabber<?>>> _crawlers;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private GrabberFactory() {
        _crawlers = new HashMap<>();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void registerInstance(final String type, final Class<? extends AbstractGrabber<?>> aclass) {
        _crawlers.put(type, aclass);
    }

    public void unregisterInstance(final String type) {
        _crawlers.remove(type);
    }

    public boolean containsInstance(final String type) {
        return _crawlers.containsKey(type);
    }

    public AbstractGrabber<?> createInstance(final String type) {
        try {
            if (_crawlers.containsKey(type)) {
                final Class<? extends AbstractGrabber<?>> aclass = _crawlers.get(type);
                return aclass.getConstructor().newInstance();
            }
        } catch (Throwable t) {
            super.error("create", t);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

        //-- register default crawlers --//
        this.registerInstance(TYPE_HTML, HtmlGrabber.class);
        this.registerInstance(TYPE_RSS_2, Rss2Grabber.class);

    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static GrabberFactory __instance;

    private static GrabberFactory instance() {
        if (null == __instance) {
            __instance = new GrabberFactory();
        }
        return __instance;
    }

    public static AbstractGrabber create(final String type) {
        return instance().createInstance(type);
    }

    public static boolean contains(final String type) {
        return instance().containsInstance(type);
    }

    public static void register(final String type, final Class<? extends AbstractGrabber<?>> aclass) {
        instance().registerInstance(type, aclass);
    }

    public static void unregister(final String type) {
        instance().unregisterInstance(type);
    }

}
