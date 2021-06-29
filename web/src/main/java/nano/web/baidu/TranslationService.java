package nano.web.baidu;

import nano.support.NeverException;
import nano.support.http.FormBodyPublisher;
import nano.web.nano.ConfigVars;
import nano.web.util.JsonPathModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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

    private final HttpClient httpClient;

    private final ConfigVars configVars;

    public TranslationService(ConfigVars configVars, HttpClient httpClient) {
        this.configVars = configVars;
        this.httpClient = httpClient;
    }

    public String translate(String input, String from, String to) {
        var appId = this.configVars.getBaiduTranslationAppId();
        var secretKey = this.configVars.getBaiduTranslationSecretKey();
        var salt = Instant.now().toString();
        var data = (appId + input + salt + secretKey).getBytes(StandardCharsets.UTF_8);
        var sign = DigestUtils.md5DigestAsHex(data);

        var publisher = new FormBodyPublisher();
        publisher.append("q", input);
        publisher.append("appid", appId);
        publisher.append("salt", salt);
        publisher.append("from", from);
        publisher.append("to", to);
        publisher.append("sign", sign);
        var url = URI.create(TRANSLATION_API);
        var request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(publisher.build())
                .build();
        try {
            var body = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return buildTranslateResult(body);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
            throw new NeverException(ex);
        }
    }

    private static String buildTranslateResult(String json) {
        try {
            if (ObjectUtils.isEmpty(json)) {
                return "The translation result is empty";
            }
            var context = JsonPathModule.parse(json);

            List<Map<String, String>> result = context.read("$.trans_result");
            if (ObjectUtils.isEmpty(result)) {
                log.warn("Translation error: {}", json);
                return "Translation error: " + context.read("$.error_msg");
            }
            return result.stream().map(it -> it.get("dst")).collect(Collectors.joining("\n"));
        } catch (Exception ex) {
            log.warn("build translate result error: {}", json);
            throw ex;
        }
    }
}
