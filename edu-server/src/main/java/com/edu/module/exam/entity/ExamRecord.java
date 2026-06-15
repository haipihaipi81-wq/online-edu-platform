package com.edu.module.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_record")
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examId;
    private Long userId;
    private Integer score;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Integer retakeCount;
}
