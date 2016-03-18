package org.ly.ext.mail.templates;

import org.junit.Test;
import org.lyj.commons.util.LocaleUtils;
import org.lyj.commons.util.StringUtils;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 14/03/16.
 */
public class TemplateManagerTest {

    @Test
    public void testGetSubject() throws Exception {

        LocaleUtils.setCurrent(Locale.ENGLISH);

        final TemplateManager templates = new TemplateManager();
        final String subject_it = templates.getSubject("it", "sample");
        final String subject_en = templates.getSubject("en", "sample");
        final String subject_jp = templates.getSubject("jp", "sample");
        assertTrue(StringUtils.hasText(subject_it));
        assertTrue(StringUtils.hasText(subject_en));
        assertTrue(StringUtils.hasText(subject_jp));

        System.out.println(subject_en);
        System.out.println(subject_it);
        System.out.println(subject_jp);
    }

    @Test
    public void testGetFile() throws Exception {
        final TemplateManager templates = new TemplateManager();
        final String file_it = templates.getHtml("it", "sample");
        final String file_en = templates.getText("en", "sample");
        assertTrue(StringUtils.hasText(file_it));
        assertTrue(StringUtils.hasText(file_en));

        System.out.println(file_en);
        System.out.println(file_it);
    }

}