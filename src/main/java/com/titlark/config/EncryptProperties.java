package com.titlark.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aes")
public class EncryptProperties {
    private String mode = "CBC";
    private String key = "1234567890123456";
    private String iv = "1234567812345678";

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
}