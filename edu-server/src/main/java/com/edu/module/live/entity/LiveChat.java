package com.edu.module.live.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("live_chat")
public class LiveChat {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    // Transient, for display
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String avatar;
}
