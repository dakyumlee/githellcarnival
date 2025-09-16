package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/tsunami")
public class TsunamiApi {

    public static class WaveRequest {
        public int wave;
        public int score;
        public int combo;
        public String action;
        public String commitType;
    }

    @PostMapping("/start")
    public Map<String, Object> startWave(@RequestBody WaveRequest req) {
        Map<String, Object> res = new HashMap<>();
        int wave = Math.max(1, req.wave);
        
        res.put("success", true);
        res.put("message", "Wave " + wave + " 시작!");
        res.put("wave", wave);
        res.put("speed", 1.0 + (wave * 0.3));
        res.put("spawnRate", Math.max(500, 2000 - (wave * 100)));
        res.put("hint", "나쁜 커밋을 클릭해서 revert하세요!");
        
        return res;
    }

    @PostMapping("/click")
    public Map<String, Object> handleClick(@RequestBody WaveRequest req) {
        Map<String, Object> res = new HashMap<>();
        String commitType = req.commitType;
        int combo = Math.max(0, req.combo);
        int score = Math.max(0, req.score);
        
        if ("bad".equals(commitType)) {
            int basePoints = 10;
            int comboBonus = combo * 2;
            int totalPoints = basePoints + comboBonus;
            
            res.put("success", true);
            res.put("message", "커밋 revert 성공!");
            res.put("scoreGain", totalPoints);
            res.put("newCombo", combo + 1);
            res.put("hint", "git revert " + generateCommitHash());
            
            if ((combo + 1) % 5 == 0) {
                res.put("bonusMessage", "COMBO BONUS! +50");
                res.put("comboBonus", 50);
            }
            
        } else if ("good".equals(commitType)) {
            res.put("success", false);
            res.put("message", "좋은 커밋을 되돌렸습니다! 생명력 -1");
            res.put("scoreGain", -20);
            res.put("newCombo", 0);
            res.put("hint", "좋은 커밋은 피해야 합니다!");
            res.put("lifeLost", true);
            
        } else {
            res.put("success", false);
            res.put("message", "알 수 없는 커밋 타입");
            res.put("scoreGain", 0);
            res.put("newCombo", combo);
        }
        
        return res;
    }

    @PostMapping("/miss")
    public Map<String, Object> handleMiss(@RequestBody WaveRequest req) {
        Map<String, Object> res = new HashMap<>();
        String commitType = req.commitType;
        
        if ("bad".equals(commitType)) {
            res.put("success", false);
            res.put("message", "나쁜 커밋이 프로덕션에 배포되었습니다!");
            res.put("scoreGain", -30);
            res.put("newCombo", 0);
            res.put("hint", "빠르게 revert해야 합니다!");
            res.put("lifeLost", true);
        } else {
            res.put("success", true);
            res.put("message", "좋은 커밋이 안전하게 통과했습니다");
            res.put("scoreGain", 5);
            res.put("newCombo", req.combo);
        }
        
        return res;
    }

    @PostMapping("/end")
    public Map<String, Object> endGame(@RequestBody WaveRequest req) {
        Map<String, Object> res = new HashMap<>();
        int finalScore = Math.max(0, req.score);
        int wave = Math.max(1, req.wave);
        
        String rank;
        String message;
        
        if (finalScore >= 2000) {
            rank = "Git Master";
            message = "완벽한 revert 컨트롤!";
        } else if (finalScore >= 1000) {
            rank = "Senior Developer";
            message = "숙련된 Git 사용자";
        } else if (finalScore >= 500) {
            rank = "Junior Developer";
            message = "기본기는 탄탄합니다";
        } else {
            rank = "Intern";
            message = "더 연습이 필요해요";
        }
        
        res.put("success", finalScore >= 500);
        res.put("message", message);
        res.put("rank", rank);
        res.put("finalScore", finalScore);
        res.put("maxWave", wave);
        res.put("scoreDiff", Math.min(200, finalScore / 10));
        res.put("hint", "git log --oneline으로 커밋 히스토리 확인");
        
        return res;
    }

    private String generateCommitHash() {
        String chars = "0123456789abcdef";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 7; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
}