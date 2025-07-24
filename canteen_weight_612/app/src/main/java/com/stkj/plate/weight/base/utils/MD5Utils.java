package com.stkj.plate.weight.base.utils;

import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
        * MD5加密工具类（带盐值增强安全性）
        * 注意：MD5已被证明存在碰撞漏洞，高安全场景推荐SHA-256
        */
public final class MD5Utils {

    public final static String TAG = "c58cba6efe9e14bd615ba09e6dab5012";
    
    private MD5Utils() {
        throw new IllegalStateException("工具类禁止实例化");
    }

    // 十六进制字符集（小写）
    private static final char[] HEX_LOWER = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    // 十六进制字符集（大写）
    private static final char[] HEX_UPPER = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    /**
            * 生成随机盐值（Base64编码）
            * @param length 盐值字节长度（推荐16-32）
            * @return 盐值字符串
     */
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(salt);
        }else {
            return "";
        }
    }

    /**
            * 字符串MD5加密（默认UTF-8）
            * @param input 原始字符串
     * @return 32位小写MD5
     */
    public static String encrypt(String input) {
        return encrypt(input, false);
    }

    /**
            * 带大小写选项的字符串加密
     * @param input 原始字符串
     * @param toUpperCase 是否输出大写
     * @return 32位MD5
     */
    public static String encrypt(String input, boolean toUpperCase) {
        try {
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            return encryptBytes(bytes, toUpperCase);
        } catch (Exception e) {
            throw new IllegalArgumentException("字符串编码异常", e);
        }
    }

    /**
            * 带盐值的MD5加密
     * @param input 原始字符串
     * @param salt 盐值（建议用generateSalt生成）
            * @param toUpperCase 是否输出大写
     * @return 加盐后的MD5
     */
    public static String encryptWithSalt(String input, String salt, boolean toUpperCase) {
        String combined = salt + input + salt; // 盐值包裹增强安全性
        return encrypt(combined, toUpperCase);
    }

    /**
            * 文件MD5校验（大文件友好）
            * @param file 目标文件
     * @return 文件MD5（小写）
            * @throws IOException 文件读取异常
     */
    public static String encryptFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            return bytesToHex(digest.digest(), false);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不支持", e);
        }
    }

    /**
            * 验证字符串与MD5是否匹配（自动处理大小写）
            * @param input 原始字符串
     * @param md5Hash 待校验MD5
     * @return 是否匹配
     */
    public static boolean verify(String input, String md5Hash) {
        String actual = encrypt(input);
        return actual.equalsIgnoreCase(md5Hash);
    }

    /**
            * 带盐值验证
     * @param input 原始字符串
     * @param salt 盐值
     * @param md5Hash 待校验MD5
     * @return 是否匹配
     */
    public static boolean verifyWithSalt(String input, String salt, String md5Hash) {
        String actual = encryptWithSalt(input, salt, md5Hash.equals(md5Hash.toUpperCase()));
        return actual.equals(md5Hash); // 严格区分大小写
    }

    // 核心加密方法（字节数组）
    private static String encryptBytes(byte[] bytes, boolean toUpperCase) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            return bytesToHex(md.digest(), toUpperCase);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不支持", e);
        }
    }

    // 字节数组转十六进制字符串
    private static String bytesToHex(byte[] bytes, boolean toUpperCase) {
        char[] hexChars = toUpperCase ? HEX_UPPER : HEX_LOWER;
        StringBuilder sb = new StringBuilder(32);
        for (byte b : bytes) {
            int high = (b & 0xF0) >>> 4;
            int low = b & 0x0F;
            sb.append(hexChars[high]).append(hexChars[low]);
        }
        return sb.toString();
    }
}
