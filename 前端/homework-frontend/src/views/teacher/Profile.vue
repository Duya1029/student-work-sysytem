<template>
  <!-- 个人信息页面根容器 -->
  <div class="profile-page">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card" v-loading="loading">
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
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getUserInfo } from '@/api'

// 加载状态
const loading = ref(false)
// 用户信息
const userInfo = ref({})

// 头像文字（姓名首字母大写）
const avatarText = computed(() => {
  const name = userInfo.value.realName || userInfo.value.username || ''
  return name ? name.charAt(0).toUpperCase() : 'T'
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

// 组件挂载时加载用户信息
onMounted(() => { loadInfo() })
</script>

<style scoped>
/* 个人信息页面容器样式 */
.profile-page {
  background: #f0f6ff;
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

/* 个人信息卡片样式 */
.profile-card {
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
</style>
