<template>
  <!-- 注册页面最外层容器 -->
  <div class="login-container">
    <!-- 注册卡片：左侧标题区 + 右侧表单区 -->
    <div class="login-card">
      <!-- 左侧背景图标题区 -->
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
          <p class="banner-desc">注册账号 · 开启高效便捷的作业管理</p>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-form-wrapper">
        <!-- 表单头部：欢迎语 -->
        <div class="form-header">
          <h2>注册账号</h2>
          <p>学生和教师均可自助注册，注册后自动登录</p>
        </div>

        <!-- 注册表单 -->
        <el-form
          ref="registerFormRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="login-form"
        >
          <!-- 身份选择：学生 / 教师 -->
          <el-form-item label="身份">
            <el-radio-group v-model="form.role" @change="handleRoleChange">
              <el-radio-button label="STUDENT">学生</el-radio-button>
              <el-radio-button label="TEACHER">教师</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <!-- 真实姓名 -->
          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="form.realName"
              placeholder="请输入真实姓名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <!-- 班级选择：仅学生显示 -->
          <el-form-item v-if="form.role === 'STUDENT'" label="班级" prop="classId">
            <el-select
              v-model="form.classId"
              placeholder="请选择班级"
              size="large"
              style="width: 100%"
              clearable
            >
              <el-option
                v-for="item in classList"
                :key="item.classId"
                :label="item.className"
                :value="item.classId"
              />
            </el-select>
          </el-form-item>

          <!-- 学号：仅学生显示，可选 -->
          <el-form-item v-if="form.role === 'STUDENT'" label="学号（可选）" prop="studentNo">
            <el-input
              v-model="form.studentNo"
              placeholder="请输入学号"
              size="large"
              clearable
            />
          </el-form-item>

          <!-- 登录账号 -->
          <el-form-item label="账号" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入账号（至少 3 位）"
              size="large"
              :prefix-icon="UserFilled"
              clearable
            />
          </el-form-item>

          <!-- 登录密码 -->
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码（至少 6 位）"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <!-- 确认密码 -->
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <!-- 注册按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleRegister"
            >
              注 册
            </el-button>
          </el-form-item>

          <!-- 底部返回登录 -->
          <div class="form-footer">
            <span>已有账号？</span>
            <el-link type="primary" :underline="false" @click="router.push('/login')">返回登录</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
// 引入响应式 API
import { ref, reactive, onMounted } from 'vue'
// 引入路由钩子
import { useRouter } from 'vue-router'
// 引入消息提示
import { ElMessage } from 'element-plus'
// 引入图标
import { User, UserFilled, Lock } from '@element-plus/icons-vue'
// 引入注册接口和班级列表接口
import { register, getRegisterClasses } from '@/api'
// 引入用户状态管理
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
// 表单引用
const registerFormRef = ref()
// 提交加载状态
const loading = ref(false)
// 班级下拉列表
const classList = ref([])

// 注册表单数据
const form = reactive({
  role: 'STUDENT',   // 身份：默认学生
  realName: '',      // 真实姓名
  classId: null,     // 班级ID（学生必填）
  studentNo: '',     // 学号（学生选填）
  username: '',      // 登录账号
  password: '',      // 登录密码
  confirmPassword: '' // 确认密码
})

// 确认密码校验：必须与密码一致
const validateConfirm = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单校验规则
const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, message: '账号至少需要 3 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少需要 6 位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validateConfirm, trigger: 'blur' }]
}

// 切换身份时清空学生专属字段
const handleRoleChange = () => {
  form.classId = null
  form.studentNo = ''
}

// 页面加载时获取班级列表
onMounted(async () => {
  try {
    const res = await getRegisterClasses()
    classList.value = res.data || []
  } catch (error) {
    classList.value = []
  }
})

// 注册处理：校验表单 -> 调用注册接口 -> 保存登录状态 -> 跳转首页
const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await register({
        username: form.username,
        password: form.password,
        realName: form.realName,
        role: form.role,
        classId: form.role === 'STUDENT' ? form.classId : null,
        studentNo: form.role === 'STUDENT' ? form.studentNo : null
      })
      const data = res.data || res
      // 保存 token 和用户信息
      localStorage.setItem('token', data.token)
      localStorage.setItem('userInfo', JSON.stringify(data))
      userStore.setToken(data.token)
      userStore.setUserInfo(data)
      ElMessage.success('注册成功，已自动登录')
      router.push('/dashboard')
    } catch (error) {
      ElMessage.error(error?.response?.data?.message || error?.message || '注册失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
/* 页面外层容器 */
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

/* 注册卡片 */
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

/* 装饰圆 */
.banner-deco {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
}

.banner-deco-1 {
  width: 220px;
  height: 220px;
  top: -80px;
  right: -80px;
}

.banner-deco-2 {
  width: 160px;
  height: 160px;
  bottom: -60px;
  left: -60px;
}

/* 标题内容区 */
.banner-content {
  position: relative;
  text-align: center;
  z-index: 1;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
}

.banner-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.banner-subtitle {
  font-size: 13px;
  opacity: 0.85;
  margin: 0 0 20px;
  letter-spacing: 1px;
}

.banner-line {
  width: 50px;
  height: 3px;
  background: rgba(255, 255, 255, 0.7);
  margin: 0 auto 20px;
  border-radius: 2px;
}

.banner-desc {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.8;
  margin: 0;
}

/* 右侧表单区 */
.login-form-wrapper {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 20px;
}

.form-header h2 {
  font-size: 22px;
  color: #1d4ed8;
  margin: 0 0 6px;
  font-weight: 600;
}

.form-header p {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.login-form {
  width: 100%;
}

.login-form :deep(.el-form-item__label) {
  font-size: 13px;
  color: #5a6a7e;
  padding-bottom: 4px;
}

/* 注册按钮 */
.login-btn {
  width: 100%;
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  border: none;
  font-size: 16px;
  letter-spacing: 6px;
  color: #fff;
  transition: all 0.3s ease;
}

.login-btn:hover,
.login-btn:focus {
  background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(29, 78, 216, 0.45);
  color: #fff;
}

.login-btn:active {
  transform: translateY(0);
}

/* 底部返回登录 */
.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

/* 响应式：小屏改为上下排列 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 100%;
  }

  .login-banner {
    padding: 30px 20px;
  }

  .banner-title {
    font-size: 22px;
  }

  .login-form-wrapper {
    padding: 24px 20px;
  }
}
</style>
