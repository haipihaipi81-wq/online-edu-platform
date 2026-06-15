package com.edu.module.exam.controller;

import com.edu.common.PageResult;
import com.edu.common.Result;
import com.edu.module.exam.entity.Homework;
import com.edu.module.exam.entity.HomeworkSubmit;
import com.edu.module.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final ExamService examService;

    @GetMapping
    public Result<?> listHomework(
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(PageResult.from(examService.listHomework(courseId, page, size)));
    }

    @PostMapping
    public Result<?> createHomework(Authentication auth, @RequestBody Homework homework) {
        homework.setTeacherId((Long) auth.getCredentials());
        return Result.ok(examService.createHomework(homework));
    }

    @GetMapping("/{id}/submits")
    public Result<?> getSubmits(@PathVariable Long id) {
        List<HomeworkSubmit> submits = examService.getHomeworkSubmits(id);
        return Result.ok(submits);
    }

    @PostMapping("/{id}/submit")
    public Result<?> submitHomework(Authentication auth, @PathVariable Long id,
                                     @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getCredentials();
        examService.submitHomework(id, userId, body.get("content"), body.get("fileUrl"));
        return Result.ok();
    }

    @PutMapping("/submits/{submitId}/grade")
    public Result<?> gradeHomework(@PathVariable Long submitId,
                                    @RequestBody Map<String, Object> body) {
        Integer score = (Integer) body.get("score");
        String comment = (String) body.get("comment");
        examService.gradeHomework(submitId, score, comment);
        return Result.ok();
    }
}
