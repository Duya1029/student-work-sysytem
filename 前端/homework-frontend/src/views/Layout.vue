<template>
  <!-- 整体布局容器：侧边栏 + 顶部导航 + 主内容区 -->
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '210px'" class="aside">
      <!-- 侧边栏顶部 Logo 区域，折叠时显示缩写 -->
      <div class="logo">
        <span v-if="!isCollapse">作业管理系统</span>
        <span v-else>HW</span>
      </div>
      <!-- 侧边栏菜单，开启路由模式，根据当前路由高亮 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        background-color="transparent"
        text-color="#ffffff"
        active-text-color="#3b82f6"
        class="el-menu-vertical"
      >
        <!-- 首页菜单项 -->
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 管理员菜单 -->
        <template v-if="role === 'ADMIN'">
          <!-- 系统管理子菜单 -->
          <el-sub-menu index="admin-system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <!-- 用户管理 -->
            <el-menu-item index="/admin/user">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <!-- 班级管理 -->
            <el-menu-item index="/admin/class">
              <el-icon><School /></el-icon>
              <span>班级管理</span>
            </el-menu-item>
            <!-- 课程管理 -->
            <el-menu-item index="/admin/course">
              <el-icon><Reading /></el-icon>
              <span>课程管理</span>
            </el-menu-item>
            <!-- 学期管理 -->
            <el-menu-item index="/admin/semester">
              <el-icon><Calendar /></el-icon>
              <span>学期管理</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 作业管理 -->
          <el-menu-item index="/admin/homework">
            <el-icon><Document /></el-icon>
            <span>作业管理</span>
          </el-menu-item>
          <!-- 公告管理 -->
          <el-menu-item index="/admin/announcement">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
          <!-- 系统日志 -->
          <el-menu-item index="/admin/log">
            <el-icon><Tickets /></el-icon>
            <span>系统日志</span>
          </el-menu-item>
          <!-- 个人中心 -->
          <el-menu-item index="/admin/profile">
            <el-icon><UserFilled /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </template>

        <!-- 教师菜单 -->
        <template v-if="role === 'TEACHER'">
          <!-- 我的课程 -->
          <el-menu-item index="/teacher/course">
            <el-icon><Reading /></el-icon>
            <span>我的课程</span>
          </el-menu-item>
          <!-- 教学管理子菜单 -->
          <el-sub-menu index="teacher-teaching">
            <template #title>
              <el-icon><EditPen /></el-icon>
              <span>教学管理</span>
            </template>
            <!-- 发布作业 -->
            <el-menu-item index="/teacher/publish">
              <el-icon><Edit /></el-icon>
              <span>发布作业</span>
            </el-menu-item>
            <!-- 作业批改 -->
            <el-menu-item index="/teacher/grade">
              <el-icon><DocumentChecked /></el-icon>
              <span>作业批改</span>
            </el-menu-item>
            <!-- 成绩统计 -->
            <el-menu-item index="/teacher/statistics">
              <el-icon><TrendCharts /></el-icon>
              <span>成绩统计</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 通知公告 -->
          <el-menu-item index="/teacher/announcement">
            <el-icon><Bell /></el-icon>
            <span>通知公告</span>
          </el-menu-item>
          <!-- 个人中心 -->
          <el-menu-item index="/teacher/profile">
            <el-icon><UserFilled /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </template>

        <!-- 学生菜单 -->
        <template v-if="role === 'STUDENT'">
          <!-- 我的课程 -->
          <el-menu-item index="/student/course">
            <el-icon><Reading /></el-icon>
            <span>我的课程</span>
          </el-menu-item>
          <!-- 我的作业 -->
          <el-menu-item index="/student/homework">
            <el-icon><Document /></el-icon>
            <span>我的作业</span>
          </el-menu-item>
          <!-- 成绩查询 -->
          <el-menu-item index="/student/grades">
            <el-icon><TrendCharts /></el-icon>
            <span>成绩查询</span>
          </el-menu-item>
          <!-- 通知公告 -->
          <el-menu-item index="/student/announcement">
            <el-icon><Bell /></el-icon>
            <span>通知公告</span>
          </el-menu-item>
          <!-- 个人中心 -->
          <el-menu-item index="/student/profile">
            <el-icon><UserFilled /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 右侧区域：顶部导航 + 主内容 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <!-- 左侧：侧边栏折叠按钮 -->
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <!-- 右侧：用户信息下拉菜单 -->
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon class="user-avatar"><UserFilled /></el-icon>
              <span class="user-name">{{ userInfo?.realName || userInfo?.username }}</span>
              <el-tag size="small" type="success" effect="light" round class="role-tag">{{ roleText }}</el-tag>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区：通过 router-view 渲染子路由页面 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 从 vue 引入响应式 API：ref、computed、生命周期钩子
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
// 引入路由钩子：useRouter（跳转）、useRoute（当前路由信息）
import { useRouter, useRoute } from 'vue-router'
// 引入 Element Plus 消息提示与确认框组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入 Element Plus 图标组件
import {
  HomeFilled, Setting, EditPen, Reading,
  Fold, Expand, UserFilled,
  User, School, Document, Bell, TrendCharts,
  Calendar, Tickets, Edit, DocumentChecked
} from '@element-plus/icons-vue'
// 引入用户状态管理 store
import { useUserStore } from '@/stores/user'
// 引入退出登录接口方法
import { logout } from '@/api'

