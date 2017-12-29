package org.lyj.commons.io.db.filedb.exporter.impl;

import org.lyj.commons.io.db.filedb.FileDBCollection;
import org.lyj.commons.io.db.filedb.FileDBEntity;
import org.lyj.commons.io.db.filedb.exporter.AbstractFileDBExporter;

import java.io.Writer;

/**
 * Export database collections in JSON format
 */
public class FileDBExporterJSON
        extends AbstractFileDBExporter {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String EXTENSION = "json";

    // ------------------------------------------------------------------------
    //                      a b s t r a c t
    // ------------------------------------------------------------------------

    @Override
    protected String extension() {
        return EXTENSION;
    }

    @Override
    protected void export(final FileDBCollection collection,
                          final String target) throws Exception {
        final Writer writer = super.getWriter(target);
        try {
            writer.write("[");
            collection.forEach((final FileDBEntity entity) -> {
                try {
                    if(entity.index()>0){
                        writer.write(",");
                    }
                    writer.write(entity.toString());
                } catch (Throwable ignored) {
                }
                return false;
            });
            writer.write("]");
        } finally {
            writer.flush();
            writer.close();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
