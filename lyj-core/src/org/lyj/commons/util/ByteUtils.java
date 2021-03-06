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

import org.lyj.commons.Delegates;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author angelo.geminiani
 */
public class ByteUtils {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_BUFFER_SIZE = 10240;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static boolean isByteArray(final Object data) {
        if (data.getClass().isArray()) {
            final Object val = Array.get(data, 0);
            if (val instanceof Byte) {
                return true;
            }
        }
        return false;
    }

    public static byte[] getBytes(final String fileName) throws IOException {
        return getBytes(new File(fileName));
    }

    public static byte[] getBytes(final File file) throws IOException {
        if (file.length() < MAX_BUFFER_SIZE) {
            return Files.readAllBytes(Paths.get(file.getPath()));
        } else {
            // large files (Java heap space error risk)
            try (final FileInputStream fis = new FileInputStream(file);) {
                return getBytes(fis);
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static byte[] getBytes(final File file,
                                  final int skip,
                                  final int len) throws IOException {
        // large files (Java heap space error risk)
        try (final FileInputStream fis = new FileInputStream(file);) {
            return getBytes(fis, DEFAULT_BUFFER_SIZE, skip, len);
        } catch (IOException e) {
            throw e;
        }
    }

    public static byte[] getBytes(final BufferedImage image) throws IOException {
        return getBytes(image, "jpg");
    }

    public static byte[] getBytes(final BufferedImage image, final String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, null != format ? format : "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    public static byte[] getBytes(final InputStream is) throws IOException {
        return getBytes(is, DEFAULT_BUFFER_SIZE);
    }

    public static byte[] getBytes(final InputStream is, final int bufferSize) throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);) {
            byte[] buffer = new byte[bufferSize];
            int len;

            while ((len = is.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
            }

            out.flush();
            return out.toByteArray();
        }
    }


    public static void read(final InputStream is, final int bufferSize,
                            final Delegates.Callback<byte[]> callback) throws IOException {

        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = is.read(buffer)) >= 0) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize)) {
                out.write(buffer, 0, len);
                Delegates.invoke(callback, out.toByteArray());
            }
        }
    }

    public static byte[] optBytes(final InputStream is) {
        return optBytes(is, DEFAULT_BUFFER_SIZE);
    }

    public static byte[] optBytes(final InputStream is, final int bufferSize) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
            try {
                byte[] buffer = new byte[bufferSize];
                int len;

                while ((len = is.read(buffer)) >= 0) {
                    out.write(buffer, 0, len);
                }
            } finally {
                out.close();
            }
            return out.toByteArray();
        } catch (Throwable ignored) {
        }
        return new byte[0];
    }

    public static byte[] getBytes(final Object data) throws IOException {
        byte[] result = new byte[0];
        if (!isByteArray(data)) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(data);
                result = bos.toByteArray();
            } finally {
                if (null != out) out.close();
                bos.close();
            }
        } else {
            result = (byte[]) data;
        }
        return result;
    }

    public static byte[] optBytes(final Object data) {
        try {
            return getBytes(data);
        } catch (Throwable ignored) {
        }
        return new byte[0];
    }

    public static Object getObject(final byte[] data)
            throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return in.readObject();
        } finally {
            if (null != in) in.close();
            bis.close();
        }
    }

    public static Object optObject(final byte[] data) {
        try {
            return getObject(data);
        } catch (Throwable ignored) {
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static byte[] getBytes(final InputStream is,
                                   final int bufferSize,
                                   final int skip,
                                   final int len) throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);) {
            byte[] buffer = new byte[bufferSize];

            is.skip(skip);

            int len_read = is.read(buffer, 0, len);
            if (len_read >= 0) {
                out.write(buffer, 0, len_read);
            }

            out.flush();
            return out.toByteArray();
        }
    }

}
