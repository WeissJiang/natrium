package nano.web.controller.nano;

import nano.web.nano.ViewService;
import nano.web.scripting.Scripting;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * View controller
 *
 * @author cbdyzj
 * @see 2020.9.22
 */
@Controller
public class ViewController {

    private final ViewService viewService;

    public ViewController(ViewService viewService) {
        this.viewService = viewService;
    }

    @GetMapping(path = "/modules/{moduleName}", produces = Scripting.TEXT_JAVASCRIPT)
    public ModelAndView getModule(@PathVariable("moduleName") String moduleName) {
        return this.viewService.buildModule(moduleName);
    }

    @GetMapping("/")
    public ModelAndView index() {
        return this.viewService.buildPage("nano", "/pages/nano/index.jsx");
    }

    @GetMapping("/nano")
    public ModelAndView nano() {
        return this.viewService.buildPage("nano", "/pages/nano/nano.jsx");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return this.viewService.buildPage("Login", "/pages/account/login.jsx");
    }

    @GetMapping("/token")
    public ModelAndView token() {
        return this.viewService.buildPage("Token", "/pages/account/token.jsx");
    }

    @GetMapping("/openjdk")
    public ModelAndView openjdk() {
        return this.viewService.buildPage("OpenJDK", "/pages/openjdk/openjdk.jsx");
    }

    @GetMapping("/mail")
    public ModelAndView mail() {
        return this.viewService.buildPage("Mail", "/pages/mail/mail.jsx");
    }

    @GetMapping("/tools/name-key")
    public ModelAndView nameKey() {
        return this.viewService.buildPage("NameKey", "/pages/tools/name_key.jsx");
    }

    @GetMapping("/counter")
    public ModelAndView counter() {
        return this.viewService.buildPage("Counter", "/pages/counter/counter.jsx");
    }

    @GetMapping("/sandbox")
    public ModelAndView sandbox() {
        return this.viewService.buildPage("Sandbox", "/pages/sandbox/sandbox.jsx");
    }

    @GetMapping("/epub")
    public ModelAndView epub() {
        return this.viewService.buildPage("ePub", "/pages/epub/epub.jsx");
    }

}
