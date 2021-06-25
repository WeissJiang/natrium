package nano.web.mediawiki;

import nano.web.util.JsonPathModule;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * MediaWiki
 *
 * @author cbdyzj
 * @see 2020.8.28
 */
public abstract class MediaWikiService {

    private final RestTemplate restTemplate;

    public MediaWikiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getPageExtract(String title, String language) {
        var queryApi = this.getQueryApi(language);
        var response = this.restTemplate.getForEntity(queryApi, String.class, title);
        var body = response.getBody();
        if (ObjectUtils.isEmpty(body)) {
            return null;
        }
        List<String> extractList = JsonPathModule.read(body, "$.query.pages.*.extract");
        if (CollectionUtils.isEmpty(extractList)) {
            return null;
        }
        var extract = extractList.get(0);
        var url = this.getPageUrl(language, encodeTitle(title));
        if (ObjectUtils.isEmpty(extract)) {
            return url;
        }
        return extract + "\n" + url;
    }

    protected static String encodeTitle(String title) {
        return UriUtils.encode(title, StandardCharsets.UTF_8);
    }

    protected abstract String getQueryApi(String language);

    protected abstract String getPageUrl(String language, String title);

}
