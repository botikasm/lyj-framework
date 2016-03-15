package org.ly.ext.mail.templates;

import org.ly.ext.mail.templates.sample.Sample;
import org.lyj.commons.i18n.DictionaryController;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

/**
 * Manage templates and templates localizations.
 * How to use:
 * - Create a class template dictionary using camel case for class name. ex: Sample.java
 * - Create a resource bundle with same name as template dictionary class. ex: Sample.properties
 * - In resource bundle declare some fields: subject, html, txt using template name (lowercase of class name, 'sample')
 *      as prefix. ex: "sample.subject", "sample.html", "sample.txt".
 *      "sample.subject": the email subject
 *      "sample.html": HTML version of your email template (file name)
 *      "sample.txt": TEXT version of your email (file name)
 * - Create template files in same package.
 *
 */
public class EmailTemplateManager
        extends DictionaryController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String SUBJECT = "subject";
    private static final String HTML = "html";
    private static final String TEXT = "txt";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected EmailTemplateManager() {

        //-- uncomment this to test sample template --//
        // super.register(Sample.class);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String getSubject(final String lang, final String templateName) {
        return super.get(lang, templateName + "." + SUBJECT);
    }

    public String getHtml(final String lang, final String templateName) {
        return this.getTemplate(lang, templateName, HTML);
    }

    public String getText(final String lang, final String templateName) {
        return this.getTemplate(lang, templateName, TEXT);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    public String getTemplate(final String lang, final String templateName, final String type) {
        final String prefix = templateName.toLowerCase();
        String resource = super.get(lang, prefix + "." + type);
        if(!StringUtils.hasText(resource)){
            resource = prefix + "_" + lang + "." + type; // try with a default pattern
        }
        if(StringUtils.hasText(resource)){
            return this.readFile(prefix, resource);
        } else {
            return "";
        }
    }

    private String readFile(final String templateName, final String fileName){
        final String class_path = PathUtils.getClassPath(this.getClass());
        final String root = PathUtils.concat(PathUtils.getParent(class_path), templateName);
        final String file_path = PathUtils.concat(root, fileName);
        return ClassLoaderUtils.getResourceAsString(this.getClass().getClassLoader(), file_path);
    }



}
