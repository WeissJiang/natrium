package nano.support.mail;

import lombok.Data;

@Data
public class TextMail {

    private String from;
    private String to;
    private String subject;
    private String text;
}
