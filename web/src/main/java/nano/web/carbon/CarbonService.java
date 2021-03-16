package nano.web.carbon;

import nano.support.Json;
import nano.support.UniqueChecker;
import nano.web.carbon.model.*;
import nano.web.nano.repository.KeyValueRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
            var overview = new CarbonAppOverview();
            var app = getApp(it);
            //
            overview.setId(app.getId());
            overview.setName(app.getName());
            overview.setDescription(app.getDescription());
            var pageList = requireNonNullElse(app.getPageList(), Collections.<CarbonPage>emptyList());
            overview.setPageCount(pageList.size());
            var keyCount = pageList.stream()
                    .map(CarbonPage::getKeyList)
                    .filter(Objects::nonNull)
                    .mapToInt(Collection::size)
                    .sum();
            overview.setKeyCount(keyCount);
            return overview;
        });
    }

    public void createApp(@NotNull CarbonApp app) {
        validateApp(app);
        this.keyValueRepository.createKeyValue("%s:%s".formatted(CARBON, app.getId()), Json.encode(app));
    }

    public void updateApp(@NotNull CarbonApp app) {
        validateApp(app);
        this.keyValueRepository.updateKeyValue("%s:%s".formatted(CARBON, app.getId()), Json.encode(app));
    }

    private static void validateApp(@NotNull CarbonApp app) {
        Assert.notNull(app, "app must be not null");
        Assert.notNull(app.getId(), "app id must be not null");
        Assert.notEmpty(app.getLocaleList(), "app locale list must be not empty");
        Assert.notNull(app.getFallbackLocale(), "app fallback locale must be not null");
        //
        var pageList = app.getPageList();
        if (CollectionUtils.isEmpty(pageList)) {
            return;
        }
        var pageCodeUniqueChecker = new UniqueChecker<String>();
        var keyKeyUniqueChecker = new UniqueChecker<String>();
        for (CarbonPage page : pageList) {
            Assert.notNull(page, "page must be not null");
            Assert.notNull(page.getCode(), "page code must be not null");
            pageCodeUniqueChecker.check(page.getCode(), "duplicate page code");
            //
            var keyList = page.getKeyList();
            if (CollectionUtils.isEmpty(keyList)) {
                continue;
            }
            for (CarbonKey key : keyList) {
                Assert.notNull(key, "key must be not null");
                Assert.notNull(key.getKey(), "key key must be not null");
                keyKeyUniqueChecker.check(key.getKey(), "duplicate key key");
                Assert.notNull(key.getPageCode(), "key page code must be not null");
                Assert.isTrue(Objects.equals(page.getCode(), key.getPageCode()), "Key page code does not match");
                Assert.notEmpty(key.getOriginal(), "key original must be not empty");
            }
        }
    }

    public @NotNull CarbonText getText(@NotNull String appId, @NotNull String key, @NotNull String locale) {
        var app = this.getApp(appId);
        var pageList = app.getPageList();
        Assert.notEmpty(pageList, "app has no pages");
        var carbonKey = pageList.stream()
                .map(CarbonPage::getKeyList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(it -> Objects.equals(key, it.getKey()))
                .findFirst()
                .orElse(null);
        Assert.notNull(carbonKey, "key is absent");
        var translation = carbonKey.getTranslation();
        Assert.notEmpty(translation, "translation is empty");
        var textMap = translation.stream()
                .collect(Collectors.toMap(CarbonText::getLocale, Function.identity()));
        var text = textMap.get(locale);
        if (text == null) {
            var fallbackLocale = app.getFallbackLocale();
            text = textMap.get(fallbackLocale);
        }
        Assert.notNull(text, "key text is absent");
        return text;
    }

    public @NotNull CarbonApp getApp(@NotNull String appId) {
        var keyValue = this.keyValueRepository.queryKeyValue("%s:%s".formatted(CARBON, appId));
        Assert.notNull(keyValue, "carbon app record is absent");
        var value = keyValue.getValue();
        Assert.notNull(value, "carbon app value is absent");
        var app = Json.decodeValue(value, CarbonApp.class);
        Assert.notNull(app, "app is absent");
        return app;
    }

}
