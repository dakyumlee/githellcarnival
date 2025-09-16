package com.example.carnival.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/reset")
public class ResetApi {

    public static class ResetRequest {
        public String mode;
        public int aheadCommits;
        public int stagedChanges;
        public int workingChanges;
    }

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody ResetRequest req) {
        Map<String, Object> res = new HashMap<>();
        int ahead = Math.max(0, req.aheadCommits);
        int staged = Math.max(0, req.stagedChanges);
        int working = Math.max(0, req.workingChanges);
        int delta = ahead > 0 ? 3 : 0;

        if ("soft".equals(req.mode)) {
            if (ahead == 0) {
                return result(false, "되돌릴 커밋이 없습니다", "git reset --soft HEAD~1", ahead, staged, working, 0);
            }
            ahead -= 1;
            staged += delta;
            return result(true, "HEAD만 이동했습니다. 이전 커밋의 변경이 스테이징에 남습니다",
                          "git reset --soft HEAD~1", ahead, staged, working, 20);
        }

        if ("mixed".equals(req.mode)) {
            if (ahead == 0) {
                return result(false, "되돌릴 커밋이 없습니다", "git reset --mixed HEAD~1", ahead, staged, working, 0);
            }
            ahead -= 1;
            working += delta;
            staged = 0;
            return result(true, "HEAD와 인덱스를 이동했습니다. 변경은 작업트리에 남습니다",
                          "git reset --mixed HEAD~1", ahead, staged, working, 20);
        }

        if ("hard".equals(req.mode)) {
            if (ahead == 0 && staged == 0 && working == 0) {
                return result(false, "되돌릴 내용이 없습니다", "git reset --hard HEAD~1", ahead, staged, working, 0);
            }
            if (ahead > 0) ahead -= 1;
            staged = 0;
            working = 0;
            return result(true, "HEAD, 인덱스, 작업트리를 모두 되돌렸습니다",
                          "git reset --hard HEAD~1", ahead, staged, working, -50);
        }

        return result(false, "알 수 없는 모드", "soft | mixed | hard", ahead, staged, working, 0);
    }

    private Map<String, Object> result(boolean success, String msg, String hint,
                                       int ahead, int staged, int working, int scoreDiff) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", success);
        r.put("message", msg);
        r.put("hint", hint);
        r.put("aheadCommits", ahead);
        r.put("stagedChanges", staged);
        r.put("workingChanges", working);
        r.put("scoreDiff", scoreDiff);
        return r;
    }
}
