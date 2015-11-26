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
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ComSegment extends Segment {
    public final byte[] comment;

    public ComSegment(final int marker, final byte segmentData[]) throws IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public ComSegment(final int marker, final int marker_length, final InputStream is)
            throws IOException {
        super(marker, marker_length);

        if (getDebug()) {
            System.out.println("ComSegment marker_length: " + marker_length);
        }

        comment = readBytes("Comment", is, marker_length,
                "Error reading JPEG comment");

        if (getDebug()) {
            System.out.println("");
        }
    }

    @Override
    public String getDescription() {
        String commentString = "";
        try {
            commentString = new String(comment, "UTF-8");
        } catch (final UnsupportedEncodingException cannotHappen) {
        }
        return "COM (" + commentString + ")";
    }

}
