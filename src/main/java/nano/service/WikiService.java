package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WikiService {

    private static final String QUERY_API = "https://zh.wikipedia.org/w/api.php" +
            "?format=json&action=query&prop=extracts&exlimit=1&explaintext=true&exintro=true&redirects=true&titles={0}";

    @NonNull
    private final RestTemplate restTemplate;

    public String getWikiExtracts(String title) {
        var response = this.restTemplate.getForEntity(QUERY_API, String.class, title);
        return response.getBody();
    }
}
