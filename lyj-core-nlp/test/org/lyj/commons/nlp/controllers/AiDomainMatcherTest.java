package org.lyj.commons.nlp.controllers;

import org.lyj.commons.nlp.controllers.domain.AiDomainMatcher;
import org.lyj.commons.nlp.controllers.domain.DomainMatch;
import org.json.JSONArray;
import org.junit.Test;
import org.lyj.commons.timewatching.TimeWatcher;
import org.lyj.commons.util.ClassLoaderUtils;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 19/07/17.
 */
public class AiDomainMatcherTest {

    @Test
    public void match() throws Exception {
        final JSONArray array = this.domain();
        final TimeWatcher watcher = new TimeWatcher();
        watcher.start();
        final DomainMatch response = AiDomainMatcher.instance().match("voglio scarpe rosse", array);
        watcher.stop();
        assertTrue(response.hasMatchValue());
        
        System.out.println("URI: " + response.uri());
        System.out.println("RATING: " + response.rating());
        System.out.println("ELAPSED ms: " + watcher.elapsed());
    }

    @Test
    public void matchLoop() throws Exception {
        final TimeWatcher watcher = new TimeWatcher();
        final JSONArray array = this.domain();
        watcher.start();
        for (int i = 0; i < 10000; i++) {
            final DomainMatch response = AiDomainMatcher.instance().match("voglio scarpe rosse", array);
        }
        watcher.stop();
        System.out.println("ELAPSED ms: " + watcher.elapsed());
    }

    private JSONArray domain() {
        final String json = ClassLoaderUtils.getResourceAsString(null, this.getClass(),
                "domain.json");
        return new JSONArray(json);
    }

}