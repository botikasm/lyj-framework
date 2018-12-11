package org.lyj.ext.selenium;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.ext.selenium.controllers.SeleniumManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumManagerTest {

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();

    }

    // ------------------------------------------------------------------------
    //                      t e s t
    // ------------------------------------------------------------------------

    @Test
    public void start() {

        final String root = SeleniumManager.instance().root();
        System.out.println(root);
    }

    @Test
    public void standAloneTest() {

        WebDriver driver = new ChromeDriver();
        driver.get("http://www.google.com");

        //driver.close();
    }

}