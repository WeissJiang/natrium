package nano.web.nano;

import org.jianzhao.jsonpath.JsonPathModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

/**
 * Handle module request
 *
 * @author cbdyzj
 * @since 2021.1.23
 */
@Service
public class ViewService {

    private final JsonPathModule.ReadContext modules;
    private final String currentProfile;

    public ViewService(@Value("classpath:/static/modules.json") Resource modulesJson,
                       @Value("${spring.profiles.active}") String profile) throws IOException {
        this.currentProfile = profile;
        try (var is = modulesJson.getInputStream()) {
            this.modules = JsonPathModule.parse(is);
        }
    }

    /**
     * module_template.mjs
     * model: moduleUrl, exportName
     */
    public ModelAndView buildModule(String moduleName) {
        Map<String, ?> module = this.modules.read("$.%s".formatted(moduleName));
        Assert.notNull(module, "module not found: " + moduleName);
        // build
        var moduleUrl = module.get(this.currentProfile);
        var exportName = module.get("exportName");
        var modal = Map.of("moduleUrl", moduleUrl, "exportName", exportName);
        var mav = new ModelAndView();
        mav.setViewName("module_template.mjs");
        mav.addAllObjects(modal);
        return mav;
    }

    /**
     * page_template.html
     * model: title, page
     */
    public ModelAndView buildPage(String title, String page) {
        var model = Map.of("title", title, "page", page);
        var mav = new ModelAndView();
        mav.setViewName("page_template.html");
        mav.addAllObjects(model);
        return mav;
    }
}
