package nano.worker.service;

import nano.support.mail.TextMail;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;

public class MailServiceTests {

    @Test
    public void testSendTextMail() throws MessagingException {
        // mock
        var mockMailSender = mock(JavaMailSender.class);
        var mockMimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        // test send
        var mailService = new MailService();
        mailService.setFromAddress("from");
        mailService.setJavaMailSender(mockMailSender);
        var mail = new TextMail();
        mail.setTo("to");
        mail.setSubject("subject");
        mail.setText("text");
        mailService.sendTextMail(mail);
        // verify
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }
}
