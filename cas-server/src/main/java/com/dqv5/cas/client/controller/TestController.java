package com.dqv5.cas.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author duqian
 * @date 2019-04-26
 */
@RestController
public class TestController {
    @GetMapping("/demo")
    public String demo() {
        return "ok";
    }
}
