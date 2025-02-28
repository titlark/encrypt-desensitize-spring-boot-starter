package com.titlark.config;

import com.titlark.annotation.EncryptField;
import com.titlark.util.AesUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 加密脱敏自动配置类
 */
@Configuration
@Aspect
@EnableConfigurationProperties(EncryptProperties.class)
@ConditionalOnClass(AesUtil.class)
@EnableAspectJAutoProxy
@ComponentScan("com.titlark")
public class EncryptAutoConfiguration {

    @Before("@annotation(com.titlark.annotation.EncryptMethod)")
    public void encryptBeforeSaveOrInsert(JoinPoint joinPoint) throws Exception {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        for (Object arg : args) {
            processEncryption(arg);
        }
    }

    private void processEncryption(Object arg) throws Exception {
        if (arg == null || isMultipartFile(arg)) {
            return;
        }

        // 处理Map类型
        if (arg instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) arg;
            for (Object value : map.values()) {
                processEncryption(value);
            }
            return;
        }

        // 处理集合类型
        if (arg instanceof Collection) {
            for (Object item : (Collection<?>) arg) {
                processEncryption(item);
            }
            return;
        }

        // 处理数组类型
        if (arg.getClass().isArray()) {
            for (Object item : (Object[]) arg) {
                processEncryption(item);
            }
            return;
        }

        // 处理基本类型和字符串
        if (arg.getClass().isPrimitive() || arg instanceof String || arg instanceof Number || arg instanceof Boolean) {
            return;
        }

        // 处理对象类型
        encryptObject(arg);
    }

    private void encryptObject(Object obj) throws Exception {
        if (obj == null) {
            return;
        }

        // 获取所有字段，包括父类的字段
        List<Field> fields = getAllFields(obj.getClass());

        for (Field field : fields) {
            if (field.isAnnotationPresent(EncryptField.class)) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value != null) {
                    if (value instanceof String) {
                        // 加密字符串字段
                        field.set(obj, AesUtil.getInstance().encrypt((String) value));
                    } else if (value instanceof Collection) {
                        // 处理集合类型的字段
                        processCollection((Collection<?>) value);
                    } else if (value instanceof Map) {
                        // 处理Map类型的字段
                        processMap((Map<?, ?>) value);
                    } else if (!value.getClass().isPrimitive() && !value.getClass().getName().startsWith("java.lang")) {
                        // 处理嵌套对象
                        encryptObject(value);
                    }
                }
            }
        }
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = type;
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    private void processCollection(Collection<?> collection) throws Exception {
        for (Object item : collection) {
            processEncryption(item);
        }
    }

    private void processMap(Map<?, ?> map) throws Exception {
        for (Object value : map.values()) {
            processEncryption(value);
        }
    }

    @AfterReturning(pointcut = "@annotation(com.titlark.annotation.DecryptMethod)", returning = "result")
    public void decryptAfterGetOrFind(Object result) throws Exception {
        processDecryption(result);
    }

    /**
     * 通过类型判断是否时MultipartFile类型，减少对spring-web包的依赖
     *
     * @param obj
     * @return
     */
    private boolean isMultipartFile(Object obj) {
        return obj.getClass().getName().contains("MultipartFile");
    }

    private void processDecryption(Object result) throws Exception {
        if (result == null || isMultipartFile(result)) {
            return;
        }

        // 处理Map类型
        if (result instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) result;
            for (Object value : map.values()) {
                processDecryption(value);
            }
            return;
        }

        // 处理集合类型
        if (result instanceof Collection) {
            for (Object item : (Collection<?>) result) {
                processDecryption(item);
            }
            return;
        }

        // 处理数组类型
        if (result.getClass().isArray()) {
            for (Object item : (Object[]) result) {
                processDecryption(item);
            }
            return;
        }

        // 处理基本类型和字符串
        if (result.getClass().isPrimitive() || result instanceof String || result instanceof Number || result instanceof Boolean) {
            return;
        }

        // 处理对象类型
        decryptObject(result);
    }

    private void decryptObject(Object obj) throws Exception {
        if (obj == null) {
            return;
        }

        List<Field> fields = getAllFields(obj.getClass());

        for (Field field : fields) {
            if (field.isAnnotationPresent(EncryptField.class)) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value != null) {
                    if (value instanceof String) {
                        // 解密字符串字段，失败时保留原始值
                        try {
                            String decryptedValue = AesUtil.getInstance().decrypt((String) value);
                            field.set(obj, decryptedValue);
                        } catch (Exception e) {
                            // 解密失败，保持原值不变
                        }
                    } else if (value instanceof Collection) {
                        try {
                            // 处理集合类型的字段
                            processCollection((Collection<?>) value);
                        } catch (Exception e) {
                            // 集合处理失败，保持原值不变
                        }
                    } else if (value instanceof Map) {
                        try {
                            // 处理Map类型的字段
                            processMap((Map<?, ?>) value);
                        } catch (Exception e) {
                            // Map处理失败，保持原值不变
                        }
                    } else if (!value.getClass().isPrimitive() && !value.getClass().getName().startsWith("java.lang")) {
                        try {
                            // 处理嵌套对象
                            decryptObject(value);
                        } catch (Exception e) {
                            // 嵌套对象处理失败，保持原值不变
                        }
                    }
                }
            }
        }
    }
}