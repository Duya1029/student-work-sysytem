package com.homework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生作业管理系统 - Spring Boot 启动类
 *
 * @MapperScan: 扫描 com.homework.mapper 包下的所有 Mapper 接口
 */
// 标记该类为 Spring Boot 应用程序的启动入口类
@SpringBootApplication
// 指定扫描 com.homework.mapper 包下的所有 Mapper 接口，并自动注册为 Spring Bean
@MapperScan("com.homework.mapper")
public class HomeworkApplication {

    // 程序主入口方法，JVM 启动时首先执行此方法
    public static void main(String[] args) {
        // 启动 Spring Boot 应用，加载配置、初始化容器并启动内嵌 Tomcat 服务器
        SpringApplication.run(HomeworkApplication.class, args);
        // 打印启动成功横幅的上分隔线
        System.out.println("====================================");
        // 打印系统启动成功提示信息
        System.out.println("  学生作业管理系统启动成功！");
        // 打印系统 API 接口访问地址
        System.out.println("  访问地址：http://localhost:8090/api");
        // 打印启动成功横幅的下分隔线
        System.out.println("====================================");
    }
}
