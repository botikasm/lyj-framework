package org.lyj.commons.network.http.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Buffer
 */
public class HttpBuffer extends LinkedList<String> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private List<String> _buffer;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpBuffer() {
        _buffer = new LinkedList<>();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public HttpBuffer write(final String data){
        super.add(data);
        return this;
    }



}
