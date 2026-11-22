<template>
  <!-- 学生个人信息页面根容器 -->
  <div class="profile-page">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card" v-loading="loading" shadow="never">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">个人信息</span>
        </div>
      </template>

      <div class="profile-body">
        <!-- 头像区 -->
        <div class="avatar-section">
          <!-- 头像（显示姓名首字母） -->
          <div class="avatar">{{ avatarText }}</div>
          <!-- 姓名 -->
          <div class="avatar-name">{{ userInfo.realName || '-' }}</div>
          <!-- 角色标签 -->
          <el-tag class="role-tag" type="success" effect="dark" round>{{ roleText }}</el-tag>
        </div>

        <!-- 信息卡片网格 -->
        <div class="info-grid">
          <!-- 姓名信息项 -->
          <div class="info-item">
            <div class="info-icon name-icon">👤</div>
            <div class="info-content">
              <div class="info-label">姓名</div>
              <div class="info-value">{{ userInfo.realName || '-' }}</div>
            </div>
          </div>
          <!-- 账号信息项 -->
          <div class="info-item">
            <div class="info-icon account-icon">🔑</div>
            <div class="info-content">
              <div class="info-label">账号</div>
              <div class="info-value">{{ userInfo.username || '-' }}</div>
            </div>
          </div>
          <!-- 角色信息项 -->
          <div class="info-item">
            <div class="info-icon role-icon">🎓</div>
            <div class="info-content">
              <div class="info-label">角色</div>
              <div class="info-value">{{ roleText }}</div>
            </div>
          </div>
          <!-- 班级信息项 -->
          <div class="info-item">
            <div class="info-icon class-icon">🏫</div>
            <div class="info-content">
              <div class="info-label">班级</div>
              <div class="info-value">{{ userInfo.className || '-' }}</div>
            </div>
          </div>
          <!-- 学号信息项 -->
          <div class="info-item">
            <div class="info-icon sno-icon">📝</div>
            <div class="info-content">
              <div class="info-label">学号</div>
              <div class="info-value">{{ userInfo.studentNo || userInfo.sno || '-' }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="pwd-card" shadow="never">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">修改密码</span>
        </div>
      </template>
      <!-- 修改密码表单 -->
      <el-form
        ref="formRef"
        :model="pwdForm"
        :rules="rules"
        label-width="100px"
        class="pwd-form"
      >
        <!-- 旧密码输入项 -->
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="pwdForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入旧密码"
          />
        </el-form-item>
        <!-- 新密码输入项 -->
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="pwdForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码（至少6位）"
          />
        </el-form-item>
        <!-- 确认密码输入项 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="pwdForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        <!-- 操作按钮区 -->
        <el-form-item>
          <el-button class="btn-submit" :loading="pwdLoading" @click="handleSubmitPwd">确认修改</el-button>
          <el-button class="btn-reset" @click="resetPwdForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, changePassword } from '@/api'

// 加载状态
const loading = ref(false)
// 用户信息
const userInfo = ref({})

// 头像文字（姓名首字母大写）
const avatarText = computed(() => {
  const name = userInfo.value.realName || userInfo.value.username || ''
  return name ? name.charAt(0).toUpperCase() : 'S'
})

// 角色文字
const roleText = computed(() => {
  const roleMap = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return roleMap[userInfo.value.role] || '-'
})

// 加载用户信息
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

// ============ 修改密码 ============
// 表单引用
const formRef = ref()
// 密码修改加载状态
const pwdLoading = ref(false)

// 修改密码表单数据
const pwdForm = reactive({
  // 旧密码
  oldPassword: '',
  // 新密码
  newPassword: '',
  // 确认密码
  confirmPassword: ''
})

// 确认密码校验：两次输入一致
const validateConfirm = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 密码修改表单校验规则
const rules = {
  // 旧密码校验
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  // 新密码校验
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  // 确认密码校验
  confirmPassword: [
    { required: true, validator: validateConfirm, trigger: 'blur' }
  ]
}

// 提交修改密码
const handleSubmitPwd = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    pwdLoading.value = true
    try {
      await changePassword({
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功')
      resetPwdForm()
    } catch (error) {
      console.error(error)
    } finally {
      pwdLoading.value = false
    }
  })
}

