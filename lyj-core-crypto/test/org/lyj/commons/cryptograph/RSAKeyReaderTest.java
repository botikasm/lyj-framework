package org.lyj.commons.cryptograph;

import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public class RSAKeyReaderTest {

    private static final String BEGIN_RSA_PRIVATE_KEY = RSAKeyReader.BEGIN_RSA_PRIVATE_KEY;
    private static final String END_RSA_PRIVATE_KEY = RSAKeyReader.END_RSA_PRIVATE_KEY;

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

    @Test
    public void RSAPrivateKeyFromText() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        final RSAKeyReader reader = new RSAKeyReader();

        final PrivateKey key =  reader.RSAPrivateKeyFromText(KEY_TEXT);
        System.out.println(key);
    }
}