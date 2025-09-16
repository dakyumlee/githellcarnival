package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/cherrypick")
public class CherryPickApi {

    private static final Map<String, String> COMMITS = new LinkedHashMap<>() {{
        put("7fa21c", "Fix null pointer bug ✅");
        put("81bd92", "Update README ❌");
        put("af2c33", "Remove console.log ❌");
        put("b9f443", "Fix user login crash ✅");
        put("2cf882", "CSS margin 조정 ❌");
        put("c8e110", "Add debug print ❌");
        put("f1a77d", "Fix race condition ✅");
        put("19be72", "Update .gitignore ❌");
    }};

    @GetMapping("/commits")
    public Map<String, String> getCommits() {
        return COMMITS;
    }

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody Map<String, List<String>> request) {
        List<String> selected = request.get("selected");
        List<String> correct = Arrays.asList("7fa21c", "b9f443", "f1a77d");

        Map<String, Object> result = new HashMap<>();
        if (selected.containsAll(correct) && correct.containsAll(selected)) {
            result.put("success", true);
            result.put("message", "Cherry-pick 성공! 😎");
            result.put("hint", "👉 git cherry-pick <commit-hash>");
        } else {
            result.put("success", false);
            result.put("message", "Cherry-pick 실패 😡 잘못된 커밋을 선택했습니다!");
            result.put("hint", "Cherry-pick은 특정 커밋만 가져올 때 사용합니다.");
        }
        return result;
    }
}