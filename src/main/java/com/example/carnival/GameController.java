package com.example.carnival;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "🎮 Welcome to Git Hell Carnival!");
        return "index";
    }
}