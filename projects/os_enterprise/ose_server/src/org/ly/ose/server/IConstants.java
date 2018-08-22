package org.ly.ose.server;

import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;

import java.util.Locale;
import java.util.Map;

public interface IConstants
        extends org.ly.ose.commons.IConstants {

    String APP_NAME = "OSEnterprise Server Node";
    String APP_VERSION = "1.0.2";

    String DB_GLOBAL = "ose_server";
    String DB_GLOBAL_LOGGING = "ose_server_log";
    String DB_PROGRAM_PREFIX = "ose_program_";

    String DB_CONFIGURATION_PATH = "databases.main";

    // programs constants
    int SESSION_TIMEOUT_MS = 30 * 1000; // 30 seconds
    int LOOP_INTERVAL_MS = 1 * 1000; // 30 seconds

    String CHANNEL_API = "api";
    String CHANNEL_SOCKET = "socket";
    String CHANNEL_FACEBOOK = "facebook";

}
