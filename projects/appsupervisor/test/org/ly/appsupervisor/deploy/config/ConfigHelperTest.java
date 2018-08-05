package org.ly.appsupervisor.deploy.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.appsupervisor.TestInitializer;
import org.ly.appsupervisor.app.model.ModelLauncher;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ConfigHelperTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void lauchers() {
        final Map<String, ModelLauncher> launchers = ConfigHelper.instance().launchers();
        assertTrue(launchers.size() > 0);
    }
}