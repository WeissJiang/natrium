package nano.web.mediawiki;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Wikipedia
 */
@Service
public class WikiService extends MediaWikiService {

    private static final String PAGE_URL = "https://%s.m.wikipedia.org/wiki/%s";
    private static final String QUERY_API = "https://%s.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exlimit=1&explaintext=true&exintro=true&redirects=true&titles={0}";

    public WikiService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected String getQueryApi(String language) {
        return QUERY_API.formatted(language);
    }

    @Override
    protected String getPageUrl(String language, String title) {
        return PAGE_URL.formatted(language, title);
    }
}
