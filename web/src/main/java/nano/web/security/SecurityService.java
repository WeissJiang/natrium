package nano.web.security;

import nano.support.Json;
import nano.web.controller.security.TokenDTO;
import nano.web.nano.ConfigVars;
import nano.web.security.entity.NanoToken;
import nano.web.security.entity.NanoUser;
import nano.web.security.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ua_parser.Parser;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nano.support.Sugar.*;
import static nano.web.security.NanoPrivilege.BASIC;
import static nano.web.security.TokenCode.generateToken;
import static nano.web.security.TokenCode.generateVerificationCode;

@Service
public class SecurityService {

    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    private static final Parser userAgentParser = createUserAgentParser();

    private final ConfigVars configVars;
    private final TokenRepository tokenRepository;

    public SecurityService(ConfigVars configVars, TokenRepository tokenRepository) {
        this.configVars = configVars;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Check nano API Key
     */
    public void checkNanoApiKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new AuthenticationException("Missing API key");
        }
        var apiToken = this.configVars.getNanoApiKey();
        if (!key.equals(apiToken)) {
            throw new AuthenticationException("Illegal API key");
        }
    }

    /**
     * 检查Token权限
     */
    public void checkTokenPrivilege(String token, List<NanoPrivilege> privilegeList) {
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("Missing token");
        }
        boolean exists = this.tokenRepository.existsTokenWithPrivilege(token, map(privilegeList, NanoPrivilege::name));
        if (!exists) {
            throw new AuthenticationException("Illegal token");
        }
    }

    /**
     * 删除Token，也作登出使用
     */
    public void deleteTheToken(String token) {
        Assert.hasText(token, "Illegal token");
        this.tokenRepository.batchDeleteByToken(List.of(token));
    }

    /**
     * 删除Token，Token管理
     */
    public void deleteSpecificToken(String token, List<Integer> idList) {
        var nanoTokenList = this.tokenRepository.queryTokenList(idList);
        if (CollectionUtils.isEmpty(nanoTokenList)) {
            return;
        }
        var currentToken = this.tokenRepository.queryToken(token);
        Assert.notNull(currentToken, "Illegal token");
        Assert.state(every(nanoTokenList, it -> Objects.equals(currentToken.getUserId(), it.getUserId())), "Abnormal operation permission");
        this.tokenRepository.batchDeleteById(map(nanoTokenList, NanoToken::getId));
    }

    /**
     * 根据Token，获取Token列表
     */
    public List<TokenDTO> getAssociatedTokenList(String token) {
        Assert.hasText(token, "Illegal token");
        var nanoTokenList = this.tokenRepository.queryAssociatedTokenList(token);
        return map(nanoTokenList, it -> {
            var tokenDTO = new TokenDTO();
            tokenDTO.setId(it.getId());
            tokenDTO.setName(it.getName());
            tokenDTO.setPrivilege(it.getPrivilege());
            tokenDTO.setCreationTime(it.getCreationTime().toInstant());
            tokenDTO.setLastActiveTime(it.getLastActiveTime().toInstant());
            tokenDTO.setCurrent(Objects.equals(token, it.getToken()));
            return tokenDTO;
        });
    }

    /**
     * 创建验证中的Token
     * 不保存原始Token，保存脱敏后的Token
     */
    public Map<String, String> createVerificatingToken(String username, String ua) throws Exception {
        var originalToken = generateToken();
        var token = new NanoToken();
        token.setToken(TokenCode.desensitizeToken(originalToken));
        token.setName(parseUserAgent(ua));
        var verificationCode = generateVerificationCode();
        token.setStatus(NanoToken.verificatingStatus(username, verificationCode));
        var now = Timestamp.from(Instant.now());
        token.setCreationTime(now);
        token.setLastActiveTime(now);
        token.setPrivilege(Json.encode(List.of(BASIC.name())));
        this.tokenRepository.createToken(token);
        return Map.of("token", originalToken, "verificationCode", verificationCode);
    }

    /**
     * 检查Token验证状态
     */
    public Map<String, String> getTokenVerification(String token) {
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
    public Map<NanoToken, String> verificateToken(NanoUser user, NanoToken telegramToken, String verificationCode) {
        var nanoTokenList = this.tokenRepository.queryVerificatingToken(user.getUsername(), verificationCode);
        var result = new HashMap<NanoToken, String>();
        var now = Timestamp.from(Instant.now());
        forEach(nanoTokenList, it -> {
            it.setUserId(user.getId());
            it.setLastActiveTime(now);
            // copy telegram token privilege
            it.setPrivilege(telegramToken.getPrivilege());
            // verification timeout
            if (verificatingTimeout(it)) {
                result.put(it, NanoToken.VERIFICATION_TIMEOUT);
            }
            // verificated
            else {
                it.setStatus(NanoToken.VALID);
                this.tokenRepository.updateToken(it);
                result.put(it, NanoToken.VERIFICATED);
            }
        });
        return result;
    }

    /**
     * 清理验证超时的Token
     *
     * @return prune count
     */
    public int pruneVerificatingTimeoutToken() {
        var tokenList = this.tokenRepository.queryVerificatingTimeoutToken();
        int count = 0;
        if (!CollectionUtils.isEmpty(tokenList)) {
            count = tokenList.size();
            this.tokenRepository.batchDeleteByToken(tokenList);
        }
        return count;
    }

    /**
     * 接口验证是否超时，5分钟超时时间
     */
    private static boolean verificatingTimeout(NanoToken nanoToken) {
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

    private static Parser createUserAgentParser() {
        try {
            return new Parser();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
