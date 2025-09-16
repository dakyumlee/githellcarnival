package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/review")
public class ReviewApi {

    public static class ReviewRequest {
        public String code;
    }

    @PostMapping("/analyze")
    public Map<String,Object> analyze(@RequestBody ReviewRequest req){
        Map<String,Object> r=new HashMap<>();
        String code=req.code==null?"":req.code;
        String[] lines=code.split("\\R");
        int lineCount=lines.length;
        int longLines=0, loops=0, nestedIf=0, maxLen=0, braceDepth=0;

        for(String ln:lines){
            int len=ln.length();
            if(len>maxLen) maxLen=len;
            if(len>100) longLines++;
            String t=ln.trim();
            if(t.startsWith("for")||t.startsWith("while")) loops++;
            if(t.startsWith("if") && braceDepth>1) nestedIf++;
            if(t.contains("{")) braceDepth++;
            if(t.contains("}")) braceDepth=Math.max(0,braceDepth-1);
        }

        int readability=100-Math.min(60,longLines*8)-Math.min(30,Math.max(0,lineCount-120));
        int performance=100-Math.min(70,loops*12)-Math.min(30,nestedIf*10);
        int aiAnger=0;
        if(lineCount>300) aiAnger+=30;
        aiAnger+=longLines*4+nestedIf*6;
        aiAnger=Math.min(100,aiAnger);

        List<String> tips=new ArrayList<>();
        if(longLines>0) tips.add("줄 길이를 100자 이하로 줄이세요");
        if(nestedIf>0) tips.add("조건문 중첩 줄이기 (early return 활용)");
        if(loops>2) tips.add("루프 최적화 또는 분리 고려");
        if(lineCount>300) tips.add("파일 분할과 함수 추출");
        if(maxLen>140) tips.add("아주 긴 라인은 변수로 추출하세요");

        r.put("success",true);
        r.put("readability",Math.max(0,readability));
        r.put("performance",Math.max(0,performance));
        r.put("aiAnger",aiAnger);
        r.put("lines",lineCount);
        r.put("tips",tips);
        r.put("message",scoreLabel(readability,performance,aiAnger));
        r.put("hint","git add . && git commit -m \"refactor: 함수 추출\" && git push");
        return r;
    }

    private String scoreLabel(int r,int p,int a){
        if(r>=80 && p>=80 && a<20) return "리뷰봇: 깔끔하다";
        if(r>=60 && p>=60 && a<40) return "리뷰봇: 나쁘지 않음, 조금 다듬자";
        if(r>=40 || p>=40) return "리뷰봇: 개선 필요";
        return "리뷰봇: 이건 소설이다";
    }
}
