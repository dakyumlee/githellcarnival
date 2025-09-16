package com.example.carnival.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CherryPickController {

    @GetMapping("/game/cherrypick")
    public String cherryPickPage() {
        return "cherrypick";
    }
}
