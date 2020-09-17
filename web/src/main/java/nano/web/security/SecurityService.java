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
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import ua_parser.Parser;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static nano.support.Sugar.forEach;
import static nano.web.security.TokenCode.generateToken;
import static nano.web.security.TokenCode.generateVerificationCode;

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
     * 检查API Token权限
     */
    public void checkNanoApiToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("Missing token");
        }
        var apiToken = this.configVars.getNanoApiToken();
        if (!token.equals(apiToken)) {
            throw new AuthenticationException("Illegal token");
        }
    }

    /**
     * 删除Token
     */
    public void deleteToken(String token) {
        Assert.hasText(token, "Illegal token");
        this.tokenRepository.deleteToken(token);
    }

    /**
     * 根据Token获取关联的User
     */
    public UserDTO getUserByToken(String token) {
        var user = this.userRepository.queryUserByToken(token);
        if (user == null) {
            return null;
        }
        // copy properties
        var userDTO = new UserDTO();
        userDTO.setId(String.valueOf(user.getId().longValue()));
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstname(user.getFirstname());
        return userDTO;
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
    public Map<String, String> checkTokenVerification(String token) {
        var nanoToken = this.tokenRepository.queryToken(token);
        Assert.state(nanoToken != null, "Token not found");
        var status = nanoToken.getStatus();
        Assert.hasText(status, "Token status requires not empty");
        var result = new HashMap<String, String>();
        switch (status) {
            case NanoToken.INVALID -> throw new IllegalStateException("Token is invalid");
            case NanoToken.VALID -> result.put("verificating", "done");
            default -> {
                // 验证中
                if (status.startsWith(NanoToken.VERIFICATING)) {
                    if (verificatingTimeout(nanoToken)) {
                        result.put("verificating", "timeout");
                    } else {
                        result.put("verificating", "pending");
                    }
                } else {
                    throw new IllegalStateException("Illegal token status");
                }
            }
        }
        return result;
    }

    /**
     * 验证Token
     */
    public Map<NanoToken, String> verificateToken(String username, String verificationCode) {
        var nanoTokenList = this.tokenRepository.queryVerificatingToken(username, verificationCode);
        var result = new HashMap<NanoToken, String>();
        forEach(nanoTokenList, nanoToken -> {
            // verification timeout
            if (verificatingTimeout(nanoToken)) {
                result.put(nanoToken, NanoToken.VERIFICATION_TIMEOUT);
            }
            // verificated
            else {
                nanoToken.setStatus(NanoToken.VALID);
                this.tokenRepository.updateTokenStatus(nanoToken.getToken(), NanoToken.VALID);
                result.put(nanoToken, NanoToken.VERIFICATED);
            }
        });
        return result;
    }

    /**
     * 接口验证是否超时，5分钟超时时间
     */
    private boolean verificatingTimeout(NanoToken nanoToken) {
        var creationTime = nanoToken.getCreationTime();
        var now = Timestamp.from(Instant.now().minusSeconds(300));
        return now.after(creationTime);
    }

    /**
     * 解析用户代理
     */
    private static String parseUserAgent(String ua) {
        try {
            Assert.notNull(userAgentParser, "userAgentParser is null");
            Assert.hasText(ua, "Illegal user agent");
            var parsed = userAgentParser.parse(ua);
            return "Website, %s on %s".formatted(parsed.userAgent.family, parsed.os.family);
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug(ex.getMessage(), ex);
            }
            return "Unknow";
        }
    }


    @SneakyThrows
    private static Parser createUserAgentParser() {
        return new Parser();
    }


}
