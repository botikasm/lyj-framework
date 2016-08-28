package org.lyj;

import org.lyj.launcher.LyjLauncher;

/**
 * TEST UNIT INITIALIZER
 */
public class TestInitializer {

    private static boolean _init = false;

    public static void init() {
        if (!_init) {
            _init = true;

            // register Dictionary extras


            // launcher
            Launcher.main(new String[]{"-w", "USERHOME/lyj-tests", "-t", "true"});
        }
    }


    private static class Launcher extends LyjLauncher {

        public Launcher(final String[] args){
            super(args);
        }

        @Override
        public void ready() {

        }

        @Override
        public void shutdown() {

        }

        public static void main(final String[] args) {
            final Launcher main = new Launcher(args);
            main.run();
        }
    }
}
