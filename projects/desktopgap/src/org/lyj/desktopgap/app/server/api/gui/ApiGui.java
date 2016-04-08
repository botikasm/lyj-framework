package org.lyj.desktopgap.app.server.api.gui;

import org.json.JSONArray;
import org.lyj.commons.io.FileWrapper;
import org.lyj.desktopgap.app.view.filechooser.FileChooserHelper;
import org.lyj.ext.netty.server.web.controllers.routing.RoutingContext;

import java.io.File;
import java.util.List;

/**
 * Connection
 */
public class ApiGui {

    public static void fileChooser(final RoutingContext context) {
        try {
            final FileChooserHelper chooser = FileChooserHelper.instance();
            final List<File> files = chooser.open();
            final JSONArray response = new JSONArray();
            for (final File file : files) {
                response.put(new FileWrapper(file));
            }
            context.write(response.toString());
        } catch (Throwable t) {
            context.writeJsonError(t);
        }
    }

}
