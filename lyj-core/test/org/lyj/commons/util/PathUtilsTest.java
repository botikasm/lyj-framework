package org.lyj.commons.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PathUtilsTest {


    @Test
    public void testGetAbsolutePath() throws Exception {

        String absolute = PathUtils.getAbsolutePath("./log/test.log");

        assertTrue(PathUtils.isAbsolute(absolute));
        System.out.println(absolute);

    }

    @Test
    public void testPathMatch() throws Exception {

        assertTrue(PathUtils.pathMatch("/root/dir1/dir2", "/root/dir1/*"));
        assertTrue(PathUtils.pathMatch("http://root/dir1/dir2", "http://root/dir1/*"));
        assertTrue(PathUtils.pathMatch("/root/dir1/dir2", "/root/dir1/dir2"));
        assertFalse(PathUtils.pathMatch("/root/dir1/dir2/", "/root/dir1/dir2"));
        assertFalse(PathUtils.pathMatch("http://root/dir1/dir2", "http://root2/dir1/*"));
        // advanced parsing
        assertFalse(PathUtils.pathMatch("http://root/dir1/dir2", "http://root2/dir1/*?query=1234"));
        assertTrue(PathUtils.pathMatch("http://root/dir1/dir2?query=1234", "http://root2/*?query=1234"));
        assertTrue(PathUtils.pathMatch("http://root/dir1/dir2?query=1234", "http://root2/*?query=*"));
    }

    @Test
    public void testGetName() throws Exception {

        File file = new File("/dir/file1.txt");
        String fname = file.getName();
        String name = PathUtils.getFilename(fname);

        Assert.assertEquals(fname, name);
        System.out.println(name);

    }

    @Test
    public void testGetDesktopDirectory() throws Exception {

        String absolute = PathUtils.getDesktopDirectory();

        assertTrue(PathUtils.isAbsolute(absolute));
        System.out.println(absolute);

    }

    @Test
    public void testgetSplitRoot() throws Exception {

        String result = PathUtils.splitPathRoot("/test/path1/file.txt");

        assertTrue(result.equalsIgnoreCase("/path1/file.txt"));
        System.out.println(result);

    }

    @Test
    public void testResolve() throws Exception {

        String root = "web://localhost/dir1/dir2/";
        String path = "../../file.html";
        String concat = root + path;
        String resolved = PathUtils.resolve(concat);

        assertTrue(resolved.equalsIgnoreCase("web://localhost/file.html"));
        System.out.println(resolved);

    }

    @Test
    public void addUriParameters() throws Exception {

        String root = "web://localhost/dir1/dir2/";
        Map<String, String> params = new HashMap<String, String>();
        params.put("param1", "value1");
        params.put("param2", "value2 value2bis");

        String resolved = PathUtils.addURIParameters(root, params, true);

        assertTrue(resolved.equalsIgnoreCase("web://localhost/dir1/dir2/?param1=value1&param2=value2+value2bis"));
        System.out.println(resolved);

        root = "web://localhost/dir1/dir2/?param1=value1";
        params = new HashMap<String, String>();
        params.put("param2", "value2 value2bis");

        resolved = PathUtils.addURIParameters(root, params, true);

        assertTrue(resolved.equalsIgnoreCase("web://localhost/dir1/dir2/?param1=value1&param2=value2+value2bis"));
        System.out.println(resolved);

        root = "web://localhost/dir1/dir2/?param1=value1";
        params = new HashMap<String, String>();
        params.put("param1", "value1_replaced");
        params.put("param2", "value2 value2bis");

        resolved = PathUtils.addURIParameters(root, params, true);

        assertTrue(resolved.equalsIgnoreCase("web://localhost/dir1/dir2/?param1=value1_replaced&param2=value2+value2bis"));
        System.out.println(resolved);
    }

}
