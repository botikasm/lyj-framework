package org.lyj.commons.io.cloudfs;

import org.lyj.commons.io.db.filedb.FileDB;
import org.lyj.commons.io.db.filedb.FileDBCollection;
import org.lyj.commons.io.db.filedb.FileDBEntity;

public class CloudFSIndex {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String DB_NAME = "cloud_db";
    private static final String COLL_DISKS = "disks";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private FileDB _db;
    private FileDBCollection _collection_disks;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CloudFSIndex(final String root) {
        _db = new FileDB(root, DB_NAME);
        _collection_disks = _db.collection(COLL_DISKS);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void upsert(final Entity entity) throws Exception {
        if (null != _collection_disks) {
            _collection_disks.upsert(entity);
        }
    }

    public Entity get(final String key) {
        if (null != _collection_disks) {
            return new Entity(_collection_disks.get(key));
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    public static class Entity
            extends FileDBEntity {

        private static final String FLD_ROOT = "root";
        private static final String FLD_MAX_SIZE = "max_size";
        private static final String FLD_USED_SIZE = "used_size";

        public Entity() {
            super();
        }

        public Entity(final Object item) {
            super(item);
        }

        public void root(final String value) {
            super.put(FLD_ROOT, value);
        }

        public void maxSize(final long value) {
            super.put(FLD_MAX_SIZE, value);
        }

        public long usedSize() {
            return super.getLong(FLD_USED_SIZE);
        }

        public void usedSize(final long value) {
            super.put(FLD_USED_SIZE, value);
        }

    }

}
