package org.lyj.commons.network.http.client.exceptions;

import org.lyj.commons.util.FormatUtils;

/**
 * Method is not supported
 */
public class UnsupportedMethodException extends Exception {

    public UnsupportedMethodException(final String method){
        super(FormatUtils.format("Method not supported: '%s'", method));
    }

}
