package nano.web.controller.view;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import nano.support.cache.LocalCached;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StaticDeps {

    private String activeProfile;

    private DocumentContext deps;

    @LocalCached
    public String getModuleUrl(String moduleName) {
        var path = "$.%s.%s".formatted(this.activeProfile, moduleName);
        return this.deps.read(path);
    }

    @Value("classpath:/static/deps.json")
    public void setDeps(Resource depsJson) throws IOException {
        try (var is = depsJson.getInputStream()) {
            this.deps = JsonPath.parse(is);
        }
    }

    @Value("${spring.profiles.active}")
    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }
}
