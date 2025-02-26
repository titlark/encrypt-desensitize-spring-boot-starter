package com.titlark.util;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES对称加密，采用CBC模式
 */
public class AesUtil {
    private static final String ALGORITHM = "AES";
    @Value("${aes.mode:CBC}")
    private String mode;
    @Value("${aes.key:1234567890123456}")
    private String key;
    @Value("${aes.iv:1234567812345678}")
    private String iv;

    // 1. 使用 volatile 关键字保证内存可见性和禁止指令重排
    private static volatile AesUtil instance;

    // 2. 私有化构造器，防止外部直接创建实例
    private AesUtil() {
        // 防止反射时创建多个实例
        if (instance != null) {
            throw new IllegalStateException("Singleton instance already created.");
        }
    }

    // 3. 提供公共的静态方法获取实例
    public static AesUtil getInstance() {
        // 4. 第一次检查：如果实例已创建，则直接返回
        if (instance == null) {
            // 5. 同步块：进入同步块后只有一个线程可以创建实例
            synchronized (AesUtil.class) {
                // 6. 第二次检查：防止多个线程在第一次检查时并发创建实例
                if (instance == null) {
                    instance = new AesUtil();
                }
            }
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