package nano.web.controller.mail;

import nano.support.Arguments;
import nano.support.mail.TextMail;
import nano.support.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public class SendTextMailValidator implements Validator {

    @Override
    public @Nullable String validate(Object @NotNull ... args) {
        var arguments = new Arguments(args);
        var textMail = arguments.get(0, TextMail.class);
        if (StringUtils.isEmpty(textMail.getSubject())) {
            return "TextMail \"subject\" is empty";
        }
        if (StringUtils.isEmpty(textMail.getTo())) {
            return "TextMail \"to\" is empty";
        }
        if (StringUtils.isEmpty(textMail.getText())) {
            return "TextMail \"text\" is empty";
        }
        return null;
    }
}
