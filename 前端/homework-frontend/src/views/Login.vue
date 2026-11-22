<template>
  <!-- 登录页面最外层容器 -->
  <div class="login-container">
    <!-- 登录卡片容器，包含左侧标题区和右侧表单区 -->
    <div class="login-card">
      <!-- 左侧绿色渐变标题区 -->
      <div class="login-banner">
        <!-- 装饰圆 1（右上角） -->
        <div class="banner-deco banner-deco-1"></div>
        <!-- 装饰圆 2（左下角） -->
        <div class="banner-deco banner-deco-2"></div>
        <!-- 标题内容区 -->
        <div class="banner-content">
          <h1 class="banner-title">学生作业管理系统</h1>
          <p class="banner-subtitle">Student Homework Management System</p>
          <div class="banner-line"></div>
          <p class="banner-desc">高效 · 便捷 · 智能的作业管理平台</p>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-form-wrapper">
        <!-- 表单头部：欢迎语 -->
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <!-- 登录表单，绑定数据、校验规则和表单引用 -->
        <el-form
          ref="loginFormRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="login-form"
        >
          <!-- 班级输入项（可选） -->
          <el-form-item label="班级（可选）" prop="className">
            <el-input
              v-model="form.className"
              placeholder="请输入班级"
              size="large"
              :prefix-icon="OfficeBuilding"
              clearable
            />
          </el-form-item>

          <!-- 账号输入项 -->
          <el-form-item label="账号" prop="account">
            <el-input
              v-model="form.account"
              placeholder="请输入账号"
              size="large"
              :prefix-icon="UserFilled"
              clearable
            />
          </el-form-item>

          <!-- 密码输入项 -->
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <!-- 登录按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>

          <!-- 底部注册入口 -->
          <div class="form-footer">
            <span>还没有账号？</span>
            <el-link type="primary" :underline="false" @click="router.push('/register')">立即注册</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
// 从 vue 引入响应式 API：ref（基本类型响应式）、reactive（对象响应式）
import { ref, reactive } from 'vue'
// 引入路由钩子，用于登录成功后页面跳转
import { useRouter } from 'vue-router'
// 引入 Element Plus 消息提示组件
import { ElMessage } from 'element-plus'
// 引入 Element Plus 图标组件：用户、锁、办公楼
import { UserFilled, Lock, OfficeBuilding } from '@element-plus/icons-vue'
// 引入登录接口方法
import { login } from '@/api'
// 引入用户状态管理 store，用于保存 token 与用户信息
import { useUserStore } from '@/stores/user'

// 路由实例
const router = useRouter()
// 用户状态管理实例
const userStore = useUserStore()
// 登录表单的引用，用于调用表单校验方法
const loginFormRef = ref()
// 登录按钮的加载状态
const loading = ref(false)

// 登录表单数据
const form = reactive({
  className: '', // 班级（可选）
  account: '',   // 账号
  password: ''   // 密码
})

// 表单校验规则
const rules = {
  // 账号必填校验
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  // 密码必填校验
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 角色与首页路由的映射表
const roleRouteMap = {
  ADMIN: '/dashboard',   // 管理员首页
  TEACHER: '/dashboard', // 教师首页
  STUDENT: '/dashboard'  // 学生首页
}

// 登录处理函数：校验表单 -> 调用登录接口 -> 存储 token 与用户信息 -> 跳转首页
const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      // 仅发送账号和密码到 API
      const res = await login({ username: form.account, password: form.password })
      const data = res.data || res
      const token = data.token
      const role = data.role
      // 存储 token 和用户信息到 localStorage
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(data))
      // 同步到 Pinia store
      userStore.setToken(token)
      userStore.setUserInfo(data)
      ElMessage.success('登录成功')
      // 根据角色跳转
      const target = roleRouteMap[role] || '/dashboard'
      router.push(target)
    } catch (error) {
      console.error('登录失败：', error)
      ElMessage.error(error?.response?.data?.message || '登录失败，请检查账号密码')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
/* 登录页面外层容器：全屏居中、渐变背景 */
.login-container {
  --el-color-primary: #1d4ed8;
  --el-color-primary-light-3: #3b82f6;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  box-sizing: border-box;
}

/* 登录卡片：左右分栏布局、白色背景、圆角阴影 */
.login-card {
  display: flex;
  width: 900px;
  max-width: 100%;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(29, 78, 216, 0.25);
  overflow: hidden;
}

/* 左侧背景图标题区 */
.login-banner {
  flex: 1;
  position: relative;
  background: url('/login-bg.jpg') center/cover no-repeat;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  overflow: hidden;
}

/* 装饰圆通用样式 */
.banner-deco {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
}

/* 装饰圆 1：右上角 */
.banner-deco-1 {
  width: 220px;
  height: 220px;
  top: -80px;
  right: -80px;
}

/* 装饰圆 2：左下角 */
.banner-deco-2 {
  width: 160px;
  height: 160px;
  bottom: -60px;
  left: -60px;
}

/* 标题内容区：居中、置于装饰圆之上 */
.banner-content {
  position: relative;
  text-align: center;
  z-index: 1;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
}

/* 主标题样式 */
.banner-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

/* 英文副标题样式 */
.banner-subtitle {
  font-size: 13px;
  opacity: 0.85;
  margin: 0 0 20px;
  letter-spacing: 1px;
}

/* 标题下方分隔线 */
.banner-line {
  width: 50px;
  height: 3px;
  background: rgba(255, 255, 255, 0.7);
  margin: 0 auto 20px;
  border-radius: 2px;
}

/* 描述文字样式 */
.banner-desc {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.8;
  margin: 0;
}

/* 右侧表单区 */
.login-form-wrapper {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 表单头部间距 */
.form-header {
  margin-bottom: 24px;
}

/* 表单头部标题样式 */
.form-header h2 {
  font-size: 22px;
  color: #1d4ed8;
  margin: 0 0 6px;
  font-weight: 600;
}

/* 表单头部提示文字样式 */
.form-header p {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

/* 登录表单宽度 */
.login-form {
  width: 100%;
}

/* 表单项标签样式 */
.login-form :deep(.el-form-item__label) {
  font-size: 13px;
  color: #5a6a7e;
  padding-bottom: 4px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  border: none;
  font-size: 16px;
  letter-spacing: 6px;
  color: #fff;
  transition: all 0.3s ease;
}

/* 登录按钮悬浮/聚焦效果 */
.login-btn:hover,
.login-btn:focus {
  background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(29, 78, 216, 0.45);
  color: #fff;
}

/* 登录按钮按下效果 */
.login-btn:active {
  transform: translateY(0);
}

/* 底部注册入口 */
.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

/* 响应式布局：小屏幕下改为上下排列 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 100%;
  }

  .login-banner {
    padding: 40px 20px;
  }

  .banner-title {
    font-size: 22px;
  }

  .login-form-wrapper {
    padding: 30px 20px;
  }
}
</style>
