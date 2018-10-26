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
package org.apache.commons.imaging.formats.pnm;

import org.apache.commons.imaging.*;
import org.apache.commons.imaging.common.ByteOrder;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.palette.PaletteFactory;
import org.apache.commons.imaging.util.Debug;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class PnmImageParser extends ImageParser implements PnmConstants {

    public PnmImageParser() {
        super.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        // setDebug(true);
    }

    @Override
    public String getName() {
        return "Pbm-Custom";
    }

    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    private static final String DEFAULT_EXTENSION = ".pnm";

    private static final String ACCEPTED_EXTENSIONS[] = {".pbm", ".pgm",
            ".ppm", ".pnm", ".pam"};

    @Override
    protected String[] getAcceptedExtensions() {
        return ACCEPTED_EXTENSIONS;
    }

    @Override
    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{
                ImageFormat.IMAGE_FORMAT_PBM, //
                ImageFormat.IMAGE_FORMAT_PGM, //
                ImageFormat.IMAGE_FORMAT_PPM, //
                ImageFormat.IMAGE_FORMAT_PNM,
                ImageFormat.IMAGE_FORMAT_PAM
        };
    }

    private FileInfo readHeader(final InputStream is) throws ImageReadException,
            IOException {
        final byte identifier1 = readByte("Identifier1", is, "Not a Valid PNM File");
        final byte identifier2 = readByte("Identifier2", is, "Not a Valid PNM File");

        if (identifier1 != PNM_PREFIX_BYTE) {
            throw new ImageReadException("PNM file has invalid prefix byte 1");
        }

        final WhiteSpaceReader wsr = new WhiteSpaceReader(is);

        if (identifier2 == PBM_TEXT_CODE ||
                identifier2 == PBM_RAW_CODE ||
                identifier2 == PGM_TEXT_CODE ||
                identifier2 == PGM_RAW_CODE ||
                identifier2 == PPM_TEXT_CODE ||
                identifier2 == PPM_RAW_CODE) {

            final int width = Integer.parseInt(wsr.readtoWhiteSpace());
            final int height = Integer.parseInt(wsr.readtoWhiteSpace());

            if (identifier2 == PBM_TEXT_CODE) {
                return new PbmFileInfo(width, height, false);
            } else if (identifier2 == PBM_RAW_CODE) {
                return new PbmFileInfo(width, height, true);
            } else if (identifier2 == PGM_TEXT_CODE) {
                final int maxgray = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PgmFileInfo(width, height, false, maxgray);
            } else if (identifier2 == PGM_RAW_CODE) {
                final int maxgray = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PgmFileInfo(width, height, true, maxgray);
            } else if (identifier2 == PPM_TEXT_CODE) {
                final int max = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PpmFileInfo(width, height, false, max);
            } else if (identifier2 == PPM_RAW_CODE) {
                final int max = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PpmFileInfo(width, height, true, max);
            } else {
                throw new ImageReadException("PNM file has invalid header.");
            }
        } else if (identifier2 == PAM_RAW_CODE) {
            int width = -1;
            boolean seenWidth = false;
            int height = -1;
            boolean seenHeight = false;
            int depth = -1;
            boolean seenDepth = false;
            int maxVal = -1;
            boolean seenMaxVal = false;
            String tupleType = "";
            boolean seenTupleType = false;

            // Advance to next line
            wsr.readLine();
            String line;
            while ((line = wsr.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }
                final StringTokenizer tokenizer = new StringTokenizer(line, " ", false);
                final String type = tokenizer.nextToken();
                if (type.equals("WIDTH")) {
                    seenWidth = true;
                    width = Integer.parseInt(tokenizer.nextToken());
                } else if (type.equals("HEIGHT")) {
                    seenHeight = true;
                    height = Integer.parseInt(tokenizer.nextToken());
                } else if (type.equals("DEPTH")) {
                    seenDepth = true;
                    depth = Integer.parseInt(tokenizer.nextToken());
                } else if (type.equals("MAXVAL")) {
                    seenMaxVal = true;
                    maxVal = Integer.parseInt(tokenizer.nextToken());
                } else if (type.equals("TUPLTYPE")) {
                    seenTupleType = true;
                    tupleType += tokenizer.nextToken();
                } else if (type.equals("ENDHDR")) {
                    break;
                } else {
                    throw new ImageReadException("Invalid PAM file header type " + type);
                }
            }

            if (!seenWidth) {
                throw new ImageReadException("PAM header has no WIDTH");
            } else if (!seenHeight) {
                throw new ImageReadException("PAM header has no HEIGHT");
            } else if (!seenDepth) {
                throw new ImageReadException("PAM header has no DEPTH");
            } else if (!seenMaxVal) {
                throw new ImageReadException("PAM header has no MAXVAL");
            } else if (!seenTupleType) {
                throw new ImageReadException("PAM header has no TUPLTYPE");
            }

            return new PamFileInfo(width, height, depth, maxVal, tupleType);
        } else {
            throw new ImageReadException("PNM file has invalid prefix byte 2");
        }
    }

    private FileInfo readHeader(final ByteSource byteSource)
            throws ImageReadException, IOException {
        InputStream is = null;

        try {
            is = byteSource.getInputStream();

            return readHeader(is);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final Exception e) {
                Debug.debug(e);
            }
        }
    }

    @Override
    public byte[] getICCProfileBytes(final ByteSource byteSource, final Map<String, Object> params)
            throws ImageReadException, IOException {
        return null;
    }

    @Override
    public Dimension getImageSize(final ByteSource byteSource, final Map<String, Object> params)
            throws ImageReadException, IOException {
        final FileInfo info = readHeader(byteSource);

        if (info == null) {
            throw new ImageReadException("PNM: Couldn't read Header");
        }

        return new Dimension(info.width, info.height);
    }

    public byte[] embedICCProfile(final byte image[], final byte profile[]) {
        return null;
    }

    @Override
    public boolean embedICCProfile(final File src, final File dst, final byte profile[]) {
        return false;
    }

    @Override
    public IImageMetadata getMetadata(final ByteSource byteSource, final Map<String, Object> params)
            throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(final ByteSource byteSource, final Map<String, Object> params)
            throws ImageReadException, IOException {
        final FileInfo info = readHeader(byteSource);

        if (info == null) {
            throw new ImageReadException("PNM: Couldn't read Header");
        }

        final List<String> Comments = new ArrayList<String>();

        final int BitsPerPixel = info.getBitDepth() * info.getNumComponents();
        final ImageFormat Format = info.getImageType();
        final String FormatName = info.getImageTypeDescription();
        final String MimeType = info.getMIMEType();
        final int NumberOfImages = 1;
        final boolean isProgressive = false;

        // boolean isProgressive = (fPNGChunkIHDR.InterlaceMethod != 0);
        //
        final int PhysicalWidthDpi = 72;
        final float PhysicalWidthInch = (float) ((double) info.width / (double) PhysicalWidthDpi);
        final int PhysicalHeightDpi = 72;
        final float PhysicalHeightInch = (float) ((double) info.height / (double) PhysicalHeightDpi);

        final String FormatDetails = info.getImageTypeDescription();

        final boolean isTransparent = info.hasAlpha();
        final boolean usesPalette = false;

        final int ColorType = info.getColorType();
        final String compressionAlgorithm = ImageInfo.COMPRESSION_ALGORITHM_NONE;

        final ImageInfo result = new ImageInfo(FormatDetails, BitsPerPixel, Comments,
                Format, FormatName, info.height, MimeType, NumberOfImages,
                PhysicalHeightDpi, PhysicalHeightInch, PhysicalWidthDpi,
                PhysicalWidthInch, info.width, isProgressive, isTransparent,
                usesPalette, ColorType, compressionAlgorithm);

        return result;
    }

    @Override
    public boolean dumpImageFile(final PrintWriter pw, final ByteSource byteSource)
            throws ImageReadException, IOException {
        pw.println("pnm.dumpImageFile");

        final ImageInfo imageData = getImageInfo(byteSource);
        if (imageData == null) {
            return false;
        }

        imageData.toString(pw, "");

        pw.println("");

        return true;
    }

    @Override
    public BufferedImage getBufferedImage(final ByteSource byteSource, final Map<String, Object> params)
            throws ImageReadException, IOException {
        InputStream is = null;

        try {
            is = byteSource.getInputStream();

            final FileInfo info = readHeader(is);

            final int width = info.width;
            final int height = info.height;

            final boolean hasAlpha = info.hasAlpha();
            final ImageBuilder imageBuilder = new ImageBuilder(width, height,
                    hasAlpha);
            info.readImage(imageBuilder, is);

            return imageBuilder.getBufferedImage();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final Exception e) {
                Debug.debug(e);
            }
        }
    }

    public static final String PARAM_KEY_PNM_RAWBITS = "PNM_RAWBITS";
    public static final String PARAM_VALUE_PNM_RAWBITS_YES = "YES";
    public static final String PARAM_VALUE_PNM_RAWBITS_NO = "NO";

    @Override
    public void writeImage(final BufferedImage src, final OutputStream os, Map<String, Object> params)
            throws ImageWriteException, IOException {
        PnmWriter writer = null;
        boolean useRawbits = true;
        final boolean hasAlpha = new PaletteFactory().hasTransparency(src);

        if (params != null) {
            final Object useRawbitsParam = params.get(PARAM_KEY_PNM_RAWBITS);
            if (useRawbitsParam != null) {
                if (useRawbitsParam.equals(PARAM_VALUE_PNM_RAWBITS_NO)) {
                    useRawbits = false;
                }
            }

            final Object subtype = params.get(PARAM_KEY_FORMAT);
            if (subtype != null) {
                if (subtype.equals(ImageFormat.IMAGE_FORMAT_PBM)) {
                    writer = new PbmWriter(useRawbits);
                } else if (subtype.equals(ImageFormat.IMAGE_FORMAT_PGM)) {
                    writer = new PgmWriter(useRawbits);
                } else if (subtype.equals(ImageFormat.IMAGE_FORMAT_PPM)) {
                    writer = new PpmWriter(useRawbits);
                } else if (subtype.equals(ImageFormat.IMAGE_FORMAT_PAM)) {
                    writer = new PamWriter();
                }
            }
        }

        if (writer == null) {
            if (hasAlpha) {
                writer = new PamWriter();
            } else {
                writer = new PpmWriter(useRawbits);
            }
        }

        // make copy of params; we'll clear keys as we consume them.
        if (params != null) {
            params = new HashMap<String, Object>(params);
        } else {
            params = new HashMap<String, Object>();
        }

        // clear format key.
        if (params.containsKey(PARAM_KEY_FORMAT)) {
            params.remove(PARAM_KEY_FORMAT);
        }

        if (params.size() > 0) {
            final Object firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }

        writer.writeImage(src, os, params);
    }

    /**
     * Extracts embedded XML metadata as XML string.
     * <p>
     *
     * @param byteSource File containing image data.
     * @param params     Map of optional parameters, defined in SanselanConstants.
     * @return Xmp Xml as String, if present. Otherwise, returns null.
     */
    @Override
    public String getXmpXml(final ByteSource byteSource, final Map<String, Object> params)
            throws ImageReadException, IOException {
        return null;
    }
}
