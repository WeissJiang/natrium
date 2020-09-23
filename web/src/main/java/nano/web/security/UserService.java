package nano.web.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.controller.security.UserDTO;
import nano.web.security.entity.NanoUser;
import nano.web.security.repository.TokenRepository;
import nano.web.security.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @NonNull
    private final TokenRepository tokenRepository;
    @NonNull
    private final UserRepository userRepository;

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
        userDTO.setId(String.valueOf(user.getId()));
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstname(user.getFirstname());
        this.tokenRepository.updateLastActiveTime(token, Timestamp.from(Instant.now()));
        return userDTO;
    }

    public void createOrUpdateUser(NanoUser user) {
        this.userRepository.upsertUser(user);
    }
}
