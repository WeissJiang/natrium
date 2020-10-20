package nano.web.controller.mail;

import nano.support.mail.TextMail;
import nano.support.validation.Validator;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public class SendTextMailValidator implements Validator {

    @Override
    public @Nullable String validate(Object... args) {
        Assert.state(args.length == 1, "args.length != 1");
        Assert.isInstanceOf(TextMail.class, args[0], "arg0 is not instance of TextMail");
        var textMail = (TextMail) args[0];
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