// 重置密码表单
const resetPwdForm = () => {
  formRef.value?.resetFields()
}

// 组件挂载时加载用户信息
onMounted(() => { loadInfo() })
</script>

<style scoped>
/* 学生个人信息页面容器样式 */
.profile-page {
  background: #f0f6ff;
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

/* 个人信息卡片样式 */
.profile-card {
  width: 100%;
  max-width: 820px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部渐变背景 */
.profile-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  padding: 16px 20px;
  border-bottom: none;
}

/* 卡片标题栏布局 */
.card-header {
  display: flex;
  align-items: center;
}

/* 标题左侧装饰竖条 */
.header-bar {
  display: inline-block;
  width: 4px;
  height: 18px;
  background: #ffffff;
  border-radius: 2px;
  margin-right: 10px;
}

/* 标题文字样式 */
.header-title {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
}

/* 卡片内容区内边距 */
.profile-card :deep(.el-card__body) {
  padding: 30px 24px;
}

/* 主体内容布局 */
.profile-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 头像区 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

/* 头像样式 */
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

/* 头像下方姓名样式 */
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

/* 信息卡片网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  width: 100%;
  max-width: 720px;
}

/* 学号卡片跨整行（在最后一行单数时居中） */
.info-item:last-child:nth-child(odd) {
  grid-column: 1 / -1;
  max-width: 50%;
  justify-self: center;
}

/* 单个信息项样式 */
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

/* 信息图标样式 */
.info-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 14px;
  flex-shrink: 0;
}

/* 姓名图标背景 */
.name-icon {
  background: rgba(29, 78, 216, 0.12);
}

/* 账号图标背景 */
.account-icon {
  background: rgba(59, 130, 246, 0.15);
}

/* 角色图标背景 */
.role-icon {
  background: rgba(29, 78, 216, 0.12);
}

/* 班级图标背景 */
.class-icon {
  background: rgba(59, 130, 246, 0.15);
}

/* 学号图标背景 */
.sno-icon {
  background: rgba(29, 78, 216, 0.12);
}

/* 信息内容区域 */
.info-content {
  flex: 1;
  min-width: 0;
}

/* 信息标签样式 */
.info-label {
  font-size: 12px;
  color: #8c9c93;
  margin-bottom: 4px;
}

/* 信息值样式 */
.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #1d4ed8;
  word-break: break-all;
}

/* ============ 修改密码卡片 ============ */
/* 修改密码卡片样式 */
.pwd-card {
  width: 100%;
  max-width: 820px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 修改密码卡片头部渐变背景 */
.pwd-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  padding: 16px 20px;
  border-bottom: none;
}

/* 修改密码卡片内容区内边距 */
.pwd-card :deep(.el-card__body) {
  padding: 30px 24px;
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

/* 修改密码输入框悬浮/聚焦阴影 */
.pwd-form :deep(.el-input__wrapper:hover),
.pwd-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
}

/* 确认修改按钮样式 */
.btn-submit {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #ffffff;
  border-radius: 6px;
  padding: 0 28px;
  font-weight: 600;
}

/* 确认修改按钮悬浮/聚焦/加载样式 */
.btn-submit:hover,
.btn-submit:focus,
.btn-submit.is-loading {
  background: #73d13d;
  border-color: #73d13d;
  color: #ffffff;
}

/* 重置按钮样式 */
.btn-reset {
  border-color: #1d4ed8;
  color: #1d4ed8;
  border-radius: 6px;
  padding: 0 22px;
}

/* 重置按钮悬浮/聚焦样式 */
.btn-reset:hover,
.btn-reset:focus {
  background: #f0f6ff;
  border-color: #1d4ed8;
  color: #1d4ed8;
}
</style>
