package org.ly.ext.mail.reader;

import org.json.JSONObject;
import org.lyj.commons.lang.Base64;
import org.lyj.commons.util.*;

import javax.mail.BodyPart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wrap Attachment
 */
public class MailMessageAttachment
        extends JSONObject {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_NAME = "name";
    private static final String FLD_PATH = "path"; // file path
    private static final String FLD_DATA = "data"; // base64 data

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MailReader _reader;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MailMessageAttachment(final MailReader reader,
                                 final BodyPart part) throws Exception {
        _reader = reader;
        this.init(part);
    }

    public MailMessageAttachment(final MailReader reader,
                                 final JSONObject obj) {
        super(obj.toString());
        _reader = reader;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String name() {
        return super.optString(FLD_NAME);
    }

    public String data() {
        return super.optString(FLD_DATA);
    }

    public String path() {
        return super.optString(FLD_PATH);
    }

    public File saveFileTo(final String root,
                           final boolean move) throws IOException {
        final String data = this.data();
        final String name = this.name();
        final String file_path = PathUtils.concat(root, name);
        final File target = new File(file_path);
        if (StringUtils.hasText(data)) {
            // has base64 data
            if (Base64.decodeToFile(data, target.getAbsolutePath())) {
                return target;
            }
        } else if (StringUtils.hasText(this.path()) && FileUtils.exists(this.path())) {
            final File source = new File(this.path());
            FileUtils.copy(source, target);
            if (move) {
                if (!source.delete()) {
                    source.deleteOnExit();
                }
            }
            return target;
        }

        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final BodyPart part) throws Exception {
        final int mem_quota = _reader.attachmentMemoryQuota();
        final int max_size = _reader.attachmentSize();
        final InputStream is = part.getInputStream();
        final String filename = part.getFileName();
        if (null != is && StringUtils.hasText(filename)) {
            final int size = part.getSize();
            if (size < max_size) {
                if (size < mem_quota) {
                    // save base64
                    final byte[] bytes = ByteUtils.getBytes(is);
                    final String base64 = Base64.encodeBytes(bytes);

                    this.put(FLD_NAME, filename);
                    this.put(FLD_PATH, filename);
                    this.put(FLD_DATA, base64);

                } else {
                    // save file
                    final String uid = RandomUtils.randomUUID(true).concat(PathUtils.getFilenameExtension(filename, true));
                    final String target = PathUtils.getTemporaryDirectory(PathUtils.concat("email_downloads", uid));
                    FileUtils.copy(is, new File(target));

                    this.put(FLD_NAME, filename);
                    this.put(FLD_PATH, target);
                    this.put(FLD_DATA, "");
                }
            } else {
                throw new Exception(FormatUtils.format(
                        "Attachment '%s' exceed max quota: Allowed=%s Current Size=%s",
                        filename, max_size, size));
            }
        }
    }

}
