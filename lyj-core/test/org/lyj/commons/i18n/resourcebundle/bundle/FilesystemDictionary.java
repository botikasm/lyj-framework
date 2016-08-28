package org.lyj.commons.i18n.resourcebundle.bundle;

import org.lyj.commons.i18n.resourcebundle.FileI18nBundle;

/**
 *
 */
public class FilesystemDictionary
        extends FileI18nBundle {


    public FilesystemDictionary(final String root) {
        super(root);
    }

    @Override
    public String getName() {
        return "i18n";
    }

}
