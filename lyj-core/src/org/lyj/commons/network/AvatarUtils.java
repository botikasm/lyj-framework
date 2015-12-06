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

package org.lyj.commons.network;

import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.lang.Base64;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.RegExpUtils;
import org.lyj.commons.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for Avatar Image.
 */
public class AvatarUtils {

    //-- gravatar --//
    public static final String GRAVATAR_URL = "web://www.gravatar.com/";
    public static final String GRAVATAR_URL_SECURE = "https://secure.gravatar.com/";
    public static final String GRAVATAR_IMAGE_PREFIX = "avatar/";

    private static final String GRAVATAR_OPT_SIZE = "s={0}";
    private static final String GRAVATAR_OPT_DEFAULT = "d={0}";
    private static final String[] GRAVATAR_DEFAULTS = new String[]{
            "404",       // 404 not found
            "mm",        //  (mystery-man) a simple, cartoon-style silhouetted outline of a person (does not vary by email hash)
            "identicon", //  a geometric pattern based on an email hash
            "monsterid", //: a generated 'monster' with different colors, faces, etc
            "wavatar", // : generated faces with differing features and backgrounds
            "retro", // : awesome generated, 8-bit arcade-style pixelated faces
            "blank", // : a transparent PNG image (border added to HTML below for demonstration purposes)
    };

    //-- robohash avatars --//
    public static final String ROBO_HASH = "web://robohash.org/{0}";
    public static final String ROBO_HASH_GRAVATAR = ROBO_HASH.concat("?gravatar=yes");

    private static final String OPT_GRAVATAR = "gravatar=yes";
    private static final String OPT_IMG_SET = "set=set{0}";
    private static final String OPT_BG_SET = "bgset=bg{0}";
    private static final String OPT_IMG_SET2 = "set=set2";
    private static final String OPT_IMG_SET3 = "set=set3";
    private static final String OPT_SIZE = "size={h}x{w}";

    private static final String DATA_URL_PREFIX = "data:image/png;base64,";

    // ------------------------------------------------------------------------
    //                      MACRO
    // ------------------------------------------------------------------------

    public static String getBase64(final String email, final int size) {
        byte[] bytes = new byte[0];

        // try with gravatar
        bytes = getGravatar(email, false, size, 0);
        if (bytes.length == 0) {
            bytes = getAvatar(email, 0, 0, size, size);
        }
        return Base64.encodeBytes(bytes);
    }

    public static String getDataUrl(final String email, final int size) {
        final String base64 = getBase64(email, size);
        return StringUtils.hasText(base64) ? DATA_URL_PREFIX.concat(base64) : "";
    }

    // ------------------------------------------------------------------------
    //                      LOW LEVEL
    // ------------------------------------------------------------------------

    public static String getGraAvatarUrl(final String email,
                                         final boolean secure,
                                         final int size,
                                         final int defaultId) {
        final String base_url = secure ? GRAVATAR_URL_SECURE : GRAVATAR_URL;

        //-- options --//
        final StringBuilder options = new StringBuilder();
        if (size > 0) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(GRAVATAR_OPT_SIZE, size));
        }
        if (defaultId > -1 && defaultId < GRAVATAR_DEFAULTS.length) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(GRAVATAR_OPT_DEFAULT, GRAVATAR_DEFAULTS[defaultId]));
        }

        return base_url.concat(GRAVATAR_IMAGE_PREFIX).concat(md5(email)).concat(options.toString());
    }

    public static String getAvatarUrl(final String email,
                                      final boolean useGravatar,
                                      final int imgSet,
                                      final int bgSet,
                                      final int height,
                                      final int width) {
        final String avatar_email = RegExpUtils.isValidEmail(email)
                ? email
                : MD5.encode(email) + "@email.com";
        final String base_url = FormatUtils.format(ROBO_HASH, avatar_email);

        //-- options --//
        final StringBuilder options = new StringBuilder();
        if (useGravatar) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(OPT_GRAVATAR);
        }

        if (imgSet > 1) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(OPT_IMG_SET, imgSet));
        }

        if (bgSet > 0) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(OPT_BG_SET, bgSet));
        }

        if (height > 0 && width > 0) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(OPT_SIZE, getSize(height, width)));
        }

        return base_url.concat(options.toString());
    }

    public static byte[] getAvatar(final String email,
                                   final int imgSet,
                                   final int bgSet,
                                   final int height,
                                   final int width) {
        try {
            final String url = getAvatarUrl(email, false, imgSet, bgSet, height, width);

            final InputStream is = URLUtils.getInputStream(url, 3000, URLUtils.TYPE_ALL);
            try {
                return ByteUtils.getBytes(is);
            } finally {
                is.close();
            }
        } catch (final Throwable t) {
            LoggingUtils.getLogger(AvatarUtils.class).log(Level.SEVERE, null, t);
        }
        return new byte[0];
    }

    public static String getAvatarBase64(final String email,
                                         final int imgSet,
                                         final int bgSet,
                                         final int height,
                                         final int width) {
        final byte[] bytes = getAvatar(email, imgSet, bgSet, height, width);
        return Base64.encodeBytes(bytes);
    }

    public static byte[] getGravatar(final String email,
                                     final boolean secure,
                                     final int size,
                                     final int defaultId) {
        try {
            final String url = getGraAvatarUrl(email, secure, size, defaultId);

            final InputStream is = URLUtils.getInputStream(url, 3000, URLUtils.TYPE_ALL);
            try {
                final byte[] bytes = ByteUtils.getBytes(is);
                if (!is404(bytes)) {
                    return bytes;
                }
            } finally {
                is.close();
            }
        } catch (FileNotFoundException ignored) {
        } catch (final Throwable t) {
            LoggingUtils.getLogger(AvatarUtils.class).log(Level.SEVERE, null, t);
        }
        return new byte[0];
    }

    public static String getGravatarBase64(final String email,
                                           final boolean secure,
                                           final int size,
                                           final int defaultId) {
        final byte[] bytes = getGravatar(email, secure, size, defaultId);
        return Base64.encodeBytes(bytes);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Map<String, Object> getSize(final int h, final int w) {
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("h", h);
        result.put("w", w);
        return result;
    }

    private static String md5(final String text) {
        if (StringUtils.hasText(text)) {
            final String trimmed = StringUtils.trim(text);
            return MD5.encode(trimmed.toLowerCase()).toLowerCase();
        }
        return "";
    }

    private static boolean is404(final byte[] bytes) {
        try {
            final String text = new String(bytes);
            return text.startsWith("404");
        } catch (Throwable ignored) {
        }
        return false;
    }
}
