package org.ly.commons.network.socket.basic.message;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.crypto.KeyManager;
import org.lyj.TestInitializer;
import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.security.Key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SocketMessageTest {


    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void socketMessageParse() throws IOException {

        final String TEXT = "ABv";

        final SocketMessage message = new SocketMessage("");
        message.signature("hello");
        message.body(TEXT);
        final byte[] bytes = message.bytes();

        final SocketMessage clone = new SocketMessage(bytes);
        final String body = new String(clone.body());

        assertEquals(body, TEXT);
        assertEquals(message.hash(), clone.hash());

        // byte reader (on memory)
        final byte[] message_encoded = message.bytes();
        final SocketMessageReader reader = new SocketMessageReader();
        int count = 0;
        while (!reader.isComplete()) {
            try {
                reader.write(message_encoded[count]);
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println(reader.toString());
                break;
            }
            count++;
        }
        System.out.println(reader.message().toString());
        reader.close();

        // file reader (on file)
        final SocketMessageReader reader_on_file = new SocketMessageReader(new File(PathUtils.getAbsolutePath("./reader_on_file.txt")));
        count = 0;
        while (!reader_on_file.isComplete()) {
            try {
                reader_on_file.write(message_encoded[count]);
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println(reader_on_file.toString());
                break;
            }
            count++;
        }
        System.out.println(reader_on_file.message().toString());
        reader_on_file.close();

        // test file upload
        File file = new File(PathUtils.getAbsolutePath("./sample_file.txt"));
        SocketMessage file_message = new SocketMessage("");
        file_message.body(file);
        System.out.println(file_message.toString());

        file = new File(PathUtils.getAbsolutePath("./testdb.t"));
        file_message = new SocketMessage("");
        file_message.body(file);
        System.out.println(file_message.toString());

        file = new File(PathUtils.getAbsolutePath("./test_pdf.pdf"));
        file_message = new SocketMessage("");
        file_message.body(file);
        System.out.println(file_message.toString());
    }

    @Test
    public void encodedMessageTest() throws Exception {

        final KeyManager keys = new KeyManager();

        final SocketMessage message = new SocketMessage("");
        message.body("Hello secure message!");

        final SocketMessage message_sec = new SocketMessage(message.bytes());
        message_sec.body(encrypt(message_sec.body(), keys.publicKey()));

        assertTrue(message.isValid());
        assertTrue(message_sec.isValid());

        System.out.println("MESSAGE: " + new String(message.body()));
        System.out.println("MESSAGE SEC: " + new String(message_sec.body()));
        System.out.println("MESSAGE SEC BYTES: " + CollectionUtils.toCommaDelimitedString(message_sec.body()));
        System.out.println("MESSAGE SEC DEC: " + new String(decrypt(message_sec.body(), keys.privateKey())));

        final byte[] message_sec_bytes = message_sec.bytes();
        final SocketMessageReader reader = new SocketMessageReader();
        int count = 0;
        while (!reader.isComplete()) {
            try {
                reader.write(message_sec_bytes[count]);
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println(reader.toString());
                break;
            }
            count++;
        }
        System.out.println(reader.message().toString());
        reader.close();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private byte[] encrypt(final byte[] data,
                           final Key public_key) throws Exception {

        final byte[] encrypted = RSAHelper.encrypt(data, public_key);
        return encrypted;

    }

    private byte[] decrypt(final byte[] data,
                           final Key private_key) throws Exception {
        final byte[] decrypted = RSAHelper.decrypt(data, private_key);
        return decrypted;
    }
}