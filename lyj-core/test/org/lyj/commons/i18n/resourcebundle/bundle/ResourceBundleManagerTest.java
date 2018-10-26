package org.lyj.commons.i18n.resourcebundle.bundle;

import org.junit.Before;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.util.PathUtils;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TEST SOME FEATURES
 */
public class ResourceBundleManagerTest {

    @Before
    public void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void getResourceFromFileSystem() throws Exception {

        final String root = PathUtils.getAbsolutePath("./i18n_resources");
        final String bundle = PathUtils.concat(root, "i18n");

        // load default lang
        final Properties props = ResourceBundleManager.getProperties(bundle, "de");
        assertNotNull(props);
        assertTrue(props.size() > 0);

        final FilesystemDictionary dic = new FilesystemDictionary(bundle);
        final String msg = dic.getMessage("common_welcome", "en");
        assertNotNull(msg);


    }

}