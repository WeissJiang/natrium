package nano.service.carbon;

import nano.service.carbon.model.*;
import nano.service.nano.repository.KeyValueRepository;
import nano.support.Json;
import nano.support.Unique;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNullElse;
import static nano.support.Sugar.map;

/**
 * carbon service
 *
 * @see <a href="https://carbonium.vercel.app/">carbon</a>
 */
@Service
public class CarbonService {

    private static final String CARBON = "carbon";

    private final KeyValueRepository keyValueRepository;

    public CarbonService(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    public @NotNull List<String> getAppIdList() {
        var pattern = "^%s:".formatted(CARBON);
        var keyList = this.keyValueRepository.queryKeyListByPattern(pattern);
        return map(keyList, it -> it.replaceFirst(pattern, ""));
    }

    public @NotNull List<CarbonAppOverview> getAppList() {
        return map(this.getAppIdList(), it -> {
            var app = this.getApp(it);
            var pageList = requireNonNullElse(app.pageList(), Collections.<CarbonPage>emptyList());
            var keyCount = pageList.stream()
                    .map(CarbonPage::keyList)
                    .filter(Objects::nonNull)
                    .mapToInt(Collection::size)
                    .sum();
            return new CarbonAppOverview(
                    app.id(),
                    app.name(),
                    app.description(),
                    app.url(),
                    pageList.size(),
                    keyCount
            );
        });
    }

    public @NotNull CarbonApp getApp(@NotNull String appId) {
        var keyValue = this.keyValueRepository.queryKeyValue("%s:%s".formatted(CARBON, appId));
        Assert.notNull(keyValue, "carbon app record is absent");
        var value = keyValue.value();
        Assert.notNull(value, "carbon app value is absent");
        var app = Json.decodeValue(value, CarbonApp.class);
        Assert.notNull(app, "app is absent");
        return app;
    }

    public void createApp(@NotNull CarbonApp app) {
        validateApp(app);
        this.keyValueRepository.createKeyValue("%s:%s".formatted(CARBON, app.id()), Json.encode(app));
    }

    public void updateApp(@NotNull CarbonApp app) {
        validateApp(app);
        this.keyValueRepository.updateKeyValue("%s:%s".formatted(CARBON, app.id()), Json.encode(app));
    }

    public @NotNull CarbonText getText(@NotNull String appId, @NotNull String key, @NotNull String locale) {
        var app = this.getApp(appId);
        var pageList = app.pageList();
        Assert.notEmpty(pageList, "app has no pages");
        var carbonKey = pageList.stream()
                .map(CarbonPage::keyList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(it -> Objects.equals(key, it.key()))
                .findFirst()
                .orElse(null);
        Assert.notNull(carbonKey, "key is absent");
        var translation = carbonKey.translation();
        Assert.notEmpty(translation, "translation is empty");
        var textMap = translation.stream()
                .collect(Collectors.toMap(CarbonText::locale, Function.identity()));
        var text = textMap.get(locale);
        if (text == null) {
            var fallbackLocale = app.fallbackLocale();
            text = textMap.get(fallbackLocale);
        }
        Assert.notNull(text, "key text is absent");
        return text;
    }



    private static void validateApp(@NotNull CarbonApp app) {
        Assert.notNull(app, "app must be not null");
        Assert.notNull(app.id(), "app id must be not null");
        Assert.notEmpty(app.localeList(), "app locale list must be not empty");
        Assert.notNull(app.fallbackLocale(), "app fallback locale must be not null");
        //
        var pageList = app.pageList();
        if (CollectionUtils.isEmpty(pageList)) {
            return;
        }
        var pageCodeUnique = new Unique<String>();
        var keyKeyUnique = new Unique<String>();
        for (CarbonPage page : pageList) {
            Assert.notNull(page, "page must be not null");
            Assert.notNull(page.code(), "page code must be not null");
            pageCodeUnique.accept(page.code(), "duplicate page code");
            //
            var keyList = page.keyList();
            if (CollectionUtils.isEmpty(keyList)) {
                continue;
            }
            for (CarbonKey key : keyList) {
                Assert.notNull(key, "key must be not null");
                Assert.notNull(key.key(), "key key must be not null");
                keyKeyUnique.accept(key.key(), "duplicate key key");
                Assert.notNull(key.pageCode(), "key page code must be not null");
                Assert.isTrue(Objects.equals(page.code(), key.pageCode()), "Key page code does not match");
                Assert.notNull(key.original(), "key original must be not null");
            }
        }
    }
}
