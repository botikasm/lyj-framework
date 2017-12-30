package org.lyj.commons.io.db.filedb;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Locker;
import org.lyj.commons.cryptograph.GUID;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.lang.Counter;
import org.lyj.commons.lang.ValueObject;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonItem;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FileDBCollection {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String KEY = IFileDBConstants.KEY; // "_key"
    private static final String ENCODING = CharEncoding.UTF_8;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final FileDB _db;
    private final String _file_path;
    private final String _name; // collection name
    private final Set<String> _field_names;

    private boolean _ready;
    private final Counter _row_count;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FileDBCollection(final FileDB db,
                            final String file_path) {
        _db = db;
        _file_path = file_path;
        _name = PathUtils.getFilename(_file_path, false);
        _field_names = new HashSet<>();

        _row_count = new Counter(0);

        try {
            // initialize
            this.init();


            _ready = true;
        } catch (Throwable t) {
            _ready = false;
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public FileDB db() {
        return _db;
    }

    public boolean isReady() {
        return _ready;
    }

    public String name() {
        return _name;
    }

    public void drop() {
        if (this.remove()) {
            _db.removeCollection(_name);
        }
    }

    public boolean exists(final Object key) {
        return null != this.get(key);
    }

    public long count() {
        return _row_count.value();
    }

    public String[] fields() {
        return _field_names.toArray(new String[_field_names.size()]);
    }

    public FileDBEntity get(final Object key) {
        final ValueObject<FileDBEntity> result = new ValueObject<>();
        this.read((item) -> {
            final Object item_key = item.key();
            if (key.equals(item_key)) {
                result.content(item);
                return true;
            }
            return false;
        });

        return result.content();
    }

    public FileDBEntity insert(final FileDBEntity item) throws Exception {
        return this.write(item, false, false);
    }

    public FileDBEntity upsert(final FileDBEntity item) throws Exception {
        return this.write(item, true, false);
    }

    public FileDBEntity remove(final FileDBEntity item) throws Exception {
        return this.write(item, false, true);
    }

    public void forEach(final Delegates.FunctionArg<FileDBEntity, Boolean> callback) {
        this.read(callback);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() throws IOException {
        synchronized (_row_count) {
            final File file = new File(_file_path);
            if (!file.exists()) {
                FileUtils.mkdirs(_file_path);
                FileUtils.writeStringToFile(file, "", ENCODING);
            } else {
                // file exists
                this.read((entity) -> {
                    _row_count.inc();
                    this.addFieldNames(entity);
                    return false;
                });
            }
        }
    }


    private boolean remove() {
        try {
            if (PathUtils.exists(_file_path)) {
                FileUtils.delete(_file_path);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void read(final Delegates.FunctionArg<FileDBEntity, Boolean> callback) {
        try {
            Locker.instance().lock(_file_path);
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(_file_path));
                long index = 0;
                while (true) {
                    final String line = reader.readLine();
                    if (null == line || !StringUtils.isJSONObject(line)) {
                        break;
                    }
                    final boolean exit = callback.call(new FileDBEntity(index, line));
                    if (exit) {
                        break;
                    }
                    index++;
                }
            } catch (Throwable ignored) {
            }
        } finally {
            Locker.instance().unlock(_file_path);
        }
    }

    private FileDBEntity write(final FileDBEntity entity,
                               final boolean upsert,
                               final boolean remove) throws Exception {
        synchronized (_row_count) {
            FileDBEntity response = null;
            try {
                Locker.instance().lock(_file_path);

                if (!entity.has(KEY)) {
                    // just append
                    entity.put(KEY, GUID.create());
                    this.addFieldNames(entity);
                    this.append(entity);
                    response = entity;
                } else {
                    final File inputFile = new File(_file_path);
                    final File tempFile = new File(_file_path + ".tmp");

                    final BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                    String line;
                    long index = 0;
                    boolean append = !remove;
                    while ((line = reader.readLine()) != null) {
                        // trim newline when comparing with lineToRemove
                        final String trimmed_line = line.trim();
                        if (StringUtils.isJSONObject(trimmed_line)) {
                            final FileDBEntity row = new FileDBEntity(index, trimmed_line);
                            if (row.key().equals(entity.get(KEY))) {
                                // ALREADY EXISTS
                                if (remove) {
                                    _row_count.inc(-1);
                                    continue;
                                } else if (upsert) {
                                    // UPSERT ITEM
                                    this.addFieldNames(entity);
                                    row.putAll(entity);
                                    writer.write(this.prepareRowToWrite(row));
                                    append = false; // no need to append
                                    response = row;
                                } else {
                                    throw new Exception("Entity already exists: " + row.toString());
                                }
                            } else {
                                writer.write(this.prepareRowToWrite(row));
                            }

                            index++;
                        }
                    }

                    writer.close();
                    reader.close();
                    boolean successful = tempFile.renameTo(inputFile);

                    if (successful && append) {
                        this.addFieldNames(entity);
                        this.append(entity);
                        response = entity;
                    }
                }

            } finally {
                Locker.instance().unlock(_file_path);
            }
            return response;
        }
    }

    private void append(final FileDBEntity entity) throws IOException {
        entity.index(_row_count.value());
        final String line = this.prepareRowToWrite(entity);
        final BufferedWriter writer = new BufferedWriter(new FileWriter(_file_path, true));
        writer.write(line);
        writer.flush();
        writer.close();
        _row_count.inc();
    }

    private String prepareRowToWrite(final JsonItem item) {
        final String separator = System.getProperty("line.separator");
        return StringUtils.replace(item.toString().trim(), separator, "") + separator;
    }

    private void addFieldNames(final JsonItem item) {
        _field_names.addAll(item.keys());
    }

}
