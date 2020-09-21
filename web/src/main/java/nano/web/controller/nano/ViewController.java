package nano.web.controller.nano;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @Autowired
    public ApplicationContext context;

    @GetMapping("/sugar")
    public String nanoView(Model model) {
        model.addAttribute("name", "Sugar");
        return "sugar.html";
    }
}
