package nano.web.baidu;

import nano.web.nano.ConfigVars;
import nano.web.util.JsonPathModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Translation service
 *
 * @author cbdyzj
 * @since 2020.8.17
 */
@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    private static final String TRANSLATION_API = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    private final RestTemplate restTemplate;

    private final ConfigVars configVars;

    public TranslationService(RestTemplate restTemplate, ConfigVars configVars) {
        this.restTemplate = restTemplate;
        this.configVars = configVars;
    }

    public String translate(String input, String from, String to) {
        var appId = this.configVars.getBaiduTranslationAppId();
        var secretKey = this.configVars.getBaiduTranslationSecretKey();
        var salt = Instant.now().toString();
        var data = (appId + input + salt + secretKey).getBytes(StandardCharsets.UTF_8);
        var sign = DigestUtils.md5DigestAsHex(data);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var form = new LinkedMultiValueMap<String, String>();
        form.add("q", input);
        form.add("appid", appId);
        form.add("salt", salt);
        form.add("from", from);
        form.add("to", to);
        form.add("sign", sign);
        var url = URI.create(TRANSLATION_API);
        var request = new RequestEntity<>(form, headers, HttpMethod.POST, url);
        var response = this.restTemplate.exchange(request, String.class);
        try {
            return buildTranslateResult(response.getBody());
        } catch (Exception ex) {
            log.warn("build translate result error: {}", response.getBody());
            throw ex;
        }
    }

    private static String buildTranslateResult(String resultJson) {
        if (ObjectUtils.isEmpty(resultJson)) {
            return "The translation result is empty";
        }
        var context = JsonPathModule.parse(resultJson);

        List<Map<String, String>> result = context.read("$.trans_result");
        if (ObjectUtils.isEmpty(result)) {
            log.warn("Translation error: {}", resultJson);
            return "Translation error: " + context.read("$.error_msg");
        }
        return result.stream().map(it -> it.get("dst")).collect(Collectors.joining("\n"));
    }
}
