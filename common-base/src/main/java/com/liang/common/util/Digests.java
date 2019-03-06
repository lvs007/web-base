/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.liang.common.util;

import com.liang.common.exception.Exceptions;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 支持SHA-1/MD5消息摘要的工具类.
 *
 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
 *
 * @author
 */
public class Digests {

    private static final String SHA1 = "SHA-1";
    private static final String MD5 = "MD5";
    private static final SecureRandom random = new SecureRandom();

    /**
     * 对输入字符串进行sha1散列.
     * @param input
     * @return 
     */
    public static byte[] sha1(byte[] input) {
        return digest(input, SHA1, null, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt) {
        return digest(input, SHA1, salt, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
        return digest(input, SHA1, salt, iterations);
    }

    public static String sha1(String input) {
        try {
            byte[] data = sha1(input.getBytes("UTF-8"));
            return Encodes.encodeHex(data);
        } catch (Exception ex) {
            throw Exceptions.unchecked(ex);
        }
    }

    public static String md5(String input) {
        try {
            byte[] data = md5(input.getBytes("UTF-8"));
            return Encodes.encodeHex(data);
        } catch (Exception ex) {
            throw Exceptions.unchecked(ex);
        }
    }

    /**
     * 对输入字符串进行md5散列.
     * @param input
     * @return 
     */
    public static byte[] md5(byte[] input) {
        return digest(input, MD5, null, 1);
    }

    public static byte[] md5(byte[] input, byte[] salt) {
        return digest(input, MD5, salt, 1);
    }

    public static byte[] md5(byte[] input, byte[] salt, int iterations) {
        return digest(input, MD5, salt, iterations);
    }

    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            if (salt != null) {
                digest.update(salt);
            }

            byte[] result = digest.digest(input);

            for (int i = 1; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 生成随机的Byte[]作为salt.
     *
     * @param numBytes byte数组的大小
     * @return 
     */
    public static byte[] generateSalt(int numBytes) {
        Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);

        byte[] bytes = new byte[numBytes];
        random.nextBytes(bytes);
        return bytes;
    }

    public static String md5(File file) throws IOException {
        try (FileInputStream fin = new FileInputStream(file)) {
            byte[] data = md5(fin);
            return Encodes.encodeHex(data);
        }
    }

    /**
     * 对文件进行md5散列.
     * @param input
     * @return 
     * @throws IOException
     */
    public static byte[] md5(InputStream input) throws IOException {
        return digest(input, MD5);
    }

    /**
     * 对文件进行sha1散列.
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] sha1(InputStream input) throws IOException {
        return digest(input, SHA1);
    }

    private static byte[] digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int bufferLength = 8 * 1024;
            byte[] buffer = new byte[bufferLength];
            int read = input.read(buffer, 0, bufferLength);

            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, bufferLength);
            }
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.unchecked(e);
        }
    }
}
