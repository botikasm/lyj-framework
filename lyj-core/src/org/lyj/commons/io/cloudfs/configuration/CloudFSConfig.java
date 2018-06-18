package org.lyj.commons.io.cloudfs.configuration;

import org.json.JSONArray;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.json.JsonItem;

import java.util.LinkedList;
import java.util.List;

/**
 * {
 * "disks": [
 * {
 * "name":"./disk1",
 * "size_mb":"10"
 * },
 * {
 * "name":"./disk2",
 * "size_mb":"10"
 * }
 * ]
 * }
 */
public class CloudFSConfig
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_DISKS = "disks";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public CloudFSConfig() {
        super();
    }

    public CloudFSConfig(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean hasDisks() {
        return super.has(FLD_DISKS) && this.diskArray().length() > 0;
    }

    public JSONArray diskArray() {
        if (!super.has(FLD_DISKS)) {
            super.put(FLD_DISKS, new JSONArray());
        }
        return super.getJSONArray(FLD_DISKS);
    }

    public Disk[] disks() {
        final List<Disk> response = new LinkedList<>();
        final JSONArray array = this.diskArray();
        CollectionUtils.forEach(array, (item) -> {
            response.add(new Disk(item));
        });
        return response.toArray(new Disk[0]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      E M B E D D E D
    // ------------------------------------------------------------------------

    /**
     * {
     * "name":"./disk1",
     * "size_mb":"10"
     * }
     */
    public static class Disk
            extends JsonItem {

        // ------------------------------------------------------------------------
        //                      c o n s t
        // ------------------------------------------------------------------------

        private static final String FLD_NAME = "name";
        private static final String FLD_SIZE_MB = "size_mb";

        // ------------------------------------------------------------------------
        //                      c o n s t r u c t o r
        // ------------------------------------------------------------------------

        public Disk() {
            super();
        }

        public Disk(final Object item) {
            super(item);
        }

        // ------------------------------------------------------------------------
        //                      p u b l i c
        // ------------------------------------------------------------------------

        public String name() {
            return super.getString(FLD_NAME);
        }

        public Disk name(final String value) {
            super.put(FLD_NAME, value);
            return this;
        }

        public int sizeMb() {
            return super.getInt(FLD_SIZE_MB);
        }

        public Disk sizeMb(final int value) {
            super.put(FLD_SIZE_MB, value);
            return this;
        }

    }

}
