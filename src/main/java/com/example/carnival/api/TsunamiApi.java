package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/tsunami")
public class TsunamiApi {

    public static class GameResult {
        public int score;
        public int wave;
        public int totalCommits;
        public int successfulReverts;
        public int maxCombo;
        public int timePlayedMs;
    }

    public static class CommitAction {
        public String commitId;
        public String commitType;
        public String action;
    }

    @PostMapping("/end-game")
    public Map<String, Object> endGame(@RequestBody GameResult result) {
        Map<String, Object> res = new HashMap<>();
        
        int successRate = result.totalCommits > 0 ? 
            (result.successfulReverts * 100) / result.totalCommits : 0;
        
        int finalScore = result.score;
        String message;
        String achievement = null;
        
        if (result.score > 2000) {
            message = "쓰나미 마스터! 완벽한 커밋 관리!";
            achievement = "tsunami-master";
            finalScore += 200;
        } else if (result.score > 1000) {
            message = "훌륭한 대응! 대부분의 쓰나미를 막았습니다!";
            achievement = "wave-defender";
            finalScore += 100;
        } else if (result.score > 500) {
            message = "나쁘지 않네요. 연습이 더 필요합니다.";
        } else {
            message = "코드 히스토리가 완전히 휘발되었습니다...";
            achievement = "history-destroyer";
        }
        
        if (result.maxCombo >= 20) {
            achievement = "combo-king";
            message += " 놀라운 콤보!";
            finalScore += 150;
        }
        
        if (result.wave >= 10) {
            achievement = "endurance-champion";
            message += " 지구력 챔피언!";
            finalScore += 100;
        }
        
        res.put("success", result.score > 500);
        res.put("message", message);
        res.put("finalScore", finalScore);
        res.put("scoreDiff", finalScore - result.score);
        res.put("successRate", successRate);
        res.put("achievement", achievement);
        res.put("stats", Map.of(
            "totalCommits", result.totalCommits,
            "successfulReverts", result.successfulReverts,
            "maxCombo", result.maxCombo,
            "finalWave", result.wave,
            "survivalTime", result.timePlayedMs / 1000 + "초"
        ));
        
        return res;
    }

    @GetMapping("/commit-templates")
    public Map<String, Object> getCommitTemplates() {
        Map<String, Object> res = new HashMap<>();
        
        List<Map<String, String>> goodCommits = Arrays.asList(
            Map.of("hash", "a1b2c3d", "message", "feat: add user authentication", "author", "alice"),
            Map.of("hash", "e4f5g6h", "message", "fix: resolve memory leak", "author", "bob"),
            Map.of("hash", "i7j8k9l", "message", "docs: update README", "author", "charlie"),
            Map.of("hash", "m0n1o2p", "message", "test: add unit tests", "author", "diana")
        );
        
        List<Map<String, String>> badCommits = Arrays.asList(
            Map.of("hash", "deadbee", "message", "console.log everywhere lol", "author", "intern"),
            Map.of("hash", "badc0de", "message", "quick fix dont review", "author", "rushed_dev"),
            Map.of("hash", "f00ba4", "message", "TODO: fix this later", "author", "lazy_coder"),
            Map.of("hash", "1337h4x", "message", "rm -rf / just kidding", "author", "script_kiddie")
        );
        
        res.put("good", goodCommits);
        res.put("bad", badCommits);
        
        return res;
    }
}