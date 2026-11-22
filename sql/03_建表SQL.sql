-- ============================================================
-- 学生作业管理系统 - 建表 SQL (SQL Server 2022 / SSMS 22)
-- 数据库：homework_db
-- 字符集：使用 NVARCHAR 存储 Unicode 字符，避免中文乱码
-- ============================================================

-- 如果数据库已存在则删除（慎用，生产环境请注释）
-- IF DB_ID('homework_db') IS NOT NULL
-- BEGIN
--     ALTER DATABASE homework_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
--     DROP DATABASE homework_db;
-- END
-- GO

-- 创建数据库 homework_db
CREATE DATABASE homework_db;
GO  -- 批处理结束标记，SQL Server 按批执行

-- 切换到 homework_db 数据库
USE homework_db;
GO

-- ============================================================
-- 1. 班级表 sys_class
-- ============================================================
-- 检查 sys_class 表是否已存在，存在则删除（重建）
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'sys_class') AND type in (N'U'))
    DROP TABLE sys_class;  -- 删除已存在的班级表
GO

-- 创建班级表
CREATE TABLE sys_class (
    class_id    BIGINT       IDENTITY(1,1) NOT NULL,  -- 班级ID，自增主键
    class_name  NVARCHAR(100) NOT NULL,                -- 班级名称
    create_time DATETIME2     DEFAULT SYSUTCDATETIME(), -- 创建时间，默认当前UTC时间
    CONSTRAINT PK_sys_class PRIMARY KEY (class_id)     -- 主键约束：class_id
);
GO

-- ============================================================
-- 2. 用户表 sys_user（管理员/教师/学生）
-- ============================================================
-- 检查 sys_user 表是否已存在，存在则删除
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'sys_user') AND type in (N'U'))
    DROP TABLE sys_user;  -- 删除已存在的用户表
GO

-- 创建用户表，存储管理员、教师、学生
CREATE TABLE sys_user (
    user_id     BIGINT        IDENTITY(1,1) NOT NULL,   -- 用户ID，自增主键
    username    NVARCHAR(50)  NOT NULL,                 -- 用户名（登录账号）
    password    NVARCHAR(100) NOT NULL,                 -- 密码（明文存储，实际项目应加密）
    real_name   NVARCHAR(50)  NOT NULL,                 -- 真实姓名
    role        NVARCHAR(20)  NOT NULL,                 -- 角色：ADMIN/TEACHER/STUDENT
    class_id    BIGINT        NULL,                      -- 班级ID（学生必填，教师/管理员为空）
    student_no  NVARCHAR(50)  NULL,                      -- 学号（学生专用）
    create_time DATETIME2     DEFAULT SYSUTCDATETIME(),  -- 创建时间
    CONSTRAINT PK_sys_user PRIMARY KEY (user_id),        -- 主键约束：user_id
    CONSTRAINT UQ_sys_user_username UNIQUE (username),   -- 唯一约束：用户名不能重复
    CONSTRAINT CK_sys_user_role CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT'))  -- 检查约束：角色只能是这三个值
);
GO

-- 外键：用户表的 class_id 关联班级表的 class_id
ALTER TABLE sys_user
    ADD CONSTRAINT FK_sys_user_class FOREIGN KEY (class_id) REFERENCES sys_class(class_id);
GO

-- ============================================================
-- 3. 作业表 sys_homework
-- ============================================================
-- 检查 sys_homework 表是否已存在，存在则删除
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'sys_homework') AND type in (N'U'))
    DROP TABLE sys_homework;  -- 删除已存在的作业表
GO

-- 创建作业表，存储教师发布的作业
CREATE TABLE sys_homework (
    homework_id  BIGINT         IDENTITY(1,1) NOT NULL,  -- 作业ID，自增主键
    title        NVARCHAR(200)  NOT NULL,                -- 作业标题
    description  NVARCHAR(MAX)  NOT NULL,                 -- 作业描述/要求
    teacher_id   BIGINT         NOT NULL,                 -- 发布教师ID
    class_id     BIGINT         NOT NULL,                 -- 班级ID
    deadline     DATETIME2       NOT NULL,                -- 截止时间
    create_time  DATETIME2      DEFAULT SYSUTCDATETIME(), -- 创建时间
    CONSTRAINT PK_sys_homework PRIMARY KEY (homework_id),                                    -- 主键约束：homework_id
    CONSTRAINT FK_sys_homework_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user(user_id), -- 外键：teacher_id 关联用户表
    CONSTRAINT FK_sys_homework_class FOREIGN KEY (class_id) REFERENCES sys_class(class_id)    -- 外键：class_id 关联班级表
);
GO

-- ============================================================
-- 4. 作业提交表 sys_submission
-- ============================================================
-- 检查 sys_submission 表是否已存在，存在则删除
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'sys_submission') AND type in (N'U'))
    DROP TABLE sys_submission;  -- 删除已存在的提交表
GO

