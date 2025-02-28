package com.titlark.util;

/**
 * 数据脱敏工具类
 */
public class DataMaskingUtil {

    /**
     * 对姓名进行脱敏
     *
     * @param name 姓名
     * @return
     */
    public static String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        return name.charAt(0) + repeat(name.length() - 1);
    }

    /**
     * 对身份证号码进行脱敏
     *
     * @param idCard 身份证号码
     * @return
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 18) return idCard;
        return idCard.substring(0, 6) + repeat(8) + idCard.substring(idCard.length() - 4);
    }

    /**
     * 对手机号进行数据脱敏
     *
     * @param phone
     * @return
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) return phone;
        return phone.substring(0, 3) + repeat(4) + phone.substring(phone.length() - 4);
    }

    /**
     * 获取脱敏符号填充数据
     *
     * @param repeatCount
     * @return
     */
    private static String repeat(int repeatCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            sb.append("*");
        }
        return sb.toString();
    }
}
