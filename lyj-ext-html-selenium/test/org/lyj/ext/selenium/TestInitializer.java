package org.lyj.ext.selenium;


/**
 * TEST UNIT INITIALIZER
 */
public class TestInitializer {

    private static boolean _init = false;

    public static void init() {
        if (!_init) {
            _init = true;

            // launcher
            Launcher.main(new String[]{"-w", "USERHOME/selenium_dir", "-t", "true"});
        }
    }



}