// 路由实例，用于页面跳转
const router = useRouter()
// 当前路由实例，用于获取当前路径
const route = useRoute()
// 用户状态管理实例
const userStore = useUserStore()

// 侧边栏是否折叠
const isCollapse = ref(false)
// 当前登录用户信息
const userInfo = computed(() => userStore.userInfo)
// 当前用户角色
const role = computed(() => userStore.userInfo?.role || '')

// 当前激活菜单，取当前路由路径
const activeMenu = computed(() => route.path)

// 角色对应的中文文本
const roleText = computed(() => {
  const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return map[role.value] || ''
})

// 处理下拉菜单命令，目前仅处理退出登录
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await logout().catch(() => {})
      userStore.clear()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (error) {
      // 取消退出
    }
  }
}

// 根据屏幕宽度自动折叠侧边栏：手机窄屏下折叠，给内容让出空间
const handleResize = () => {
  isCollapse.value = window.innerWidth < 768
}

// 页面挂载时初始化并监听窗口尺寸变化
onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

// 页面卸载时移除监听，避免内存泄漏
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 布局容器：占满整个视口高度 */
.layout-container {
  height: 100vh;
}

/* 侧边栏：蓝色渐变背景，宽度变化带过渡动画 */
.aside {
  background: linear-gradient(180deg, #1e3a8a 0%, #1d4ed8 100%);
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(26, 92, 58, 0.2);
}

/* Logo 区域样式 */
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 16px;
  font-weight: bold;
  background: #1e3a8a;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  white-space: nowrap;
  letter-spacing: 1px;
}

/* 侧边栏菜单：透明背景、去掉右边框 */
.el-menu-vertical {
  border-right: none;
  background: transparent;
}

/* 菜单项与子菜单标题文字颜色 */
.el-menu-vertical :deep(.el-menu-item),
.el-menu-vertical :deep(.el-sub-menu__title) {
  color: #ffffff;
}

/* 菜单项与子菜单标题悬浮效果 */
.el-menu-vertical :deep(.el-menu-item:hover),
.el-menu-vertical :deep(.el-sub-menu__title:hover) {
  background-color: rgba(59, 130, 246, 0.18);
  color: #ffffff;
}

/* 当前激活菜单项样式 */
.el-menu-vertical :deep(.el-menu-item.is-active) {
  background-color: rgba(59, 130, 246, 0.25);
  color: #3b82f6;
  font-weight: 600;
  position: relative;
}

/* 激活菜单项左侧高亮指示条 */
.el-menu-vertical :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: #3b82f6;
}

/* 子菜单内部背景色 */
.el-menu-vertical :deep(.el-sub-menu .el-menu) {
  background-color: rgba(0, 0, 0, 0.15);
}

/* 顶部导航栏：左右分布、白底蓝底边 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  border-bottom: 2px solid #1d4ed8;
  box-shadow: 0 2px 8px rgba(29, 78, 216, 0.08);
}

/* 顶部导航左侧容器 */
.header-left {
  display: flex;
  align-items: center;
}

/* 折叠按钮样式 */
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #5a5e66;
  transition: all 0.3s ease;
  padding: 6px;
  border-radius: 6px;
}

/* 折叠按钮悬浮效果 */
.collapse-btn:hover {
  color: #1d4ed8;
  background-color: #eff6ff;
  transform: scale(1.1);
}

/* 顶部导航右侧容器 */
.header-right {
  display: flex;
  align-items: center;
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #333333;
  font-size: 14px;
  padding: 6px 14px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

/* 用户信息区域悬浮效果 */
.user-info:hover {
  background-color: #eff6ff;
}

/* 用户头像图标样式 */
.user-avatar {
  font-size: 18px;
  color: #1d4ed8;
  margin-right: 8px;
  padding: 4px;
  background: #eff6ff;
  border-radius: 50%;
}

/* 用户名样式 */
.user-name {
  font-weight: 500;
  margin-right: 8px;
}

/* 角色标签样式 */
.role-tag {
  font-weight: 500;
}

/* 主内容区：浅蓝背景、内边距、可滚动 */
.main {
  background: #f0f6ff;
  padding: 20px;
  overflow-y: auto;
  overflow-x: auto;
}

/* 手机端适配：减小内边距，隐藏用户名只留角色标签 */
@media (max-width: 768px) {
  .main {
    padding: 10px;
  }

  .user-name {
    display: none;
  }

  .user-info {
    padding: 6px 8px;
  }
}
</style>
