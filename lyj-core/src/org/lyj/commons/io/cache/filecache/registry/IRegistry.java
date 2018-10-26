package org.lyj.commons.io.cache.filecache.registry;

import java.io.IOException;

public interface IRegistry {

    public enum Mode {

        File((byte) 0),
        Memory((byte) 1);

        private final byte _value;

        private Mode(byte value) {
            _value = value;
        }

        public byte getValue() {
            return _value;
        }

        public static IRegistry.Mode getEnum(byte value) {
            for (IRegistry.Mode v : values())
                if (v.getValue() == value) return v;
            throw new IllegalArgumentException();
        }
    }

    void start();

    void interrupt();

    void join();

    void clear();


    void reloadSettings();

    long getCheck();

    void setCheck(final long value);


    boolean trySave();

    void save() throws IOException;

    boolean has(final String key);

    IRegistryItem get(final String key);

    boolean addItem(final String path, final long duration) throws Exception;

    boolean addItem(final String key, final String path, final long duration) throws Exception;

    boolean removeItem(final IRegistryItem item) throws Exception;

    boolean removeItem(final String key) throws Exception;

    boolean removeItemByPath(final String path) throws Exception;

    String[] removeExpired() throws Exception;

}
