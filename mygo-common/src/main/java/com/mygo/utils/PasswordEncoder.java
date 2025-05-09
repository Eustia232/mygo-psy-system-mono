package com.mygo.utils;

import cn.hutool.core.util.RandomUtil;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 密码加密类，使用md5进行哈希。<br>
 * 都是静态方法。
 */
public class PasswordEncoder {

    public static String encode(String password) {
        //生成盐
        String salt = RandomUtil.randomString(20);
        //加密
        return encode(password, salt);
    }

    private static String encode(String password, String salt) {
        //加密
        return salt + "@" + DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
    }

    public static Boolean matches(String encodedPassword, String rawPassword) {
        if (encodedPassword == null || rawPassword == null) {
            return false;
        }
        if (!encodedPassword.contains("@")) {
            return false;
        }
        String[] arr = encodedPassword.split("@");
        //获取盐
        String salt = arr[0];
        //比较密码
        return encodedPassword.equals(encode(rawPassword, salt));
    }

}
