package nano.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "nano");
        model.addAttribute("page", "/pages/nano/index.jsx");
        return "page.html";
    }

    @GetMapping("/nano")
    public String nano(Model model) {
        model.addAttribute("title", "nano");
        model.addAttribute("page", "/pages/nano/nano.jsx");
        return "page.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("page", "/pages/account/login.jsx");
        return "page.html";
    }

    @GetMapping("/token")
    public String token(Model model) {
        model.addAttribute("title", "Token");
        model.addAttribute("page", "/pages/account/token.jsx");
        return "page.html";
    }

    @GetMapping("/openjdk")
    public String openjdk(Model model) {
        model.addAttribute("title", "OpenJDK");
        model.addAttribute("page", "/pages/openjdk/openjdk.jsx");
        return "page.html";
    }

    @GetMapping("/mail")
    public String mail(Model model) {
        model.addAttribute("title", "Mail");
        model.addAttribute("page", "/pages/mail/mail.jsx");
        return "page.html";
    }

    @GetMapping("/util/name-key")
    public String nameKey(Model model) {
        model.addAttribute("title", "NameKey");
        model.addAttribute("page", "/pages/util/name_key.jsx");
        return "page.html";
    }

}
