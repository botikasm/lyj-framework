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

package org.lyj.commons.tokenizers.files;

import org.lyj.commons.tokenizers.IProgressCallback;
import org.lyj.commons.tokenizers.IProgressTokenCallback;
import org.lyj.commons.tokenizers.TokenInfo;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Split and join files or bytes.
 * Files are splitted into multiple files of a defined size and joined again with "join" method.
 */
public class FileTokenizer {

    private static final String CHUNK_EXT = ".chunk";

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    private FileTokenizer() {
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public static String[] splitFile(final String source_filename,
                                     final long chunkSize,
                                     final IProgressCallback progressCallback) throws Exception {
        return splitFile(source_filename, null, chunkSize, progressCallback);
    }

    public static String[] splitFile(final String source_filename,
                                     final String target_folder,
                                     final long chunkSize,
                                     final IProgressCallback progressCallback) throws Exception {
        final File file = new File(source_filename);
        return splitFile(file, target_folder, chunkSize, progressCallback);
    }

    public static String[] splitFile(final File file,
                                     final String target_folder,
                                     final long chunkSize,
                                     final IProgressCallback progressCallback) throws Exception {
        final TokenInfo info = new TokenInfo(file.length(), chunkSize);
        return splitFile(file, target_folder, info, progressCallback);
    }

    public static void split(final File file,
                             final long chunk_size,
                             final IProgressTokenCallback progress_callback) throws Exception {
        split(file, new TokenInfo(file.length(), chunk_size), progress_callback);
    }

    public static void split(final File file,
                             final TokenInfo token_info,
                             final IProgressTokenCallback progress_callback) throws Exception {
        try (final BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
            split(is, token_info, progress_callback);
        }
    }

    public static void split(final byte[] data,
                             final long chunk_size,
                             final IProgressTokenCallback progress_callback) throws Exception {
        try (final ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            split(is, data.length, chunk_size, progress_callback);
        }
    }

    public static void split(final InputStream is,
                                  final long length,
                                  final long chunk_size,
                                  final IProgressTokenCallback progress_callback) throws Exception {
        split(is, new TokenInfo(length, chunk_size), progress_callback);
    }

    public static void split(final InputStream is,
                                  final TokenInfo token_info,
                                  final IProgressTokenCallback progress_callback) throws Exception {
        splitData(is, token_info, progress_callback);
    }

    public static String join(final String[] input_files,
                              final String output_file,
                              final IProgressCallback progress_callback) throws IOException {

        joinFiles(input_files, output_file, progress_callback);

        return output_file;
    }

    public static void append(final byte[] bytes,
                              final String output_file) throws Exception {
        try (final FileOutputStream fos = new FileOutputStream(output_file, true)) {
            appendBytesTo(bytes, fos);
        }
    }
    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static String[] splitFile(final File file,
                                      final String target_folder,
                                      final TokenInfo info,
                                      final IProgressCallback progress_callback) throws Exception {

        final String filename = file.getAbsolutePath();
        final String name = PathUtils.getFilename(filename, true);
        final String root = StringUtils.hasText(target_folder)
                ? PathUtils.concat(PathUtils.getTemporaryDirectory("TOKENIZER/"), target_folder)
                : PathUtils.getTemporaryDirectory("TOKENIZER/");

        // root must exist
        FileUtils.mkdirs(root);

        final Collection<String> names = new LinkedList<>();

        split(file, info, (index, count, progress, bytes) -> {
            final String file_name = PathUtils.concat(root, name + CHUNK_EXT + "_" + index);
            try (final FileOutputStream output = new FileOutputStream(file_name)) {
                output.write(bytes);
                output.flush();
                names.add(file_name);
                if (null != progress_callback) {
                    progress_callback.onProgress(index, count, progress);
                }
            } catch (Throwable ignored) {
                // some error writing file
            }
        });

        return names.toArray(new String[0]);
    }

    private static void splitData(final InputStream is,
                                       final TokenInfo token_info,
                                       final IProgressTokenCallback progress_callback) throws Exception {
        final int chunk_count = token_info.getChunkCount();
        final long chunk_size = token_info.getChunkSize();

        // creates chunks.
        int terminated = 0;
        for (int i = 0; i < chunk_count; i++) {

            // create
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                writeChunk(is, output, i, chunk_size);
                //-- call progress --//
                if (null != progress_callback) {
                    terminated++;
                    double progress = (double) terminated / (double) chunk_count;
                    progress_callback.onProgress(terminated, chunk_count, progress, output.toByteArray());
                }
            }
        }
    }

    private static void writeChunk(final InputStream input,
                                   final OutputStream output,
                                   final long index,
                                   final long chunkSize) throws IOException {

        long offset = index * chunkSize;

        // input.skip(offset);

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
        // ensure output write is complete
        output.flush();
    }

    private static void joinFiles(final String[] filenames,
                                  final String outputFilename,
                                  final IProgressCallback progressCallback) throws IOException {
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

    private static void appendBytesTo(final byte[] bytes,
                                      final OutputStream output) throws Exception {
        try (final ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            byte[] buffer = new byte[32768];
            int read;
            while ((read = is.read(buffer, 0, buffer.length)) > 0) {
                output.write(buffer, 0, read);
            }
            output.flush();
        }
    }

}
