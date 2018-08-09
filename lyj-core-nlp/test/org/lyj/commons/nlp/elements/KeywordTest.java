package org.lyj.commons.nlp.elements;

import org.junit.Test;

import static org.junit.Assert.*;

public class KeywordTest {

    @Test
    public void clearKeyword() {

        String response = Keyword.clearKeyword("olèéòç!!   !!,.-_ #");
        System.out.println(response);
        assertEquals("olèéòç", response);

        response = Keyword.clearKeyword("Hello, I'm a text with some Invalid Characters!! \uD83D\uDC4D");
        System.out.println(response);
        assertEquals("helloimatextwithsomeinvalidcharacters", response);


    }
}