package com.example.carnival.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cherrypick")
public class CherryPickApiController {

    private final Map<String, String> commits = new LinkedHashMap<>();

    public CherryPickApiController() {
        commits.put("a1b2c3d", "Fix bug in login flow");
        commits.put("d4e5f6g", "Add README details");
        commits.put("h7i8j9k", "Optimize DB query");
        commits.put("l0m1n2o", "Fix typo in UI");
    }

    @GetMapping("/commits")
    public Map<String, Object> getCommits() {
        Map<String, Object> result = new HashMap<>();
        result.put("commits", commits);
        return result;
    }

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody Map<String, String> body) {
        String input = body.get("command");
        Map<String, Object> result = new HashMap<>();

        if (input == null || !input.startsWith("git cherry-pick")) {
            result.put("success", false);
            result.put("message", "❌ 명령어는 'git cherry-pick <hash>' 형식이어야 해요.");
            return result;
        }

        String[] parts = input.split(" ");
        if (parts.length < 3) {
            result.put("success", false);
            result.put("message", "⚠️ 커밋 해시를 입력해야 합니다.");
            return result;
        }

        String hash = parts[2].trim();
        if (commits.containsKey(hash) && commits.get(hash).toLowerCase().contains("fix")) {
            result.put("success", true);
            result.put("message", "✅ 성공! '" + commits.get(hash) + "' 체리픽 완료 🍒");
        } else if (commits.containsKey(hash)) {
            result.put("success", false);
            result.put("message", "💥 '" + commits.get(hash) + "' 은(는) 버그 픽스가 아닙니다! -999점");
        } else {
            result.put("success", false);
            result.put("message", "🤔 알 수 없는 커밋 해시: " + hash);
        }

        return result;
    }
}
