package org.ly.ose.server.application.programming.tools.persistence;

import org.ly.ose.server.application.persistence.global.model.ModelSession;
import org.ly.ose.server.application.persistence.global.service.ServiceSession;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramTool;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.utils.Converter;

import java.util.Set;

/**
 * Works on "sessions" collection into ose_server GLOBAL database
 * and save/remove all persistent data for a session
 */
public class Tool_session
        extends OSEProgramTool {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "session"; // used as $db.

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String __session_id;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_session(final OSEProgram program) {
        super(NAME, program);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {
        this.drop();
    }

    public String sessionId() {
        if (!StringUtils.hasText(__session_id)) {
            final String program_session = super.sessionId();
            __session_id = StringUtils.hasText(program_session) ? program_session : RandomUtils.randomUUID();
        }
        return __session_id;
    }

    public String getId() {
        return this.sessionId();
    }

    public int getElapsed() {
        return ServiceSession.instance().getOrCreate(this.sessionId()).renew().elapsed();
    }

    /**
     * remove session.
     */
    public void drop() {
        try {
            ServiceSession.instance().removeBySessionId(this.sessionId());
        } catch (Throwable ignored) {
            // ignored
        }
    }

    public void set(final String key, final Object value) {
        final ModelSession entity = ServiceSession.instance().getOrCreate(this.sessionId());
        entity.put(key, Converter.toJsonCompatible(value));
        ServiceSession.instance().upsert(entity);
    }

    public Object get(final String key) {
        final ModelSession entity = ServiceSession.instance().getOrCreate(this.sessionId());
        final Object response = entity.get(key);
        return null != response ? response : false;
    }

    public Set<String> keys(){
        final ModelSession entity = ServiceSession.instance().getOrCreate(this.sessionId());
        return entity.keys();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }


}
