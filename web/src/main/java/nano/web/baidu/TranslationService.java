package nano.web.baidu;

import nano.support.http.Fetch;
import nano.web.nano.ConfigVars;
import nano.web.util.JsonPathModule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    private final ConfigVars configVars;

    public TranslationService(ConfigVars configVars) {
        this.configVars = configVars;
    }

    public String translate(String input, String from, String to) {
        var appId = this.configVars.getBaiduTranslationAppId();
        var secretKey = this.configVars.getBaiduTranslationSecretKey();
        var salt = Instant.now().toString();
        var data = (appId + input + salt + secretKey).getBytes(UTF_8);
        var sign = DigestUtils.md5DigestAsHex(data);

        var publisher = Fetch.newFormBodyPublisher();
        publisher.append("q", input);
        publisher.append("appid", appId);
        publisher.append("salt", salt);
        publisher.append("from", from);
        publisher.append("to", to);
        publisher.append("sign", sign);
        var url = URI.create(TRANSLATION_API);
        var request = HttpRequest.newBuilder(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(publisher.build())
                .build();
        var response = Fetch.fetch(request);
        return buildTranslateResult(response.body());
    }

    private static String buildTranslateResult(@NotNull InputStream stream) {
        if (ObjectUtils.isEmpty(stream)) {
            return "The translation result is empty";
        }
        var context = JsonPathModule.parse(stream);
        List<Map<String, String>> result = context.read("$.trans_result");
        if (ObjectUtils.isEmpty(result)) {
            return "Translation error: " + context.read("$.error_msg");
        }
        return result.stream().map(it -> it.get("dst")).collect(Collectors.joining("\n"));
    }
}
