package org.ly.commons.network.socket.basic.message.chunks;

import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.tokenizers.files.FileTokenizer;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;

import java.io.*;
import java.util.*;

public class ChunkManager {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, ChunkList> _chunks;
    private final ChunkCache _chunk_cache;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ChunkManager() {
        _chunks = Collections.synchronizedMap(new HashMap<>());
        _chunk_cache = new ChunkCache();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        _chunk_cache.open();
    }

    public void close() {
        _chunk_cache.close();
    }

    /**
     * Add message to internal list and returns TRUE if the list is complete
     *
     * @param message Message to add to internal list
     * @return True if list of chunks is complete
     */
    public boolean add(final SocketMessage message) {
        synchronized (_chunks) {
            final String key = message.headers().chunkUid();
            if (!_chunks.containsKey(key)) {
                _chunks.put(key, new ChunkList(_chunk_cache));
            }
            _chunks.get(key).add(message);

            return _chunks.get(key).isComplete();
        }
    }

    public SocketMessage[] get(final String chunk_uid) {
        synchronized (_chunks) {
            if (_chunks.containsKey(chunk_uid)) {
                return _chunks.get(chunk_uid).toArray();
            }
            return new SocketMessage[0];
        }
    }

    public boolean isComplete(final String chunk_uid) {
        synchronized (_chunks) {
            if (_chunks.containsKey(chunk_uid)) {
                return _chunks.get(chunk_uid).isComplete();
            }
            return false;
        }
    }

    public void remove(final String chunk_uid) {
        synchronized (_chunks) {
            if (_chunks.containsKey(chunk_uid)) {
                _chunks.remove(chunk_uid).toArray();
            }
        }
    }

    public SocketMessage compose(final String chunk_uid) throws Exception {
        // get sorted chunks
        final SocketMessage[] chunks = this.get(chunk_uid); // sorted list

        // clean cached chunks
        this.remove(chunk_uid);

        // prepare a response
        if (chunks.length > 0) {
            final SocketMessage response = new SocketMessage(chunks[0].ownerId());
            // restore original headers
            response.headers().putAll(chunks[0].headers().headers());
            final byte type = response.headers().type();
            // restore original type
            response.type(SocketMessage.MessageType.getEnum(type));
            final boolean has_temp_file = SocketMessage.MessageType.File.equals(response.type())
                    || SocketMessage.MessageType.Binary.equals(response.type());
            final String temp_file_name = has_temp_file ? getTempFileName(response) : "";
            if (has_temp_file) {
                response.headers().fileName(temp_file_name);
                response.body(PathUtils.getFilename(temp_file_name, true));
            }
            // loop 
            for (final SocketMessage chunk : chunks) {
                final int index = chunk.headers().chunkIndex();
                if (has_temp_file) {
                    try (final FileOutputStream fos = new FileOutputStream(temp_file_name, true)) {

                        // get bytes
                        final String cache_key = new String(chunk.body());
                        final byte[] bytes = _chunk_cache.getBytes(cache_key);

                        // append data to file
                        fos.write(bytes);
                        fos.flush();
                    }
                    // response.body(chunk.body(), true);
                } else {
                    // append bytes to body
                    response.body(chunk.body(), true); // append to body
                }
            }

            return response;
        }
        return null;
    }

    public void split(final SocketMessage message,
                      final int chunk_size,
                      final Delegates.Callback<SocketMessage> callback) throws Exception {
        final String uid = RandomUtils.randomUUID(true);
        final SplitInfo info = getSplitInfo(message);
        try (final InputStream is = info.input_stream) {
            FileTokenizer.split(is, info.length, chunk_size, (index, count, progress, bytes) -> {
                try {
                    final SocketMessage token_message = createToken(message, uid, index, count, bytes);
                    Delegates.invoke(callback, token_message);
                } catch (Exception e) {
                    // error sending token
                }
            });
        }
    }

