package org.faicai.d4c.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {

    @RequestMapping(value = {"/", "/{path:^(?!api|static|.*\\.).*$}"})
    public String forward() {
        return "forward:/index.html";
    }
}