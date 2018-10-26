package org.lyj.commons.network.http.client.exceptions;

import org.lyj.commons.util.FormatUtils;

/**
 * Something wrong in request
 */
public class ConnectionException extends Exception {

    public ConnectionException(final int code, final String message) {
        super(FormatUtils.format("Server responded with error '%s': '%s'", code, message));
    }

}
