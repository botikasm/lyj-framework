package org.lyj.ext.db;

import org.lyj.ext.db.exceptions.DatabaseDoesNotExists;

/**
 * connection to database
 */
public interface IDatabaseConnection {

    IDatabase database(String name) throws DatabaseDoesNotExists;

    boolean open();

    boolean close();

    boolean isOpen();

    String[] databaseNames();

}
