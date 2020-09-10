package nano.web.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("prod")
@Controller
public class ViewController {

    @GetMapping("/deps.mjs")
    public String profileDeps() {
        return "forward:deps.prod.mjs";
    }
}

