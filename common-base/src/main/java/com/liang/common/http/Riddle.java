package com.liang.common.http;

import org.apache.commons.lang3.StringUtils;

/**
 * 签名，加密和解密实现。
 */
public abstract class Riddle {

    abstract boolean signEquals(String actual, String expected);
    abstract String sign(String toSign, String key);
    abstract byte[] encrypt(byte[] toEncrypt, String key);
    abstract byte[] decrypt(byte[] toDecrypt, String key);

    public static Riddle from(String sign) {
        if (sign == null || StringUtils.isBlank(sign) || sign.length() == 32) {
            return Riddle.ONE;
        }
        if (sign.length() == 34) {
            return Riddle.TWO;
        }
        return NOT_FOUND;
    }

    static final Riddle NOT_FOUND = new Riddle() {
        @Override
        boolean signEquals(String actual, String expected) {
            return false;
        }

        @Override
        String sign(String toSign, String key) {
            return null;
        }

        @Override
        byte[] encrypt(byte[] toEncrypt, String key) {
            return null;
        }

        @Override
        byte[] decrypt(byte[] toDecrypt, String key) {
            return null;
        }
    };

    public static final Riddle ONE = new RiddleOne();

    public static final Riddle TWO = new RiddleTwo();
}
