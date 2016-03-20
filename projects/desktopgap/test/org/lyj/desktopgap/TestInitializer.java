package org.lyj.desktopgap;



/**
 * TEST UNIT INITIALIZER
 */
public class TestInitializer {

    private static boolean _init = false;

    public static void init() {
        if (!_init) {
            _init = true;

            // launcher
            DesktopGapAppLauncher.main(new String[]{"-w", "USERHOME/desktopgap", "-t", "true"});
        }
    }

}
