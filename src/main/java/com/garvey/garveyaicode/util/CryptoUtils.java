package com.garvey.garveyaicode.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

public class CryptoUtils {

    @Value("app.crypto")
    private static String cryptoKey;

    public static String encrypt(String origin) {
        return DigestUtils.md5DigestAsHex((cryptoKey + origin).getBytes());
    }
}
