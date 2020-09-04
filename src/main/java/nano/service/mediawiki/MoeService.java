package nano.service.mediawiki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 萌娘百科
 */
@Service
public class MoeService extends MediaWikiService {

    private static final String PAGE_URL = "https://m%s.moegirl.org.cn/%s";
    private static final String QUERY_API = "https://%s.moegirl.org.cn/api.php?format=json&action=query&prop=extracts&exlimit=1&explaintext=true&exintro=true&redirects=true&titles={0}";

    @Autowired
    public MoeService(RestTemplate restTemplate) {
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
