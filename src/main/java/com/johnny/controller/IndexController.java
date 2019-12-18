package com.johnny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/firmware")
public class IndexController {
    @RequestMapping("/index")
    public String index(){
        //TODO
        return "index";
    }
}
