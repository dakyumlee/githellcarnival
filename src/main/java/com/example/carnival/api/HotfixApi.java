package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/hotfix")
public class HotfixApi {

    public static class HotfixRequest {
        public int completedSteps;
        public int elapsedTimeMs;
        public boolean success;
    }

    @PostMapping("/complete")
    public Map<String, Object> complete(@RequestBody HotfixRequest req) {
        Map<String, Object> res = new HashMap<>();
        
        int elapsedMinutes = req.elapsedTimeMs / 60000;
        int revenuePerMinute = 2830;
        int totalLoss = elapsedMinutes * revenuePerMinute;
        
        int score = 0;
        String message;
        String achievement = null;
        
        if (req.success && req.completedSteps >= 8) {
            score = 100;
            message = "핫픽스 성공! 서버가 다시 살아났습니다!";
            
            if (elapsedMinutes < 2) {
                score += 100;
                achievement = "speed-demon";
                message += " 번개같은 속도!";
            } else if (elapsedMinutes < 3) {
                score += 50;
                achievement = "quick-fix";
                message += " 빠른 대응!";
            }
            
            res.put("serverStatus", "UP");
        } else {
            score = -50;
            message = "시간 초과! 서버가 계속 다운상태입니다...";
            res.put("serverStatus", "DOWN");
            achievement = "server-killer";
        }
        
        res.put("success", req.success);
        res.put("message", message);
        res.put("scoreDiff", score);
        res.put("totalLoss", totalLoss);
        res.put("completionRate", Math.min(100, (req.completedSteps * 100) / 8));
        res.put("achievement", achievement);
        
        return res;
    }

    @GetMapping("/status")
    public Map<String, Object> getServerStatus() {
        Map<String, Object> res = new HashMap<>();
        res.put("serverStatus", "DOWN");
        res.put("affectedUsers", 12547 + (int)(Math.random() * 1000));
        res.put("revenuePerMinute", 2830);
        res.put("urgency", "CRITICAL");
        return res;
    }

    @PostMapping("/step")
    public Map<String, Object> completeStep(@RequestBody Map<String, Integer> req) {
        int step = req.get("step");
        Map<String, Object> res = new HashMap<>();
        
        String[] stepMessages = {
            "",
            "에러 로그 분석 완료",
            "핫픽스 브랜치 생성됨",
            "코드 수정 완료",
            "테스트 통과",
            "코드 리뷰 승인",
            "스테이징 배포 성공",
            "운영 배포 완료",
            "서버 상태 정상 확인"
        };
        
        if (step >= 1 && step <= 8) {
            res.put("success", true);
            res.put("message", stepMessages[step]);
            res.put("step", step);
            res.put("progress", (step * 100) / 8);
        } else {
            res.put("success", false);
            res.put("message", "잘못된 단계입니다");
        }
        
        return res;
    }
}