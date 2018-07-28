package org.ly.ose.server.application.persistence.global.service;


import org.ly.ose.server.application.persistence.BaseService;
import org.ly.ose.server.application.persistence.DBController;
import org.ly.ose.server.application.persistence.global.model.ModelSession;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.MapBuilder;

public class ServiceSession
        extends BaseService<ModelSession> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String COLLECTION = ModelSession.COLLECTION;

    private static final String FLD_SESSION_ID = ModelSession.FLD_SESSION_ID;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ServiceSession() {
        super(DBController.instance().db(), COLLECTION, ModelSession.class);

        //indexes
        super.collection().schema().addIndex(new String[]{FLD_SESSION_ID}, true);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void forEach(final Delegates.Callback<ModelSession> callback) {
        super.collection().forEach((item) -> {
            Delegates.invoke(callback, item);
            return false;
        });
    }

    @Override
    public ModelSession upsert(final ModelSession entity) {
        // update timestamp
        entity.renew();
        return super.upsert(entity);
    }

    public ModelSession getOrCreate(final String session_id){
         ModelSession entity = super.collection().findOneEqual(MapBuilder.createSO()
                 .put(FLD_SESSION_ID, session_id).toMap());
         if(null==entity){
            entity = new ModelSession();
            entity.sessionId(session_id);
         }
         return this.upsert(entity);
    }

    public void removeBySessionId(final String session_id) {
        super.collection().removeEqual(MapBuilder.createSO().put(FLD_SESSION_ID, session_id).toMap());
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static ServiceSession __instance;

    public static ServiceSession instance() {
        if (null == __instance) {
            __instance = new ServiceSession();
        }
        return __instance;
    }

}
