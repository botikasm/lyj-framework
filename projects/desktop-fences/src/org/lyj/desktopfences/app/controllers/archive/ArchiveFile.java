package org.lyj.desktopfences.app.controllers.archive;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.*;
import org.lyj.desktopfences.app.IConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ArchiveFile {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ID = IConstants.ID;
    private static final String ORIGIN = "origin";
    private static final String NAME = "name";
    private static final String EXT = "ext";
    private static final String TITLE = "title";
    private static final String SIZE = "size";
    private static final String DATE_LAST_MODIFIED = "date_last_modified";
    private static final String DATE_CREATION = "date_creation";

    private static final String CATEGORY = "category"; // tags for category
    private static final String TAG = "tag"; // custom tags

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonWrapper _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ArchiveFile(final JSONObject data) {
        _data = new JsonWrapper(data);
    }

    private ArchiveFile(final File file) {
        this(new JSONObject());
        this.load(file);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public ArchiveFile id(final String value) {
        _data.put(ID, value);
        return this;
    }

    public String id() {
        return _data.optString(ID);
    }

    public ArchiveFile origin(final String value) {
        _data.put(ORIGIN, value);
        return this;
    }

    public String origin() {
        return _data.optString(ORIGIN);
    }

    public ArchiveFile name(final String value) {
        _data.put(NAME, value);
        return this;
    }

    public String name() {
        return _data.optString(NAME);
    }

    public ArchiveFile title(final String value) {
        _data.put(TITLE, value);
        return this;
    }

    public String title() {
        return _data.optString(TITLE);
    }

    public ArchiveFile ext(final String value) {
        _data.put(EXT, value.toLowerCase());
        return this;
    }

    public String ext() {
        return _data.optString(EXT);
    }

    public ArchiveFile size(final long value) {
        _data.put(SIZE, value);
        return this;
    }

    public long size() {
        return _data.optLong(SIZE);
    }

    public ArchiveFile creationDate(final long value) {
        _data.put(DATE_CREATION, value);
        return this;
    }

    public long creationDate() {
        return _data.optLong(DATE_CREATION);
    }

    public Date creationDateAsDate() {
        return new Date(_data.optLong(DATE_CREATION));
    }

    public ArchiveFile lastModified(final long value) {
        _data.put(DATE_LAST_MODIFIED, value);
        return this;
    }

    public long lastModified() {
        return _data.optLong(DATE_LAST_MODIFIED);
    }

    public Date lastModifiedAsDate() {
        return new Date(_data.optLong(DATE_LAST_MODIFIED));
    }

    public ArchiveFile category(final JSONArray value) {
        _data.put(CATEGORY, value);
        return this;
    }

    public JSONArray category() {
        return _data.optJSONArray(CATEGORY);
    }

    public ArchiveFile tag(final JSONArray value) {
        _data.put(TAG, value);
        return this;
    }

    public JSONArray tag() {
        if(!_data.has(TAG)){
            this.tag(new JSONArray());
        }
        return _data.optJSONArray(TAG);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void reloadTags() {
        // category
        this.category(this.evalCategoryTags());

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void load(final File file) {
        this.id(FileUtils.getCRC(file) + "");
        this.origin(file.getAbsolutePath());
        this.name(file.getName());
        this.title(PathUtils.getFilename(file.getName(), false));
        this.ext(PathUtils.getFilenameExtension(file.getName(), false));
        this.size(FileUtils.getSize(file));
        this.creationDate(DateUtils.now().getTime());
        this.lastModified(file.lastModified());

        this.category(evalCategoryTags());
    }

    private JSONArray evalCategoryTags() {
        final JSONArray response = new JSONArray();
        response.put(IConstants.DEFAULT_CATEGORY);

        final String ext = this.ext();
        final Set<String> tags = IConstants.EXT_TAGS.keySet();
        for (final String tag : tags) {
            final List extensions = IConstants.EXT_TAGS.get(tag);
            if (extensions.contains(ext)) {
                response.put(tag);
            }
        }
        return response;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static ArchiveFile create(final JSONObject data) {
        return new ArchiveFile(data); // wrap existing object
    }

    public static ArchiveFile create(final File file) throws FileNotFoundException {
        final ArchiveFile response = new ArchiveFile(file);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        return response;
    }

    public static boolean isValid(final File file) {
        if (file.exists()) {
            final String ext = PathUtils.getFilenameExtension(file.getName(), false);
            if (!IConstants.EXCLUDE.contains(ext)) {
                return true;
            }
        }
        return false;
    }

}
