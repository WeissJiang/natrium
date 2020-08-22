package nano.service.baidu;

import lombok.Data;

@Data
public class TranslationPayload {

    private String input;
    private String from;
    private String to;
}
