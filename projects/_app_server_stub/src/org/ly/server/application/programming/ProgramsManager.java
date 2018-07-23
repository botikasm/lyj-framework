package org.ly.server.application.programming;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ProgramsManager
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                     c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = PathUtils.getAbsolutePath("ose_programs");

    private static final String[] PROTECTED_NAMESPACES = new String[]{"system"};
    private static final String[] TEXT_FILES = new String[]{"txt", "json", "js", "html", "properties"};
    // ------------------------------------------------------------------------
    //                     f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, AppProgramInfo> _programs;

    // ------------------------------------------------------------------------
    //                     c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ProgramsManager() {
        _programs = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    public void open() {
        this.init();
    }

    public void close() {
        this.finish();
    }

    public void install(final AppProgramInfo program_info) {
        if (null != program_info && !program_info.files().isEmpty()) {
            // ready to install this program
            this.installProgram(program_info);
        }
    }

    /**
     * Return new instance of a not initialized program
     *
     * @param class_name Full class name. i.e. "system.utils"
     * @return Null or new Program instance (not initialized)
     */
    public AppProgram getNew(final String class_name) {
        if (_programs.containsKey(class_name)) {
            return new AppProgram(_programs.get(class_name));
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.clean();
    }

    private void finish() {
        if (!_programs.isEmpty()) {
            _programs.forEach((key, value) -> {
                try {
                    // value.close();
                } catch (Throwable t) {
                    super.error("finish", t);
                }
            });
        }
        this.clean();
    }

    private void clean() {
        // remove program info
        _programs.clear();

        // clean protected namespaces
        for (final String path : PROTECTED_NAMESPACES) {
            final String dir = PathUtils.concat(ROOT, path);
            FileUtils.tryDelete(dir);
        }
    }

    private boolean isProtected(final String namespace) {
        for (final String path : PROTECTED_NAMESPACES) {
            final String dir = PathUtils.concat(ROOT, path);
            if (PathUtils.pathMatch(namespace, dir)
                    || PathUtils.pathMatch(namespace, PathUtils.concat(dir, "*"))) {
                return !FileUtils.isEmptyDir(namespace, true);
            }
        }
        return false;
    }

    private void installProgram(final AppProgramInfo program_info) {
        final String namespace_path = StringUtils.replace(program_info.namespace(), ".", "/");
        final String root = PathUtils.concat(ROOT, namespace_path);
        FileUtils.tryMkdirs(root);

        // check path is not protected namespace
        if (!this.isProtected(root)) {
            // deploy files
            final String program_root = PathUtils.concat(ROOT, program_info.namespace() + "/" + program_info.name());
            final int count = this.deployFiles(program_root, program_info);
            if (count > 0) {
                program_info.installationRoot(program_root);

                _programs.put(program_info.uid(), program_info);

                this.info("installProgram", "Installed: " + program_info.toString());
            }
        } else {
            this.warning("installProgram", "Unable to install program into a protected namespace: " + program_info.toString());
        }
    }

    private int deployFiles(final String root,
                            final AppProgramInfo program) {
        final Counter counter = new Counter(0);
        program.files().forEach((file, relative_path) -> {
            try {
                final String target_path = PathUtils.concat(root, relative_path);
                FileUtils.tryMkdirs(target_path);

                // read file to get and substitute internal valiables
                if (CollectionUtils.contains(TEXT_FILES, PathUtils.getFilenameExtension(file.getAbsolutePath(), false))) {
                    final String content = FileUtils.readFileToString(file);
                    final String text = FormatUtils.formatTemplate(content, "*|", "|*", program.toMap());
                    FileUtils.writeStringToFile(new File(target_path), text, CharEncoding.getDefault());
                } else {
                    FileUtils.copy(file, new File(target_path));
                }

                counter.inc();
            } catch (Throwable t) {
                super.error("deployFiles", t);
            }
        });
        return counter.valueAsInt();
    }

    // ------------------------------------------------------------------------
    //                     S I N G L E T O N
    // ------------------------------------------------------------------------

    private static ProgramsManager __instance;

    public static synchronized ProgramsManager instance() {
        if (null == __instance) {
            __instance = new ProgramsManager();
        }
        return __instance;
    }


}
