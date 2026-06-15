package com.edu.module.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("homework_submit")
public class HomeworkSubmit {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long homeworkId;
    private Long userId;
    private String content;
    private String fileUrl;
    private Integer score;
    private String comment;
    private LocalDateTime submitTime;
    private String status;
}
