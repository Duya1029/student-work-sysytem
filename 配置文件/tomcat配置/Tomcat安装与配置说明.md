# ============================================================
# Tomcat 安装与配置指南 - 学生作业管理系统
# ============================================================

## 一、Tomcat 下载

### 方式一：官网下载
1. 访问 Tomcat 官网：https://tomcat.apache.org/
2. 下载 Tomcat 10.1.x（对应 Jakarta EE，适配 Spring Boot 3.x）
   - Windows 版：apache-tomcat-10.1.xx.zip
3. 解压到指定目录，例如：C:\apache-tomcat-10.1.0

### 方式二：命令下载（如果有 winget）
```powershell
winget install Apache.Tomcat
```

## 二、Spring Boot 内嵌 Tomcat（推荐）

本项目默认使用 Spring Boot 内嵌 Tomcat，无需单独安装 Tomcat。

### 配置位置：application.yml
```yaml
server:
  port: 8080                              # Tomcat 端口
  servlet:
    encoding:
      charset: UTF-8
      force: true
  tomcat:
    max-connections: 200                  # 最大连接数
    max-threads: 200                      # 最大工作线程数
    min-spare-threads: 10                # 最小空闲线程数
    accept-count: 100                    # 等待队列长度
    connection-timeout: 20000            # 连接超时（毫秒）
    uri-encoding: UTF-8                   # URI 编码
```

### 启动方式
```powershell
# 方式1：开发环境 - 使用 Maven 插件启动
cd "C:\Users\28467\homework-system\backend"
mvn spring-boot:run

# 方式2：生产环境 - 打包后启动
mvn clean package
java -jar target/homework-system-1.0.0.jar
```

## 三、外部 Tomcat 部署（可选）

如需部署到独立 Tomcat 服务器，按以下步骤操作：

### 1. 修改 pom.xml
```xml
<!-- 将打包方式改为 war -->
<packaging>war</packaging>

<!-- 排除内嵌 Tomcat -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

### 2. 修改启动类
```java
public class HomeworkApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HomeworkApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(HomeworkApplication.class, args);
    }
}
```

### 3. 配置 Tomcat
1. 将项目提供的 `server.xml` 复制到 `{TOMCAT_HOME}/conf/` 目录
2. 或直接修改 Tomcat 默认配置的端口和编码设置

### 4. 打包部署
```powershell
# 打包
mvn clean package
# 生成 target/homework-system-1.0.0.war

# 部署：将 war 包复制到 Tomcat 的 webapps 目录
copy target\homework-system-1.0.0.war C:\apache-tomcat-10.1.0\webapps\

# 启动 Tomcat
cd C:\apache-tomcat-10.1.0\bin
startup.bat

# 访问
# http://localhost:8080/homework-system-1.0.0/api/...
```

## 四、JDK 17 配置

### 1. 下载 JDK 17
- Oracle JDK 17: https://www.oracle.com/java/technologies/downloads/
- OpenJDK 17: https://adoptium.net/

### 2. 配置环境变量
```powershell
# JAVA_HOME
JAVA_HOME = C:\Program Files\Java\jdk-17

# PATH（添加）
%JAVA_HOME%\bin

# 验证
java -version
```

### 3. Tomcat 使用 JDK 17
如果使用外部 Tomcat，需确保 Tomcat 使用 JDK 17：
```powershell
# 设置 JAVA_HOME 环境变量后，Tomcat 会自动使用
# 或在 Tomcat 的 setenv.bat 中指定：
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

## 五、端口规划

| 服务              | 端口  | 说明                       |
|-------------------|-------|---------------------------|
| Tomcat (内嵌/外部) | 8080  | Spring Boot API 服务       |
| Nginx              | 80    | 前端静态资源 + 反向代理     |
| SQL Server         | 1433  | 数据库                     |

## 六、常见问题

### Q1: 端口 8080 被占用？
```powershell
# 查看占用
netstat -ano | findstr :8080
# 结束进程
taskkill /PID 进程号 /F
```

### Q2: 启动报错找不到 JDK？
- 检查 JAVA_HOME 环境变量是否正确
- 确认 JDK 版本为 17

### Q3: 中文乱码？
- 确认 application.yml 中 encoding 配置为 UTF-8
- 确认 Tomcat server.xml 中 URIEncoding="UTF-8"
- 数据库连接字符串已配置编码
