package com.babypal.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    @GetMapping(value = "/", produces = "text/html")
    public String hello() {
        return "<html><body><h1>BabyPal Backend</h1></body></html>";
    }
}