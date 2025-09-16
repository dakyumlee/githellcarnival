package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/push")
public class PushApi {
    private int consecutiveFails = 0;
    private int totalPushes = 0;

    public static class PushRequest {
        public String strategy;
        public String command;
    }

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody PushRequest req) {
        Map<String, Object> result = new HashMap<>();
        totalPushes++;
        
        double baseSuccessRate = getBaseSuccessRate(req.strategy);
        double finalRate = applyModifiers(baseSuccessRate);
        boolean success = Math.random() < finalRate;
        
        if (success) {
            int score = calculateScore(req.strategy, true);
            consecutiveFails = 0;
            result.put("success", true);
            result.put("message", generateSuccessMessage(req.strategy));
            result.put("hint", generateSuccessHint());
            result.put("scoreDiff", score);
        } else {
            int penalty = calculateScore(req.strategy, false);
            consecutiveFails++;
            result.put("success", false);
            result.put("message", generateFailMessage(req.strategy));
            result.put("hint", generateFailHint());
            result.put("scoreDiff", penalty);
        }
        
        result.put("combo", consecutiveFails == 0 ? getSuccessCombo() : 0);
        result.put("teamMood", getTeamMood());
        return result;
    }

    private double getBaseSuccessRate(String strategy) {
        switch (strategy) {
            case "safe": return 0.85;
            case "risky": return 0.45;
            case "yolo": return 0.25;
            default: return 0.70;
        }
    }

    private double applyModifiers(double baseRate) {
        if (consecutiveFails >= 3) baseRate += 0.15;
        baseRate -= (totalPushes * 0.02);
        return Math.max(0.1, Math.min(0.95, baseRate));
    }

    private int calculateScore(String strategy, boolean success) {
        Map<String, Integer> baseScores = Map.of(
            "safe", success ? 10 : -5,
            "risky", success ? 25 : -15,
            "yolo", success ? 50 : -30
        );
        return baseScores.getOrDefault(strategy, success ? 15 : -10);
    }

    private String generateSuccessMessage(String strategy) {
        String[] safe = {"안전하게 배포 완료", "모든 테스트 통과", "코드 리뷰 승인"};
        String[] risky = {"위험한 푸시 성공!", "아슬아슬하게 통과", "기적적으로 성공"};
        String[] yolo = {"YOLO 푸시 대성공!", "불가능을 가능으로", "잭팟! 운영 무사"};
        
        String[] messages = strategy.equals("safe") ? safe : 
                           strategy.equals("risky") ? risky : yolo;
        return messages[(int)(Math.random() * messages.length)];
    }

    private String generateFailMessage(String strategy) {
        String[] safe = {"예상치 못한 실패", "일부 테스트 실패", "코드 리뷰 요청"};
        String[] risky = {"빌드 실패!", "운영 서버 에러", "배포 롤백 필요"};
        String[] yolo = {"서버 폭발!", "시스템 전체 다운", "팀장이 찾고 있음"};
        
        String[] messages = strategy.equals("safe") ? safe : 
                           strategy.equals("risky") ? risky : yolo;
        return messages[(int)(Math.random() * messages.length)];
    }

    private String generateSuccessHint() {
        String[] hints = {
            "tests: 91 passed, coverage: +3%",
            "build: 2m 34s, deploy: green",
            "lighthouse: 94/100, no regressions"
        };
        return hints[(int)(Math.random() * hints.length)];
    }

    private String generateFailHint() {
        String[] hints = {
            "ERROR: dependency conflict detected",
            "tests: 15 failed, coverage: -12%",
            "build: timeout after 10m"
        };
        return hints[(int)(Math.random() * hints.length)];
    }

    private int getSuccessCombo() {
        return Math.max(0, totalPushes - consecutiveFails);
    }

    private String getTeamMood() {
        if (consecutiveFails == 0) return "😊";
        if (consecutiveFails <= 2) return "😐";
        if (consecutiveFails <= 4) return "😠";
        return "🤬";
    }
}