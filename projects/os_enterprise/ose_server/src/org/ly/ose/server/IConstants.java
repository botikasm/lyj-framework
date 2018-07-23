package org.ly.ose.server;

import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;

import java.util.Locale;
import java.util.Map;

public interface IConstants
        extends org.ly.ose.commons.IConstants {

    String APP_NAME = "OSEnterprise Server Node";
    String APP_VERSION = "1.0.1";

    String DB_GLOBAL = "ose_server";
    String DB_CUSTOM_PREFIX = "ose_server_";
    String DB_CONFIGURATION_PATH = "databases.main";


}
