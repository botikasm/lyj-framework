package org.lyj.commons.network.http.client.exceptions;

/**
 * Something wrong in request
 */
public class BadStateException extends Exception {

    public BadStateException(final String message){
        super(message);
    }

}
