package org.zerock.HelloWorld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController2 {

    @GetMapping("/home")
    public void home() {
    }

}
