package org.lyj.commons.nlp.data.numbers;

import org.junit.Test;
import org.lyj.commons.nlp.entities.data.numbers.AiNumberMatcher;
import org.lyj.commons.util.StringUtils;

public class AiNumberMatcherTest {

    @Test
    public void match() {

        Number n = AiNumberMatcher.instance().matchOne("it", "questo è un numero uno");
        System.out.println(n);

        String text =  "questo è un numero uno con un numero due";
        Number[] nn = AiNumberMatcher.instance().matchAll("it", text);
        int count = AiNumberMatcher.instance().count("it", text);
        System.out.println(text + ": count=" + count + ", number=" + StringUtils.toString(nn));

        Number[] n2 = AiNumberMatcher.instance().matchAll("it", "ne voglio venti");
        System.out.println(StringUtils.toString(n2));

        text =  "ven-11233455323";
        nn = AiNumberMatcher.instance().matchAll("it", text);
        count = AiNumberMatcher.instance().count("it", text);
        System.out.println(text + ": count=" + count + ", number=" + StringUtils.toString(nn));
    }
}