package nano.web.baidu;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

/**
 * Baidu encyclopedia service
 *
 * @author cbdyzj
 * @since 2020.8.28
 */
@Service
public class BaiduEncyclopediaService {

    private static final Pattern pattern = Pattern.compile("<meta name=\"description\" content=\"(?<desc>.+)\">");

    private static final String QUERY_API = "https://baike.baidu.com/item/";

    private final RestTemplate restTemplate;

    public BaiduEncyclopediaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getPageExtract(String keyword) {
        var url = QUERY_API + "{0}";
        var response = this.restTemplate.getForEntity(url, String.class, keyword);
        var body = response.getBody();
        if (ObjectUtils.isEmpty(body)) {
            return null;
        }
        var m = pattern.matcher(body);
        if (!m.find()) {
            return null;
        }
        var extract = m.group("desc");
        if (ObjectUtils.isEmpty(extract)) {
            return null;
        }
        return extract + "\n" + QUERY_API + keyword;
    }

}
