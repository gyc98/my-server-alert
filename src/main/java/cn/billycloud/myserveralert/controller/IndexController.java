package cn.billycloud.myserveralert.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String hello(){
        return "redirect:/view/index.html";
    }
}
