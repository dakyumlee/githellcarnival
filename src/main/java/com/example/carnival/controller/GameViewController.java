package com.example.carnival.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameViewController {

	@GetMapping("/arcade")
	public String arcade() {
	    return "index";
	 }

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

    @GetMapping("/game/stash")
    public String stashView() {
        return "stash";
    }

    @GetMapping("/game/detached")
    public String detachedView() {
        return "detached";
    }

    @GetMapping("/game/merge")
    public String mergeView() {
        return "merge";
    }

    @GetMapping("/game/review")
    public String reviewView() {
        return "review";
    }
}
