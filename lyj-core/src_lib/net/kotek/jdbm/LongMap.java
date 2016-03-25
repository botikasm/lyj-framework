package net.kotek.jdbm;

import java.util.Iterator;

/**
 * Same as 'java.util.Map' but uses primitive 'long' keys to minimise boxing (and GC) overhead.
 *
 * @author Jan Kotek
 */
public abstract class LongMap<V> {

    /**
     * Removes all mappings from this hash map, leaving it empty.
     *
     * @see #isEmpty
     * @see #size
     */
    public abstract void clear();

    /**
     * Returns the value of the mapping with the specified key.
     *
     * @param key the key.
     * @return the value of the mapping with the specified key, or {@code null}
     *         if no mapping for the specified key is found.
     */
    public abstract V get(long key);

    /**
     * Returns whether this map is empty.
     *
     * @return {@code true} if this map has no elements, {@code false}
     *         otherwise.
     * @see #size()
     */
    public abstract boolean isEmpty();

    /**
     * Maps the specified key to the specified value.
     *
     * @param key   the key.
     * @param value the value.
     * @return the value of any previous mapping with the specified key or
     *         {@code null} if there was no such mapping.
     */
    public abstract V put(long key, V value);


    /**
     * Removes the mapping from this map
     *
     * @param key to remove
     *  @return value contained under this key, or null if value did not exist
     */
    public abstract V remove(long key);

    /**
     * Returns the number of elements in this map.
     *
     * @return the number of elements in this map.
     */
    public abstract int size();


    /**
     * @return iterator over values in map
     */
    public abstract Iterator<V> valuesIterator();

    public abstract LongMapIterator<V> longMapIterator();


    public interface LongMapIterator<V>{
        boolean moveToNext();
        long key();
        V value();
    }

    public String toString(){
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName());
        b.append('[');
        boolean first = true;
        LongMapIterator<V> iter = longMapIterator();
        while(iter.moveToNext()){
            b.append(iter.key());
            b.append(" => ");
            b.append(iter.value());
            if(first){
                first = false;
                b.append(", ");
            }
        }
        b.append(']');
        return b.toString();
    }
}
