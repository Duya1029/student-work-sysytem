# ============================================================
# Nginx 安装与配置指南 - 学生作业管理系统
# ============================================================

## 一、下载 Nginx

### 方式一：官网下载（推荐）
1. 访问 Nginx 官网：http://nginx.org/en/download.html
2. 下载 Windows 稳定版：nginx/Windows-x.x.xx.zip（如 nginx-1.24.0.zip）
3. 解压到指定目录，例如：C:\nginx

### 方式二：命令下载（如果有 winget）
```powershell
winget install Nginx.Nginx
```

## 二、配置 Nginx

1. 打开 Nginx 配置目录：`C:\nginx\conf\`
2. 备份原始配置文件：`copy nginx.conf nginx.conf.bak`
3. 将本项目提供的 `nginx.conf` 文件复制到 `C:\nginx\conf\` 目录覆盖原文件
4. 修改配置文件中的前端资源路径：
   ```
   root  "C:/Users/28467/homework-system/frontend/dist";
   ```
   将路径改为您实际的前端打包输出路径

## 三、前端打包部署

1. 进入前端项目目录：
   ```powershell
   cd "C:\Users\28467\homework-system\frontend"
   ```
2. 安装依赖：
   ```powershell
   npm install
   ```
3. 打包构建：
   ```powershell
   npm run build
   ```
4. 将生成的 `dist` 目录复制到 Nginx 配置的路径下

## 四、启动 Nginx

1. 打开 CMD 或 PowerShell
2. 切换到 Nginx 目录：
   ```powershell
   cd C:\nginx
   ```
3. 启动 Nginx：
   ```powershell
   start nginx
   ```
4. 验证启动：浏览器访问 http://localhost，应看到前端登录页

## 五、常用 Nginx 命令

```powershell
# 启动
start nginx

# 停止（优雅关闭，等待请求处理完毕）
nginx -s quit

# 强制停止
nginx -s stop

# 重新加载配置（修改配置后无需重启）
nginx -s reload

# 检查配置文件语法是否正确
nginx -t

# 查看版本
nginx -v
```

## 六、验证反向代理

1. 确保后端 Spring Boot 已启动（端口 8080）
2. 确保前端已打包并部署到 Nginx
3. 启动 Nginx
4. 浏览器访问 http://localhost → 前端页面
5. 登录请求 `POST http://localhost/api/login` → 代理到 `http://localhost:8080/api/login`

## 七、常见问题

### Q1: 端口 80 被占用？
- 检查占用程序：`netstat -ano | findstr :80`
- 修改 nginx.conf 中的 listen 端口为 8081 或其他空闲端口

### Q2: 访问出现 502 Bad Gateway？
- 检查后端 Spring Boot 是否已启动
- 检查 8080 端口是否正常监听

### Q3: 前端页面空白？
- 检查 dist 目录路径是否正确
- 检查 Nginx 配置中 root 路径是否使用了正斜杠 `/`
