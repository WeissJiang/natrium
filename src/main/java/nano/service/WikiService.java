package nano.service;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WikiService {

    private static final String PAGE_URL_PREFIX = "https://%s.m.wikipedia.org/wiki/";
    private static final String QUERY_API = "https://%s.wikipedia.org/w/api.php" +
            "?format=json&action=query&prop=extracts&exlimit=1&explaintext=true&exintro=true&redirects=true&titles={0}";

    @NonNull
    private final RestTemplate restTemplate;

    public String getWikiExtract(String title, String language) {
        var queryApi = QUERY_API.formatted(language);
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
        var url = PAGE_URL_PREFIX.formatted(language) + title;
        if (StringUtils.isEmpty(extract)) {
            return url;
        }
        return extract + "\n" + url;
    }
}
