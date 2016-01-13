package org.lyj.commons.network.http.client.exceptions;

import org.lyj.commons.util.FormatUtils;

/**
 * Something wrong in request
 */
public class BadStateException extends Exception {

    public BadStateException(final String message){
        super(message);
    }

}
