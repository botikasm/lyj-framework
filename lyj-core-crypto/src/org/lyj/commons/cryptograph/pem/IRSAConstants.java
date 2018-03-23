package org.lyj.commons.cryptograph.pem;

import org.lyj.commons.util.FormatUtils;

public interface IRSAConstants {

    String RSA_PUBLIC_KEY = "RSA PUBLIC KEY";
    String RSA_PRIVATE_KEY = "RSA PRIVATE KEY";

    String BEGIN_RSA_PUBLIC_KEY = FormatUtils.format("-----BEGIN %s-----\n", RSA_PUBLIC_KEY);
    String END_RSA_PUBLIC_KEY = FormatUtils.format("\n-----END %s-----", RSA_PUBLIC_KEY);
    String BEGIN_RSA_PRIVATE_KEY = FormatUtils.format("-----BEGIN %s-----\n", RSA_PRIVATE_KEY);
    String END_RSA_PRIVATE_KEY = FormatUtils.format("\n-----END %s-----", RSA_PRIVATE_KEY);

    String BEGIN = "-----BEGIN -----\n";
    String END = "\n-----END -----";

}
