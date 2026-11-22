<template>
  <!-- 首页仪表盘容器 -->
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <!-- 欢迎头像图标 -->
        <div class="welcome-avatar">
          <el-icon :size="40"><HomeFilled /></el-icon>
        </div>
        <!-- 欢迎文字与角色标签 -->
        <div class="welcome-text">
          <h2>欢迎回来，{{ userInfo?.realName || userInfo?.username }}！</h2>
          <p>您当前的角色是：<el-tag type="success" effect="dark" round>{{ roleText }}</el-tag></p>
        </div>
        <!-- 装饰圆形 -->
        <div class="welcome-deco"></div>
      </div>
    </el-card>

    <!-- 管理员：统计卡片 + 图表 -->
    <template v-if="role === 'ADMIN'">
      <!-- 统计卡片行：学生数、教师数、班级数等 -->
      <el-row :gutter="20" class="stat-row">
        <el-col :xs="12" :sm="6" v-for="item in adminCards" :key="item.title">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <!-- 统计图标容器，背景色取自配置 -->
              <div class="stat-icon-wrap" :style="{ background: item.bgColor }">
                <el-icon :size="30" color="#ffffff"><component :is="item.icon" /></el-icon>
              </div>
              <!-- 统计标题与数值 -->
              <div class="stat-info">
                <div class="stat-title">{{ item.title }}</div>
                <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <!-- 柱状图：各班级学生人数 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="never">
            <div class="chart-title"><span class="chart-bar"></span>各班级学生人数分布</div>
            <!-- 柱状图区域，加载时显示 loading -->
            <div class="bar-chart" v-loading="loading">
              <div v-if="classDist.length === 0" class="empty-tip">暂无数据</div>
              <!-- 单个柱状条项 -->
              <div v-for="(item, idx) in classDist" :key="idx" class="bar-item">
                <div class="bar-label" :title="item.name">{{ item.name }}</div>
                <div class="bar-track">
                  <!-- 进度填充条，宽度按百分比 -->
                  <div class="bar-fill" :style="{ width: item.percent + '%' }">
                    <span v-if="item.percent > 15" class="bar-fill-text">{{ item.count }}</span>
                  </div>
                  <span v-if="item.percent <= 15" class="bar-value">{{ item.count }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 饼图：作业完成率 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="never">
            <div class="chart-title"><span class="chart-bar"></span>作业完成率统计</div>
            <!-- 饼图区域 -->
            <div class="pie-wrap" v-loading="loading">
              <!-- 环形饼图，通过 conic-gradient 实现 -->
              <div class="pie-chart" :style="pieStyle">
                <!-- 中间镂空区域，显示完成率 -->
                <div class="pie-hole">
                  <div class="pie-rate">{{ submitRate }}%</div>
                  <div class="pie-rate-label">完成率</div>
                </div>
              </div>
              <!-- 图例区 -->
              <div class="pie-legend">
                <!-- 已提交图例 -->
                <div class="legend-item">
                  <span class="dot submitted"></span>
                  <span class="legend-label">已提交</span>
                  <span class="legend-value">{{ submittedCount }}</span>
                </div>
                <!-- 未提交图例 -->
                <div class="legend-item">
                  <span class="dot unsubmitted"></span>
                  <span class="legend-label">未提交</span>
                  <span class="legend-value">{{ unsubmittedCount }}</span>
                </div>
                <!-- 作业总数 -->
                <div class="legend-item total">
                  <span class="legend-label">作业总数</span>
                  <span class="legend-value">{{ submittedCount + unsubmittedCount }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 非管理员：统计卡片 -->
    <template v-else>
      <el-row :gutter="20" class="stat-row">
        <el-col :xs="24" :sm="12" :md="8" v-for="item in statList" :key="item.title">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <!-- 统计图标容器 -->
              <div class="stat-icon-wrap" :style="{ background: item.bgColor }">
                <el-icon :size="32" color="#ffffff">
                  <component :is="item.icon" />
                </el-icon>
              </div>
              <!-- 统计标题与数值 -->
              <div class="stat-info">
                <div class="stat-title">{{ item.title }}</div>
                <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
// 从 vue 引入：ref、computed、onMounted
import { ref, computed, onMounted } from 'vue'
// 引入 Element Plus 图标组件
import { HomeFilled, User, UserFilled, Document, EditPen, School, Notebook, Calendar } from '@element-plus/icons-vue'
// 引入用户状态管理 store
import { useUserStore } from '@/stores/user'
// 引入统计数据接口
import { getStatistics } from '@/api'

// 用户状态管理实例
const userStore = useUserStore()
// 当前登录用户信息
const userInfo = computed(() => userStore.userInfo)
// 当前用户角色
const role = computed(() => userStore.userInfo?.role || '')

// 加载状态
const loading = ref(false)
// 统计数据
const stats = ref({})

// 角色对应的中文文本
const roleText = computed(() => {
  const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return map[role.value] || ''
})

// 将值安全转换为数字，非数字返回 0
const num = (v) => {
  const n = Number(v)
  return Number.isFinite(n) ? n : 0
}

// 管理员统计卡片：扩展为 学生数、教师数、班级数、课程数、作业数、本周作业数
// 仅当后端返回了对应字段时才显示对应卡片
const adminCards = computed(() => {
  const cards = [
    { title: '学生数', value: num(stats.value.studentCount), raw: stats.value.studentCount, icon: User, color: '#1d4ed8', bgColor: 'linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%)' },
    { title: '教师数', value: num(stats.value.teacherCount), raw: stats.value.teacherCount, icon: UserFilled, color: '#1e3a8a', bgColor: 'linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 100%)' },
    { title: '班级数', value: num(stats.value.classCount), raw: stats.value.classCount, icon: School, color: '#2563eb', bgColor: 'linear-gradient(135deg, #2563eb 0%, #3b82f6 100%)' },
    { title: '课程数', value: num(stats.value.courseCount), raw: stats.value.courseCount, icon: Notebook, color: '#1e3a8a', bgColor: 'linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%)' },
    { title: '作业数', value: num(stats.value.homeworkCount), raw: stats.value.homeworkCount, icon: Document, color: '#1d4ed8', bgColor: 'linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%)' },
    { title: '本周作业数', value: num(stats.value.weeklyHomeworkCount ?? stats.value.thisWeekHomeworkCount ?? stats.value.weekHomeworkCount), raw: stats.value.weeklyHomeworkCount ?? stats.value.thisWeekHomeworkCount ?? stats.value.weekHomeworkCount, icon: Calendar, color: '#2563eb', bgColor: 'linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)' }
  ]
  // 仅在后端返回了该字段（非 undefined/null）时显示对应卡片
  return cards.filter(c => c.raw !== undefined && c.raw !== null)
})

// 各班级学生人数分布数据（柱状图），兼容多种字段名
const classDist = computed(() => {
  const list = stats.value.classDistribution || stats.value.classList || stats.value.classes || []
  const arr = Array.isArray(list) ? list : []
  const max = arr.reduce((m, it) => Math.max(m, num(it.studentCount ?? it.count ?? it.value)), 0) || 1
  return arr.map(it => ({
    name: it.className ?? it.name ?? '-',
    count: num(it.studentCount ?? it.count ?? it.value),
    percent: Math.round((num(it.studentCount ?? it.count ?? it.value) / max) * 100)
  }))
})

// 已提交作业数
const submittedCount = computed(() => num(stats.value.submittedCount ?? stats.value.submitted))
// 未提交作业数
const unsubmittedCount = computed(() => num(stats.value.unsubmittedCount ?? stats.value.unsubmitted))
// 作业总数
const totalCount = computed(() => {
  const t = num(stats.value.totalCount)
  return t || (submittedCount.value + unsubmittedCount.value)
})
// 作业完成率（百分比）
const submitRate = computed(() => {
  const total = totalCount.value
  if (!total) return 0
  return Math.round((submittedCount.value / total) * 100)
})

// 饼图样式：使用 conic-gradient 绘制环形图
const pieStyle = computed(() => ({
  background: `conic-gradient(#3b82f6 0 ${submitRate.value}%, #d6e8df ${submitRate.value}% 100%)`
}))

// 非管理员统计卡片列表（教师/学生）
const statList = computed(() => {
  if (role.value === 'TEACHER') {
    return [
      { title: '已发布作业', value: '-', icon: EditPen, color: '#1d4ed8', bgColor: 'linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%)' },
      { title: '待批改', value: '-', icon: Document, color: '#1e3a8a', bgColor: 'linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 100%)' },
      { title: '已批改', value: '-', icon: HomeFilled, color: '#2563eb', bgColor: 'linear-gradient(135deg, #2563eb 0%, #3b82f6 100%)' }
    ]
  } else if (role.value === 'STUDENT') {
    return [
      { title: '待完成作业', value: '-', icon: Document, color: '#1d4ed8', bgColor: 'linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%)' },
      { title: '已完成作业', value: '-', icon: EditPen, color: '#1e3a8a', bgColor: 'linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 100%)' },
      { title: '全部作业', value: '-', icon: HomeFilled, color: '#2563eb', bgColor: 'linear-gradient(135deg, #2563eb 0%, #3b82f6 100%)' }
    ]
  }
  return []
})

// 加载统计数据
const loadStats = async () => {
  loading.value = true
  try {
    const res = await getStatistics()
    stats.value = res.data || {}
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 组件挂载后，仅管理员加载统计数据
onMounted(() => {
  if (role.value === 'ADMIN') loadStats()
})
</script>

<style scoped>
/* 仪表盘容器：无内边距 */
.dashboard {
  padding: 0;
}

/* 欢迎卡片：圆角、无边框、溢出隐藏 */
.welcome-card {
  margin-bottom: 20px;
  border-radius: 16px;
  border: none;
  overflow: hidden;
}

/* 欢迎卡片内部：蓝色渐变背景 */
.welcome-card :deep(.el-card__body) {
  background: linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 60%, #3b82f6 100%);
  padding: 32px;
  position: relative;
  overflow: hidden;
}

/* 欢迎内容：水平居中布局 */
.welcome-content {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
}

/* 欢迎头像：圆形半透明背景 */
.welcome-avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  margin-right: 24px;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

/* 欢迎标题 */
.welcome-text h2 {
  margin: 0 0 10px;
  color: #ffffff;
  font-size: 22px;
  font-weight: 600;
}

/* 欢迎描述文字 */
.welcome-text p {
  margin: 0;
  color: rgba(255, 255, 255, 0.92);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 欢迎卡片装饰圆（右上角） */
.welcome-deco {
  position: absolute;
  right: -40px;
  top: -40px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  z-index: 0;
}

/* 统计卡片行：下边距 */
.stat-row {
  margin-bottom: 20px;
}

/* 统计卡片：圆角、悬浮上移动画 */
.stat-card {
  border-radius: 12px;
  border: none;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  border-top: 3px solid transparent;
}

/* 统计卡片悬浮效果 */
.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(29, 78, 216, 0.2);
  border-top-color: #3b82f6;
}

/* 统计内容：图标 + 信息 水平布局 */
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 统计图标容器：圆角方块、阴影 */
.stat-icon-wrap {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(29, 78, 216, 0.25);
}

/* 统计信息区 */
.stat-info {
  flex: 1;
}

/* 统计标题 */
.stat-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

/* 统计数值 */
.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

/* 图表卡片 */
.chart-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.1);
  margin-bottom: 20px;
}

/* 图表卡片内边距 */
.chart-card :deep(.el-card__body) {
  padding: 20px;
}

/* 图表标题 */
.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d4ed8;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 图表标题前的装饰竖条 */
.chart-bar {
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 2px;
}

/* 柱状图容器：纵向排列 */
.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 200px;
}

/* 单个柱状条项 */
.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 柱状条左侧标签 */
.bar-label {
  width: 90px;
  font-size: 13px;
  color: #5a6e6a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-shrink: 0;
}

/* 柱状条轨道 */
.bar-track {
  flex: 1;
  height: 26px;
  background: #eaf3ec;
  border-radius: 13px;
  overflow: hidden;
  display: flex;
  align-items: center;
  position: relative;
}

/* 柱状条填充部分 */
.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #1d4ed8 0%, #3b82f6 100%);
  border-radius: 13px;
  transition: width 0.6s ease;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 10px;
  min-width: 4px;
}

