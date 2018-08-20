package org.ly.ose.server.application.controllers.validation;

import org.ly.ose.server.application.controllers.validation.encoders.EncoderMD5;
import org.lyj.commons.util.ClassLoaderUtils;

import java.util.HashMap;
import java.util.Map;

public class ValidationController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Class<? extends AbstractValidator>> _validators;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ValidationController() {
        _validators = new HashMap<>();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void validate(final Map<String, Object> values,
                         final Map<String, String> transform) {
        if (null != transform && null != values) {
            transform.forEach((key, expression) -> {
                if (values.containsKey(key)) {
                    final Object value = values.get(key);
                    try {
                        values.put(key, this.validate(value, expression));
                    } catch (Throwable ignored) {
                        // ignored for a group of values
                    }
                }
            });
        }
    }

    /**
     * Validate or Transform a value
     *
     * @param value      The value
     * @param expression ex: "format|number|#.00"
     * @return Validated value
     * @throws Exception Validation Error
     */
    public Object validate(final Object value,
                           final String expression) throws Exception {
        final AbstractValidator validator = this.getValidator(expression);
        if (null != validator) {
            return validator.validate(value);
        }
        return value;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // add transformers or validators
        _validators.put(EncoderMD5.NAME, EncoderMD5.class);
    }

    /**
     * @param expression ex: "format|number|#.00"
     * @return
     */
    private AbstractValidator getValidator(final String expression) {
        try {
            final String name = AbstractValidator.name(expression);
            if (_validators.containsKey(name)) {
                final Class<? extends AbstractValidator> aclass = _validators.get(name);
                return (AbstractValidator) ClassLoaderUtils.newInstance(aclass, new Object[]{expression});
            }
        } catch (Throwable ignored) {
            // ignored
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static ValidationController __instance;

    public static synchronized ValidationController instance() {
        if (null == __instance) {
            __instance = new ValidationController();
        }
        return __instance;
    }

}
