# 学生作业管理系统 - 自动启动脚本
# 作用：开机登录后自动启动 Nginx 前端和 Spring Boot 后端，无需手动打开文件夹

$ErrorActionPreference = "SilentlyContinue"

# 1. 启动 Nginx（监听 80 端口）
$nginxRunning = Get-NetTCPConnection -LocalPort 80 -State Listen
if (-not $nginxRunning) {
    Start-Process -FilePath "C:\nginx\nginx-1.30.4\nginx.exe" `
        -WorkingDirectory "C:\nginx\nginx-1.30.4" `
        -WindowStyle Hidden
}

# 2. 启动后端服务（监听 8090 端口）
$backendRunning = Get-NetTCPConnection -LocalPort 8090 -State Listen
if (-not $backendRunning) {
    Start-Process -FilePath "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\java.exe" `
        -ArgumentList "-jar", "C:\学生作业管理系统\后端\homework-system\target\homework-system-1.0.0.jar" `
        -WorkingDirectory "C:\学生作业管理系统\后端\homework-system" `
        -WindowStyle Hidden
}

# 3. 启动 cpolar 公网隧道（把本地 80 端口映射到公网）
$cpolarRunning = Get-Process cpolar -ErrorAction SilentlyContinue
if (-not $cpolarRunning) {
    Start-Process -FilePath "C:\Users\28467\cpolar\cpolar.exe" `
        -ArgumentList "http", "80" `
        -WindowStyle Hidden
}
