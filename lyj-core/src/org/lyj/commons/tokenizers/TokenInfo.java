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

package org.lyj.commons.tokenizers;

/**
 * Info descriptor for tokens
 */
public class TokenInfo {

    public static final int MAX_CHUNKS = -1;  // no token limits
    public static final long DEFAULT_CHUNK_SIZE = 1024 * 5 * 1000; //5Mb
    public static final int DEFAULT_CHUNK_COUNT = 0;

    private long _total_size;
    private long _chunk_size;
    private int _chunk_count;
    private int _max_chunks;
    private long[] _chunk_offsets;


    //-----------------------------------------------
    //             c o n s t r u c t o r
    //-----------------------------------------------

    public TokenInfo(long total_size) throws Exception {
        this(total_size, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_COUNT, MAX_CHUNKS);
    }

    public TokenInfo(long total_size, long chunk_size) throws Exception {
        this(total_size, chunk_size, DEFAULT_CHUNK_COUNT, MAX_CHUNKS);
    }

    public TokenInfo(long total_size, long chunkSize, int maxChunks) throws Exception {
        this(total_size, chunkSize, DEFAULT_CHUNK_COUNT, maxChunks);
    }

    public TokenInfo(long total_size, int chunkCount) throws Exception {
        this(total_size, DEFAULT_CHUNK_SIZE, chunkCount, MAX_CHUNKS);
    }

    public TokenInfo(long total_size, long chunkSize, int chunkCount, int maxChunks) throws Exception {
        _total_size = total_size;
        _chunk_size = chunkSize;
        _max_chunks = maxChunks;
        _chunk_count = chunkCount;

        this.recalculate();
    }

    //-----------------------------------------------
    //             p r o p e r t i e s
    //-----------------------------------------------

    /**
     * Size of every chunk
     *
     * @return Size of chunk
     */
    public long getChunkSize() {
        return _chunk_size;
    }

    /**
     * NUmber of Chunks
     *
     * @return Number of Chunks
     */
    public int getChunkCount() {
        return _chunk_count;
    }

    /**
     * Offsets array.
     *
     * @return Array with offsets
     */
    public long[] getChunkOffsets() {
        return null != _chunk_offsets ? _chunk_offsets : new long[0];
    }

    //-----------------------------------------------
    //             p r i v a t e
    //-----------------------------------------------

    private void recalculate() throws Exception {
        if (_chunk_size > 0) {
            // calculate by chunk size
            _chunk_count = (int) Math.ceil((double) ((double) _total_size / (double) _chunk_size));
            if (_chunk_count > _max_chunks && _max_chunks > 0) {
                _chunk_count = _max_chunks;
                _chunk_size = (long) Math.ceil((double) ((double) _total_size / (double) _max_chunks));
            }
        } else if (_chunk_count > 0) {
            // calculate by chunk count
            _chunk_size = (long) Math.floor((double) (_total_size / _chunk_count));
        }

        // verify
        long check = (_chunk_count * _chunk_size);
        if (_total_size > check) {
            throw new Exception("Invalid or incoherent parameters!");
        }

        // generate chunks
        _chunk_offsets = new long[_chunk_count];
        for (int i = 0; i < _chunk_count; i++) {
            long offset = i * _chunk_size;
            _chunk_offsets[i] = offset;
        }
    }

}
