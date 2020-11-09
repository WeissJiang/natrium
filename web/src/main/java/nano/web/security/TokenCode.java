package nano.web.security;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Token相关
 */
public abstract class TokenCode {

    public static final String X_TOKEN = "X-Token";

    public static final String DESENSITIZED_X_TOKEN = "DESENSITIZED_X_TOKEN";

    /**
     * 生成随机6位验证码
     */
    public static @NotNull String generateVerificationCode() {
        var randomInt = ThreadLocalRandom.current().nextInt(1_000_000);
        // left pad with '0'
        return String.format("%6d", randomInt).replaceAll(" ", "0");
    }

    /**
     * 字符串是否为验证码
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isVerificationCode(@Nullable String text) {
        return text != null && text.matches("^[0-9]{6}$");
    }

    /**
     * 生成随机Token
     */
    public static @NotNull String generateToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成Token摘要
     */
    @Contract(value = "null -> null", pure = true)
    public static String desensitizeToken(@Nullable String originalToken) {
        if (originalToken == null) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(originalToken.getBytes(StandardCharsets.UTF_8));
    }
}
