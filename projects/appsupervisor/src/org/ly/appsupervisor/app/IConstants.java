package org.ly.appsupervisor.app;

public interface IConstants {

    String APP_VERSION = "1.1.1";

    String PATH_INSTALL = "./install";

    String TYPE_MEMORY = "memory"; // check memory free
    String TYPE_CLOCK = "clock";  // check date time
    String TYPE_PING = "ping";    // check a ping response timeout
    String TYPE_NULL = "null";    // check if application is running

    String MU_DATE = "date";
    String MU_TIME = "time"; // check time
    String MU_DATETIME = "datetime"; // check datetime
    String MU_MB = "mb";  // megabyte
    String MU_GB = "gb";  // gigabyte

    String PATTERN_DATE = "yyyyMMdd";
    String PATTERN_TIME = "HH:mm";
    String PATTERN_DATE_TIME = PATTERN_DATE + " " + PATTERN_TIME;

}
