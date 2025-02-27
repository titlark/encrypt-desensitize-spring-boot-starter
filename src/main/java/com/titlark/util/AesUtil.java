package com.titlark.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES对称加密，采用CBC模式
 */
@Component
public class AesUtil {
    private static final String ALGORITHM = "AES";
    @Value("${aes.mode:CBC}")
    private String mode;
    @Value("${aes.key:1234567890123456}")
    private String key;
    @Value("${aes.iv:1234567812345678}")
    private String iv;
    private static AesUtil instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static AesUtil getInstance() {
        if (instance != null) {
            throw new IllegalStateException("Singleton instance already created.");
        }
        return instance;
    }

    /**
     * 加密方法
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");

        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } else if ("CBC".equals(mode)) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        } else if ("GCM".equals(mode)) {
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv.getBytes(StandardCharsets.UTF_8)); // 128 位标签长度
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密方法
     *
     * @param encryptedData
     * @return
     * @throws Exception
     */
    public String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");

        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } else if ("CBC".equals(mode)) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        } else if ("GCM".equals(mode)) {
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}