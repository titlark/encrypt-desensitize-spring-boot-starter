package com.titlark.util;

import java.util.Base64;

/**
 * Base64和Hex之间互转
 */
public class Base64HexConverter {
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    /**
     * Base64 转 Hex
     *
     * @param base64Str
     * @return
     */
    public static String base64ToHex(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) {
            return "";
        }
        // 解码 Base64 为字节数组
        byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
        // 转为 Hex 字符串
        char[] hexChars = new char[decodedBytes.length * 2];
        for (int i = 0; i < decodedBytes.length; i++) {
            int v = decodedBytes[i] & 0xFF; // 转为无符号整数
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];    // 高四位
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F]; // 低四位
        }
        return new String(hexChars);
    }

    /**
     * Hex 转 Base64
     *
     * @param hexStr
     * @return
     */
    public static String hexToBase64(String hexStr) {
        if (hexStr == null || hexStr.isEmpty() || hexStr.length() % 2 != 0) {
            return "";
        }
        // 转为字节数组
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length(); i += 2) {
            String byteStr = hexStr.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(byteStr, 16);
        }
        // 编码为 Base64
        return Base64.getEncoder().encodeToString(bytes);
    }
}
