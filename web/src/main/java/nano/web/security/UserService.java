package nano.web.security;

import nano.support.Json;
import nano.web.controller.security.UserDTO;
import nano.web.security.entity.NanoToken;
import nano.web.security.entity.NanoUser;
import nano.web.security.repository.TokenRepository;
import nano.web.security.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static nano.support.Sugar.map;

@Service
public class UserService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public UserService(TokenRepository tokenRepository,
                       UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * 根据Token获取关联的User
     */
    public UserDTO getUserByToken(String token) {
        this.tokenRepository.updateLastActiveTime(token, Timestamp.from(Instant.now()));
        var user = this.userRepository.queryUserByToken(token);
        Assert.notNull(user, "Token is not associated with a user");
        return convert(user);
    }

    public void createOrUpdateUser(NanoUser user) {
        this.userRepository.upsertUser(user);
    }

    public List<NanoToken> queryUserToken(Long userId) {
        return this.tokenRepository.queryUserTokenList(userId);
    }

    public List<NanoPrivilege> getUserPrivilege(Long userId) {
        var nanoTokens = this.queryUserToken(userId);
        if (CollectionUtils.isEmpty(nanoTokens)) {
            return emptyList();
        }
        return nanoTokens.stream()
                .map(NanoToken::getPrivilege)
                .map(Json::decodeValueAsList)
                .map(it -> map(it, String::valueOf))
                .flatMap(Collection::stream)
                .distinct()
                .map(NanoPrivilege::valueOf)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUserList() {
        var userList = this.userRepository.queryUserList();
        return map(userList, UserService::convert);
    }

    private static UserDTO convert(@NotNull NanoUser user) {
        var userDTO = new UserDTO();
        userDTO.setId(String.valueOf(user.getId()));
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstname(user.getFirstname());
        return userDTO;
    }
}
