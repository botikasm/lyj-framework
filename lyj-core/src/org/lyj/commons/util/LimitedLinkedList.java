package org.lyj.commons.util;

import sun.util.resources.cldr.vai.CalendarData_vai_Latn_LR;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 */
public class LimitedLinkedList<T>
        extends LinkedList<T> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private int _max_size;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LimitedLinkedList() {
        super();
        _max_size = -1; // no limits
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public int maxSize() {
        return _max_size;
    }

    public LimitedLinkedList<T> maxSize(final int value) {
        _max_size = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public boolean add(T t) {
        final boolean response = super.add(t);
        this.shrink();
        return response;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        this.shrink();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        final boolean response = super.addAll(c);
        this.shrink();
        return response;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        final boolean response = super.addAll(index, c);
        this.shrink();
        return response;
    }

    @Override
    public void addFirst(T t) {
        super.addFirst(t);
        this.shrink();
    }

    @Override
    public void addLast(T t) {
        super.addLast(t);
        this.shrink();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void shrink() {
        synchronized (this) {
            if (_max_size > 0 && this.size() > _max_size) {
                while (this.size() > _max_size) {
                    try {
                        this.remove(0);
                    }catch(Throwable ignored){
                        break;
                    }
                }
            }
        }
    }


}
