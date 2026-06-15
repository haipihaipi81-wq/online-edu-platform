-- ============================================================
-- 在线教育平台 — 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS edu_platform
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE edu_platform;

-- ============================================================
-- 模块1：用户与认证（Person A）
-- ============================================================

-- 用户表
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL DEFAULT 'STUDENT' COMMENT '角色',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码 ROLE_STUDENT/ROLE_TEACHER/ROLE_ADMIN'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色-权限关联表
CREATE TABLE role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 用户-角色关联表（扩展用）
CREATE TABLE user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================================
-- 模块2：课程与内容管理（Person A）
-- ============================================================

-- 课程分类表
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID 0为顶级',
    sort INT DEFAULT 0 COMMENT '排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

-- 课程表
CREATE TABLE course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '课程名称',
    description TEXT COMMENT '课程简介',
    cover VARCHAR(255) COMMENT '封面图URL',
    teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10, 2) DEFAULT 0.00 COMMENT '价格',
    status ENUM('DRAFT', 'PUBLISHED', 'CLOSED') DEFAULT 'DRAFT' COMMENT '状态',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 用户选课表
CREATE TABLE user_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    progress DECIMAL(5, 2) DEFAULT 0.00 COMMENT '学习进度 0-100',
    enrolled_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    last_study_at DATETIME COMMENT '最近学习时间',
    UNIQUE KEY uk_user_course (user_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户选课表';

-- 章节表
CREATE TABLE chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '章节标题',
    sort INT DEFAULT 0 COMMENT '排序',
    INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- 课时表
CREATE TABLE section (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    title VARCHAR(200) NOT NULL COMMENT '课时标题',
    content_type ENUM('VIDEO', 'DOCUMENT') NOT NULL DEFAULT 'VIDEO' COMMENT '内容类型',
    content_url VARCHAR(500) COMMENT '内容URL',
    duration INT DEFAULT 0 COMMENT '时长(秒)',
    sort INT DEFAULT 0 COMMENT '排序',
    is_free TINYINT DEFAULT 0 COMMENT '是否免费试学 1是 0否',
    INDEX idx_chapter (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课时表';

-- 用户课时学习记录
CREATE TABLE user_lesson_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    section_id BIGINT NOT NULL COMMENT '课时ID',
    status ENUM('NOT_STARTED', 'LEARNING', 'FINISHED') DEFAULT 'NOT_STARTED' COMMENT '学习状态',
    study_time INT DEFAULT 0 COMMENT '已学习时长(秒)',
    finished_at DATETIME COMMENT '完成时间',
    UNIQUE KEY uk_user_lesson (user_id, section_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户课时学习记录';

-- ============================================================
-- 模块3：直播教学（Person B）
-- ============================================================

-- 直播间表
CREATE TABLE live_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT COMMENT '关联课程ID',
    title VARCHAR(200) NOT NULL COMMENT '直播标题',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    push_url VARCHAR(500) COMMENT '推流地址',
    pull_url VARCHAR(500) COMMENT '拉流地址',
    start_time DATETIME COMMENT '计划开始时间',
    end_time DATETIME COMMENT '计划结束时间',
    status ENUM('WAITING', 'LIVING', 'ENDED') DEFAULT 'WAITING' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播间表';

-- 直播录制表
CREATE TABLE live_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL COMMENT '直播间ID',
    record_url VARCHAR(500) COMMENT '录制文件URL',
    duration INT DEFAULT 0 COMMENT '时长(秒)',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播录制表';

-- 直播弹幕/聊天表
CREATE TABLE live_chat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL COMMENT '直播间ID',
    user_id BIGINT NOT NULL COMMENT '发送者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_room (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播聊天表';

-- ============================================================
-- 模块4：作业与考试（Person B）
-- ============================================================

-- 题库表
CREATE TABLE question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT COMMENT '关联课程ID',
    type ENUM('SINGLE', 'MULTI', 'JUDGE', 'FILL', 'ESSAY') NOT NULL COMMENT '题型',
    content TEXT NOT NULL COMMENT '题目内容',
    options JSON COMMENT '选项(选择题用) JSON数组',
    answer VARCHAR(500) COMMENT '正确答案',
    analysis TEXT COMMENT '题目解析',
    difficulty INT DEFAULT 3 COMMENT '难度等级 1-5',
    score INT DEFAULT 5 COMMENT '默认分值',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

-- 试卷表
CREATE TABLE exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT COMMENT '关联课程ID',
    title VARCHAR(200) NOT NULL COMMENT '试卷标题',
    duration INT NOT NULL DEFAULT 60 COMMENT '考试时长(分钟)',
    total_score INT NOT NULL DEFAULT 100 COMMENT '总分',
    pass_score INT DEFAULT 60 COMMENT '及格分',
    start_time DATETIME COMMENT '考试开始时间',
    end_time DATETIME COMMENT '考试结束时间',
    retake_limit INT DEFAULT 0 COMMENT '允许重考次数 0不限',
    status ENUM('DRAFT', 'PUBLISHED', 'ENDED') DEFAULT 'DRAFT' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 试卷-题目关联表
CREATE TABLE exam_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL COMMENT '试卷ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    score INT DEFAULT 5 COMMENT '本题分值',
    sort INT DEFAULT 0 COMMENT '排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- 用户考试记录表
CREATE TABLE exam_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL COMMENT '试卷ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    score INT COMMENT '得分',
    status ENUM('IN_PROGRESS', 'SUBMITTED', 'GRADED') DEFAULT 'IN_PROGRESS' COMMENT '状态',
    start_time DATETIME COMMENT '开始答题时间',
    submit_time DATETIME COMMENT '提交时间',
    retake_count INT DEFAULT 0 COMMENT '重考次数',
    UNIQUE KEY uk_user_exam_try (user_id, exam_id, retake_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户考试记录';

-- 用户答题记录表
CREATE TABLE exam_answer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL COMMENT '考试记录ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    user_answer TEXT COMMENT '用户答案',
    is_correct TINYINT COMMENT '是否正确 1是 0否',
    score INT DEFAULT 0 COMMENT '得分',
    INDEX idx_record (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户答题记录';

-- 作业表
CREATE TABLE homework (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT COMMENT '关联课程ID',
    teacher_id BIGINT NOT NULL COMMENT '发布教师ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    content TEXT COMMENT '作业要求',
    deadline DATETIME COMMENT '截止时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 作业提交表
CREATE TABLE homework_submit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    homework_id BIGINT NOT NULL COMMENT '作业ID',
    user_id BIGINT NOT NULL COMMENT '学生ID',
    content TEXT COMMENT '作业内容',
    file_url VARCHAR(500) COMMENT '附件URL',
    score INT COMMENT '评分',
    comment VARCHAR(500) COMMENT '教师评语',
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    status ENUM('SUBMITTED', 'GRADED') DEFAULT 'SUBMITTED' COMMENT '状态',
    UNIQUE KEY uk_homework_user (homework_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 默认角色
INSERT INTO role (name, code) VALUES
('学生', 'ROLE_STUDENT'),
('教师', 'ROLE_TEACHER'),
('管理员', 'ROLE_ADMIN');

-- 默认管理员 (密码: 123456)
INSERT INTO user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$eO2FbVY6geKDmmXjEb44x.dZZTVn0VuQL20kVfJIbbofBL7yLGoPS', '系统管理员', 'ADMIN', 1);
