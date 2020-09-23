package nano.web.messageing;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class MailService {

    private String fromAddress;

    private JavaMailSender javaMailSender;

    @SneakyThrows
    public void sendTextMail(String to, String text) {
        Assert.hasText(this.fromAddress, "this.fromAddress is empty");
        Assert.notNull(this.javaMailSender, "this.javaMailSender is null");
        var mailSender = this.javaMailSender;
        // create mail message
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);
        helper.setFrom(this.fromAddress);
        helper.setTo(to);
        helper.setSubject("Shinonome Research Institute Mail Service");
        helper.setText(text);
        mailSender.send(message);
    }

    @Autowired(required = false)
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        log.info("Mail sender set: {}", javaMailSender);
        this.javaMailSender = javaMailSender;
    }

    @Value("${spring.mail.username:}")
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
}
