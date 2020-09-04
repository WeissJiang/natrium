package nano.service.mediawiki;

import com.jayway.jsonpath.JsonPath;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class MediaWikiService {

    private final RestTemplate restTemplate;

    public MediaWikiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getPageExtract(String title, String language) {
        var queryApi = this.getQueryApi(language);
        var response = this.restTemplate.getForEntity(queryApi, String.class, title);
        var body = response.getBody();
        if (StringUtils.isEmpty(body)) {
            return null;
        }
        var documentContext = JsonPath.parse(body);
        List<String> extractList = documentContext.read("$.query.pages.*.extract");
        if (CollectionUtils.isEmpty(extractList)) {
            return null;
        }
        var extract = extractList.get(0);
        var url = this.getPageUrl(language, title);
        if (StringUtils.isEmpty(extract)) {
            return url;
        }
        return extract + "\n" + url;
    }

    protected abstract String getQueryApi(String language);

    protected abstract String getPageUrl(String language, String title);

}
