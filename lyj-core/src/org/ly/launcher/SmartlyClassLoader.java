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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A <code>ClassLoader</code> used for application class loading. This class
 * provides constructors and methods to add jar directories to the classpath.<br/>
 * <p>
 * Adding a jar file after a class has already been looked up (in constructor) won't make the classes available.
 * </p>
 */
public class SmartlyClassLoader extends URLClassLoader {

    public SmartlyClassLoader() {
        this(new URL[0]);
    }

    public SmartlyClassLoader(final URL[] urls) {
        this(urls, SmartlyClassLoader.class.getClassLoader());
    }

    public SmartlyClassLoader(final File home, final String[] classpath)
            throws MalformedURLException {
        this(new URL[0]);
        for (final String item : classpath) {
            if (item.endsWith(File.separator + "**") || item.endsWith("/**")) {
                final File dir = getAbsoluteFile(home, item.substring(0, item.length() - 2));
                this.addClasspathWildcard(dir, true);
            } else if (item.endsWith(File.separator + "*") || item.endsWith("/*")) {
                final File dir = getAbsoluteFile(home, item.substring(0, item.length() - 1));
                this.addClasspathWildcard(dir, false);
            } else {
                final File file = getAbsoluteFile(home, item);
                super.addURL(new URL("file:" + file));
            }
        }
    }

    public SmartlyClassLoader(final URL[] urls, final ClassLoader parent) {
        super(urls, parent);
    }


    // --------------------------------------------------------------------
    //               p r o t e c t e d
    // --------------------------------------------------------------------

    protected void addClasspathWildcard(final File dir, final boolean recursive)
            throws MalformedURLException {
        if (!dir.exists()) {
            throw new IllegalArgumentException("Directory '" + dir + "' does not exist");
        } else if (!dir.isDirectory()) {
            throw new IllegalArgumentException("'" + dir + "' is not a directory");
        }
        final File[] files = dir.listFiles();
        assert files != null;
        for (final File file : files) {
            if (recursive && file.isDirectory()) {
                this.addClasspathWildcard(file, true);
            } else {
                final String name = file.getName().toLowerCase();
                if (file.isFile() && (name.endsWith(".jar") || (name.endsWith(".zip")))) {
                    super.addURL(new URL("file:" + file));
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C - p r i v a t e
    // --------------------------------------------------------------------

    private static File getAbsoluteFile(final File home, final String path) {
        final File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(home, path);
    }

}
