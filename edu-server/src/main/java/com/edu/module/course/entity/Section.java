package com.edu.module.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("section")
public class Section {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long chapterId;
    private String title;
    private String contentType;
    private String contentUrl;
    private Integer duration;
    private Integer sort;
    private Integer isFree;
}
