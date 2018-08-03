package org.lyj.commons.nlp.elements.custom;

import org.junit.Test;
import org.lyj.commons.nlp.elements.custom.CustomExpression;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 19/07/17.
 */
public class CustomParserTest {

    @Test
    public void collection() throws Exception {

        CustomExpression parser = new CustomExpression("@tb_products.uid");
        assertEquals("tb_products", parser.collection());

        parser = new CustomExpression("@=.tb_products.uid");
        assertEquals("tb_products", parser.collection());

        System.out.println(parser);
    }

}