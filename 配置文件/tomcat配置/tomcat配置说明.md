# ============================================================
# Tomcat 服务器配置 - 学生作业管理系统
# 说明：本项目使用 Spring Boot 内嵌 Tomcat，以下为配置说明
# ============================================================

# ============================================================
# 方式一：Spring Boot 内嵌 Tomcat（推荐，默认方式）
# ============================================================

## 1. application.yml 中的 Tomcat 配置

server:
  port: 8080                              # Tomcat 端口
  servlet:
    context-path: /                       # 上下文路径
    encoding:
      charset: UTF-8                      # 字符编码
      enabled: true
      force: true

  # Tomcat 连接器配置
  tomcat:
    max-connections: 200                   # 最大连接数
    accept-count: 100                     # 等待队列长度
    max-threads: 200                      # 最大工作线程数
    min-spare-threads: 10                 # 最小空闲线程数
    connection-timeout: 20000             # 连接超时（毫秒）
    uri-encoding: UTF-8                   # URI 编码

# ============================================================
# 方式二：外部 Tomcat 部署（可选，如需独立部署到 Tomcat）
# ============================================================

## 1. 修改 pom.xml，将打包方式改为 war
##    将 <packaging>jar</packaging> 改为 <packaging>war</packaging>
##    或添加：
##    <packaging>war</packaging>

## 2. 排除内嵌 Tomcat
##    在 pom.xml 的 spring-boot-starter-web 依赖中排除 Tomcat：
##    <dependency>
##        <groupId>org.springframework.boot</groupId>
##        <artifactId>spring-boot-starter-web</artifactId>
##        <exclusions>
##            <exclusion>
##                <groupId>org.springframework.boot</groupId>
##                <artifactId>spring-boot-starter-tomcat</artifactId>
##            </exclusion>
##        </exclusions>
##    </dependency>
##    <dependency>
##        <groupId>org.springframework.boot</groupId>
##        <artifactId>spring-boot-starter-tomcat</artifactId>
##        <scope>provided</scope>
##    </dependency>

## 3. 修改启动类，继承 SpringBootServletInitializer
##    public class HomeworkApplication extends SpringBootServletInitializer {
##        @Override
##        protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
##            return builder.sources(HomeworkApplication.class);
##        }
##        public static void main(String[] args) {
##            SpringApplication.run(HomeworkApplication.class, args);
##        }
##    }

## 4. 打包：mvn clean package
##    生成 target/homework-system-1.0.0.war

## 5. 部署到外部 Tomcat：
##    将 war 包复制到 Tomcat 的 webapps 目录
##    启动 Tomcat：startup.bat 或 startup.sh
##    访问地址：http://localhost:8080/homework-system-1.0.0/api/...
