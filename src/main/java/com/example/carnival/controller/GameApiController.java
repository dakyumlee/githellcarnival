package com.example.carnival.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class GameApiController {

    private final List<String> expected = Arrays.asList(
            "git add .",
            "git commit -m",
            "git push origin main"
    );

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody Map<String, String> body) {
        String input = body.get("command");
        Map<String, Object> result = new HashMap<>();

        if (input == null || input.isBlank()) {
            result.put("success", false);
            result.put("message", "😴 아무 것도 안 치고 Enter라니... 졸고 있나?");
            return result;
        }

        List<String> lines = Arrays.asList(input.split("\\r?\\n"));
        int step = 0;

        for (String line : lines) {
            line = line.trim();
            if (step == 0 && line.equals("git add .")) {
                step++;
            } else if (step == 1 && line.startsWith("git commit -m")) {
                step++;
            } else if (step == 2 && line.equals("git push origin main")) {
                step++;
            } else {
                result.put("success", false);
                result.put("message", "❌ '" + line + "' 은(는) 틀렸습니다. 👉 정답 예시: " + expected.get(step));
                return result;
            }
        }

        if (step == expected.size()) {
            result.put("success", true);
            result.put("message", "✅ Push 성공! 갓발자 등극 🎉");
        } else {
            result.put("success", false);
            result.put("message", "⌛ 아직 단계 부족! 다음 명령: " + expected.get(step));
        }

        return result;
    }
}
