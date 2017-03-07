/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package liang.common.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 封装各种格式的编码解码工具类.
 *
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 *
 * @author
 */
public class Encodes {

    private static final String DEFAULT_URL_ENCODING = "UTF-8";
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static Pattern pattern = Pattern.compile("\\\\u([0-9A-F]{4})", Pattern.DOTALL);

    public static byte[] intToBytes(int value) {
        byte[] bs = new byte[4];
        for (int i = 0; i < bs.length; i++) {
            bs[bs.length - 1 - i] = (byte) ((value >> (8 * i)) & 0xFF);
        }
        return bs;
    }

    public static int bytesToInt(byte[] bs) {
        int value = 0;
        for (int i = 0; i < bs.length; i++) {
            value |= (((int) (bs[bs.length - 1 - i] & 0xFF)) << 8 * i);
        }
        return value;
    }

    public static byte[] longToBytes(long value) {
        byte[] bs = new byte[8];
        for (int i = 0; i < bs.length; i++) {
            bs[bs.length - 1 - i] = (byte) ((value >> (8 * i)) & 0xFF);
        }
        return bs;
    }

    public static long bytesToLong(byte[] bs) {
        long value = 0;
        for (int i = 0; i < bs.length; i++) {
            value |= (((long) (bs[bs.length - 1 - i] & 0xFF)) << 8 * i);
        }
        return value;
    }

    public static String encodeChinese(String input) {
        String back = (StringEscapeUtils.escapeEcmaScript(input));
        Matcher m = pattern.matcher(back);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            sb.append("&#");
            sb.append(Integer.parseInt(m.group(1), 16));
            sb.append(";");
        }
        return sb.toString();
    }

    /**
     * Hex编码.
     */
    public static String encodeHex(byte[] input) {
        return Hex.encodeHexString(input);
    }

    /**
     * Hex解码.
     */
    public static byte[] decodeHex(String input) {
        if(StringUtils.isBlank(input)){
            return null;
        }
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            return null;
        }
    }

    /**
     * Base64编码.
     */
    public static String encodeBase64(byte[] input) {
        return Base64.encodeBase64String(input);
    }

    /**
     * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
     */
    public static String encodeUrlSafeBase64(byte[] input) {
        return Base64.encodeBase64URLSafeString(input);
    }

    /**
     * Base64解码.
     */
    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input);
    }

    /**
     * Base62编码。
     */
    public static String encodeBase62(byte[] input) {
        char[] chars = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
        }
        return new String(chars);
    }

    /**
     * Html 转码.
     */
    public static String escapeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    /**
     * Html 解码.
     */
    public static String unescapeHtml(String htmlEscaped) {
        return StringEscapeUtils.unescapeHtml4(htmlEscaped);
    }

    /**
     * Xml 转码.
     */
    public static String escapeXml(String xml) {
        return StringEscapeUtils.escapeXml(xml);
    }

    /**
     * Xml 解码.
     */
    public static String unescapeXml(String xmlEscaped) {
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String part) {
        if (StringUtils.isBlank(part)) {
            return "";
        }
        try {
            return URLEncoder.encode(part, DEFAULT_URL_ENCODING);
        } catch (Exception e) {
            return part;
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String urlDecode(String part) {
        if (StringUtils.isBlank(part)) {
            return "";
        }
        try {
            return URLDecoder.decode(part, DEFAULT_URL_ENCODING);
        } catch (Exception e) {
            return part;
        }
    }
}
