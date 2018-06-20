package org.example.app.web1.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @Value("${welcome.message}")
    private String welcomeMessage;

    @RequestMapping("/")
    public String index() {
        return welcomeMessage;
    }
}
