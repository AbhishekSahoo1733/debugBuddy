package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoomController {

    @GetMapping("/boom")
    public String boom() {
        throw new IllegalStateException("Intentional failure to test local log analyzer MVP");
    }
}
