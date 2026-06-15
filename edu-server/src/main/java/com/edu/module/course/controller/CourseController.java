package com.edu.module.course.controller;

import com.edu.common.PageResult;
import com.edu.common.Result;
import com.edu.module.course.entity.Category;
import com.edu.module.course.entity.Course;
import com.edu.module.course.entity.UserCourse;
import com.edu.module.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ===== Categories =====

    @GetMapping("/categories")
    public Result<?> getCategories() {
        return Result.ok(courseService.getCategoryTree());
    }

    @PostMapping("/admin/categories")
    public Result<?> addCategory(@RequestBody Category category) {
        courseService.addCategory(category);
        return Result.ok();
    }

    // ===== Courses =====

    @GetMapping("/courses")
    public Result<?> listCourses(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.ok(PageResult.from(courseService.listCourses(page, size, categoryId, keyword)));
    }

    @GetMapping("/courses/{id}")
    public Result<?> getCourse(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        List<Map<String, Object>> structure = courseService.getCourseStructure(id);
        return Result.ok(Map.of("course", course, "chapters", structure));
    }

    @PostMapping("/courses")
    public Result<?> createCourse(Authentication auth, @RequestBody Course course) {
        course.setTeacherId((Long) auth.getCredentials());
        return Result.ok(courseService.createCourse(course));
    }

    @PutMapping("/courses/{id}")
    public Result<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        courseService.updateCourse(id, course);
        return Result.ok();
    }

    @DeleteMapping("/courses/{id}")
    public Result<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.ok();
    }

    @GetMapping("/teacher/courses")
    public Result<?> myCourses(Authentication auth,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer size) {
        Long teacherId = (Long) auth.getCredentials();
        return Result.ok(PageResult.from(courseService.listTeacherCourses(teacherId, page, size)));
    }

    // ===== Enrollment =====

    @PostMapping("/courses/{id}/enroll")
    public Result<?> enroll(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getCredentials();
        courseService.enroll(userId, id);
        return Result.ok();
    }

    @GetMapping("/user/me/courses")
    public Result<?> myEnrolledCourses(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return Result.ok(courseService.getMyCourses(userId));
    }
}
