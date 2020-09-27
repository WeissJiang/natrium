package nano.web.controller.view;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class StaticDeps {

    private Resource depsJson;
    private String activeProfile;
    private DocumentContext deps;

    @PostConstruct
    public void init() throws IOException {
        try (var is = this.depsJson.getInputStream()) {
            this.deps = JsonPath.parse(is);
        }
    }

    public String getModuleUrl(String moduleName) {
        var path = "$.%s.%s".formatted(this.activeProfile, moduleName);
        return deps.read(path);
    }

    @Value("classpath:/static/deps.json")
    public void setDepsJson(Resource depsJson) {
        this.depsJson = depsJson;
    }

    @Value("${spring.profiles.active}")
    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }
}
