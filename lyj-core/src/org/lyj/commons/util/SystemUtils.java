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

/*
 *
 */

package org.lyj.commons.util;


import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author angelo.geminiani
 */
public abstract class SystemUtils {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    /**
     * File Types in format "file://"
     */
    public enum FileType {

        file("file://"),
        zip("zip://"),
        jar("jar://"),
        tar("tar://"),
        tgz("tgz://"),
        tbz2("tbz2://"),
        gz("gz://"),
        bz2("bz2://"),
        http("http://"),
        https("https://"),
        webdav("webdav://"),
        ftp("ftp://"),
        sftp("sftp://"),
        smb("smb://"),
        tmp("tmp://"),
        res("res://"),
        ram("ram://");
        private final String _value;

        FileType(String value) {
            _value = value;
        }

        @Override
        public String toString() {
            return super.toString();
        }

        public String getValue() {
            return _value;
        }

        /**
         * Retrieve a File System Type value
         *
         * @param path a path. i.e. "http://folder/file.txt"
         * @return
         */
        public static FileType getType(final String path) {
            final FileType[] values = FileType.values();
            for (final FileType sfs : values) {
                final String value = sfs.toString().concat(":");
                if (path.startsWith(value)) {
                    return sfs;
                }
            }
            return null;
        }

        public static String[] getValues() {
            final List<String> result = new ArrayList<String>();
            final FileType[] values = FileType.values();
            for (final FileType sfs : values) {
                result.add(sfs.getValue());
            }
            return result.toArray(new String[result.size()]);
        }


    }


    private static final String REPORT_SEP = "------------------------------------------";
    private static final String REPORT_TAB = "    ";

    public static final String OS_UNDEFINED = "undefined";
    public static final String OS_MAC = "mac";
    public static final String OS_LINUX_32 = "linux_32";
    public static final String OS_LINUX_64 = "linux_64";
    public static final String OS_WIN_32 = "win_32";
    public static final String OS_WIN_64 = "win_64";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private static Boolean _iswindow = null;
    private static Boolean _islinux = null;
    private static Boolean _ismac = null;
    private static String _os_symbol = null;  // win-32, win-64, mac, linux-32, linux-64

    private static Runtime __runtime;

    // ------------------------------------------------------------------------
    //                      O S
    // ------------------------------------------------------------------------

    public static String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    public static String getOSVersion() {
        return System.getProperty("os.version");
    }

    public static String getOSAchitecture() {
        return System.getProperty("os.arch");
    }

    public static String getOSSymbol() {
        if (!StringUtils.hasText(_os_symbol)) {
            final String os = getOperatingSystem();
            if (isWindows()) {
                _os_symbol = os.contains("32") ? OS_WIN_32 : OS_WIN_64;
            } else if (isLinux()) {
                _os_symbol = os.contains("32") ? OS_LINUX_32 : OS_LINUX_64;
            } else if (isMac()) {
                _os_symbol = os.contains("32") ? OS_MAC : OS_MAC;
            } else {
                _os_symbol = OS_UNDEFINED; // undefined
            }
        }
        return _os_symbol;

    }

    public static boolean isWindows() {
        if (null == _iswindow) {
            final String os = getOperatingSystem();
            _iswindow = os.toLowerCase().startsWith("win");
        }
        return _iswindow;
    }

    public static boolean isLinux() {
        if (null == _islinux) {
            final String os = getOperatingSystem();
            _islinux = os.toLowerCase().startsWith("linux");
        }
        return _islinux;
    }

    public static boolean isMac() {
        if (null == _ismac) {
            final String os = getOperatingSystem();
            _ismac = os.toLowerCase().startsWith("mac");
        }
        return _ismac;
    }

    // ------------------------------------------------------------------------
    //                      U R L
    // ------------------------------------------------------------------------