    public Thread[] splitAsync(final SocketMessage message,
                               final int chunk_size,
                               final Delegates.Callback<SocketMessage> callback) throws Exception {
        final Collection<Thread> response = new ArrayList<>();

        final String uid = RandomUtils.randomUUID(true);
        final SplitInfo info = getSplitInfo(message);
        try (final InputStream is = info.input_stream) {
            FileTokenizer.split(is, info.length, chunk_size, (index, count, progress, bytes) -> {
                try {
                    final SocketMessage token_message = createToken(message, uid, index, count, bytes);
                    response.add(Async.wrap(callback, token_message));
                } catch (Exception e) {
                    // error sending token
                }
            });
        }

        return response.toArray(new Thread[0]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String getTempFileName(final SocketMessage message) {
        String response = PathUtils.getTemporaryFile(RandomUtils.randomUUID(true) + ".tmp");
        if (StringUtils.hasText(message.headers().fileName())) {
            final String name = PathUtils.getFilename(message.headers().fileName(), true);
            if (StringUtils.hasText(name)) {
                response = PathUtils.suggestFileName(PathUtils.getTemporaryFile(name), true);
            }
        }
        FileUtils.tryMkdirs(response);
        return response;
    }

    private SocketMessage createToken(final SocketMessage message,
                                      final String uid,
                                      final int index,
                                      final int count,
                                      final byte[] bytes) {
        final SocketMessage token_message = new SocketMessage("");
        token_message.ownerId(message.ownerId(), false); // does not encode
        // signature for encryption
        token_message.signature(message.signature());
        // original data
        token_message.headers().headers().putAll(message.headers().toJson());
        // chunk data
        token_message.type(SocketMessage.MessageType.Chunk);
        token_message.headers().chunkUid(uid);
        token_message.headers().chunkIndex(index);
        token_message.headers().chunkCount(count);

        // write body
        token_message.body(bytes);

        return token_message;
    }

    private SplitInfo getSplitInfo(final SocketMessage message) throws Exception {
        final SplitInfo info = new SplitInfo();
        if (message.type().equals(SocketMessage.MessageType.File)) {
            final File file = new File(message.headers().fileName());
            info.input_stream = new FileInputStream(file);
            info.length = message.headers().fileSize();
        } else {
            info.input_stream = new ByteArrayInputStream(message.body());
            info.length = message.bodyLength();
        }
        return info;
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ChunkManager __instance;

    public static ChunkManager instance() {
        if (null == __instance) {
            __instance = new ChunkManager();
        }
        return __instance;
    }


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    private static class ChunkList {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private final ChunkCache _chunk_cache;
        private final Set<SocketMessage> _data;


        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public ChunkList(final ChunkCache chunk_cache) {
            _chunk_cache = chunk_cache;
            _data = new HashSet<>();
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public void add(final SocketMessage message) {
            if (_chunk_cache.isRunning()) {

                // add bytes to file cache to avoid memory problems
                final String key = message.headers().chunkUid() +
                        "_" + message.headers().chunkIndex() +
                        "_" + message.headers().chunkCount();
                _chunk_cache.put(key, message.body());

                // change message body adding key
                message.body(key);

                // add message to internal store
                _data.add(message);
            }
        }

        public boolean isComplete() {
            if (!_data.isEmpty()) {
                final SocketMessage first = _data.iterator().next();
                return first.headers().chunkCount() == _data.size();
            }
            return false;
        }

        /**
         * Returns sorted array
         */
        public SocketMessage[] toArray() {
            final List<SocketMessage> list = new ArrayList<>(_data);
            list.sort(new Comparator<SocketMessage>() {
                @Override
                public int compare(SocketMessage e1, SocketMessage e2) {
                    return Integer.compare(e1.headers().chunkIndex(), e2.headers().chunkIndex());
                }
            });
            return list.toArray(new SocketMessage[0]);
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------


    }


    private static class SplitInfo {

        InputStream input_stream;
        long length;

    }

}
