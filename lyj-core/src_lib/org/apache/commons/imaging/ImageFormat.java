/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.imaging;

/**
 * Provides a definition for an image format.
 */
public class ImageFormat {
    private final String name;
    private final String extension;
    public final boolean actual;

    private ImageFormat(final String name, final boolean actual) {
        this.name = name;
        this.extension = name;
        this.actual = actual;
    }

    private ImageFormat(final String name) {
        this.name = name;
        this.extension = name;
        actual = true;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ImageFormat)) {
            return false;
        }

        final ImageFormat other = (ImageFormat) o;

        return other.getName().equals(getName());

    }

    public String getName() {
        return name;
    }
    
    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return "{" + getName() + ": " + getExtension() + "}";
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public static final ImageFormat IMAGE_FORMAT_UNKNOWN = new ImageFormat(
            "UNKNOWN", false);
    public static final ImageFormat IMAGE_FORMAT_BMP = new ImageFormat("BMP");
    public static final ImageFormat IMAGE_FORMAT_DCX = new ImageFormat("DCX");
    public static final ImageFormat IMAGE_FORMAT_GIF = new ImageFormat("GIF");
    public static final ImageFormat IMAGE_FORMAT_ICNS = new ImageFormat("ICNS");
    public static final ImageFormat IMAGE_FORMAT_ICO = new ImageFormat("ICO");
    public static final ImageFormat IMAGE_FORMAT_JBIG2 = new ImageFormat("JBig2");
    public static final ImageFormat IMAGE_FORMAT_JPEG = new ImageFormat("JPEG");
    public static final ImageFormat IMAGE_FORMAT_PAM = new ImageFormat("PAM");
    public static final ImageFormat IMAGE_FORMAT_PSD = new ImageFormat("PSD");
    public static final ImageFormat IMAGE_FORMAT_PBM = new ImageFormat("PBM");
    public static final ImageFormat IMAGE_FORMAT_PGM = new ImageFormat("PGM");
    public static final ImageFormat IMAGE_FORMAT_PNM = new ImageFormat("PNM");
    public static final ImageFormat IMAGE_FORMAT_PPM = new ImageFormat("PPM");
    public static final ImageFormat IMAGE_FORMAT_PCX = new ImageFormat("PCX");
    public static final ImageFormat IMAGE_FORMAT_PNG = new ImageFormat("PNG");
    public static final ImageFormat IMAGE_FORMAT_RGBE = new ImageFormat("RGBE");
    public static final ImageFormat IMAGE_FORMAT_TGA = new ImageFormat("TGA");
    public static final ImageFormat IMAGE_FORMAT_TIFF = new ImageFormat("TIFF");
    public static final ImageFormat IMAGE_FORMAT_WBMP = new ImageFormat("WBMP");
    public static final ImageFormat IMAGE_FORMAT_XBM = new ImageFormat("XBM");
    public static final ImageFormat IMAGE_FORMAT_XPM = new ImageFormat("XPM");
    
    

    /**
     * Gets an array of ImageFormat objects for all supported image
     * formats.
     * @return A valid array of ImageFormat objects.
     */
    public static ImageFormat[] getAllFormats() {
        final ImageFormat result[] = {
                IMAGE_FORMAT_UNKNOWN,
                IMAGE_FORMAT_BMP,
                IMAGE_FORMAT_DCX,
                IMAGE_FORMAT_GIF,
                IMAGE_FORMAT_ICNS,
                IMAGE_FORMAT_ICO,
                IMAGE_FORMAT_JBIG2,
                IMAGE_FORMAT_JPEG,
                IMAGE_FORMAT_PNG,
                IMAGE_FORMAT_PAM,
                IMAGE_FORMAT_PSD,
                IMAGE_FORMAT_PBM,
                IMAGE_FORMAT_PCX,
                IMAGE_FORMAT_PGM,
                IMAGE_FORMAT_PNM,
                IMAGE_FORMAT_PPM,
                IMAGE_FORMAT_RGBE,
                IMAGE_FORMAT_TGA,
                IMAGE_FORMAT_TIFF, 
                IMAGE_FORMAT_WBMP, 
                IMAGE_FORMAT_XBM,
                IMAGE_FORMAT_XPM,
        };

        return result;
    }
}
