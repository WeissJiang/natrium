package nano.service;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaiduService {

    private static final Predicate<String> chinese = Pattern.compile("[\u4e00-\u9fa5]").asPredicate();

    private static final String TRANSLATION_API = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    @NonNull
    private final RestTemplate restTemplate;

    @NonNull
    private final Environment env;

    public String autoTranslate(String input) {
        var options = new HashMap<String, String>();
        options.put("input", input);
        if (chinese.test(input)) {
            // 中译英
            options.put("from", "zh");
            options.put("to", "en");
        } else {
            // 英译中
            options.put("from", "en");
            options.put("to", "zh");
        }
        return this.translate(options);
    }

    @SneakyThrows
    public String translate(Map<String, String> options) {
        var input = options.get("input");
        var from = options.get("from");
        var to = options.get("to");
        var appId = this.getAppId();
        var secretKey = this.getSecretKey();
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
        return buildTranslateResult(response.getBody());
    }

    private static String buildTranslateResult(String resultJson) {
        if (StringUtils.isEmpty(resultJson)) {
            return "翻译结果为空";
        }
        var documentContext = JsonPath.parse(resultJson);
        List<Map<String, String>> result = documentContext.read("$.trans_result");
        if (ObjectUtils.isEmpty(result)) {
            log.warn("翻译异常：{}", resultJson);
            return "翻译异常：" + documentContext.read("$.error_msg");
        }
        return result.stream().map(it -> it.get("dst")).collect(Collectors.joining("\n"));
    }

    private String getAppId() {
        return this.env.getProperty("BAIDU_TRANSLATION_APP_ID", "");
    }

    private String getSecretKey() {
        return this.env.getProperty("BAIDU_TRANSLATION_SECRET_KEY", "");
    }
}
