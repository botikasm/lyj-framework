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
package org.apache.commons.imaging.common.bytesource;

import org.apache.commons.imaging.util.Debug;

import java.io.*;

public class ByteSourceFile extends ByteSource {
    private final File file;

    public ByteSourceFile(final File file) {
        super(file.getName());
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        FileInputStream is = null;
        BufferedInputStream bis = null;
        is = new FileInputStream(file);
        bis = new BufferedInputStream(is);
        return bis;
    }

    @Override
    public byte[] getBlock(final long start, final int length) throws IOException {

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");

            // We include a separate check for int overflow.
            if ((start < 0) || (length < 0) || (start + length < 0)
                    || (start + length > raf.length())) {
                throw new IOException("Could not read block (block start: "
                        + start + ", block length: " + length
                        + ", data length: " + raf.length() + ").");
            }

            return getRAFBytes(raf, start, length,
                    "Could not read value from file");
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (final Exception e) {
                Debug.debug(e);
            }

        }
    }

    @Override
    public long getLength() {
        return file.length();
    }

    @Override
    public byte[] getAll() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            is = new BufferedInputStream(is);
            final byte buffer[] = new byte[1024];
            int read;
            while ((read = is.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (final IOException e) {
                Debug.debug(e);
            }
        }
    }

    @Override
    public String getDescription() {
        return "File: '" + file.getAbsolutePath() + "'";
    }

}
