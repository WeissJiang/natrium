package nano.support.mail;

import lombok.Data;

@Data
public class TextMail {

    private String to;
    private String subject;
    private String text;
}