    public static void openURL(final String url) {
        final String osName = getOperatingSystem();
        try {
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (null != desktop && desktop.isSupported(Desktop.Action.BROWSE)) {
                final URI uri = new URI(url);
                desktop.browse(uri);
            } else {
                if (osName.startsWith("Mac OS")) {
                    Class fileMgr = Class.forName("com.apple.eio.FileManager");
                    Method openURL = fileMgr.getDeclaredMethod("openURL",
                            new Class[]{String.class});
                    openURL.invoke(null, new Object[]{url});
                } else if (osName.startsWith("Windows")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else { //assume Unix or Linux
                    String[] browsers = {
                            "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                    String browser = null;
                    for (int count = 0; count < browsers.length && browser == null; count++)
                        if (Runtime.getRuntime().exec(
                                new String[]{"which", browsers[count]}).waitFor() == 0)
                            browser = browsers[count];
                    if (browser == null)
                        throw new Exception("Could not find web browser");
                    else
                        Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {

        }
    }


    public static void selectFile(final String path) throws Exception {
        final File file = new File(PathUtils.getAbsolutePath(path));
        if (file.exists()) {
            open(file, true);
        }
    }

    public static void openFile(final String path) throws Exception {
        final File file = new File(PathUtils.getAbsolutePath(path));
        if (file.exists()) {
            open(file, false);
        }
    }

    // ------------------------------------------------------------------------
    //                      M E M O R Y
    // ------------------------------------------------------------------------

    public static long getMaxMemory() {
        return runtime().maxMemory();
    }

    public static long getTotalMemory() {
        return runtime().totalMemory();
    }

    public static long getFreeMemory() {
        return runtime().freeMemory();
    }

    public static int countProcessors() {
        return runtime().availableProcessors();
    }

    public static void gc() {
        runtime().gc();
    }

    // ------------------------------------------------------------------------
    //                      R E P O R T
    // ------------------------------------------------------------------------

    public static String printSystemStatus() {
        final StringBuilder sb = new StringBuilder();

        sb.append("\n");

        //-- header --//
        sb.append(REPORT_SEP).append("\n");
        sb.append(" ").append(DateWrapper.parse(DateUtils.now()).toString("yyyy MM dd, HH:mm:ss")).append("\n");
        sb.append(REPORT_SEP).append("\n");

        //-- os --//
        sb.append(REPORT_TAB);
        sb.append("OS: ").append(getOperatingSystem());
        sb.append(" - ").append(getOSVersion());
        sb.append(" - ").append(getOSAchitecture());
        sb.append("\n");

        //-- cpu --//
        sb.append(REPORT_TAB).append("CPU: ").append(countProcessors()).append("\n");

        //-- mem --//
        sb.append(REPORT_TAB).append("Max Memory: ").append(FormatUtils.formatNumber(ConversionUtils.bytesToMbyte(getMaxMemory(), 2), LocaleUtils.DEFAULT)).append(" MB\n");
        sb.append(REPORT_TAB).append("Available Memory: ").append(FormatUtils.formatNumber(ConversionUtils.bytesToMbyte(getTotalMemory(), 2), LocaleUtils.DEFAULT)).append(" MB\n");
        sb.append(REPORT_TAB).append("Used Memory: ").append(FormatUtils.formatNumber(ConversionUtils.bytesToMbyte(getTotalMemory() - getFreeMemory(), 2), LocaleUtils.DEFAULT)).append(" MB\n");
        sb.append(REPORT_TAB).append("Free Memory: ").append(FormatUtils.formatNumber(ConversionUtils.bytesToMbyte(getFreeMemory(), 2), LocaleUtils.DEFAULT)).append(" MB\n");

        sb.append(REPORT_SEP).append("\n");

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Runtime runtime() {
        if (null == __runtime) {
            __runtime = Runtime.getRuntime();
        }
        return __runtime;
    }

    private static Process open(final File file,
                                final boolean select_only) throws Exception {
        final String osName = getOperatingSystem();
        final String command;
        if (osName.startsWith("Mac OS")) {
            // mac
            command = select_only
                    ? "open -R " + file.getAbsolutePath()
                    : "open " + file.getAbsolutePath();
        } else if (osName.startsWith("Windows")) {
            // windows
            command = select_only
                    ? "explorer.exe /select," + file.getAbsolutePath()
                    : "explorer.exe," + file.getAbsolutePath();
        } else {
            //assume Unix or Linux
            command = "";
        }
        return StringUtils.hasText(command) ? runtime().exec(command) : null;
    }

}
