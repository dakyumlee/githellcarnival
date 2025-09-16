package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/team")
public class TeamNpcApi {

    private static final Map<String, Integer> teamMood = new HashMap<>();
    private static final List<String> slackMessages = new ArrayList<>();
    
    static {
        teamMood.put("alice", 70);
        teamMood.put("bob", 85);
        teamMood.put("charlie", 60);
        teamMood.put("diana", 90);
        teamMood.put("eve", 50);
        
        slackMessages.add("alice: 또 merge conflict 났네요 😤");
        slackMessages.add("bob: 이번 주 야근 3번째...");
        slackMessages.add("charlie: 누가 main에 직접 푸시했나요?");
        slackMessages.add("diana: 테스트 코드 좀 써주세요 🙏");
        slackMessages.add("eve: 커피 떨어졌어요");
    }

    public static class ActionRequest {
        public String action;
        public String target;
        public String message;
    }

    @GetMapping("/status")
    public Map<String, Object> getTeamStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("teamMood", teamMood);
        response.put("averageMood", calculateAverageMood());
        response.put("slackMessages", slackMessages.subList(Math.max(0, slackMessages.size() - 5), slackMessages.size()));
        response.put("hint", "팀 분위기 관리가 중요합니다");
        return response;
    }

    @PostMapping("/action")
    public Map<String, Object> performAction(@RequestBody ActionRequest req) {
        Map<String, Object> response = new HashMap<>();
        String action = req.action;
        String target = req.target;
        
        if (!teamMood.containsKey(target)) {
            response.put("success", false);
            response.put("message", "존재하지 않는 팀원입니다");
            return response;
        }
        
        int currentMood = teamMood.get(target);
        int moodChange = 0;
        String message = "";
        
        switch (action) {
            case "praise":
                moodChange = 15;
                message = target + "의 기분이 좋아졌습니다!";
                addSlackMessage(target + ": 고마워요! 😊");
                break;
            case "blame":
                moodChange = -20;
                message = target + "의 기분이 나빠졌습니다...";
                addSlackMessage(target + ": 제 잘못인가요... 😔");
                break;
            case "coffee":
                moodChange = 10;
                message = target + "에게 커피를 줬습니다";
                addSlackMessage(target + ": 커피 고마워요! ☕");
                break;
            default:
                response.put("success", false);
                response.put("message", "알 수 없는 액션입니다");
                return response;
        }
        
        int newMood = Math.max(0, Math.min(100, currentMood + moodChange));
        teamMood.put(target, newMood);
        
        response.put("success", true);
        response.put("message", message);
        response.put("moodChange", moodChange);
        response.put("newMood", newMood);
        response.put("averageMood", calculateAverageMood());
        response.put("hint", "팀워크가 생산성에 영향을 줍니다");
        
        return response;
    }
    
    private int calculateAverageMood() {
        return (int) teamMood.values().stream().mapToInt(Integer::intValue).average().orElse(0);
    }
    
    private void addSlackMessage(String message) {
        slackMessages.add(message);
        if (slackMessages.size() > 20) {
            slackMessages.remove(0);
        }
    }
}