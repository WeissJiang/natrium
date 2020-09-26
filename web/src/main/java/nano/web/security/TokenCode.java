package nano.web.security;

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

    public static final String X_TOKEN = "X-Token";
    public static final String D_TOKEN = "DESENSITIZED_TOKEN";

    private static final String BLOWFISH = "Blowfish";
    private static final Charset utf8 = StandardCharsets.UTF_8;
    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
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
    public static String desensitizeToken(@NotNull String originalToken) throws Exception {
        var tokenBytes = originalToken.getBytes(utf8);
        var secretKeySpec = new SecretKeySpec(tokenBytes, BLOWFISH);
        var cipher = Cipher.getInstance(BLOWFISH);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        var decrypted = cipher.doFinal(tokenBytes);
        return encodeHex(decrypted);
    }

    /**
     * Encode bytes to hex string
     */
    private static String encodeHex(byte[] bytes) {
        int len = bytes.length;
        char[] chars = new char[len << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < len; i++) {
            chars[j++] = HEX_CHARS[(0xF0 & bytes[i]) >>> 4];
            chars[j++] = HEX_CHARS[0x0F & bytes[i]];
        }
        return new String(chars);
    }
}
