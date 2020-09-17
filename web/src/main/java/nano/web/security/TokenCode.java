package nano.web.security;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Token相关
 */
public abstract class TokenCode {

    /**
     * 生成随机6位验证码
     */
    public static String generateVerificationCode() {
        var randomInt = ThreadLocalRandom.current().nextInt(1_000_000);
        // left pad with '0'
        return String.format("%6d", randomInt).replaceAll(" ", "0");
    }

    /**
     * 字符串是否为验证码
     */
    public static boolean isVerificationCode(String text) {
        return text != null && text.matches("^[0-9]{6}$");
    }

    /**
     * 生成随机Token
     */
    public static String generateToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
