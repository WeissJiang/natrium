package nano.support.mail;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * 文本邮件
 */
public class TextMail {

    private String to;
    private String subject;
    private String text;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMail textMail = (TextMail) o;
        return Objects.equals(to, textMail.to) &&
               Objects.equals(subject, textMail.subject) &&
               Objects.equals(text, textMail.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, subject, text);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TextMail.class.getSimpleName() + "[", "]")
                .add("to='" + to + "'")
                .add("subject='" + subject + "'")
                .add("text='" + text + "'")
                .toString();
    }
}
