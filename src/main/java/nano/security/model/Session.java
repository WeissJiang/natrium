package nano.security.model;

import lombok.Data;
import lombok.experimental.Delegate;
import nano.security.entity.NanoChat;
import nano.security.entity.NanoSession;
import nano.security.entity.NanoUser;

@Data
public class Session {

    @Delegate
    private NanoSession delegate;

    private NanoChat chat;
    private NanoUser user;
}
