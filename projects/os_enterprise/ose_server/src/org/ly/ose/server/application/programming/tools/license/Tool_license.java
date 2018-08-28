package org.ly.ose.server.application.programming.tools.license;

import org.ly.ose.server.application.controllers.license.LicenseController;
import org.ly.ose.server.application.controllers.license.LicenseItem;
import org.ly.ose.server.application.controllers.license.LicenseRegistry;
import org.ly.ose.server.application.programming.OSEProgram;
import org.ly.ose.server.application.programming.tools.OSEProgramToolRequest;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.ext.script.program.engines.javascript.utils.JavascriptConverter;

/**
 *
 */
public class Tool_license
        extends OSEProgramToolRequest {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "license";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _project_uid;
    private int _trial_days;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Tool_license(final OSEProgram program) {
        super(NAME, program);

        _project_uid = MD5.encode(super.info().fullName());
        _trial_days = 0;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    //-- s e t t i n g s  --//

    public String getProjectUid() {
        return _project_uid;
    }

    public void setProjectUid(final String value) {
        _project_uid = value;
    }

    public int getTrialDays() {
        return _trial_days;
    }

    public void setTrialDays(final int value) {
        _trial_days = value;
    }


    //-- m e t h o d s  --//

    public boolean hasLicense(final String uid) {
        return LicenseController.instance().registry(_project_uid).exists(uid);
    }

    public LicenseWrapper getLicense(final String uid) {
        try {
            final LicenseRegistry registry = LicenseController.instance().registry(_project_uid);
            final LicenseItem license = registry.getLicense(uid);
            if (null != license) {
                return new LicenseWrapper(registry, license);
            }
        } catch (Throwable ignored) {
            // problem searching license
        }
        return null;
    }

    public LicenseWrapper createLicense(final String uid,
                                        final String email,
                                        final String name) {
        try {
            final String lang = super.getLang();
            final LicenseRegistry registry = LicenseController.instance().registry(_project_uid);
            registry.trialDays(_trial_days);
            final LicenseItem license = registry.register(uid, email, name, lang);
            if (null != license) {
                return new LicenseWrapper(registry, license);
            }
        } catch (Throwable ignored) {
            // problem creating license
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * L I C E N S E    W R A P P E R
     */
    private static class LicenseWrapper {

        // ------------------------------------------------------------------------
        //                      f i e l d s
        // ------------------------------------------------------------------------

        private final LicenseRegistry _registry;
        private final LicenseItem _item;

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public LicenseWrapper(final LicenseRegistry registry, final LicenseItem item) {
            _registry = registry;
            _item = item;
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public Object getData() {
            try {
                return null != _item ? JavascriptConverter.toScriptObject(_item) : null;
            } catch (Throwable ignored) {
                // ignoed
            }
            return null;
        }

        public String getUid() {
            if (null != _item) {
                return _item.uid();
            }
            return "";
        }


        public boolean getExpired() {
            if (null != _item) {
                return _item.expired();
            }
            return true;
        }

        public boolean getEnabled() {
            if (null != _item) {
                return _item.enabled();
            }
            return false;
        }

        public void setEnabled(final boolean value) {
            if (null != _item) {
                _item.enabled(value);
                _registry.update(_item);
            }
        }

        public String getEmail() {
            if (null != _item) {
                return _item.email();
            }
            return "";
        }

        public void setEmail(final String value) {
            if (null != _item) {
                _item.email(value);
                _registry.update(_item);
            }
        }

        public int getDurationDays() {
            if (null != _item) {
                return _item.durationDays();
            }
            return 0;
        }

        public void postpone(final int days) {
            if (null != _item) {
                _item.postpone(days);
                _registry.update(_item);
            }
        }

        public String getExpirationDate() {
            return _item.getExpirationDate();
        }

        /**
         * Set expiration date
         *
         * @param date yyyyMMdd
         */
        public void setExpirationDate(final String date) {
            _item.expirationDate(date);
            _registry.update(_item);
        }

        public Object attribute(final String name) {
            return _item.attributes().opt(name);
        }

        public void attribute(final String name,
                              final Object value) {
            _item.attributes().put(name, value);
            _registry.update(_item);
        }

    }

}
