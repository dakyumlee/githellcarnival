package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/detached")
public class DetachedApi {

    @PostMapping("/jump")
    public Map<String,Object> jump(@RequestBody Map<String,String> body) {
        String action = body.get("action");
        Map<String,Object> res = new HashMap<>();
        if ("branch".equals(action)) {
            res.put("success", true);
            res.put("message", "HEAD가 브랜치를 찾았습니다!");
            res.put("hint", "git switch main");
        } else {
            res.put("success", false);
            res.put("message", "HEAD가 허공을 떠돌다 추락...");
            res.put("hint", "Detached HEAD 상태: 브랜치로 돌아가야 합니다");
        }
        return res;
    }
}
