package org.lyj.desktopfences.app;


import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Application constants
 */
public interface IConstants {

    String VERSION = "1.0.0";

    // ------------------------------------------------------------------------
    //                      ARCHIVE
    // ------------------------------------------------------------------------

    String ARCHIVE_PATH = PathUtils.getAbsolutePath("archive");

    // ------------------------------------------------------------------------
    //                      MODEL FIELDS
    // ------------------------------------------------------------------------

    String ID = "_id";

    // ------------------------------------------------------------------------
    //                      CATEGORIES
    // ------------------------------------------------------------------------

    String DEFAULT_CATEGORY = "file";

    Map<String, List> EXT_TAGS = MapBuilder.create(String.class, List.class)
            .put("image", Arrays.asList("bmp", "jpg", "jpeg", "jfif", "png", "gif", "tif", "tiff", "svg"))
            .put("photo", Arrays.asList("jpg", "jpeg", "jfif", "png"))
            .put("music", Arrays.asList("3gp", "aa", "aac", "aax", "act", "aiff", "amr", "mp3", "ogg", "oga", "tta", "wav", "wma", "wv"))
            .put("film", Arrays.asList("webm", "flv", "vob", "ogv", "ogg", "drc", "gif", "gifv", "mng", "avi", "mov", "qt", "wmv", "yuv", "rm", "rmvb", "asf", "mp4", "m4p ", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "3gp", "3g2", "flv"))
            .put("office", Arrays.asList("doc", "docx", "docb", "docm", "dotm",
                    "xls", "xlsx", "xlsm", "xlm", "xlsb", "xla", "xlam",
                    "ppt", "pot", "pps", "pptx", "pptm", "potx", "potm", "ppam", "ppsx", "ppsm", "sldx", "sldm",
                    "accdb", "mdb", "accde", "accdt",
                    "odt", "ods", "odp", "ott", "odm", "odg", "otg", "odp", "otp", "odf"))
            .put("template", Arrays.asList("dotx", "dotm", "xlt", "xltx", "xltm", "pot", "potx", "potm", "accdt",
                    "ott", "odm", "ots", "otg", "otp", "odc", "odb"))
            .put("developer", Arrays.asList("docm", "dotm", "xlm", "xltm", "pptm", "potm", "ppsm", "sldm", "accde", "odf", "odb",
                    "html", "htm", "js", "php", "xml", "json", "css", "java", "cpp", "pas", "md"))
            .put("document", Arrays.asList("txt", "doc", "docx", "odt", "md", "pdf"))
            .put("archive", Arrays.asList("zip", "rar", "7z", "gzip", "bzip2", "tar", "gz"))
            .put("ebook", Arrays.asList("epub", "pdf"))
            .put("bookmark", Arrays.asList("webloc"))
            .put("ISO", Arrays.asList("iso"))
            .toMap();

    List<String> EXCLUDE_EXTENSIONS = Arrays.asList("lnk", "DS_Store", "localized");
    List<String> EXCLUDE_DIRECTORIES = Arrays.asList("teleport");

}
