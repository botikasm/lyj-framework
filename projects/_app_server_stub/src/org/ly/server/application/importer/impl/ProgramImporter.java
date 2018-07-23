package org.ly.server.application.importer.impl;

import org.ly.server.application.importer.AbstractImporter;
import org.ly.server.application.importer.ImportConfig;
import org.ly.server.application.programming.AppProgramInfo;
import org.ly.server.application.programming.ProgramsManager;
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

    private static final String[] VALID_EXTENSIONS = {"js", "json", "properties"};

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
        // create info for installer
        final AppProgramInfo info = new AppProgramInfo();

        // add properties
        final ImportConfig config = super.config();
        info.name(config.getString("name"));
        info.description(config.getString("description"));
        info.version(config.getString("version"));
        info.author(config.getString("author"));
        info.namespace(config.getString("namespace"));

        // add files
        for (final File file : super.files()) {
            info.files().put(file, PathUtils.subtract(super.root(), file.getPath()));
        }

        ProgramsManager.instance().install(info);
    }

    @Override
    public boolean isValidFile(final File file) {
        final String ext = PathUtils.getFilenameExtensionNotNull(file.getAbsolutePath(), false);
        return CollectionUtils.contains(VALID_EXTENSIONS, ext);
    }

}
