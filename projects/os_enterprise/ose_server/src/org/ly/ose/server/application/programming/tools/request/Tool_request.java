package org.ly.ose.server.application.programming.tools.request;

import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;

/**
 * Request utilities.
 * $request.client_id;
 * $request.lang;
 */
public class Tool_request
        extends OSEProgramToolRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "request"; // used as $request.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------



    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_request(final OSEProgram program) {
        super(NAME, program);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------




}
