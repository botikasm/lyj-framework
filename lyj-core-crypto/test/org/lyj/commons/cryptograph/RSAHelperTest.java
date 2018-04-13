package org.lyj.commons.cryptograph;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lyj.TestInitializer;
import org.lyj.commons.cryptograph.pem.IRSAConstants;
import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RSAHelperTest {


    private static final String KEY_TEXT = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJLORhDfV6VwpZCF\n" +
            "0ZgCKc7cP3Sy5EFkkHY1LKDNqhYmU+ey7GzNjJbm/JIZQwtufcpP8K14kEq2rHo/\n" +
            "F2hN5DQ0hRpSAXljxuz0Y0l5DeELQYrYjcjFMOU5071ebBNuCxm7x3hqSXt/LlmW\n" +
            "GBo73rf3cksI5sxlKEvr69fl4vUJAgMBAAECgYBViTxHzmn56hV9jIrff7suXSPX\n" +
            "8feOpnKJfVgAZXSJrVFL+fNJPcaBkhptYayvt3QxcbxwxoOEFMWQALy5uFCSuWk8\n" +
            "WOiUVlVoCZUjW1fn6z3N0WiuW0yJH315yYjHunFI9THaToIT8XXANZPMS6Jw84Qy\n" +
            "EmNKF6DgirYKvmEyAQJBAORadaff+2yh9r8r25SIIujQpogYMB+JbyAtfSwHMPIC\n" +
            "ZWC137ah1hYj82KutL0yQ9r/6iM+/lY9a5W6SvpWpkECQQCklFfjFxlQ13VrkrGp\n" +
            "h8duqbWm0BdX32lqaTNPscDaz60GWw7LPNhb0LY2mlTP1iSgWdt3VCWKvowHA1Qh\n" +
            "92zJAkANxEJZl5BB0VXd2pgHUVnBbWrMw6CLFi8D4posQFa8EFbqSKyvBvywIwS2\n" +
            "S1AMI+6hUlJcQ5zzuAo3YZ7JjtXBAkARrd3DBzgw9vQmGhv7mhpSSOi6gp//UICC\n" +
            "bcqVRQLyflyX0jBEpMSZGFJ6ixmROe+1SfHJX8Cops9j8XeYLnwBAkEAqxZUZ4NB\n" +
            "kfTwjtxIsZhwaBHKPduFPg+kSKCvTwqpn87D2E0jk/dL4oFk3/otj4L1j1aelFb8\n" +
            "hmciN50QAVL2Bg==\n" +
            "-----END RSA PRIVATE KEY-----";

    @BeforeClass
    public static void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void RSAPrivateKeyFromText() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        final RSAHelper reader = new RSAHelper();

        final PrivateKey key = reader.readRSAPrivateKeyFromText(KEY_TEXT);
        System.out.println(key);
    }

    @Test
    public void generateKeyPairTest() throws Exception {
        final RSAHelper reader = new RSAHelper();

        final KeyPair key = RSAHelper.generateRSAKeyPair();
        System.out.println("PUBLIC: " + new String(key.getPublic().getEncoded()));
        System.out.println("PRIVATE: " + new String(key.getPublic().getEncoded()));

        final String file_private = PathUtils.getAbsolutePath("./crypto/private_key");
        final String file_public = PathUtils.getAbsolutePath("./crypto/public_key.pub");

        FileUtils.mkdirs(file_public);

        RSAHelper.writePemFile(key.getPublic(), IRSAConstants.RSA_PUBLIC_KEY, file_public);
        RSAHelper.writePemFile(key.getPrivate(), IRSAConstants.RSA_PRIVATE_KEY, file_private);

        final String string_private =  RSAHelper.writePemString(key.getPrivate(), IRSAConstants.RSA_PRIVATE_KEY);
        final String string_public =  RSAHelper.writePemString(key.getPublic(), IRSAConstants.RSA_PUBLIC_KEY);

        assertEquals(string_private, RSAHelper.toString(key.getPrivate()));
        assertEquals(string_public, RSAHelper.toString(key.getPublic()));

        System.out.println(string_private);
        System.out.println(string_public);
        System.out.println(RSAHelper.writePemString(key.getPrivate(), ""));
    }

    @Test
    public void writePemFilesTest() throws Exception {

        final KeyPair key_pair = RSAHelper.generateRSAKeyPair();
        final String[] response = RSAHelper.writePemFiles(key_pair, PathUtils.getAbsolutePath("./keyStore"));
        assertTrue(response.length == 2);

        System.out.println(StringUtils.toString(response));

        // try read private key
        System.out.println(RSAHelper.readKeyFile(response[0], true));
        System.out.println(RSAHelper.readKeyFile(response[0], false));

        final String public_key = RSAHelper.readKeyFile(response[1], true);
        System.out.println("'" + public_key.trim() + "'");
        System.out.println(public_key.trim().length());
        final PublicKey key = RSAHelper.readRSAPublicKeyFromText(public_key.trim());
    }

}