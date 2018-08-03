package org.ly.ose.server.application.persistence.debugging.service;


import org.ly.ose.server.application.persistence.BaseService;
import org.ly.ose.server.application.persistence.DBController;
import org.ly.ose.server.application.persistence.debugging.model.ModelLogging;
import org.lyj.commons.Delegates;
import org.lyj.commons.util.MapBuilder;

public class ServiceLogging
        extends BaseService<ModelLogging> {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String COLLECTION = ModelLogging.COLLECTION;

    private static final String FLD_SESSION_ID = ModelLogging.FLD_SESSION_ID;
    private static final String FLD_TIMESTAMP = ModelLogging.FLD_TIMESTAMP;
    private static final String FLD_PROGRAM_NAME = ModelLogging.FLD_PROGRAM_NAME;
    private static final String FLD_LEVEL = ModelLogging.FLD_LEVEL;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ServiceLogging() {
        super(DBController.instance().dbLogging(), COLLECTION, ModelLogging.class);

        //indexes
        super.collection().schema().addIndex(new String[]{FLD_SESSION_ID}, false);
        super.collection().schema().addIndex(new String[]{FLD_TIMESTAMP}, false);
        super.collection().schema().addIndex(new String[]{FLD_PROGRAM_NAME}, false);
        super.collection().schema().addIndex(new String[]{FLD_LEVEL}, false);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void forEach(final Delegates.Callback<ModelLogging> callback) {
        super.collection().forEachDesc(
                new String[]{FLD_TIMESTAMP},
                (item) -> {
                    Delegates.invoke(callback, item);
                    return false;
                }
        );
    }

    public void forEachLevel(final String level,
                             final Delegates.Callback<ModelLogging> callback) {
        super.collection().forEachEqualDesc(
                MapBuilder.createSO().put(FLD_LEVEL, level).toMap(),
                new String[]{FLD_TIMESTAMP},
                (item) -> {
                    Delegates.invoke(callback, item);
                    return false;
                }
        );
    }

    public void forEachProgram(final String program,
                               final Delegates.Callback<ModelLogging> callback) {
        super.collection().forEachEqualDesc(
                MapBuilder.createSO().put(FLD_PROGRAM_NAME, program).toMap(),
                new String[]{FLD_TIMESTAMP},
                (item) -> {
                    Delegates.invoke(callback, item);
                    return false;
                }
        );
    }

    public void forEachProgramLevel(final String program,
                                    final String level,
                                    final Delegates.Callback<ModelLogging> callback) {
        super.collection().forEachEqualDesc(
                MapBuilder.createSO().put(FLD_PROGRAM_NAME, program).put(FLD_LEVEL, level).toMap(),
                new String[]{FLD_TIMESTAMP},
                (item) -> {
                    Delegates.invoke(callback, item);
                    return false;
                }
        );
    }

    @Override
    public ModelLogging upsert(final ModelLogging entity) {
        return super.upsert(entity);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static ServiceLogging __instance;

    public static ServiceLogging instance() {
        if (null == __instance) {
            __instance = new ServiceLogging();
        }
        return __instance;
    }

}
