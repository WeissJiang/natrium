package nano.security.model;

import lombok.Getter;
import nano.security.entity.NanoChat;
import nano.security.entity.NanoSession;
import nano.security.entity.NanoUser;
import nano.support.Json;
import org.springframework.util.function.SingletonSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Session {

    private final Map<String, Object> attributes = new HashMap<>();

    public Session(NanoSession internalSession) {
        this.internalSession = internalSession;
        var attributesJson = internalSession.getAttributes();
        this.attributes.putAll(Json.decodeValueAsMap(attributesJson));
    }

    @Getter
    private final NanoSession internalSession;

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) this.attributes.get(key);
    }

    public void putAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public abstract NanoChat getChat();

    public abstract NanoUser getUser();

    public static Session create(NanoSession session, Supplier<NanoChat> chat, Supplier<NanoUser> user) {
        var chatRef = SingletonSupplier.of(chat);
        var userRef = SingletonSupplier.of(user);

        return new Session(session) {

            @Override
            public NanoChat getChat() {
                return chatRef.get();
            }

            @Override
            public NanoUser getUser() {
                return userRef.get();
            }
        };
    }
}
