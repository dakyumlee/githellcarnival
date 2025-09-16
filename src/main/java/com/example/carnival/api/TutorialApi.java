package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/tutorial")
public class TutorialApi {

    public static class ProgressRequest {
        public String lessonId;
        public String action;
        public int quizAnswer;
        public Map<String, Boolean> progress;
    }

    private static final Map<String, Map<String, Object>> LESSONS = new HashMap<>();
    
    static {
        Map<String, Object> basics = new HashMap<>();
        basics.put("id", "basics");
        basics.put("title", "Git 기초");
        basics.put("icon", "📖");
        basics.put("difficulty", 1);
        basics.put("points", 100);
        basics.put("quiz", Map.of(
            "question", "Git의 세 가지 주요 영역이 아닌 것은?",
            "options", Arrays.asList("Working Directory", "Staging Area", "Git Repository", "Backup Directory"),
            "correct", 3
        ));
        LESSONS.put("basics", basics);

        Map<String, Object> addCommit = new HashMap<>();
        addCommit.put("id", "add-commit");
        addCommit.put("title", "Add & Commit");
        addCommit.put("icon", "💾");
        addCommit.put("difficulty", 2);
        addCommit.put("points", 150);
        addCommit.put("practiceGame", "push");
        LESSONS.put("add-commit", addCommit);

        Map<String, Object> branches = new HashMap<>();
        branches.put("id", "branches");
        branches.put("title", "브랜치");
        branches.put("icon", "🌿");
        branches.put("difficulty", 3);
        branches.put("points", 200);
        LESSONS.put("branches", branches);

        Map<String, Object> mergeRebase = new HashMap<>();
        mergeRebase.put("id", "merge-rebase");
        mergeRebase.put("title", "Merge & Rebase");
        mergeRebase.put("icon", "🔄");
        mergeRebase.put("difficulty", 4);
        mergeRebase.put("points", 250);
        mergeRebase.put("practiceGame", "merge");
        LESSONS.put("merge-rebase", mergeRebase);

        Map<String, Object> cherryPick = new HashMap<>();
        cherryPick.put("id", "cherry-pick");
        cherryPick.put("title", "Cherry Pick");
        cherryPick.put("icon", "🍒");
        cherryPick.put("difficulty", 3);
        cherryPick.put("points", 200);
        cherryPick.put("practiceGame", "cherrypick");
        LESSONS.put("cherry-pick", cherryPick);

        Map<String, Object> stash = new HashMap<>();
        stash.put("id", "stash");
        stash.put("title", "Stash");
        stash.put("icon", "📦");
        stash.put("difficulty", 2);
        stash.put("points", 150);
        stash.put("practiceGame", "stash");
        LESSONS.put("stash", stash);

        Map<String, Object> reset = new HashMap<>();
        reset.put("id", "reset");
        reset.put("title", "Reset");
        reset.put("icon", "🔥");
        reset.put("difficulty", 5);
        reset.put("points", 300);
        reset.put("practiceGame", "reset");
        LESSONS.put("reset", reset);
    }

