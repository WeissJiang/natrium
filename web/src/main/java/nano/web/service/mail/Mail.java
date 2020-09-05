package nano.web.service.mail;

import lombok.Data;
import org.springframework.core.io.InputStreamSource;

import java.util.List;

@Data
public class Mail {

    private String from;
    private String to;
    private String text;
    private List<Attachment> attachmentList;

    @Data
    public static class Attachment {
        private String filename;
        private InputStreamSource source;
    }
}
