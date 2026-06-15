package com.edu.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.module.course.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCourseMapper extends BaseMapper<UserCourse> {
}