    @GetMapping("/lessons")
    public Map<String, Object> getLessons() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("lessons", LESSONS.values());
        response.put("totalLessons", LESSONS.size());
        return response;
    }

    @GetMapping("/lesson/{id}")
    public Map<String, Object> getLesson(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        if (!LESSONS.containsKey(id)) {
            response.put("success", false);
            response.put("message", "존재하지 않는 레슨입니다");
            return response;
        }
        
        response.put("success", true);
        response.put("lesson", LESSONS.get(id));
        response.put("hint", "차근차근 따라해보세요");
        
        return response;
    }

    @PostMapping("/complete")
    public Map<String, Object> completeLesson(@RequestBody ProgressRequest req) {
        Map<String, Object> response = new HashMap<>();
        
        if (!LESSONS.containsKey(req.lessonId)) {
            response.put("success", false);
            response.put("message", "존재하지 않는 레슨입니다");
            return response;
        }
        
        Map<String, Object> lesson = LESSONS.get(req.lessonId);
        int points = (Integer) lesson.get("points");
        String title = (String) lesson.get("title");
        
        response.put("success", true);
        response.put("message", title + " 학습을 완료했습니다!");
        response.put("scoreDiff", points);
        response.put("hint", "다음 레슨으로 넘어가세요");
        
        Map<String, String> achievement = checkAchievements(req.lessonId, req.progress);
        if (achievement != null) {
            response.put("achievement", achievement);
        }
        
        return response;
    }

    @PostMapping("/quiz")
    public Map<String, Object> submitQuiz(@RequestBody ProgressRequest req) {
        Map<String, Object> response = new HashMap<>();
        
        if (!LESSONS.containsKey(req.lessonId)) {
            response.put("success", false);
            response.put("message", "존재하지 않는 레슨입니다");
            return response;
        }
        
        Map<String, Object> lesson = LESSONS.get(req.lessonId);
        Map<String, Object> quiz = (Map<String, Object>) lesson.get("quiz");
        
        if (quiz == null) {
            response.put("success", false);
            response.put("message", "이 레슨에는 퀴즈가 없습니다");
            return response;
        }
        
        int correctAnswer = (Integer) quiz.get("correct");
        boolean isCorrect = req.quizAnswer == correctAnswer;
        
        response.put("success", isCorrect);
        response.put("correct", isCorrect);
        response.put("correctAnswer", correctAnswer);
        
        if (isCorrect) {
            response.put("message", "정답입니다! 🎉");
            response.put("scoreDiff", 50);
            response.put("hint", "git 명령어를 잘 이해하고 있네요!");
        } else {
            response.put("message", "틀렸습니다. 다시 시도해보세요!");
            response.put("scoreDiff", 0);
            response.put("hint", "레슨 내용을 다시 한번 읽어보세요");
        }
        
        return response;
    }

    @PostMapping("/practice")
    public Map<String, Object> linkPractice(@RequestBody ProgressRequest req) {
        Map<String, Object> response = new HashMap<>();
        
        if (!LESSONS.containsKey(req.lessonId)) {
            response.put("success", false);
            response.put("message", "존재하지 않는 레슨입니다");
            return response;
        }
        
        Map<String, Object> lesson = LESSONS.get(req.lessonId);
        String practiceGame = (String) lesson.get("practiceGame");
        
        if (practiceGame == null) {
            response.put("success", false);
            response.put("message", "이 레슨에는 실습 게임이 없습니다");
            return response;
        }
        
        response.put("success", true);
        response.put("message", "실습 게임으로 이동합니다");
        response.put("gameUrl", "/game/" + practiceGame);
        response.put("hint", "게임을 통해 배운 내용을 연습해보세요!");
        
        return response;
    }

    @GetMapping("/progress/{totalCompleted}")
    public Map<String, Object> getProgress(@PathVariable int totalCompleted) {
        Map<String, Object> response = new HashMap<>();
        
        int totalLessons = LESSONS.size();
        int progressPercent = Math.round((float) totalCompleted / totalLessons * 100);
        
        response.put("success", true);
        response.put("totalCompleted", totalCompleted);
        response.put("totalLessons", totalLessons);
        response.put("progressPercent", progressPercent);
        
        String rank = calculateRank(progressPercent);
        response.put("rank", rank);
        response.put("message", "현재 진행률: " + progressPercent + "%");
        
        return response;
    }

    private Map<String, String> checkAchievements(String lessonId, Map<String, Boolean> progress) {
        if (progress == null) return null;
        
        int completed = 0;
        for (Boolean value : progress.values()) {
            if (value != null && value) completed++;
        }
        
        Map<String, String> achievement = new HashMap<>();
        
        if ("basics".equals(lessonId)) {
            achievement.put("id", "first-lesson");
            achievement.put("name", "첫 수업");
            achievement.put("type", "good");
            return achievement;
        }
        
        if (completed >= LESSONS.size()) {
            achievement.put("id", "git-master");
            achievement.put("name", "Git 마스터");
            achievement.put("type", "good");
            return achievement;
        }
        
        if (completed >= 3) {
            achievement.put("id", "learning-streak");
            achievement.put("name", "학습왕");
            achievement.put("type", "good");
            return achievement;
        }
        
        return null;
    }

    private String calculateRank(int progressPercent) {
        if (progressPercent >= 100) return "Git Master";
        if (progressPercent >= 80) return "Senior Developer";
        if (progressPercent >= 60) return "Developer";
        if (progressPercent >= 40) return "Junior Developer";
        if (progressPercent >= 20) return "Intern";
        return "Beginner";
    }
}