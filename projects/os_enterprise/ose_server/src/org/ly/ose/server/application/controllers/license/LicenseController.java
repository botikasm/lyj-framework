package org.ly.ose.server.application.controllers.license;

import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Multi-Project License Controller.
 * Each project have a project-key and a registry of licences.
 * ex: "drillio_project" has its own license registry containing all drillio-project's licences.
 *
 */
public class LicenseController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./licenses";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, LicenseRegistry> _licenses; // one registry for each license project (project-key, registry)
    private final String _root;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private LicenseController() {
        _licenses = Collections.synchronizedMap(new HashMap<>());
        _root = PathUtils.getAbsolutePath(ROOT);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void refresh() {
        synchronized (_licenses) {
            this.init();

            _licenses.forEach((key, value) -> {
                value.refresh();
            });
        }
    }

    /**
     * Get (or create new) registry for a project key.
     * @param project_key project-key of registry to retrieve or create.
     * @return A Licence Registry
     */
    public LicenseRegistry registry(final String project_key) {
        return this.registry(project_key, true);
    }

    public LicenseRegistry registry(final String project_key,
                                    final boolean auto_add) {
        synchronized (_licenses) {
            LicenseRegistry registry = _licenses.get(project_key);
            if (null == registry && auto_add) {
                final String project_path = PathUtils.combine(_root, project_key);
                FileUtils.tryMkdirs(project_path);
                registry = new LicenseRegistry(project_path);
                _licenses.put(project_key, registry);
            }
            return registry;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        _licenses.clear();
        final File[] projects = FileUtils.listDirs(_root);
        for (final File project : projects) {
            final String project_key = project.getName();
            final String project_path = PathUtils.combine(_root, project_key);
            _licenses.put(project_key, new LicenseRegistry(project_path));
        }
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static LicenseController __instance;

    public static LicenseController instance() {
        if (null == __instance) {
            __instance = new LicenseController();
        }
        return __instance;
    }


}
