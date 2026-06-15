package com.edu.module.live.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("live_record")
public class LiveRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private String recordUrl;
    private Integer duration;
    private Long fileSize;
    private LocalDateTime createdAt;
}
