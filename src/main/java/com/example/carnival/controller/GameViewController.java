package com.example.carnival.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameViewController {

    @GetMapping("/game/push")
    public String pushView() {
        return "push";
    }

    @GetMapping("/game/cherrypick")
    public String cherryPickView() {
        return "cherrypick";
    }
    
    @GetMapping("/game/reset")
    public String resetView() {
        return "reset";
    }
}
