package org.lyj.ext.db.model;

import org.lyj.commons.util.converters.MapConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 */
public class MapList
        extends LinkedList<Object> {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MapList() {

    }

    public MapList(final Collection<Object> items) {
        super(null != items ? items : new ArrayList<>());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean add(final Object element) {
        return super.add(MapConverter.convert(element));
    }

    @Override
    public void addFirst(final Object element) {
        super.addFirst(MapConverter.convert(element));
    }

    @Override
    public void addLast(final Object element) {
        super.addLast(MapConverter.convert(element));
    }

    @Override
    public void add(final int index, final Object element) {
        super.add(index, MapConverter.convert(element));
    }

    @Override
    public boolean addAll(final Collection<? extends Object> c) {
        boolean changed = false;
        for (final Object item : c) {
            if (!this.contains(item)) {
                this.add(item);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Object> c) {
        boolean changed = false;
        for (final Object item : c) {
            if (!this.contains(item)) {
                this.add(index, item);
                changed = true;
            }
        }
        return changed;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
