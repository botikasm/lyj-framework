package org.lyj.ext.netty.server.web.handlers.impl.upload;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;

public class UploadFileInfo extends HashMap<String, String> {

    private static final String FLD_STATUS = "status";
    private static final String FLD_LOCALE_ROOT = "local_root";
    private static final String FLD_LOCALE_RELATIVE = "local_relative";
    private static final String FLD_LOCALE_ABSOLUTE = "local_full";
    private static final String FLD_CONTENT_TYPE = "content_type";
    private static final String FLD_CONTENT_LENGTH = "content_length";
    private static final String FLD_ERROR_MESSAGE = "error_message";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public UploadFileInfo() {
        this.status(HttpResponseStatus.CREATED); // default status
    }


    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public HttpResponseStatus status() {
        final String s = super.get(FLD_STATUS);
        return StringUtils.hasText(s) ? HttpResponseStatus.valueOf(ConversionUtils.toInteger(s)) : HttpResponseStatus.CREATED;
    }

    /**
     * Default status is:
     *      HttpResponseStatus.CREATED
     */
    public UploadFileInfo status(final HttpResponseStatus value) {
        if (null != value) {
            this.status(value.codeAsText().toString());
        }

        return this;
    }

    public UploadFileInfo status(final String value) {
        super.put(FLD_STATUS, value);
        return this;
    }

    public String localeRoot() {
        return super.get(FLD_LOCALE_ROOT);
    }

    public UploadFileInfo localeRoot(final String value) {
        super.put(FLD_LOCALE_ROOT, value);
        return this;
    }

    public String localeRelative() {
        return super.get(FLD_LOCALE_RELATIVE);
    }

    public UploadFileInfo localeRelative(final String value) {
        super.put(FLD_LOCALE_RELATIVE, value);
        return this;
    }

    public String localeAbsolute() {
        return super.get(FLD_LOCALE_ABSOLUTE);
    }

    public UploadFileInfo localeAbsolute(final String value) {
        super.put(FLD_LOCALE_ABSOLUTE, value);
        return this;
    }

    public String contentType() {
        return super.get(FLD_CONTENT_TYPE);
    }

    public UploadFileInfo contentType(final String value) {
        super.put(FLD_CONTENT_TYPE, value);
        return this;
    }

    public long contentLength() {
        return ConversionUtils.toLong(super.get(FLD_CONTENT_LENGTH), 0L);
    }

    public UploadFileInfo contentLength(final String value) {
        super.put(FLD_CONTENT_LENGTH, value);
        return this;
    }

    public String errorMessage() {
        return super.get(FLD_ERROR_MESSAGE);
    }

    public UploadFileInfo errorMessage(final String value) {
        super.put(FLD_ERROR_MESSAGE, value);
        return this;
    }

}
