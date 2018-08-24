package org.ly.ose.server.application.programming;

import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final String ROOT_REGISTRY = PathUtils.concat(ROOT, "_registry");

    private static final String[] PROTECTED_NAMESPACES = IConstants.PROTECTED_NAMESPACES;
    private static final String[] TEXT_FILES = IConstants.TEXT_FILES;

    // ------------------------------------------------------------------------
    //                     f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, OSEProgramInfo> _programs;

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

    public void install(final OSEProgramInfo program_info) throws Exception {
        if (null != program_info && !program_info.files().isEmpty()) {
            // ready to install this program
            this.installProgram(program_info);
        }
    }

    public boolean contains(final String class_name) {
        return _programs.containsKey(class_name);
    }

    public OSEProgramInfo getInfo(final String class_name) {
        if (_programs.containsKey(class_name)) {
            return _programs.get(class_name);
        }
        return null;
    }


    // ------------------------------------------------------------------------
    //                     p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        this.clean();
        this.loadFromRegistry();
        OSEProgramSessions.instance().start();
        OSEProgramInvokerMonitor.instance().open();
    }

    private void finish() {
        OSEProgramSessions.instance().stop();
        OSEProgramInvokerMonitor.instance().close();
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

    private static boolean isProtected(final String namespace) {
        for (final String path : PROTECTED_NAMESPACES) {
            final String dir = PathUtils.concat(ROOT, path);
            if (PathUtils.pathMatch(namespace, dir)
                    || PathUtils.pathMatch(namespace, PathUtils.concat(dir, "*"))) {
                return !FileUtils.isEmptyDir(namespace, true);
            }
        }
        return false;
    }

    private void installProgram(final OSEProgramInfo program_info) throws Exception {
        final String namespace_path = toPath(program_info.namespace());
        final String root = PathUtils.concat(ROOT, namespace_path);
        FileUtils.tryMkdirs(root);

        // check path is not protected namespace
        if (!this.isProtected(root)) {
            // deploy files
            final String program_root = PathUtils.concat(ROOT, namespace_path + "/" + program_info.name());
            final int count = this.deployFiles(program_root, program_info);
            if (count > 0) {
                program_info.installationRoot(program_root);

                // add program to internal registry
                this.addToRegistry(program_info);

                this.info("installProgram", "Installed: " + program_info.toString());
            }
        } else {
            this.warning("installProgram", "Unable to install program into a protected namespace: " + program_info.toString());
        }
    }

    private void addToRegistry(final OSEProgramInfo program_info) throws Exception {

        // add to file system (only not-protected packages)
        if (!isProtected(program_info.installationRoot())) {

            // save new registry file
            final String file_name = program_info.namespace().concat(".").concat(program_info.name()).concat(".dat");
            final String file_path = PathUtils.concat(ROOT_REGISTRY, file_name);
            FileUtils.mkdirs(file_path);
            final byte[] bytes = ByteUtils.getBytes(program_info);
            FileUtils.copy(bytes, new FileOutputStream(new File(file_path)));

            // clear cache
            OSEProgram.clearCache(program_info.installationRoot());

            // remove singleton instance
            if (program_info.singleton()) {
                final OSEProgramInfo old_info = _programs.get(program_info.uid());
                if(null!=old_info){
                    final String program_session_id = (String) old_info.data().get(OSEProgramInfo.FLD_SESSION_ID);
                    if (StringUtils.hasText(program_session_id)) {
                        final OSEProgram program = OSEProgramSessions.instance().remove(program_session_id);
                        if (null != program) {
                            program.close();
                        }
                    }
                }
            }
        }

        // add to memory register
        _programs.put(program_info.uid(), program_info);

    }

    private void loadFromRegistry() {
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(ROOT_REGISTRY), "*.dat");

        for (final File file : files) {
            try {
                final byte[] bytes = FileUtils.copyToByteArray(file);
                final OSEProgramInfo info = (OSEProgramInfo) ByteUtils.getObject(bytes);
                // add to memory register
                _programs.put(info.uid(), info);
            } catch (Throwable t) {

            }
        }

    }

    private String toPath(final String namespace) {
        return StringUtils.replace(namespace, ".", "/");
    }

    private int deployFiles(final String root,
                            final OSEProgramInfo program) {
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
