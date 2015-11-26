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
package org.apache.commons.imaging.common.mylzw;

import org.apache.commons.imaging.common.ByteOrder;

import java.io.IOException;
import java.io.InputStream;

public class MyBitInputStream extends InputStream {
    private final InputStream is;
    private final ByteOrder byteOrder;
    private boolean tiffLZWMode = false;

    public MyBitInputStream(final InputStream is, final ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        return readBits(8);
    }

    private long bytesRead = 0;
    private int bitsInCache = 0;
    private int bitCache = 0;

    public void setTiffLZWMode() {
        tiffLZWMode = true;
    }

    public int readBits(final int SampleBits) throws IOException {
        while (bitsInCache < SampleBits) {
            final int next = is.read();

            if (next < 0) {
                if (tiffLZWMode) {
                    // pernicious special case!
                    return 257;
                }
                return -1;
            }

            final int newByte = (0xff & next);

            if (byteOrder == ByteOrder.NETWORK) {
                bitCache = (bitCache << 8) | newByte;
            } else {
                bitCache = (newByte << bitsInCache) | bitCache;
            }

            bytesRead++;
            bitsInCache += 8;
        }
        final int sampleMask = (1 << SampleBits) - 1;

        int sample;

        if (byteOrder == ByteOrder.NETWORK) {
            sample = sampleMask & (bitCache >> (bitsInCache - SampleBits));
        } else {
            sample = sampleMask & bitCache;
            bitCache >>= SampleBits;
        }

        final int result = sample;

        bitsInCache -= SampleBits;
        final int remainderMask = (1 << bitsInCache) - 1;
        bitCache &= remainderMask;

        return result;
    }

    public void flushCache() {
        bitsInCache = 0;
        bitCache = 0;
    }

    public long getBytesRead() {
        return bytesRead;
    }

}
