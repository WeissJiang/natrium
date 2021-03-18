package nano.web.controller.nano;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/view")
public class ViewController {

    @GetMapping("/redirect")
    public RedirectView redirect(@RequestParam("url") String url) {
        return new RedirectView(url);
    }
}
