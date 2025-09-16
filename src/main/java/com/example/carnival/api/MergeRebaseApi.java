package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/merge")
public class MergeRebaseApi {

    public static class MergeRequest {
        public String strategy;
        public int localAhead;
        public int remoteAhead;
        public boolean teamLikesLinear;
    }

    @PostMapping("/execute")
    public Map<String,Object> execute(@RequestBody MergeRequest req){
        Map<String,Object> r=new HashMap<>();
        int la=Math.max(0,req.localAhead);
        int ra=Math.max(0,req.remoteAhead);
        int complexity=la+ra;
        int conflicts=Math.max(0,complexity-3);
        int score;
        String msg;
        String hint;
        String politics;
        if("merge".equalsIgnoreCase(req.strategy)){
            score=10-(conflicts*10);
            if(score< -50) score=-50;
            msg=conflicts>0?"머지 커밋 생성, 충돌 "+conflicts+"건 발생":"빠른 머지 성공";
            hint="git merge origin/main";
            politics=req.teamLikesLinear?"팀원: '히스토리 더러워졌네'":"팀원: '머지 커밋 좋아요'";
        }else if("rebase".equalsIgnoreCase(req.strategy)){
            score=15-(conflicts*12)+(req.teamLikesLinear?10:0);
            if(score< -60) score=-60;
            msg=conflicts>0?"리베이스 중 충돌 "+conflicts+"건 처리 필요":"리베이스 깔끔히 완료";
            hint="git fetch origin && git rebase origin/main";
            politics=req.teamLikesLinear?"팀원: '깔끔하다'":"팀원: '강제 푸시 위험 아님?'";
        }else{
            r.put("success",false);
            r.put("message","알 수 없는 전략");
            r.put("hint","merge | rebase");
            r.put("scoreDiff",0);
            r.put("conflicts",0);
            return r;
        }
        boolean success=conflicts<=3;
        r.put("success",success);
        r.put("message",msg);
        r.put("hint",hint);
        r.put("scoreDiff",score);
        r.put("conflicts",conflicts);
        r.put("diagram",buildDiagram(la,ra,req.strategy));
        r.put("npc",politics);
        return r;
    }

    private String buildDiagram(int la,int ra,String s){
        StringBuilder b=new StringBuilder();
        b.append("main: ");
        for(int i=0;i<ra;i++) b.append("─●");
        b.append("─●\n");
        b.append("feat: ");
        for(int i=0;i<la;i++) b.append("─●");
        b.append("─● ");
        if("merge".equalsIgnoreCase(s)) b.append("+ ⧉");
        if("rebase".equalsIgnoreCase(s)) b.append("⇢ rebased");
        return b.toString();
    }
}
