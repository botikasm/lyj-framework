package org.ly.ose.commons;

import org.lyj.commons.util.MapBuilder;
import org.lyj.commons.util.PathUtils;

import java.util.Locale;
import java.util.Map;

public interface IConstants {

    Locale LOCALE = Locale.ENGLISH;
    String DEF_LANG = org.lyj.IConstants.DEF_LANG;
    String BASE_LANG = org.lyj.IConstants.BASE_LANG;
    String ROOT = PathUtils.getAbsolutePath("");
    String ROOT_FILES = PathUtils.combine(ROOT, "files");

    String STR_NULL = "null";

    String APP_TOKEN_CLIENT_API = "iuhdiu87w23ruh897dfyc2w3r";
    String APP_TOKEN = "io8dcfvjkkleaqwhjtuyqwuyeio";

    String TYPE_PROGRAM = "program";
    String TYPE_DATABASE = "database";
    String TYPE_ERROR = "error";

    String GENDER_MALE = "m";
    String GENDER_FEMALE = "f";
    String GENDER_UNDEFINED = "u";
    Map<String, String> GENDER_MAP = MapBuilder.createSS()
            .put("male", GENDER_MALE).put("m", GENDER_MALE).put("MALE", GENDER_MALE).put("M", GENDER_MALE)
            .put("female", GENDER_FEMALE).put("f", GENDER_FEMALE).put("FEMALE", GENDER_FEMALE).put("F", GENDER_FEMALE)
            .toMap();

}
