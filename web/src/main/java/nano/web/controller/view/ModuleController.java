package nano.web.controller.view;

import nano.web.nano.ModuleService;
import nano.web.scripting.Scripting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Module controller
 *
 * @author cbdyzj
 * @see 2020.9.22
 */
@Controller
@RequestMapping(path = "/modules")
public class ModuleController {

    public ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping(path = "/{moduleName}", produces = Scripting.TEXT_JAVASCRIPT)
    public String getModule(@PathVariable("moduleName") String moduleName, Model model) {
        Map<String, ?> module = this.moduleService.getModuleModel(moduleName);
        model.addAllAttributes(module);
        return "module_template.mjs";
    }

}
