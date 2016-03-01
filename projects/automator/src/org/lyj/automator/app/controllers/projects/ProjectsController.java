package org.lyj.automator.app.controllers.projects;

import org.lyj.Lyj;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.util.*;

/**
 * Main controller for projects
 */
public final class ProjectsController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String PATH = "projects";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final Map<String, File> _projects;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ProjectsController() {
        _root = Lyj.getAbsolutePath(PATH);
        _projects = new HashMap<>();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String path() {
        return _root;
    }

    public void reload() {
        this.init();
    }

    public Set<String> getProjectNames() {
        return _projects.keySet();
    }

    public Project getProject(final String name) throws Exception {
        // try refresh data
        if (_projects.size() == 0) {
            this.reload();
        }

        final String text = this.readFile(_projects.get(name));
        if (StringUtils.isJSON(text)) {
            return new Project(text).setName(name);
        }
        return new Project().setName(name);
    }

    public void saveProject(final Project project) {
        this.saveProject(project.getName(), project.json().toString());
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // load projects
        _projects.clear();
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(_root), "*.json");
        for (final File file : files) {
            try {
                final String text = this.readFile(file);
                if (StringUtils.isJSON(text)) {
                    final String name = PathUtils.getFilename(file.getName(), false);
                    _projects.put(name, file);
                }
            } catch (Throwable t) {
                super.error("init", t);
            }
        }
    }

    private String readFile(final File file) {
        if (null != file) {
            try {
                final String text = FileUtils.readFileToString(file, CharEncoding.getDefault());
                if (StringUtils.isJSON(text)) {
                    return text;
                } else {
                    super.warning("readFile", FormatUtils.format("Invalid project file: '%s'", file.getName()));
                }
            } catch (Throwable t) {
                super.error("readFile", t);
            }
        }
        return "";
    }

    private void saveProject(final String name, final String json) {
        try {
            if (StringUtils.hasText(name) && StringUtils.hasText(json)) {
                final String path = PathUtils.concat(_root, name + ".json");
                FileUtils.writeStringToFile(new File(path), json, CharEncoding.getDefault());
            }
        } catch (Throwable t) {
            super.error("saveProject", t);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ProjectsController __instance;

    public static ProjectsController getInstance() {
        if (null == __instance) {
            __instance = new ProjectsController();
        }
        return __instance;
    }

}
