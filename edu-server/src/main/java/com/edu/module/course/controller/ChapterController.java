package com.edu.module.course.controller;

import com.edu.common.Result;
import com.edu.module.course.entity.Chapter;
import com.edu.module.course.entity.Section;
import com.edu.module.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final CourseService courseService;

    @GetMapping
    public Result<?> getChapters(@PathVariable Long courseId) {
        return Result.ok(courseService.getCourseStructure(courseId));
    }

    @PostMapping
    public Result<?> addChapter(@PathVariable Long courseId, @RequestBody Chapter chapter) {
        return Result.ok(courseService.addChapter(courseId, chapter));
    }

    @PutMapping("/{chapterId}")
    public Result<?> updateChapter(@PathVariable Long chapterId, @RequestBody Chapter chapter) {
        courseService.updateChapter(chapterId, chapter);
        return Result.ok();
    }

    @DeleteMapping("/{chapterId}")
    public Result<?> deleteChapter(@PathVariable Long chapterId) {
        courseService.deleteChapter(chapterId);
        return Result.ok();
    }

    // Sections within a chapter

    @PostMapping("/{chapterId}/sections")
    public Result<?> addSection(@PathVariable Long chapterId, @RequestBody Section section) {
        return Result.ok(courseService.addSection(chapterId, section));
    }

    @PutMapping("/{chapterId}/sections/{sectionId}")
    public Result<?> updateSection(@PathVariable Long sectionId, @RequestBody Section section) {
        courseService.updateSection(sectionId, section);
        return Result.ok();
    }

    @DeleteMapping("/{chapterId}/sections/{sectionId}")
    public Result<?> deleteSection(@PathVariable Long sectionId) {
        courseService.deleteSection(sectionId);
        return Result.ok();
    }
}
