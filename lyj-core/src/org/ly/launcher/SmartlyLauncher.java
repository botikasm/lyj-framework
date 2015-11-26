/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ly.launcher;



/**
 * Basic Launcher implementation.
 */
public class SmartlyLauncher
        extends AbstractLauncher {


    public SmartlyLauncher(final String[] args) {
        super(args);
    }

    @Override
    protected void onLoadPackage(final SmartlyPackageLoader loader) {
        // nothing to do here. only for packages
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    /**
     * Smartly main method. This retrieves the Smartly home directory, creates the
     * classpath and invokes run().
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final SmartlyLauncher main = new SmartlyLauncher(args);
        main.run();
    }
}
