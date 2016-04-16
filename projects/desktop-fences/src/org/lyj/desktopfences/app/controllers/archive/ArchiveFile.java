package org.lyj.desktopfences.app.controllers.archive;

import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.*;
import org.lyj.desktopfences.app.IConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 */
public class ArchiveFile {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ID = IConstants.ID;
    private static final String CRC = "crc";
    private static final String PATH_ORIGIN = "path_origin";    // original path
    private static final String PATH_LOGIC = "path_logic";      // original logic structure
    private static final String PATH_TARGET = "path_target";    // archive path
    private static final String NAME = "name";
    private static final String EXT = "extension";
    private static final String TITLE = "title";
    private static final String SIZE = "size";
    private static final String DIRECTORY = "directory";
    private static final String DATE_LAST_MODIFIED = "date_last_modified";
    private static final String DATE_CREATION = "date_creation";

    private static final String CATEGORY = "category"; // tags for category
    private static final String TAG = "tag"; // custom tags

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MapBuilder<String, Object> _data;
    private File _file;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private ArchiveFile(final Map<String, Object> data) {
        _data = MapBuilder.create(data);
    }

    private ArchiveFile(final File file,
                        final boolean checkCRC) {
        this(new HashMap<String, Object>());

        _file = file;

        this.load(file, checkCRC);
    }

    @Override
    public String toString() {
        return _data.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public ArchiveFile id(final String value) {
        _data.put(ID, value);
        return this;
    }

    public String id() {
        return _data.getString(ID);
    }

    public ArchiveFile crc(final long value) {
        _data.put(CRC, value);
        return this;
    }

    public long crc() {
        return _data.getLong(CRC);
    }

    public ArchiveFile pathOrigin(final String value) {
        _data.put(PATH_ORIGIN, value);
        return this;
    }

    public String pathOrigin() {
        return _data.getString(PATH_ORIGIN);
    }

    public ArchiveFile pathLogic(final String value) {
        _data.put(PATH_LOGIC, value);
        return this;
    }

    public String pathLogic() {
        return _data.getString(PATH_LOGIC);
    }

    public ArchiveFile pathArchive(final String value) {
        _data.put(PATH_TARGET, value);
        return this;
    }

    public String pathArchive() {
        return _data.getString(PATH_TARGET);
    }

    public ArchiveFile name(final String value) {
        _data.put(NAME, value);
        return this;
    }

    public String name() {
        return _data.getString(NAME);
    }

    public ArchiveFile title(final String value) {
        _data.put(TITLE, value);
        return this;
    }

    public String title() {
        return _data.getString(TITLE);
    }

    public ArchiveFile directory(final String value) {
        _data.put(DIRECTORY, value);
        return this;
    }

    public String directory() {
        return _data.getString(DIRECTORY);
    }

    public ArchiveFile ext(final String value) {
        _data.put(EXT, null != value ? value.toLowerCase() : "");
        return this;
    }

    public String ext() {
        return _data.getString(EXT);
    }

    public ArchiveFile size(final long value) {
        _data.put(SIZE, value);
        return this;
    }

    public long size() {
        return _data.getLong(SIZE);
    }

    public ArchiveFile creationDate(final long value) {
        _data.put(DATE_CREATION, value);
        return this;
    }

    public long creationDate() {
        return _data.getLong(DATE_CREATION);
    }

    public Date creationDateAsDate() {
        return new Date(_data.getLong(DATE_CREATION));
    }

    public ArchiveFile lastModified(final long value) {
        _data.put(DATE_LAST_MODIFIED, value);
        return this;
    }

    public long lastModified() {
        return _data.getLong(DATE_LAST_MODIFIED);
    }

    public Date lastModifiedAsDate() {
        return new Date(_data.getLong(DATE_LAST_MODIFIED));
    }

    public ArchiveFile category(final Set<String> value) {
        _data.put(CATEGORY, value);
        return this;
    }

    public Set<String> category() {
        if (!_data.has(CATEGORY)) {
            this.category(new HashSet<>());
        }
        return _data.getSet(CATEGORY);
    }

    public ArchiveFile tag(final Set<String> value) {
        _data.put(TAG, value);
        return this;
    }

    public Set<String> tag() {
        if (!_data.has(TAG)) {
            this.tag(new HashSet<>());
        }
        return _data.getSet(TAG);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Map<String, Object> map() {
        return null != _data ? _data.toMap() : new HashMap<>();
    }

    public boolean isDirectory() {
        return null != _file && _file.isDirectory();
    }

    public void reloadTags() {
        // category
        this.category(this.evalCategoryTags());

    }

    public void addCategory(final String value) {
        this.category().add(value);
    }

    public void addTag(final String value) {
        this.tag().add(value);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void load(final File file,
                      final boolean checkCRC) {
        final long crc = checkCRC ? FileUtils.getCRC(file) : 0; // require much time to read entire file
        this.id(crc > 0 ? MD5.encode(crc + "") : MD5.encode(file.getAbsolutePath()));
        this.crc(crc);
        this.pathOrigin(file.getAbsolutePath());
        this.pathLogic(PathUtils.subtract(PathUtils.getDesktopDirectory(), this.pathOrigin()));
        this.directory(PathUtils.getParent(this.pathLogic()));
        this.name(file.getName());
        this.title(PathUtils.getFilename(file.getName(), false));
        this.ext(PathUtils.getFilenameExtensionNotNull(file.getName(), false));
        this.size(FileUtils.getSize(file));
        this.creationDate(DateUtils.now().getTime());
        this.lastModified(file.lastModified());

        this.category(evalCategoryTags());
    }

    private Set<String> evalCategoryTags() {
        final Set<String> response = new HashSet<>();
        response.add(IConstants.DEFAULT_CATEGORY);

        final String ext = this.ext();
        final Set<String> tags = IConstants.EXT_TAGS.keySet();
        for (final String tag : tags) {
            final List extensions = IConstants.EXT_TAGS.get(tag);
            if (extensions.contains(ext)) {
                response.add(tag);
            }
        }
        return response;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static ArchiveFile create(final Map<String, Object> data) {
        return new ArchiveFile(data); // wrap existing object
    }

    public static ArchiveFile create(final File file,
                                     final boolean chechCRC) throws FileNotFoundException {
        final ArchiveFile response = new ArchiveFile(file, chechCRC);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        return response;
    }

    public static boolean isValid(final File file) {
        if (file.exists()) {
            //-- extension --//
            final String ext = PathUtils.getFilenameExtension(file.getName(), false);
            if (StringUtils.hasText(ext)) {
                if (IConstants.EXCLUDE_EXTENSIONS.contains(ext)) {
                    return false;
                }
            }

            //-- directories --//
            if (file.isDirectory()) {
                final String name = file.getName();
                if (IConstants.EXCLUDE_DIRECTORIES.contains(file.getName())) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

}
