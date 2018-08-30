package org.ly.ose.server.application.importer.impl;

import org.ly.ose.server.application.importer.AbstractImporter;
import org.ly.ose.server.application.importer.ImportConfig;
import org.ly.ose.server.application.programming.IConstants;
import org.ly.ose.server.application.programming.OSEProgramInfo;
import org.ly.ose.server.application.programming.ProgramsManager;
import org.ly.ose.server.deploy.config.ConfigHelper;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.util.Collection;

public class ProgramImporter
        extends AbstractImporter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String TYPE = "program";

    private static final String[] VALID_EXTENSIONS = IConstants.VALID_EXTENSIONS;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ProgramImporter(final Collection<File> files) {
        super(files);
        if (super.isReady()) {
            // ready to configure
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void startImport() {
        try {
            // create info for installer
            final OSEProgramInfo info = new OSEProgramInfo();

            // add properties
            final ImportConfig config = super.config();
            info.name(config.getString("name"));
            info.description(config.getString("description"));
            info.version(config.getString("version"));
            info.author(config.getString("author"));
            info.namespace(config.getString("namespace"));
            info.loopInterval(config.getInt("loop_interval"));      // interval for main loop
            info.sessionTimeout(config.getInt("session_timeout"));  // session duration
            info.singleton(config.getBoolean("singleton", false));
            info.logLevel(config.getString("log_level"));
            info.autostart(config.getBoolean("autostart"));

            // add files
            for (final File file : super.files()) {
                info.files().put(file, PathUtils.subtract(super.root(), file.getPath()));
            }

            // add further params
            info.apiHost(ConfigHelper.instance().apiHost());

            ProgramsManager.instance().install(info);
        }catch(Throwable t){
            super.error("startImport", t);
        }
    }

    @Override
    public boolean isValidFile(final File file) {
        final String ext = PathUtils.getFilenameExtensionNotNull(file.getAbsolutePath(), false);
        return CollectionUtils.contains(VALID_EXTENSIONS, ext);
    }

}
