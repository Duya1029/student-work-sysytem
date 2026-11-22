<template>
  <!-- 个人中心页面容器 -->
  <div class="profile-page">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card" v-loading="loading" shadow="never">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">个人信息</span>
        </div>
      </template>

      <div class="profile-body">
        <!-- 头像区：显示头像、姓名、角色标签 -->
        <div class="avatar-section">
          <div class="avatar">{{ avatarText }}</div>
          <div class="avatar-name">{{ userInfo.realName || '-' }}</div>
          <el-tag class="role-tag" type="success" effect="dark" round>{{ roleText }}</el-tag>
        </div>

        <!-- 信息卡片网格：姓名、账号、角色、邮箱 -->
        <div class="info-grid">
          <!-- 姓名信息项 -->
          <div class="info-item">
            <div class="info-icon name-icon">
              <el-icon :size="22"><User /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">姓名</div>
              <div class="info-value">{{ userInfo.realName || '-' }}</div>
            </div>
          </div>
          <!-- 账号信息项 -->
          <div class="info-item">
            <div class="info-icon account-icon">
              <el-icon :size="22"><Key /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">账号</div>
              <div class="info-value">{{ userInfo.username || '-' }}</div>
            </div>
          </div>
          <!-- 角色信息项 -->
          <div class="info-item">
            <div class="info-icon role-icon">
              <el-icon :size="22"><Avatar /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">角色</div>
              <div class="info-value">{{ roleText }}</div>
            </div>
          </div>
          <!-- 邮箱信息项（仅当存在邮箱时显示） -->
          <div class="info-item" v-if="userInfo.email">
            <div class="info-icon account-icon">
              <el-icon :size="22"><Message /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">邮箱</div>
              <div class="info-value">{{ userInfo.email }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="pwd-card" shadow="never">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">修改密码</span>
        </div>
      </template>
      <!-- 修改密码表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="pwd-form"
      >
        <!-- 旧密码输入项 -->
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            show-password
            placeholder="请输入旧密码"
          />
        </el-form-item>
        <!-- 新密码输入项 -->
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码（至少6位）"
          />
        </el-form-item>
        <!-- 确认密码输入项 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        <!-- 提交与重置按钮 -->
        <el-form-item>
          <el-button class="btn-submit" :loading="pwdLoading" @click="handleSubmit">确认修改</el-button>
          <el-button class="btn-reset" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
// 从 vue 引入：ref、reactive、computed、onMounted
import { ref, reactive, computed, onMounted } from 'vue'
// 引入 Element Plus 消息提示组件
import { ElMessage } from 'element-plus'
// 引入 Element Plus 图标组件：用户、钥匙、头像、消息
import { User, Key, Avatar, Message } from '@element-plus/icons-vue'
// 引入用户相关接口：获取用户信息、修改密码
import { getUserInfo, changePassword } from '@/api'

// 个人信息加载状态
const loading = ref(false)
// 修改密码提交加载状态
const pwdLoading = ref(false)
// 用户信息
const userInfo = ref({})

// 头像文字：取姓名/账号首字母大写，默认 A
const avatarText = computed(() => {
  const name = userInfo.value.realName || userInfo.value.username || ''
  return name ? name.charAt(0).toUpperCase() : 'A'
})

// 角色对应的中文文本
const roleText = computed(() => {
  const roleMap = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return roleMap[userInfo.value.role] || '-'
})

// 修改密码表单引用
const formRef = ref()
// 修改密码表单数据
const form = reactive({
  oldPassword: '',     // 旧密码
  newPassword: '',     // 新密码
  confirmPassword: ''  // 确认密码
})

// 确认密码校验：非空且与新密码一致
const validateConfirm = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单校验规则
const rules = {
  // 旧密码必填
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  // 新密码必填且不少于 6 位
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  // 确认密码使用自定义校验
  confirmPassword: [
    { required: true, validator: validateConfirm, trigger: 'blur' }
  ]
}

// 加载用户个人信息
const loadInfo = async () => {
  loading.value = true
  try {
    const res = await getUserInfo()
    userInfo.value = res.data || {}
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 提交修改密码：先校验表单，再调用接口
const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    pwdLoading.value = true
    try {
      await changePassword({
        oldPassword: form.oldPassword,
        newPassword: form.newPassword
      })
      ElMessage.success('密码修改成功')
      resetForm()
    } catch (error) {
      console.error(error)
    } finally {
      pwdLoading.value = false
    }
  })
}

