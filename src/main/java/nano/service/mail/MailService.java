package nano.service.mail;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class MailService {

    private JavaMailSender javaMailSender;

    @SneakyThrows
    public void sendMail(Mail mail) {
        Assert.notNull(this.javaMailSender, "this.javaMailSender");
        var mailSender = this.javaMailSender;
        var withAttachment = !CollectionUtils.isEmpty(mail.getAttachmentList());
        // create mail message
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, withAttachment);
        helper.setFrom(mail.getFrom());
        helper.setTo(mail.getTo());
        helper.setText(mail.getText());
        if (withAttachment) {
            for (Mail.Attachment attachment : mail.getAttachmentList()) {
                helper.addAttachment(attachment.getFilename(), attachment.getSource());
            }
        }
        mailSender.send(message);
    }

    @Autowired(required = false)
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        log.info("Mail sender set: {}", javaMailSender);
        this.javaMailSender = javaMailSender;
    }
}
