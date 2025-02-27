package com.titlark.config;

import com.titlark.annotation.EncryptField;
import com.titlark.util.AesUtil;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 加密脱敏自动配置类
 */
@Configuration
@Aspect
@Import(AesUtil.class)
public class EncryptAutoConfiguration {

    /**
     * 插入数据时加密存储
     *
     * @param entity
     * @throws Exception
     */
    @Before("@annotation(com.titlark.annotation.EncryptMethod) && args(entity)")
    public void encryptBeforeSaveOrInsert(Object entity) throws Exception {
        for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(EncryptField.class)) {
                field.setAccessible(true);
                String value = (String) field.get(entity);
                if (value != null) {
                    field.set(entity, AesUtil.getInstance().encrypt(value));
                }
            }
        }
    }

    /**
     * 获取数据时做解密操作
     *
     * @param result
     * @throws Exception
     */
    @AfterReturning(pointcut = "@annotation(com.titlark.annotation.EncryptMethod)", returning = "result")
    public void decryptAfterGetOrFind(Object result) throws Exception {
        if (result != null) {
            for (java.lang.reflect.Field field : result.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(EncryptField.class)) {
                    field.setAccessible(true);
                    String value = (String) field.get(result);
                    if (value != null) {
                        field.set(result, AesUtil.getInstance().decrypt(value));
                    }
                }
            }
        }
    }
}