package nano.web.security;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Token相关
 */
public abstract class TokenCode {

    public static final String BLOWFISH = "Blowfish";
    public static final Charset utf8 = StandardCharsets.UTF_8;

    public static final String X_TOKEN = "X-Token";

    /**
     * desensitized token
     */
    public static final String D_TOKEN = "DESENSITIZED_TOKEN";

    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };

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

    /**
     * 对Token生成摘要用于脱敏
     */
    @SneakyThrows
    public static String desensitizeToken(@NotNull String originalToken) {
        var tokenBytes = originalToken.getBytes(utf8);
        var secretKeySpec = new SecretKeySpec(tokenBytes, BLOWFISH);
        var cipher = Cipher.getInstance(BLOWFISH);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        var decrypted = cipher.doFinal(tokenBytes);
        return new String(encodeHex(decrypted));
    }

    private static char[] encodeHex(byte[] bytes) {
        char[] chars = new char[32];
        for (int i = 0; i < chars.length; i = i + 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
            chars[i + 1] = HEX_CHARS[b & 0xf];
        }
        return chars;
    }
}
