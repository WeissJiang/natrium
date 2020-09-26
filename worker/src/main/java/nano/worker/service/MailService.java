package nano.worker.service;

import nano.support.mail.TextMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.mail.MessagingException;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private String fromAddress;

    private JavaMailSender javaMailSender;

    public void sendTextMail(TextMail mail) throws MessagingException {
        Assert.hasText(this.fromAddress, "this.fromAddress is empty");
        Assert.notNull(this.javaMailSender, "this.javaMailSender is null");
        var mailSender = this.javaMailSender;
        // create mail message
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);
        helper.setFrom(this.fromAddress);
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getText());
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
