package org.lyj.commons.nlp.entities;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.lyj.commons.nlp.elements.custom.CustomExpression;
import org.lyj.commons.util.ClassLoaderUtils;

public class NEntityMatcherTest {


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Test
    public void matchTest() throws Exception {

        final String phrase = "Ciao, vorrei che tu prendessi " +
                "un po' di dati. Io sono " +
                "Mario Rossi ed il mio telefono è 347 78587 oppure via emal a angelo@gmail.com. Anche se non ti frega niente, mia moglie si chiama Marisa Semprini" +
                " Il mio ordine è VEN-1234.." +
                " MY95762512 AS62716632 RL66791012";
        final NEntitySchema schema = new NEntitySchema(this.schema());

        final NEntityMatcher.Entity[] response = NEntityMatcher.instance().match("it", phrase, schema, this::expressionCallback);
        Assert.assertTrue(response.length > 0);

        for (final NEntityMatcher.Entity entity : response) {
            System.out.println(entity);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JSONObject schema() {
        final String json = ClassLoaderUtils.getResourceAsString(null, this.getClass(),
                "schema.json");
        return new JSONObject(json);
    }

    private Object expressionCallback(final CustomExpression expression,
                                      final String[] tokens,
                                      final JSONObject node) {
        return null;
    }

}