package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/tutorial")
public class TutorialApi {

    public static class LessonProgress {
        public String lessonId;
        public boolean completed;
        public int score;
        public Map<String, Object> quizAnswers;
    }

    public static class OverallProgress {
        public Map<String, Boolean> completedLessons;
        public int totalScore;
        public int completionPercentage;
    }

    @PostMapping("/complete-lesson")
    public Map<String, Object> completeLesson(@RequestBody LessonProgress progress) {
        Map<String, Object> res = new HashMap<>();
        
        int baseScore = 100;
        int bonusScore = 0;
        
        if (progress.quizAnswers != null) {
            int correctAnswers = 0;
            for (Object answer : progress.quizAnswers.values()) {
                if (Boolean.TRUE.equals(answer)) {
                    correctAnswers++;
                }
            }
            bonusScore = correctAnswers * 25;
        }
        
        int finalScore = baseScore + bonusScore;
        
        res.put("success", true);
        res.put("message", "레슨 완료! 학습을 축하합니다!");
        res.put("lessonId", progress.lessonId);
        res.put("earnedScore", finalScore);
        res.put("bonusScore", bonusScore);
        
        String achievement = checkAchievement(progress.lessonId);
        if (achievement != null) {
            res.put("achievement", achievement);
        }
        
        return res;
    }

    @GetMapping("/progress")
    public Map<String, Object> getProgress() {
        Map<String, Object> res = new HashMap<>();
        
        Map<String, Boolean> completedLessons = new HashMap<>();
        completedLessons.put("basics", false);
        completedLessons.put("add-commit", false);
        completedLessons.put("branches", false);
        completedLessons.put("merge-rebase", false);
        completedLessons.put("cherry-pick", false);
        completedLessons.put("stash", false);
        completedLessons.put("reset", false);
        
        res.put("completedLessons", completedLessons);
        res.put("totalScore", 0);
        res.put("completionPercentage", 0);
        res.put("currentLesson", "basics");
        
        return res;
    }

    @PostMapping("/quiz-answer")
    public Map<String, Object> submitQuizAnswer(@RequestBody Map<String, Object> answer) {
        String lessonId = (String) answer.get("lessonId");
        int selectedOption = (Integer) answer.get("selectedOption");
        int correctOption = (Integer) answer.get("correctOption");
        
        Map<String, Object> res = new HashMap<>();
        boolean isCorrect = selectedOption == correctOption;
        
        res.put("correct", isCorrect);
        res.put("selectedOption", selectedOption);
        res.put("correctOption", correctOption);
        
        if (isCorrect) {
            res.put("message", "정답입니다! 잘 이해하고 계시네요.");
            res.put("scoreEarned", 25);
        } else {
            res.put("message", getExplanation(lessonId, correctOption));
            res.put("scoreEarned", 0);
        }
        
        return res;
    }

    @GetMapping("/lessons")
    public Map<String, Object> getLessons() {
        Map<String, Object> res = new HashMap<>();
        
        List<Map<String, Object>> lessons = Arrays.asList(
            createLesson("basics", "📖", "Git 기초", "버전 관리의 개념과 Git의 기본 구조"),
            createLesson("add-commit", "💾", "Add & Commit", "파일을 스테이징하고 커밋하는 방법"),
            createLesson("branches", "🌿", "브랜치", "브랜치를 생성하고 관리하는 방법"),
            createLesson("merge-rebase", "🔄", "Merge & Rebase", "브랜치를 병합하는 두 가지 방법"),
            createLesson("cherry-pick", "🍒", "Cherry Pick", "특정 커밋만 선택해서 가져오기"),
            createLesson("stash", "📦", "Stash", "작업 중인 변경사항을 임시 저장"),
            createLesson("reset", "🔥", "Reset", "Git의 가장 위험하지만 강력한 명령어")
        );
        
        res.put("lessons", lessons);
        res.put("totalLessons", lessons.size());
        
        return res;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> res = new HashMap<>();
        
        res.put("totalUsers", 0);
        res.put("completedTutorials", 0);
        res.put("averageScore", 0);
        res.put("mostDifficultLesson", "");
        res.put("easiestLesson", "");
        
        Map<String, Integer> lessonCompletionRates = new HashMap<>();
        lessonCompletionRates.put("basics", 0);
        lessonCompletionRates.put("add-commit", 0);
        lessonCompletionRates.put("branches", 0);
        lessonCompletionRates.put("merge-rebase", 0);
        lessonCompletionRates.put("cherry-pick", 0);
        lessonCompletionRates.put("stash", 0);
        lessonCompletionRates.put("reset", 0);
        
        res.put("lessonCompletionRates", lessonCompletionRates);
        
        return res;
    }

    private Map<String, Object> createLesson(String id, String icon, String title, String description) {
        Map<String, Object> lesson = new HashMap<>();
        lesson.put("id", id);
        lesson.put("icon", icon);
        lesson.put("title", title);
        lesson.put("description", description);
        lesson.put("completed", false);
        lesson.put("available", id.equals("basics"));
        return lesson;
    }

    private String checkAchievement(String lessonId) {
        switch (lessonId) {
            case "basics":
                return "first-lesson";
            case "reset":
                return "danger-zone";
            default:
                return null;
        }
    }

    private String getExplanation(String lessonId, int correctOption) {
        Map<String, Map<Integer, String>> explanations = new HashMap<>();
        
        Map<Integer, String> basicsExplanations = new HashMap<>();
        basicsExplanations.put(1, "Working Directory, Staging Area, Git Repository가 Git의 세 가지 주요 영역입니다.");
        
        explanations.put("basics", basicsExplanations);
        
        return explanations.getOrDefault(lessonId, new HashMap<>())
                          .getOrDefault(correctOption, "다시 한 번 학습 내용을 확인해보세요.");
    }
}