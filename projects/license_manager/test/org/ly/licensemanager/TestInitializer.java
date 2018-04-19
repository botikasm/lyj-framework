package org.ly.licensemanager;


/**
 * TEST UNIT INITIALIZER
 */
public class TestInitializer {

    private static boolean _init = false;

    public static void init() {
        if (!_init) {
            _init = true;

            // register Dictionary extras
            //Dictionary.instance().register(Beta.class);

            // launcher
            Launcher.main(new String[]{"-w", "USERHOME/license_manager", "-t", "true"});
        }
    }

}
