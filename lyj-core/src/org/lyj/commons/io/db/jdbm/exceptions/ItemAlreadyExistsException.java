package org.lyj.commons.io.db.jdbm.exceptions;


import org.lyj.commons.util.FormatUtils;

/**
 *
 */
public class ItemAlreadyExistsException extends Exception {

    public ItemAlreadyExistsException(final Object itemId, final String collection){
        super(FormatUtils.format("Item '%s' already exists in collection '%s'", itemId, collection));
    }

}
