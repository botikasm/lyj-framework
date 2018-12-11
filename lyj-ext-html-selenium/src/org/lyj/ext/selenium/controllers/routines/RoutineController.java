package org.lyj.ext.selenium.controllers.routines;

import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.selenium.IConstants;
import org.lyj.ext.selenium.controllers.SeleniumManager;
import org.lyj.ext.selenium.controllers.routines.controller.RoutineAsyncExecutor;
import org.lyj.ext.selenium.controllers.routines.model.ModelPackage;

import java.io.File;
import java.util.*;

public class RoutineController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH = IConstants.PATH_ROUTINES;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _selenium_root;
    private final List<ModelPackage> _list; // list of script packages to run
    private final Set<RoutineAsyncExecutor> _running_tasks;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RoutineController(final SeleniumManager parent) {
        _selenium_root = parent.root();
        _list = Collections.synchronizedList(new ArrayList<>());
        _running_tasks = new HashSet<>();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        this.run(); // auto-run all enabled scripts
    }

    public void close() {
        synchronized (_running_tasks) {
            for (final RoutineAsyncExecutor exec : _running_tasks) {
                try {
                    exec.stop();
                } catch (Throwable ignored) {
                    // ignored
                }
            }
            _running_tasks.clear();
        }
    }

    public void register(final File package_file) {
        try {
            final String json = FileUtils.readFileToString(package_file);
            if (StringUtils.isJSONObject(json)) {
                final ModelPackage model = new ModelPackage(json);
                this.register(model);
            }
        } catch (Throwable ignored) {

        }
    }

    public void run() {
        synchronized (_list) {
            for (final ModelPackage item : _list) {
                if (item.enabled()) {
                    this.run(item);
                }
            }
        }
    }

    public void run(final String name) {
        synchronized (_list) {
            for (final ModelPackage item : _list) {
                if (item.enabled() && item.name().equalsIgnoreCase(name)) {
                    this.run(item);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(PATH), "package.json");
        for (final File file : files) {
            this.register(file);
        }
    }

    private void register(final ModelPackage model) {
        synchronized (_list) {
            _list.add(model);
        }
    }

    private void run(final ModelPackage model) {
        synchronized (_running_tasks) {
            final RoutineAsyncExecutor exec = new RoutineAsyncExecutor(_selenium_root, model);
            exec.run(this::taskTerminated);
            _running_tasks.add(exec);
        }
    }

    private void taskTerminated(final RoutineAsyncExecutor exec) {
        synchronized (_running_tasks) {
            _running_tasks.remove(exec);
        }
    }

}
