package com.titlark.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aes")
public class EncryptProperties {
    /**
     * 加密模式
     */
    private String mode = "CBC";
    /**
     * 对称密钥
     */
    private String key = "1234567890123456";
    /**
     * CBC模式下iv参数
     */
    private String iv = "1234567812345678";
    /**
     * 加密后数据格式 true：十六进制格式数据 false：Base64格式
     */
    private boolean hex = false;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public boolean isHex() {
        return hex;
    }

    public void setHex(boolean hex) {
        this.hex = hex;
    }
}