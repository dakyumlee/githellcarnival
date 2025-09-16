package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/stash")
public class StashApi {

    private static final Map<String,String> STASHES = new LinkedHashMap<>();
    static {
        STASHES.put("stash@{0}", "Fix login bug ✅");
        STASHES.put("stash@{1}", "Temp debug ❌");
        STASHES.put("stash@{2}", "CSS tweak ❌");
        STASHES.put("stash@{3}", "Fix NPE in service ✅");
        STASHES.put("stash@{4}", "Random console.log ❌");
    }

    @GetMapping("/list")
    public Map<String,String> list() {
        return STASHES;
    }

    @PostMapping("/pick")
    public Map<String,Object> pick(@RequestBody Map<String,String> body) {
        String key = body.get("key");
        Map<String,Object> res = new HashMap<>();
        if (key == null || !STASHES.containsKey(key)) {
            res.put("success", false);
            res.put("message", "존재하지 않는 stash 입니다");
            res.put("hint", "git stash list");
            return res;
        }
        boolean good = STASHES.get(key).contains("✅");
        res.put("success", good);
        res.put("message", good ? "필요한 stash 복원 성공!" : "쓸모없는 stash... 코드 증발!");
        res.put("hint", good ? "git stash apply "+key : "git stash drop "+key);
        return res;
    }
}
