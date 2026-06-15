package com.edu.module.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String type;
    private String content;
    private String options;
    private String answer;
    private String analysis;
    private Integer difficulty;
    private Integer score;
    private LocalDateTime createdAt;
}
