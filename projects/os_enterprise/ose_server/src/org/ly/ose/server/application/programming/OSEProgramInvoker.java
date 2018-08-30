package org.ly.ose.server.application.programming;

import org.json.JSONArray;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.lyj.commons.util.*;
import org.lyj.ext.script.utils.Converter;

import java.io.File;
import java.util.Collection;

/**
 * Invoke a program and return converted value to OSEResponse.
 * Not converted:
 * - File
 * - Exception
 * - HTML
 * - XML
 */
public class OSEProgramInvoker {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final Object SYNCHRONIZER = new Object(); // empty static object

    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private OSEProgramInvoker() {

    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    public boolean isMicroservice(final String namespace) {
        final OSEProgramInfo info = ProgramsManager.instance().getInfo(getClassName(namespace));
        if (null != info) {
            return info.microservice();
        }
        return false;
    }

    public Object callMember(final OSERequest request,
                             final String client_session_id,
                             final long session_timeout,
                             final String namespace,
                             final String function,
                             final Collection<Object> payload_params) throws Exception {
        final String class_name = getClassName(namespace);
        final OSEProgramInfo info = ProgramsManager.instance().getInfo(class_name);
        if (null == info) {
            throw new Exception(FormatUtils.format("Invalid Program Exception: Namespace '%s' not found!",
                    namespace));
        }
        return this.callMember(request, client_session_id, session_timeout, info, function, payload_params);
    }

    public Object callMember(final OSERequest request,
                             final String client_session_id,
                             final long session_timeout,
                             final OSEProgramInfo info,
                             final String function,
                             final Collection<Object> payload_params) throws Exception {
        if (null == info) {
            throw new Exception("Invalid ProgramInfo Exception: info cannot be NULL");
        }

        // get program
        final OSEProgram program = this.get(info, client_session_id, session_timeout);
        if (null == program) {
            throw new Exception(FormatUtils.format("Invalid Program Exception: Namespace '%s' not found!",
                    info.className()));
        }

        return this.callMember(request, program, function, payload_params);
    }

    public Object callMember(final OSERequest request,
                             final OSEProgram program,
                             final String function,
                             final Collection<Object> payload_params) throws Exception {

        final Object[] params = CollectionUtils.isEmpty(payload_params)
                ? null
                : payload_params.toArray(new Object[0]);

        return this.callMember(request, program, function, params);
    }

    public Object callMember(final OSERequest request,
                             final OSEProgram program,
                             final String function,
                             final Object[] params) throws Exception {
        // call required function
        try {
            // syncronized invoke for singletons
            final Object program_result = callMemberSync(request, program, function, params);
            if (program_result instanceof JSONArray) {
                final OSEResponse response = OSERequest.generateResponse(request);
                response.payload((JSONArray) program_result);
                return response;
            } else {
                // special response
                return program_result;
            }
        } finally {
            // close program if not in session
            final String program_session_id = (String) program.info().data().get(OSEProgramInfo.FLD_SESSION_ID);
            if (!StringUtils.hasText(program_session_id)
                    || !OSEProgramSessions.instance().containsKey(program_session_id)) {
                program.close();
            }
        }
    }

    public synchronized void autostartSingleton(final OSEProgramInfo info) throws Exception {
        if (null != info && info.autostart()) {
            final String session_id = getSingletonSessionId(info);
            // ensure program is not stillin session
            if (!OSEProgramSessions.instance().containsKey(session_id)) {
                final OSEProgram program = this.get(info, session_id, 0);
                if (null == program) {
                    // something wrong in program creation
                    throw new Exception("Abnormal program state for: " + info.toString());
                }
            }
        }
    }
    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------


    private Object callMemberSync(final OSERequest request,
                                  final OSEProgram program,
                                  final String function,
                                  final Object[] params) throws Exception {
        if (null != program) {
            if (program.info().singleton()) {
                synchronized (SYNCHRONIZER) {
                    program.request(request);
                    final Object result = null != params
                            ? program.callMember(function, params)
                            : program.callMember(function);
                    return convert(result);
                }
            } else {
                program.request(request);
                final Object result = null != params
                        ? program.callMember(function, params)
                        : program.callMember(function);
                return convert(result);
            }
        }
        return new JSONArray();
    }

    private static Object convert(final Object result) throws Exception {
        if (result instanceof File) {
            // FILE
            return result;
        } else if (result instanceof Exception) {
            // EXCEPTION
            throw (Exception) result;
        } else if (StringUtils.isHTML(result)) {
            // HTML
            return result;
        } else if (StringUtils.isXml(result)) {
            // XML
            return result;
        }
        return Converter.toJsonArray(result);
    }

    /**
     * Get or Create a Program
     *
     * @param namespace         Dot separated full name. ex: "ai.nlp.ventis"
     * @param client_session_id Optional Client ID
     * @param session_timeout   Optional session timeout
     */
    private OSEProgram get(final String namespace,
                           final String client_session_id,
                           final long session_timeout) throws Exception {
        final String class_name = StringUtils.replace(namespace, "_", ".");
        final OSEProgramInfo info = ProgramsManager.instance().getInfo(class_name);
        if (null == info) {
            throw new Exception(FormatUtils.format("Invalid Program Exception: Namespace '%s' not found!",
                    namespace));
        }
        return this.get(info, client_session_id, session_timeout);
    }

    private OSEProgram get(final OSEProgramInfo info,
                           final String client_session_id,
                           final long session_timeout) throws Exception {
        if (null != info) {
            // init session and timeout
            final long program_timeout;
            final String program_session_id;
            if (info.singleton()) {
                program_timeout = DateUtils.infinite().getTime(); // INFINITE (never expires)
                program_session_id = getSingletonSessionId(info);
            } else {
                program_timeout = session_timeout > -1 ? session_timeout : info.sessionTimeout();
                program_session_id = StringUtils.hasText(client_session_id) ? client_session_id : RandomUtils.randomUUID();
            }

            final OSEProgram program;
            if (StringUtils.hasText(program_session_id)) {
                if (OSEProgramSessions.instance().containsKey(program_session_id)) {
                    program = OSEProgramSessions.instance().get(program_session_id);
                } else {
                    // new program
                    program = new OSEProgram(info);

                    // add some advanced info to program
                    program.info().data().put(OSEProgramInfo.FLD_CLIENT_ID, client_session_id); // set real session ID
                    program.info().data().put(OSEProgramInfo.FLD_SESSION_ID, program_session_id);

                    // add program to session manager and initialize
                    OSEProgramSessions.instance().put(program_session_id, program, program_timeout);
                }

            } else {
                // no session
                program = new OSEProgram(info);
            }

            return program;
        } else {
            throw new Exception("Invalid ProgramInfo Exception: info cannot be NULL");
        }
    }

    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static String getSingletonSessionId(final OSEProgramInfo info) {
        return "singleton_" + info.fullName();
    }

    private static String getClassName(final String namespace) {
        return StringUtils.replace(namespace, "_", ".");
    }

    // ------------------------------------------------------------------------
    //                     S I N G L E T O N
    // ------------------------------------------------------------------------

    private static OSEProgramInvoker __instance;

    public static synchronized OSEProgramInvoker instance() {
        if (null == __instance) {
            __instance = new OSEProgramInvoker();
        }
        return __instance;
    }

}
