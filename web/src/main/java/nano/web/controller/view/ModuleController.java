package nano.web.controller.view;

import nano.web.scripting.Scripting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/modules")
public class ModuleController {

    private static final String MODULE_TEMPLATE = "module_template.mjs";

    private final StaticDeps staticDeps;

    public ModuleController(StaticDeps staticDeps) {
        this.staticDeps = staticDeps;
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
        return this.staticDeps.getModuleUrl(moduleName);
    }

}
