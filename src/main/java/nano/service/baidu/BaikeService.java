package nano.service.baidu;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BaikeService {

    private static final Pattern pattern = Pattern.compile("<meta name=\"description\" content=\"(.+)\">");

    private static final String QUERY_API = "https://baike.baidu.com/item/";

    @NonNull
    private final RestTemplate restTemplate;

    public String getBaikeExtract(String keyword) {
        var url = QUERY_API + "{0}";
        var response = this.restTemplate.getForEntity(url, String.class, keyword);
        var body = response.getBody();
        if (StringUtils.isEmpty(body)) {
            return null;
        }
        var m = pattern.matcher(body);
        if (!m.find()) {
            return null;
        }
        var extract = m.group(1);
        if (StringUtils.isEmpty(extract)) {
            return null;
        }
        return extract + "\n" + QUERY_API + keyword;
    }

}
