package org.ly.ose.server.application.controllers.license;

import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class LicenseRegistry
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./licenses";
    private static final String LIC_EXT = ".lic";
    private static final int PATH_DETAIL = 3;
    private static final int DEFAULT_TRIAL_DAYS = 0;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;
    private final Map<String, File> _files;

    private int _path_detail;
    private int _trial_days;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LicenseRegistry() {
        this(PathUtils.getAbsolutePath(ROOT));
    }

    public LicenseRegistry(final String root) {
        _root = root;
        _path_detail = PATH_DETAIL;
        _files = Collections.synchronizedMap(new HashMap<>());
        _trial_days = DEFAULT_TRIAL_DAYS;

        this.refresh();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public int pathDetail() {
        return _path_detail;
    }

    public LicenseRegistry pathDetail(final int value) {
        _path_detail = value;
        return this;
    }

    public int trialDays() {
        return _trial_days;
    }

    public LicenseRegistry trialDays(final int value) {
        _trial_days = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void refresh() {
        this.init();
    }

    public boolean exists(final String uid) {
        synchronized (_files) {
            return _files.containsKey(uid);
        }
    }

    public String[] keys() {
        synchronized (_files) {
            return _files.keySet().toArray(new String[0]);
        }
    }

    public LicenseItem getLicense(final String uid) {
        synchronized (_files) {
            if (_files.containsKey(uid)) {
                final File file = _files.get(uid);
                if (file.exists()) {
                    try {
                        return new LicenseItem(FileUtils.readFileToString(file));
                    } catch (Throwable t) {
                        // error reading license
                        super.error("getLicense", t);
                    }
                } else {
                    _files.remove(uid);
                }
            }
            return null;
        }
    }

    public boolean isValidLicense(final String uid) {
        final LicenseItem license = this.getLicense(uid);
        return null != license && license.enabled() && !license.expired();
    }

    /**
     * Get or create new License
     * If trial days are greater than 0, license is enabled otherwise is disabled by default.
     */
    public synchronized LicenseItem register(final String uid,
                                             final String email,
                                             final String name,
                                             final String lang) {
        LicenseItem license = this.getLicense(uid);
        if (null == license) {
            license = new LicenseItem();
            license.uid(uid);
            license.email(email);
            license.name(name);
            license.lang(lang);
            license.durationDays(_trial_days); // trial
            license.enabled(_trial_days > 0);

            this.save(license);
        }
        return license;
    }

    public synchronized LicenseItem update(final String uid,
                                           final String email,
                                           final String name,
                                           final int inc_duration_days) {
        final LicenseItem license = this.getLicense(uid);
        if (null != license) {
            if (StringUtils.hasText(email)) {
                license.email(email);
            }
            if (StringUtils.hasText(name)) {
                license.name(name);
            }
            if (inc_duration_days > 0) {
                license.postpone(inc_duration_days);
            }

            this.save(license);
        }
        return license;
    }

    public synchronized LicenseItem update(final LicenseItem license) {
        if (null != license) {
            this.save(license);
        }
        return license;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

        //-- reload files --//
        _files.clear();
        final List<File> files = new ArrayList<>();
        FileUtils.listFiles(files, new File(_root), "*.lic");
        for (final File file : files) {
            final String uid = PathUtils.getFilename(file.getName(), false);
            _files.put(uid, file);
        }

    }

    private void save(final LicenseItem item) {
        synchronized (_files) {
            if (null != item && StringUtils.hasText(item.uid())) {
                final String uid = item.uid();
                if (!_files.containsKey(uid)) {
                    // add new file
                    final String file_parent = PathUtils.concat(this.root(), PathUtils.getDateTimePath(_path_detail));
                    final String file_name = uid + LIC_EXT;
                    final File file = new File(PathUtils.concat(file_parent, file_name));
                    _files.put(uid, file);
                }
                this.save(item.toString(), _files.get(uid));
            }
        }
    }

    private boolean save(final String content, final File file) {
        try {
            FileUtils.tryMkdirs(file.getAbsolutePath());
            try (final FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes());
                fos.flush();
            }
        } catch (Throwable ignored) {
            // ignored
        }
        return false;
    }
}
