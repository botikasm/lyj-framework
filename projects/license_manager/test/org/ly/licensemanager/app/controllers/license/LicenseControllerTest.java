package org.ly.licensemanager.app.controllers.license;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.licensemanager.TestInitializer;

public class LicenseControllerTest {

    @BeforeClass
    public static void setUp() throws Exception {

        TestInitializer.init();

    }

    @Test
    public void instance() {

        LicenseController.instance();

    }
}