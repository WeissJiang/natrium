package nano.web.security;

import nano.support.Json;
import nano.web.controller.security.TokenDTO;
import nano.web.nano.ConfigVars;
import nano.web.nano.entity.NanoToken;
import nano.web.nano.entity.NanoUser;
import nano.web.nano.repository.TokenRepository;
import nano.web.util.JsonPathModule;
import nano.web.util.UserAgentParserModule;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nano.support.Sugar.*;
import static nano.web.security.Privilege.BASIC;
import static nano.web.security.TokenCode.generateUUID;
import static nano.web.security.TokenCode.generateVerificationCode;

/**
 * Security and token service
 *
 * @author cbdyzj
 * @since 2020.8.18
 */
@Service
public class SecurityService {

    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    private final ConfigVars configVars;

    private final TokenRepository tokenRepository;

    private final Environment env;

    public SecurityService(ConfigVars configVars, TokenRepository tokenRepository, Environment env) {
        this.configVars = configVars;
        this.tokenRepository = tokenRepository;
        this.env = env;
    }

    /**
     * Check nano API Key
     */
    public void checkNanoApiKey(@Nullable String key) {
        if (ObjectUtils.isEmpty(key)) {
            throw new AuthenticationException("Missing API key");
        }
        var apiToken = this.configVars.getNanoApiKey();
        if (!key.equals(apiToken)) {
            throw new AuthenticationException("Illegal API key");
        }
    }

    /**
     * Check token privilege
     */
    public void checkTokenPrivilege(@Nullable String token, @NotNull List<@NotNull String> privilegeList) {
        if (CollectionUtils.isEmpty(privilegeList)) {
            return;
        }
        authState(StringUtils.hasText(token), "Missing token");
        var nanoToken = this.tokenRepository.queryToken(token);
        authState(nanoToken != null, "Illegal token");
        authState(NanoToken.VALID.equals(nanoToken.getStatus()), "Invalid token");
        var tokenPrivileges = mapToString(Json.decodeValueAsList(nanoToken.getPrivilege()));
        var exists =privilegeList.stream().anyMatch(tokenPrivileges::contains);
        authState(exists, "Insufficient token privilege");
    }

    /**
     * Delete token and log out
     */
    public void deleteTheToken(@NotNull String token) {
        Assert.hasText(token, "Illegal token");
        this.tokenRepository.batchDeleteByToken(List.of(token));
    }

    /**
     * Delete token, token management
     */
    public void deleteSpecificToken(@NotNull String token, @NotNull List<@NotNull Integer> idList) {
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
     * Get the token list by token
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
     * Create the token in validation
     * Do not save the original token, save the desensitized token
     */
    public @NotNull Map<String, String> createVerifyingToken(@NotNull String username, String ua) {
        var originalToken = generateUUID();
        var token = new NanoToken();
        token.setToken(TokenCode.desensitizeToken(originalToken));
        token.setName(this.parseUserAgent(ua));
        var verificationCode = generateVerificationCode();
        token.setStatus(NanoToken.verifyingStatus(username, verificationCode));
        var now = Timestamp.from(Instant.now());
        token.setCreationTime(now);
        token.setLastActiveTime(now);
        token.setPrivilege(Json.encode(List.of(BASIC)));
        this.tokenRepository.createToken(token);
        return Map.of("token", originalToken, "verificationCode", verificationCode);
    }

    /**
     * Check token verification status
     */
    public @NotNull Map<String, String> getTokenVerification(@NotNull String token) {
        var nanoToken = this.tokenRepository.queryToken(token);
        Assert.state(nanoToken != null, "Token not found");
        var status = nanoToken.getStatus();
        Assert.hasText(status, "Token status requires not empty");
        var result = new HashMap<String, String>();
        switch (status) {
            case NanoToken.VALID -> result.put("verifying", "done");
            case NanoToken.INVALID -> throw new IllegalStateException("Token is invalid");
            default -> {
                // verifying
                if (status.startsWith(NanoToken.VERIFYING)) {
                    if (verifyingTimeout(nanoToken)) {
                        result.put("verifying", "timeout");
                    } else {
                        result.put("verifying", "pending");
                    }
                } else {
                    throw new IllegalStateException("Illegal token status");
                }
            }
        }
        return result;
    }

    /**
     * Verify token
     */
    public Map<NanoToken, String> verifyToken(NanoUser user, NanoToken telegramToken, String verificationCode) {
        var nanoTokenList = this.tokenRepository.queryVerifyingToken(user.getUsername(), verificationCode);
        var result = new HashMap<NanoToken, String>();
        var now = Timestamp.from(Instant.now());
        forEach(nanoTokenList, it -> {
            it.setUserId(user.getId());
            it.setLastActiveTime(now);
            // copy telegram token privilege
            it.setPrivilege(telegramToken.getPrivilege());
            // verification timeout
            if (verifyingTimeout(it)) {
                result.put(it, NanoToken.VERIFYING_TIMEOUT);
            }
            // verified
            else {
                it.setStatus(NanoToken.VALID);
                this.tokenRepository.updateToken(it);
                result.put(it, NanoToken.VERIFIED);
            }
        });
        return result;
    }

    /**
     * Resolve user agent
     */
    private @NotNull String parseUserAgent(@Nullable String ua) {
        try {
            Assert.hasText(ua, "Illegal user agent");
            var client = UserAgentParserModule.parseToString(ua);
            var context = JsonPathModule.parse(client);
            var userAgentFamily = (String) context.read("$.user_agent.family");
            var osFamily = (String) context.read("$.os.family");
            return "Website, %s on %s".formatted(userAgentFamily, osFamily);
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug(ex.getMessage(), ex);
            }
            return "Unknown";
        }
    }

    /**
     * Verification timeout, 5 minutes timeout
     */
    private static boolean verifyingTimeout(NanoToken nanoToken) {
        var creationTime = nanoToken.getCreationTime();
        var now = Timestamp.from(Instant.now().minusSeconds(300));
        return now.after(creationTime);
    }

    public static void authState(boolean expression, @Nls String message) {
        if (!expression) {
            throw new AuthenticationException(message);
        }
    }

    /**
     * Check ticket permission
     */
    public void checkTicketPermission(@Nullable String ticket, @NotNull List<@NotNull String> ticketNameList) {
        if (CollectionUtils.isEmpty(ticketNameList)) {
            return;
        }
        var ticketValid = ticketNameList.stream().map(this.env::getProperty).anyMatch(it -> Objects.equals(ticket, it));
        authState(ticketValid, "Insufficient ticket permission");
    }
}
