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
package org.apache.commons.imaging.formats.tiff.write;

import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.ByteOrder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TiffImageWriterLossy extends TiffImageWriterBase {

    public TiffImageWriterLossy() {
    }

    public TiffImageWriterLossy(final ByteOrder byteOrder) {
        super(byteOrder);
    }

    @Override
    public void write(final OutputStream os, final TiffOutputSet outputSet)
            throws IOException, ImageWriteException {
        final TiffOutputSummary outputSummary = validateDirectories(outputSet);

        final List<TiffOutputItem> outputItems = outputSet
                .getOutputItems(outputSummary);

        updateOffsetsStep(outputItems);

        outputSummary.updateOffsets(byteOrder);

        final BinaryOutputStream bos = new BinaryOutputStream(os, byteOrder);

        writeStep(bos, outputItems);
    }

    private void updateOffsetsStep(final List<TiffOutputItem> outputItems) {
        int offset = TIFF_HEADER_SIZE;

        for (int i = 0; i < outputItems.size(); i++) {
            final TiffOutputItem outputItem = outputItems.get(i);

            outputItem.setOffset(offset);
            final int itemLength = outputItem.getItemLength();
            offset += itemLength;

            final int remainder = imageDataPaddingLength(itemLength);
            offset += remainder;
        }
    }

    private void writeStep(final BinaryOutputStream bos,
                           final List<TiffOutputItem> outputItems) throws IOException,
            ImageWriteException {
        writeImageFileHeader(bos);

        for (int i = 0; i < outputItems.size(); i++) {
            final TiffOutputItem outputItem = outputItems.get(i);

            outputItem.writeItem(bos);

            final int length = outputItem.getItemLength();

            final int remainder = imageDataPaddingLength(length);
            for (int j = 0; j < remainder; j++) {
                bos.write(0);
            }
        }

    }
}
