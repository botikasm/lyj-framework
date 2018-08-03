package org.lyj.commons.nlp.elements;

import org.junit.Test;
import org.lyj.commons.nlp.elements.KeywordsSolver;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 28/07/17.
 */
public class KeywordsSolverTest {

    @Test
    public void matchKeywords() throws Exception {

        assertTrue(KeywordsSolver.instance().matchKeywords("voglio delle scarpe rosse", new String[]{"(scarpe) + (ross*)"}));
        assertTrue(KeywordsSolver.instance().matchKeywords("voglio delle scarpe rossi", new String[]{"(scarpe) + (ross*)"}));
        assertFalse(KeywordsSolver.instance().matchKeywords("voglio delle scarpe ross", new String[]{"(scarpe) + (ross*)"}));
        assertFalse(KeywordsSolver.instance().matchKeywords("voglio delle scarpe rosse", new String[]{"pippo"}));

    }

}