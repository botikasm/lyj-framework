package org.ly.ose.server.application.programming;

/**
 * Program constants
 */
public interface IConstants
        extends org.ly.ose.server.IConstants {

    String[] PROTECTED_NAMESPACES = new String[]{"system"};
    String[] ALLOWED_NAMESPACES = new String[]{"system.utils", "system.license"};

    String[] TEXT_FILES = new String[]{"txt", "json", "js", "html", "properties", "xml"};

    String[] VALID_EXTENSIONS = {"js", "json", "properties", "html", "xml"};



}
