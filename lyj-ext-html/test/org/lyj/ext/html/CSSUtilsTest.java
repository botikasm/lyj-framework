package org.lyj.ext.html;

import org.junit.Test;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.PathUtils;

import static org.junit.Assert.*;

/**
 * Created by angelogeminiani on 15/03/16.
 */
public class CSSUtilsTest {

    @Test
    public void cssTest(){
        String path = PathUtils.getParent( PathUtils.getClassPath(this.getClass()) );
        String file_path = PathUtils.concat(path, "sample.html");
        String html = ClassLoaderUtils.getResourceAsString(this.getClass().getClassLoader(), file_path);
        String inlinecss = CSSInlineStyler.instance().convert(html);
        System.out.println(inlinecss);
    }

}