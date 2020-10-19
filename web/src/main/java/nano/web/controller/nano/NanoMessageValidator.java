package nano.web.controller.nano;

import nano.support.validation.Validator;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @see NanoController#message
 */
@Component
public class NanoMessageValidator implements Validator {

    @Override
    public @Nullable String validate(Object... args) {
        Assert.state(args.length == 1, "args.length != 1");
        var m = args[0];
        if (StringUtils.isEmpty(m)) {
            return "Illegal message body";
        }
        return null;
    }
}
