package org.lyj.automator.app;


import org.json.JSONArray;
import org.lyj.Lyj;
import org.lyj.automator.app.controllers.projects.Project;
import org.lyj.automator.app.controllers.projects.ProjectsController;
import org.lyj.commons.async.Async;
import org.lyj.commons.async.future.Task;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.launcher.LyjLauncher;

import java.util.ArrayList;
import java.util.List;

/**
 * Application Server
 */
public class App
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String NAME = "Automator";
    public static final String VERSION = "1.0.1";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private LyjLauncher _launcher;
    private final JsonRepository _config;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public App(final LyjLauncher launcher) {
        _launcher = launcher;
        _config = Lyj.getConfiguration();

        this.getLogger().info(FormatUtils.format("Starting: %s ver. %s", NAME, VERSION));
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void start() throws Exception {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {
        try {
            final List<Task<Object>> execution_tasks = new ArrayList<>();
            final JSONArray arr = _config.getJSONArray("config.autorun");
            CollectionUtils.forEach(arr, (item, key, index) -> {
                if (item instanceof String) {
                    final String name = item.toString();
                    try {
                        final Project proj = ProjectsController.getInstance().getProject(name);
                        if (null != proj && proj.isEnabled()) {
                            execution_tasks.add(proj.run(null));
                        }
                    } catch (Throwable t) {
                        super.error("init#run", FormatUtils.format("Error retrieving project '%s': %s", name, t));
                    }
                }
            });

            Async.joinAll(execution_tasks);

        } catch (Throwable t) {
            super.error("init", t);
        }
    }


}
