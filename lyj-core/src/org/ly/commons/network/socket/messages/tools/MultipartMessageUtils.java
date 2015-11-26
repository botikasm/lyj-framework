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

package org.ly.commons.network.socket.messages.tools;

import org.ly.IConstants;
import org.ly.commons.io.filetokenizer.FileTokenizer;
import org.ly.commons.network.socket.messages.UserToken;
import org.ly.commons.network.socket.messages.multipart.Multipart;
import org.ly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.ly.commons.util.FileUtils;
import org.ly.commons.util.PathUtils;
import org.ly.commons.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Utility methods for multipart messages.
 */
public class MultipartMessageUtils {

    public static final String STORE = PathUtils.getTemporaryDirectory("chunks");

    /**
     * Save single part message on disk (temporary folder) and replace PartName with output file name.
     * If error occurred, PartName is replaced with "NULL" (string) and exception is added to Error field.
     * If par has no data, PartName is replaced with "NULL" (string).
     *
     * @param part Part to save on disk.
     */
    public static long saveOnDisk(final MultipartMessagePart part) {
        if (part.hasData()) {
            try {
                final long length = part.getDataLength();
                final String root = PathUtils.concat(STORE,
                        part.getUid());

                // ensure directory exists
                FileUtils.tryMkdirs(root);

                final String output = PathUtils.concat(root,
                        PathUtils.getFilename(part.getInfo().getPartName()));

                FileUtils.copy(part.getDataBytes(), new File(output));
                part.getInfo().setPartName(output);

                return length;
            } catch (Throwable t) {
                part.getInfo().setPartName(IConstants.NULL);
                part.setError(t);
            } finally {
                part.clearData(); // remove data to free memory on MultipartPool
            }
        } else {
            // no data in this part
            part.getInfo().setPartName(IConstants.NULL);
        }
        return 0;
    }

    /**
     * Join all parts in a single file and remove temporary files.
     *
     * @param multipart Multipart to save on disk
     */
    public static String saveOnDisk(final Multipart multipart,
                                    final String outputPath) throws Exception {
        if (null != multipart && StringUtils.hasText(outputPath) && multipart.isFull()) {
            final Throwable partError = multipart.getError();
            if (null == partError) {
                // ensure directory exists
                FileUtils.tryMkdirs(outputPath);
                // get output filename and join chunks
                final String partName = multipart.getName();
                final String outputFile;
                if (PathUtils.isDirectory(outputPath)) {
                    if (StringUtils.hasText(partName)) {
                        outputFile = PathUtils.concat(outputPath, partName);
                    } else {
                        outputFile = PathUtils.concat(outputPath, "new_file");
                    }
                } else {
                    outputFile = outputPath;
                }
                final String[] names = multipart.getPartNames();
                FileTokenizer.join(names, outputFile, null);
                // remove temp
                FileUtils.delete(PathUtils.getParent(names[0]));

                return outputFile;
            } else {
                try {
                    remove(multipart);
                } catch (Throwable ignored) {
                }
                throw new Exception(partError);
            }
        }
        return "";
    }

    public static void remove(final Multipart multipart) throws IOException {
        final String[] names = multipart.getPartNames();
        if (names.length > 0) {
            FileUtils.delete(names);
            FileUtils.delete(PathUtils.getParent(names[0]));
        }
    }

    public static void setPartBytes(final MultipartMessagePart part) {
        try {
            final UserToken userToken = new UserToken(part.getUserToken());
            final String sourceName = userToken.getSourceAbsolutePath();
            if (FileUtils.exists(sourceName)) {
                final int index = part.getInfo().getIndex();
                final long chunkSize = part.getInfo().getPartLength();
                part.setData(FileUtils.copyToByteArray(new File(sourceName), index * chunkSize, chunkSize));
            }
        } catch (final Throwable t) {
            part.setError(t);
        }
    }
}
