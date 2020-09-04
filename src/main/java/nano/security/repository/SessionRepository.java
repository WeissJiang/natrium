package nano.security.repository;

import nano.security.entity.Session;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepository {

    public Integer createSession(Session session) {
        return 0;
    }

    public Session querySession(Number chatId, Number userId) {
        return new Session();
    }

    public void updateSession(Session session) {
    }

}
