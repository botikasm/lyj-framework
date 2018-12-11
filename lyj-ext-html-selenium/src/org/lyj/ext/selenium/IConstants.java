package org.lyj.ext.selenium;

import org.lyj.commons.util.PathUtils;

public interface IConstants {

    String DRIVER_BASE = "BASE";
    String DRIVER_CHROME = "chrome";
    String DRIVER_FIREFOX = "firefox";


    String PATH_PROXYLIST = PathUtils.getAbsolutePath("./files/proxy_list");
    String PATH_ROUTINES = PathUtils.getAbsolutePath("./files/routines");

}
