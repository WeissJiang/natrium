package nano.web.security.model;

import lombok.Getter;
import nano.web.security.entity.NanoChat;
import nano.web.security.entity.NanoSession;
import nano.web.security.entity.NanoToken;
import nano.web.security.entity.NanoUser;

import java.util.HashMap;
import java.util.Map;

public class Session {

    @Getter
    private final Map<String, Object> attributes = new HashMap<>();
    @Getter
    private final NanoSession internalSession;
    @Getter
    private final NanoChat chat;
    @Getter
    private final NanoUser user;
    @Getter
    private final NanoToken token;

    public Session(NanoSession internalSession,
                   NanoChat chat, NanoUser user, NanoToken token) {
        this.internalSession = internalSession;
        this.chat = chat;
        this.user = user;
        this.token = token;
    }

    public <T> T getAttribute(String key, Class<T> clazz) {
        var attribute = this.getAttribute(key);
        return clazz.cast(attribute);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public void putAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

}
