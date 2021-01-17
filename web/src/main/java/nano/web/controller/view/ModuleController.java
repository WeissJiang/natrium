package nano.web.controller.view;

import nano.web.scripting.Scripting;
import org.jianzhao.jsonpath.JsonPathModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Module controller
 *
 * @author cbdyzj
 * @see 2020.9.22
 */
@Controller
@RequestMapping("/modules")
public class ModuleController {

    private static final String MODULE_TEMPLATE = "module_template.mjs";

    private final Map<String, String> moduleUrlMap = new HashMap<>();

    public ModuleController(@Value("classpath:/static/deps.json") Resource depsJson,
                            @Value("${spring.profiles.active}") String activeProfile) throws IOException {
        try (var is = depsJson.getInputStream()) {
            var jsonPath = "$.%s".formatted(activeProfile);
            this.moduleUrlMap.putAll(JsonPathModule.read(is,jsonPath));
        }
    }

    @GetMapping(path = "/react", produces = Scripting.TEXT_JAVASCRIPT)
    public String react(Model model) {
        model.addAttribute("moduleUrl", this.getUrl("react"));
        model.addAttribute("exportName", "React");
        return MODULE_TEMPLATE;
    }

    @GetMapping(path = "/react-dom", produces = Scripting.TEXT_JAVASCRIPT)
    public String reactDOM(Model model) {
        model.addAttribute("moduleUrl", this.getUrl("react-dom"));
        model.addAttribute("exportName", "ReactDOM");
        return MODULE_TEMPLATE;
    }

    @GetMapping(path = "/react-dom-server", produces = Scripting.TEXT_JAVASCRIPT)
    public String reactDOMServer(Model model) {
        model.addAttribute("moduleUrl", this.getUrl("react-dom-server"));
        model.addAttribute("exportName", "ReactDOMServer");
        return MODULE_TEMPLATE;
    }

    /**
     * Get module url by module name
     *
     * @param moduleName module name
     * @return module url
     */
    private String getUrl(String moduleName) {
        return this.moduleUrlMap.get(moduleName);
    }

}
