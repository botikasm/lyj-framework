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

package org.lyj.commons.io.filetokenizer;

import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Split file into multiple chunks.
 * Join file from multiple chunks
 */
public class FileTokenizer {

    public static final String CHUNK_EXT = ".chunk";

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    private FileTokenizer() {
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public static String getChunkName(final String filename, final int index){
        final String name = PathUtils.getFilename(filename, true);
        return name + CHUNK_EXT + "_" + index;
    }

    public static String[] splitFromChunkSize(final String filename,
                                              final long chunkSize,
                                              final IFileTokenizerCallback progressCallback) throws Exception {
        return splitFromChunkSize(filename, null, chunkSize, progressCallback);
    }

    public static String[] splitFromChunkSize(final String filename,
                                              final String folder,
                                              final long chunkSize,
                                              final IFileTokenizerCallback progressCallback) throws Exception {
        final File file = new File(filename);
        final FileChunkInfo info = new FileChunkInfo(file.length(), chunkSize);
        return splitFile(file, folder, info, progressCallback);
    }

    public static String join(final String[] fileNames,
                              final String outputFilename,
                              final IFileTokenizerCallback progressCallback) throws IOException {

        joinFiles(fileNames, outputFilename, progressCallback);

        return outputFilename;
    }


    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static String[] splitFile(final File file,
                                      final String folder,
                                      final FileChunkInfo info,
                                      final IFileTokenizerCallback progressCallback) throws Exception {
        final String filename = file.getAbsolutePath();
        final String name = PathUtils.getFilename(filename, true);
        final String root = StringUtils.hasText(folder)
                ? PathUtils.concat(PathUtils.getTemporaryDirectory("TOKENIZER/"), folder)
                : PathUtils.getTemporaryDirectory("TOKENIZER/");

        // root must exist
        FileUtils.mkdirs(root);

        // create file names
        final String[] names = new String[info.getChunkCount()];
        for (int i = 0; i < names.length; i++) {
            names[i] = PathUtils.concat(root, name + CHUNK_EXT + "_" + i);
        }

        // creates chunks.
        int terminated = 0;
        for (int i = 0; i < names.length; i++) {
            // create
            createChunk(filename, names[i], i, info.getChunkSize());
            //-- call progress --//
            if (null != progressCallback) {
                terminated++;
                double progress = (double) terminated / (double) info.getChunkCount();
                progressCallback.onProgress(terminated, info.getChunkCount(), progress);
            }
        }

        return names;
    }

    private static void createChunk(final String inputFilename,
                                    final String outputFilename,
                                    final long index,
                                    final long chunkSize) throws IOException {

        FileOutputStream output = new FileOutputStream(outputFilename);

        FileInputStream input = new FileInputStream(inputFilename);
        long offset = index * chunkSize;
        //long max = offset + chunkSize;

        try {
            input.skip(offset);

            byte[] buffer = new byte[32768];
            long count = 0;
            while (true) {
                long remaining = chunkSize - count;
                int read = input.read(buffer, 0, remaining > buffer.length ? buffer.length : (int) remaining);
                if (read <= 0) break;
                count += read;
                output.write(buffer, 0, read);
                if (count >= chunkSize) break;
            }
        } finally {
            output.flush();
            output.close();
            output = null;

            input.close();
            input = null;
        }
    }

    private static void joinFiles(final String[] filenames,
                                  final String outputFilename,
                                  final IFileTokenizerCallback progressCallback) throws IOException {
        FileUtils.delete(outputFilename);
        FileOutputStream output = new FileOutputStream(outputFilename);
        try {
            //-- read all files one by one and append stream in outputFileName --//
            int terminated = 0;
            for (int i = 0; i < filenames.length; i++) {
                FileInputStream input = new FileInputStream(filenames[i]);
                try {
                    byte[] buffer = new byte[32768];
                    int read;
                    while ((read = input.read(buffer, 0, buffer.length)) > 0) {
                        output.write(buffer, 0, read);
                    }
                } finally {
                    input.close();
                    input = null;
                }

                //-- call progress --//
                if (null != progressCallback) {
                    terminated++;
                    double progress = (double) terminated / (double) filenames.length;
                    progressCallback.onProgress(terminated, filenames.length, progress);
                }
            }
        } finally {
            output.flush();
            output.close();
            output = null;
        }
    }


}
