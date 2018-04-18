package org.lyj.commons.io.cache.filecache.registry;

public interface IRegistryItem {

    IRegistryItem path(final String value);

    String path();

    IRegistryItem uid(final String value);

    String uid();

    long timestamp();

    IRegistryItem duration(final long value);

    long duration();

    boolean expired();

    boolean isDir();

    boolean isFileOrEmptyDir();

}