// 重置修改密码表单
const resetForm = () => {
  formRef.value?.resetFields()
}

// 组件挂载时加载用户信息
onMounted(() => { loadInfo() })
</script>

<style scoped>
/* 个人中心页面：浅蓝背景、内边距 */
.profile-page {
  background: #f0f6ff;
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

/* 个人信息卡片与修改密码卡片：居中、圆角、阴影 */
.profile-card,
.pwd-card {
  max-width: 820px;
  margin: 0 auto 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部：蓝色渐变背景 */
.profile-card :deep(.el-card__header),
.pwd-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  padding: 16px 20px;
  border-bottom: none;
}

/* 卡片头部内容：水平布局 */
.card-header {
  display: flex;
  align-items: center;
}

/* 头部左侧装饰竖条 */
.header-bar {
  display: inline-block;
  width: 4px;
  height: 18px;
  background: #ffffff;
  border-radius: 2px;
  margin-right: 10px;
}

/* 头部标题文字 */
.header-title {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
}

/* 卡片内容区内边距 */
.profile-card :deep(.el-card__body),
.pwd-card :deep(.el-card__body) {
  padding: 30px 24px;
}

/* 个人信息主体：纵向居中布局 */
.profile-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 头像区：纵向居中 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

/* 头像：圆形蓝色渐变背景 */
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  color: #ffffff;
  font-size: 32px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(29, 78, 216, 0.3);
  margin-bottom: 12px;
}

/* 头像下方姓名 */
.avatar-name {
  font-size: 20px;
  font-weight: 600;
  color: #1d4ed8;
  margin-bottom: 8px;
}

/* 角色标签样式 */
.role-tag.el-tag--success {
  background: #f6ffed;
  border-color: #3b82f6;
  color: #1d4ed8;
  font-weight: 600;
}

/* 信息卡片网格：两列布局 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  width: 100%;
  max-width: 720px;
}

/* 最后一个信息项为奇数时跨列居中 */
.info-item:last-child:nth-child(odd) {
  grid-column: 1 / -1;
  max-width: 50%;
  justify-self: center;
}

/* 单个信息项：图标 + 内容 水平布局 */
.info-item {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  background: #f8fcfa;
  border: 1px solid #d6e9dd;
  border-radius: 10px;
  transition: all 0.2s ease;
}

/* 信息项悬浮效果 */
.info-item:hover {
  box-shadow: 0 4px 12px rgba(29, 78, 216, 0.12);
  border-color: #3b82f6;
  transform: translateY(-2px);
}

/* 信息图标容器 */
.info-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  flex-shrink: 0;
  color: #1d4ed8;
}

/* 姓名、角色图标背景 */
.name-icon,
.role-icon {
  background: rgba(29, 78, 216, 0.12);
}

/* 账号图标背景 */
.account-icon {
  background: rgba(59, 130, 246, 0.15);
}

/* 信息内容区 */
.info-content {
  flex: 1;
  min-width: 0;
}

/* 信息标签（小字） */
.info-label {
  font-size: 12px;
  color: #8c9c93;
  margin-bottom: 4px;
}

/* 信息值（大字） */
.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #1d4ed8;
  word-break: break-all;
}

/* 修改密码表单标签样式 */
.pwd-form :deep(.el-form-item__label) {
  color: #1d4ed8;
  font-weight: 500;
}

/* 修改密码输入框圆角 */
.pwd-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 修改密码输入框悬浮/聚焦边框色 */
.pwd-form :deep(.el-input__wrapper:hover),
.pwd-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
}

/* 确认修改按钮：蓝色主题 */
.btn-submit {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #ffffff;
  border-radius: 6px;
  padding: 0 28px;
  font-weight: 600;
}

/* 确认修改按钮悬浮/加载态 */
.btn-submit:hover,
.btn-submit:focus,
.btn-submit.is-loading {
  background: #73d13d;
  border-color: #73d13d;
  color: #ffffff;
}

/* 重置按钮：蓝色描边 */
.btn-reset {
  border-color: #1d4ed8;
  color: #1d4ed8;
  border-radius: 6px;
  padding: 0 22px;
}

/* 重置按钮悬浮态 */
.btn-reset:hover,
.btn-reset:focus {
  background: #f0f6ff;
  border-color: #1d4ed8;
  color: #1d4ed8;
}
</style>
