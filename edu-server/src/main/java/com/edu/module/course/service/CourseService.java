package com.edu.module.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.BusinessException;
import com.edu.module.course.entity.*;
import com.edu.module.course.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CategoryMapper categoryMapper;
    private final ChapterMapper chapterMapper;
    private final SectionMapper sectionMapper;
    private final UserCourseMapper userCourseMapper;

    // ===== Category =====

    public List<Category> getCategoryTree() {
        List<Category> all = categoryMapper.selectList(null);
        Map<Long, List<Category>> childrenMap = all.stream()
                .filter(c -> c.getParentId() != 0)
                .collect(Collectors.groupingBy(Category::getParentId));
        List<Category> roots = all.stream()
                .filter(c -> c.getParentId() == 0)
                .collect(Collectors.toList());
        return roots;
    }

    public void addCategory(Category category) {
        categoryMapper.insert(category);
    }

    // ===== Course =====

    public Page<Course> listCourses(Integer page, Integer size, Long categoryId, String keyword) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .eq(categoryId != null, Course::getCategoryId, categoryId)
                .like(keyword != null, Course::getTitle, keyword)
                .eq(Course::getStatus, "PUBLISHED")
                .orderByDesc(Course::getCreatedAt);
        return courseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Page<Course> listTeacherCourses(Long teacherId, Integer page, Integer size) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .orderByDesc(Course::getCreatedAt);
        return courseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Course getCourseById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        // Increment view count
        Course update = new Course();
        update.setId(id);
        update.setViewCount(course.getViewCount() + 1);
        courseMapper.updateById(update);
        course.setViewCount(course.getViewCount() + 1);
        return course;
    }

    public Course createCourse(Course course) {
        course.setStatus("DRAFT");
        course.setViewCount(0);
        courseMapper.insert(course);
        return course;
    }

    public void updateCourse(Long id, Course course) {
        course.setId(id);
        courseMapper.updateById(course);
    }

    public void deleteCourse(Long id) {
        courseMapper.deleteById(id);
        // Also delete children
        List<Chapter> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>().eq(Chapter::getCourseId, id));
        for (Chapter ch : chapters) {
            sectionMapper.delete(new LambdaQueryWrapper<Section>().eq(Section::getChapterId, ch.getId()));
        }
        chapterMapper.delete(new LambdaQueryWrapper<Chapter>().eq(Chapter::getCourseId, id));
    }

    // ===== Chapter & Section (tree) =====

    public List<Map<String, Object>> getCourseStructure(Long courseId) {
        List<Chapter> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>().eq(Chapter::getCourseId, courseId).orderByAsc(Chapter::getSort));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Chapter ch : chapters) {
            Map<String, Object> chMap = new HashMap<>();
            chMap.put("id", ch.getId());
            chMap.put("title", ch.getTitle());
            chMap.put("sort", ch.getSort());
            List<Section> sections = sectionMapper.selectList(
                    new LambdaQueryWrapper<Section>().eq(Section::getChapterId, ch.getId()).orderByAsc(Section::getSort));
            chMap.put("sections", sections);
            result.add(chMap);
        }
        return result;
    }

    public Chapter addChapter(Long courseId, Chapter chapter) {
        chapter.setCourseId(courseId);
        chapterMapper.insert(chapter);
        return chapter;
    }

    public void updateChapter(Long id, Chapter chapter) {
        chapter.setId(id);
        chapterMapper.updateById(chapter);
    }

    public void deleteChapter(Long id) {
        chapterMapper.deleteById(id);
        sectionMapper.delete(new LambdaQueryWrapper<Section>().eq(Section::getChapterId, id));
    }

    public Section addSection(Long chapterId, Section section) {
        section.setChapterId(chapterId);
        sectionMapper.insert(section);
        return section;
    }

    public void updateSection(Long id, Section section) {
        section.setId(id);
        sectionMapper.updateById(section);
    }

    public void deleteSection(Long id) {
        sectionMapper.deleteById(id);
    }

    // ===== Enrollment =====

    @Transactional
    public void enroll(Long userId, Long courseId) {
        if (userCourseMapper.selectCount(new LambdaQueryWrapper<UserCourse>()
                .eq(UserCourse::getUserId, userId)
                .eq(UserCourse::getCourseId, courseId)) > 0) {
            throw new BusinessException(400, "已选过该课程");
        }
        UserCourse uc = new UserCourse();
        uc.setUserId(userId);
        uc.setCourseId(courseId);
        uc.setProgress(java.math.BigDecimal.ZERO);
        uc.setEnrolledAt(LocalDateTime.now());
        userCourseMapper.insert(uc);
    }

    public List<UserCourse> getMyCourses(Long userId) {
        return userCourseMapper.selectList(
                new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getUserId, userId));
    }
}