-- 创建作业提交表，存储学生提交的作业
CREATE TABLE sys_submission (
    submission_id  BIGINT         IDENTITY(1,1) NOT NULL,  -- 提交ID，自增主键
    homework_id    BIGINT         NOT NULL,                 -- 作业ID
    student_id     BIGINT         NOT NULL,                 -- 学生ID
    content        NVARCHAR(MAX)  NOT NULL,                 -- 提交内容
    submit_time    DATETIME2      DEFAULT SYSUTCDATETIME(),  -- 提交时间
    score          INT            NULL,                      -- 分数（0-100，未批改为NULL）
    comment        NVARCHAR(500)  NULL,                      -- 教师评语
    grade_time     DATETIME2      NULL,                      -- 批改时间
    CONSTRAINT PK_sys_submission PRIMARY KEY (submission_id),                                        -- 主键约束
    CONSTRAINT FK_sys_submission_homework FOREIGN KEY (homework_id) REFERENCES sys_homework(homework_id), -- 外键：关联作业表
    CONSTRAINT FK_sys_submission_student FOREIGN KEY (student_id) REFERENCES sys_user(user_id),        -- 外键：关联用户表
    CONSTRAINT CK_sys_submission_score CHECK (score IS NULL OR (score >= 0 AND score <= 100)),         -- 检查约束：分数在0-100之间
    CONSTRAINT UQ_sys_submission UNIQUE (homework_id, student_id)  -- 唯一约束：一个学生同一作业只能提交一次
);
GO

-- ============================================================
-- 索引：提升查询性能
-- ============================================================
-- 用户表索引：按角色查询（如查所有教师）
CREATE INDEX IX_sys_user_role ON sys_user(role);
-- 用户表索引：按班级查询学生
CREATE INDEX IX_sys_user_class_id ON sys_user(class_id);
-- 作业表索引：按教师查询其发布的作业
CREATE INDEX IX_sys_homework_teacher_id ON sys_homework(teacher_id);
-- 作业表索引：按班级查询作业
CREATE INDEX IX_sys_homework_class_id ON sys_homework(class_id);
-- 提交表索引：按作业查询提交列表
CREATE INDEX IX_sys_submission_homework_id ON sys_submission(homework_id);
-- 提交表索引：按学生查询其提交记录
CREATE INDEX IX_sys_submission_student_id ON sys_submission(student_id);
GO

-- ============================================================
-- 初始测试数据
-- ============================================================

-- 插入3个班级
INSERT INTO sys_class (class_name) VALUES (N'计算机科学与技术2022-1班');
INSERT INTO sys_class (class_name) VALUES (N'计算机科学与技术2022-2班');
INSERT INTO sys_class (class_name) VALUES (N'软件工程2022-1班');
GO

-- 插入用户数据（管理员、教师、学生）
-- 密码均为 123456（实际项目应使用 BCrypt 加密存储）
INSERT INTO sys_user (username, password, real_name, role, class_id, student_no) VALUES
(N'admin', N'123456', N'系统管理员', N'ADMIN', NULL, NULL),        -- 管理员账号
(N'teacher01', N'123456', N'张老师', N'TEACHER', NULL, NULL),     -- 教师1
(N'teacher02', N'123456', N'李老师', N'TEACHER', NULL, NULL),     -- 教师2
(N'student001', N'123456', N'王同学', N'STUDENT', 1, N'20220001'), -- 学生1（班级1）
(N'student002', N'123456', N'李同学', N'STUDENT', 1, N'20220002'), -- 学生2（班级1）
(N'student003', N'123456', N'赵同学', N'STUDENT', 1, N'20220003'), -- 学生3（班级1）
(N'student004', N'123456', N'钱同学', N'STUDENT', 2, N'20220004'), -- 学生4（班级2）
(N'student005', N'123456', N'孙同学', N'STUDENT', 2, N'20220005'); -- 学生5（班级2）
GO

-- 插入2份作业数据
INSERT INTO sys_homework (title, description, teacher_id, class_id, deadline) VALUES
(N'第一次作业 - Java基础', N'完成第一章习题：1. 简述Java的三大特性；2. 编写一个简单的Hello World程序并运行；3. 解释JVM、JRE、JDK的区别。', 2, 1, '2024-12-31 23:59:59'),  -- 教师2发布给班级1
(N'第一次作业 - 数据结构', N'完成数据结构第一章习题：1. 解释时间复杂度与空间复杂度；2. 手写冒泡排序算法；3. 简述链表与数组的区别。', 3, 2, '2024-12-31 23:59:59');  -- 教师3发布给班级2
GO

-- 插入2条作业提交数据
INSERT INTO sys_submission (homework_id, student_id, content, submit_time, score, comment, grade_time) VALUES
(1, 4, N'1. Java的三大特性为封装、继承、多态。封装是指将数据和方法封装在类中...；2. public class Hello { public static void main(String[] args) { System.out.println("Hello World"); } }；3. JVM是Java虚拟机...', '2024-09-10 15:30:00', 90, N'回答全面，思路清晰，表现优秀！继续努力！', '2024-09-12 10:00:00'),  -- 学生4提交作业1，得90分
(1, 5, N'1. 封装、继承、多态。2. Hello World代码已编写完成。3. JVM是虚拟机，JRE是运行环境，JDK是开发工具包。', '2024-09-11 09:00:00', 75, N'基本概念正确，但第二题回答过于简略，建议补充完整代码和运行说明。', '2024-09-12 10:30:00');  -- 学生5提交作业1，得75分
GO

-- ============================================================
-- 验证数据：查询各表数据确认插入成功
-- ============================================================
SELECT * FROM sys_class;  -- 查询所有班级
SELECT user_id, username, real_name, role, class_id, student_no FROM sys_user;  -- 查询用户（不显示密码）
SELECT * FROM sys_homework;  -- 查询所有作业
SELECT * FROM sys_submission;  -- 查询所有提交记录
GO