/* 填充内部数值文字 */
.bar-fill-text {
  color: #fff;
  font-size: 12px;
  font-weight: 600;
}

/* 轨道右侧数值（当百分比太小时显示在外部） */
.bar-value {
  position: absolute;
  right: 10px;
  font-size: 12px;
  font-weight: 600;
  color: #1d4ed8;
}

/* 空数据提示 */
.empty-tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 60px 0;
}

/* 饼图整体区域：饼图 + 图例 */
.pie-wrap {
  display: flex;
  align-items: center;
  gap: 30px;
  flex-wrap: wrap;
  min-height: 220px;
}

/* 环形饼图：通过背景渐变实现 */
.pie-chart {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.6s ease;
  box-shadow: 0 6px 18px rgba(29, 78, 216, 0.15);
}

/* 饼图中间镂空圆 */
.pie-hole {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 完成率数值 */
.pie-rate {
  font-size: 30px;
  font-weight: 700;
  color: #1d4ed8;
}

/* 完成率标签 */
.pie-rate-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

/* 图例容器 */
.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 单个图例项 */
.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

/* 图例颜色点 */
.legend-item .dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  flex-shrink: 0;
}

/* 已提交图例点颜色 */
.legend-item .dot.submitted {
  background: #3b82f6;
}

/* 未提交图例点颜色 */
.legend-item .dot.unsubmitted {
  background: #d6e8df;
}

/* 图例文字 */
.legend-label {
  color: #5a6e6a;
}

/* 图例数值 */
.legend-value {
  font-weight: 600;
  color: #1d4ed8;
  margin-left: auto;
  padding-left: 18px;
}

/* 作业总数项：顶部虚线分隔 */
.legend-item.total {
  margin-top: 6px;
  padding-top: 10px;
  border-top: 1px dashed #d6e8df;
}
</style>
