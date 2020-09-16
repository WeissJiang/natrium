package nano.web.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.web.ConfigVars;
import nano.web.controller.user.UserDTO;
import nano.web.security.entity.NanoToken;
import nano.web.security.repository.TokenRepository;
import nano.web.security.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import ua_parser.Parser;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private static final Parser userAgentParser = createUserAgentParser();

    @NonNull
    private final TokenRepository tokenRepository;
    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final ConfigVars configVars;

    /**
     * 检查Token权限
     */
    public void checkNanoToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("Missing token");
        }

        var nanoToken = this.configVars.getNanoToken();
        if (!token.equals(nanoToken)) {
            throw new AuthenticationException("Illegal token");
        }
    }

    /**
     * 创建验证中的Token
     */
    public Map<String, String> createVerificatingToken(String username, String ua) {
        var token = new NanoToken();
        token.setToken(generateToken());
        token.setName(parseUserAgent(ua));
        var verificationCode = generateVerificationCode();
        token.setStatus(NanoToken.verificatingStatus(username, verificationCode));
        var now = Timestamp.from(Instant.now());
        token.setCreationTime(now);
        token.setLastActiveTime(now);
        this.tokenRepository.createToken(token);
        return Map.of("token", token.getToken(), "verificationCode", verificationCode);
    }

    /**
     * 检查Token验证状态
     * 如果状态为VALID，返回Token关联的用户
     */
    public UserDTO checkTokenVerification(String token) {
        var nanoToken = this.tokenRepository.queryToken(token);
        Assert.state(nanoToken != null, "Token not found");
        var status = nanoToken.getStatus();
        Assert.hasText(status, "status requires not empty");
        switch (status) {
            case NanoToken.INVALID -> throw new IllegalStateException("Token is invalid");
            case NanoToken.VALID -> {
                return this.getUserByToken(token);
            }
            default -> {
                // 验证中
                if (status.startsWith(NanoToken.VERIFICATING)) {
                    return null;
                } else {
                    throw new IllegalStateException("Illegal token status");
                }
            }
        }
    }

    /**
     * 根据Token获取关联的User
     */
    public UserDTO getUserByToken(String token) {
        var user = this.userRepository.queryUserByToken(token);
        if (user == null) {
            return null;
        }
        var userDTO = new UserDTO();
        // same properties
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }


    /**
     * 解析用户代理
     */
    private static String parseUserAgent(String ua) {
        try {
            Assert.notNull(userAgentParser, "userAgentParser is null");
            Assert.hasText(ua, "Illegal user agent");
            var parsed = userAgentParser.parse(ua);
            return "Website, %s on %s".formatted(parsed.os.family, parsed.userAgent.family);
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug(ex.getMessage(), ex);
            }
            return "Unknow";
        }
    }

    /**
     * 生成随机Token
     */
    private static String generateToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成随机6位验证码
     */
    private static String generateVerificationCode() {
        var randomInt = ThreadLocalRandom.current().nextInt(1_000_000);
        // left pad with '0'
        return String.format("%6d", randomInt).replaceAll(" ", "0");
    }

    @SneakyThrows
    private static Parser createUserAgentParser() {
        return new Parser();
    }

}
