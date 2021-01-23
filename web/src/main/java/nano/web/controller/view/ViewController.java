package nano.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * View controller
 *
 * @author cbdyzj
 * @see 2020.9.22
 */
@Controller
public class ViewController {

    private static final String PAGE_TEMPLATE = "page_template.html";

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "nano");
        model.addAttribute("page", "/pages/nano/index.jsx");
        return PAGE_TEMPLATE;
    }

    @GetMapping("/nano")
    public String nano(Model model) {
        model.addAttribute("title", "nano");
        model.addAttribute("page", "/pages/nano/nano.jsx");
        return PAGE_TEMPLATE;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("page", "/pages/account/login.jsx");
        return PAGE_TEMPLATE;
    }

    @GetMapping("/token")
    public String token(Model model) {
        model.addAttribute("title", "Token");
        model.addAttribute("page", "/pages/account/token.jsx");
        return PAGE_TEMPLATE;
    }

    @GetMapping("/openjdk")
    public String openjdk(Model model) {
        model.addAttribute("title", "OpenJDK");
        model.addAttribute("page", "/pages/openjdk/openjdk.jsx");
        return PAGE_TEMPLATE;
    }

    @GetMapping("/mail")
    public String mail(Model model) {
        model.addAttribute("title", "Mail");
        model.addAttribute("page", "/pages/mail/mail.jsx");
        return PAGE_TEMPLATE;
    }

    @GetMapping("/tools/name-key")
    public String nameKey(Model model) {
        model.addAttribute("title", "NameKey");
        model.addAttribute("page", "/pages/tools/name_key.jsx");
        return PAGE_TEMPLATE;
    }

    @GetMapping("/counter")
    public String counter(Model model) {
        model.addAttribute("title", "Mail");
        model.addAttribute("page", "/pages/counter/counter.jsx");
        return PAGE_TEMPLATE;
    }
}
