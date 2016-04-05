package org.lyj.desktopfences.app.client.api;

import org.json.JSONObject;
import org.lyj.desktopfences.app.DesktopFences;
import org.lyj.desktopfences.app.IConstants;
import org.lyj.desktopfences.app.controllers.archive.ArchiveController;
import org.lyj.ext.netty.server.web.controllers.routing.RoutingContext;

/**
 * Utility client API
 */
public class ApiUtils {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public static void version (final RoutingContext context) {
        context.writeJson(IConstants.VERSION);
    }

    public static void activityReport (final RoutingContext context) {
        try {
            final boolean reload = context.params().getBoolean("reload");

            final JSONObject response = new JSONObject();
            response.put("threadsCount", ArchiveController.instance().countCompletedTasks());
            response.put("threadsActive", ArchiveController.instance().countActiveTasks());
            response.put("filesArchived", ArchiveController.instance().countArchived());
            response.put("filesProcessed", ArchiveController.instance().countProcessed());
            response.put("filesInArchive", ArchiveController.instance().countInArchive());
            response.put("tags", ArchiveController.instance().tags().json(false));
            response.put("categories", ArchiveController.instance().categories().json(false));
            response.put("directories", ArchiveController.instance().directories().json(false));
            context.writeJson(response);

            if(reload){
                ArchiveController.instance().reloadIndexes();
            }
        }catch(Throwable t){
            context.writeJsonError(t);
        }
    }

    public static void settings (final RoutingContext context) {
        try {
            context.writeJson(DesktopFences.instance().settings().toJson());
        }catch(Throwable t){
            context.writeJsonError(t);
        }
    }

}
