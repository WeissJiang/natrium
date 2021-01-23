package nano.web.nano;

import org.jianzhao.jsonpath.JsonPathModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;

/**
 * Handle module request
 *
 * @author cbdyzj
 * @since 2021.1.23
 */
@Service
public class ModuleService {

    private final JsonPathModule.ReadContext modules;
    private final String currentProfile;

    public ModuleService(@Value("classpath:/static/modules.json") Resource modulesJson,
                         @Value("${spring.profiles.active}") String profile) throws IOException {
        this.currentProfile = profile;
        try (var is = modulesJson.getInputStream()) {
            this.modules = JsonPathModule.parse(is);
        }
    }

    public Map<String, ?> getModuleModel(String moduleName) {
        Map<String, String> module = this.modules.read("$.%s".formatted(moduleName));
        Assert.notNull(module, "module not found: " + moduleName);

        var profile = module.get(this.currentProfile);
        String exportName = module.get("export");
        return Map.of("moduleUrl", profile, "exportName", exportName);
    }
}
