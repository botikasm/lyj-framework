package org.ly.ose.server.application.controllers.validation.encoders;

import org.ly.ose.server.application.controllers.validation.AbstractValidator;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

public class EncoderMD5
        extends AbstractValidator {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "md5";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------


    public EncoderMD5(final String expression) {
        super(expression);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public Object validate(final Object value) throws Exception {
        final String text_value = StringUtils.toString(value);
        final boolean force = ConversionUtils.toBoolean(CollectionUtils.get(super.params(), 0));

        return encodePassword(text_value, force);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String encodePassword(final String value) {
        return encodePassword(value, false);
    }

    private static String encodePassword(final String value,
                                        final boolean force) {
        try {
            if (!value.startsWith("data:") || force) {
                return "data:" + MD5.encode(value);
            }
        } catch (Throwable ignored) {

        }
        return value;
    }

}
