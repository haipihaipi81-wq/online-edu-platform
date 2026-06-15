package com.edu.module.exam.controller;

import com.edu.common.PageResult;
import com.edu.common.Result;
import com.edu.module.exam.entity.Exam;
import com.edu.module.exam.entity.Question;
import com.edu.module.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    // ===== Question Bank =====

    @GetMapping("/questions")
    public Result<?> listQuestions(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.ok(PageResult.from(examService.listQuestions(courseId, type, difficulty, page, size)));
    }

    @PostMapping("/questions")
    public Result<?> addQuestion(@RequestBody Question question) {
        return Result.ok(examService.addQuestion(question));
    }

    @PutMapping("/questions/{id}")
    public Result<?> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        examService.updateQuestion(id, question);
        return Result.ok();
    }

    @DeleteMapping("/questions/{id}")
    public Result<?> deleteQuestion(@PathVariable Long id) {
        examService.deleteQuestion(id);
        return Result.ok();
    }

    // ===== Exam =====

    @GetMapping("/exams")
    public Result<?> listExams(
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(PageResult.from(examService.listExams(courseId, page, size)));
    }

    @PostMapping("/exams")
    public Result<?> createExam(@RequestBody Map<String, Object> body) {
        // body: { title, courseId, duration, ... exam fields, questionItems: [...] }
        Exam exam = new Exam();
        exam.setCourseId(Long.valueOf(body.get("courseId").toString()));
        exam.setTitle((String) body.get("title"));
        exam.setDuration((Integer) body.getOrDefault("duration", 60));
        exam.setPassScore((Integer) body.getOrDefault("passScore", 60));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("questionItems");
        return Result.ok(examService.createExam(exam, items));
    }

    @PostMapping("/exams/auto-generate")
    public Result<?> autoGenerateExam(@RequestBody Map<String, Object> body) {
        Long courseId = Long.valueOf(body.get("courseId").toString());
        String title = (String) body.get("title");
        @SuppressWarnings("unchecked")
        Map<String, Object> rules = (Map<String, Object>) body.getOrDefault("rules", Map.of());
        return Result.ok(examService.autoGenerateExam(courseId, title, rules));
    }

    @PutMapping("/exams/{id}/publish")
    public Result<?> publishExam(@PathVariable Long id) {
        examService.publishExam(id);
        return Result.ok();
    }

    @GetMapping("/exams/{id}")
    public Result<?> getExam(@PathVariable Long id) {
        Exam exam = examService.getExamForStudent(id);
        List<Map<String, Object>> questions = examService.getExamQuestions(id);
        return Result.ok(Map.of("exam", exam, "questions", questions));
    }

    @PostMapping("/exams/{id}/start")
    public Result<?> startExam(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getCredentials();
        return Result.ok(examService.startExam(id, userId));
    }

    @PostMapping("/exams/records/{recordId}/submit")
    public Result<?> submitExam(Authentication auth, @PathVariable Long recordId,
                                @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getCredentials();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
        return Result.ok(examService.submitExam(recordId, userId, answers));
    }

    @GetMapping("/exams/records/{recordId}")
    public Result<?> getExamResult(@PathVariable Long recordId) {
        return Result.ok(examService.getExamResult(recordId));
    }

    @GetMapping("/user/me/exam-records")
    public Result<?> getMyExamRecords(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return Result.ok(examService.getMyExamRecords(userId));
    }
}
