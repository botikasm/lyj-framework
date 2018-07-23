package org.ly.server;


/**
 * TEST UNIT INITIALIZER
 */
public class TestInitializer {

    private static boolean _init = false;

    public static void init() {
        if (!_init) {
            _init = true;

            // launcher
            Launcher.main(new String[]{"-w", "USERHOME/app_server", "-t", "true"});
        }
    }

    public static void initAll() {
        if (!_init) {
            _init = true;

            // launcher
            Launcher.main(new String[]{"-w", "USERHOME/app_server"});
        }
    }

}
