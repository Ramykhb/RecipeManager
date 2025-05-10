package com.project.recipemanager;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
public class Controller {
    @RequestMapping(value = "/{path:[^\\.]+}")
    public String redirect() {
        return "redirect:/recipes";
    }

    @RequestMapping(value = "/")
    public String Home() {
        return "redirect:/recipes";
    }

    @RequestMapping(value = "/swagger-ui")
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }

    @RequestMapping(value = "/swagger-ui/")
    public String swaggerv2() {
        return "redirect:/swagger-ui/index.html";
    }

}
