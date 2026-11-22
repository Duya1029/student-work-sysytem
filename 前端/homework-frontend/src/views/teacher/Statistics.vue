<template>
  <!-- 统计页面根容器 -->
  <div class="statistics-page">
    <!-- 作业完成统计卡片 -->
    <el-card class="stats-card" v-loading="loading">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">作业完成统计</span>
          <!-- 返回按钮 -->
          <el-button class="btn-back" size="small" @click="$router.back()">返回</el-button>
        </div>
      </template>

      <!-- 三个统计卡片：总人数、已提交、未提交 -->
      <div class="stat-cards">
        <!-- 总人数卡片 -->
        <div class="stat-item stat-total">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalStudents }}</div>
            <div class="stat-label">总人数</div>
          </div>
        </div>
        <!-- 已提交人数卡片 -->
        <div class="stat-item stat-submitted">
          <div class="stat-icon">✓</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.submittedCount }}</div>
            <div class="stat-label">已提交</div>
          </div>
        </div>
        <!-- 未提交人数卡片 -->
        <div class="stat-item stat-unsubmitted">
          <div class="stat-icon">✗</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.unsubmittedCount }}</div>
            <div class="stat-label">未提交</div>
          </div>
        </div>
      </div>

      <!-- 提交率进度条 -->
      <div class="rate-section">
        <div class="rate-header">
          <span class="rate-title">提交率</span>
          <span class="rate-value">{{ submitRate }}%</span>
        </div>
        <el-progress
          :percentage="submitRate"
          :stroke-width="18"
          :color="rateColor"
          class="rate-progress"
        />
      </div>

      <!-- 未提交学生列表分隔线 -->
      <el-divider content-position="left" class="hw-divider">
        <span class="divider-text">未提交学生列表</span>
      </el-divider>

      <!-- 未提交学生列表表格 -->
      <el-table :data="unsubmittedStudents" border class="hw-table" empty-text="暂无未提交学生">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 姓名列：prop绑定studentName字段 -->
        <el-table-column prop="studentName" label="姓名" min-width="120" />
        <!-- 学号列：prop绑定studentNo字段 -->
        <el-table-column prop="studentNo" label="学号" min-width="120" />
        <!-- 班级列：prop绑定className字段 -->
        <el-table-column prop="className" label="班级" min-width="150" />
      </el-table>
    </el-card>

    <!-- 成绩统计卡片 -->
    <el-card class="stats-card grade-card" v-loading="gradeLoading">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">成绩分布统计</span>
        </div>
      </template>

      <!-- 四个成绩统计卡片：平均分、已批改人数、最高分、最低分 -->
      <div class="stat-cards grade-stat-cards">
        <!-- 平均分卡片 -->
        <div class="stat-item stat-avg">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ averageScore }}</div>
            <div class="stat-label">平均分</div>
          </div>
        </div>
        <!-- 已批改人数卡片 -->
        <div class="stat-item stat-graded">
          <div class="stat-icon">✎</div>
          <div class="stat-info">
            <div class="stat-value">{{ gradeStats.gradedCount }}</div>
            <div class="stat-label">已批改人数</div>
          </div>
        </div>
        <!-- 最高分卡片 -->
        <div class="stat-item stat-max">
          <div class="stat-icon">★</div>
          <div class="stat-info">
            <div class="stat-value">{{ gradeStats.maxScore }}</div>
            <div class="stat-label">最高分</div>
          </div>
        </div>
        <!-- 最低分卡片 -->
        <div class="stat-item stat-min">
          <div class="stat-icon">▼</div>
          <div class="stat-info">
            <div class="stat-value">{{ gradeStats.minScore }}</div>
            <div class="stat-label">最低分</div>
          </div>
        </div>
      </div>

      <!-- 分数分布柱状图 -->
      <div class="dist-section">
        <div class="dist-title">分数段分布</div>
        <div class="bar-chart">
          <!-- 每个分数段的柱状图 -->
          <div
            v-for="item in distribution"
            :key="item.key"
            class="bar-col"
          >
            <!-- 人数 -->
            <div class="bar-count">{{ item.count }}</div>
            <div class="bar-wrap">
              <!-- 柱状填充 -->
              <div
                class="bar-fill"
                :class="'bar-' + item.key"
                :style="{ height: barHeight(item.count) + '%' }"
              ></div>
            </div>
            <!-- 分数段标签 -->
            <div class="bar-label">{{ item.label }}</div>
            <!-- 分数段范围 -->
            <div class="bar-range">{{ item.range }}</div>
          </div>
        </div>
      </div>

      <!-- 无成绩数据提示 -->
      <div v-if="!gradeLoading && gradeStats.gradedCount === 0" class="empty-tip">
        暂无成绩数据
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHomeworkStatistics, getGradeStatistics } from '@/api'

// 路由对象
const route = useRoute()
// 完成统计加载状态
const loading = ref(false)
// 成绩统计加载状态
const gradeLoading = ref(false)
// 完成统计数据
const stats = ref({
  // 总人数
  totalStudents: 0,
  // 已提交人数
  submittedCount: 0,
  // 未提交人数
  unsubmittedCount: 0
})
// 未提交学生列表
const unsubmittedStudents = ref([])

// 成绩统计
const gradeStats = ref({
  // 已批改人数
  gradedCount: 0,
  // 平均分
  averageScore: 0,
  // 最高分
  maxScore: 0,
  // 最低分
  minScore: 0,
  excellent: 0,   // 优秀 >=90
  good: 0,        // 良好 80-89
  medium: 0,      // 中等 70-79
  pass: 0,        // 及格 60-69
  fail: 0         // 不及格 <60
})

// 分数段分布数据
const distribution = computed(() => [
  { key: 'excellent', label: '优秀', range: '90-100', count: gradeStats.value.excellent },
  { key: 'good', label: '良好', range: '80-89', count: gradeStats.value.good },
  { key: 'medium', label: '中等', range: '70-79', count: gradeStats.value.medium },
  { key: 'pass', label: '及格', range: '60-69', count: gradeStats.value.pass },
  { key: 'fail', label: '不及格', range: '<60', count: gradeStats.value.fail }
])

// 平均分显示格式化
const averageScore = computed(() => {
  const avg = Number(gradeStats.value.averageScore) || 0
  return Number.isInteger(avg) ? avg : avg.toFixed(1)
})

// 提交率百分比
const submitRate = computed(() => {
  const total = stats.value.totalStudents
  if (!total || total === 0) return 0
  const rate = Math.round((stats.value.submittedCount / total) * 100)
  return rate
})

// 提交率进度条颜色
const rateColor = computed(() => {
  const rate = submitRate.value
  if (rate >= 80) return '#3b82f6'
  if (rate >= 50) return '#faad14'
  return '#ff4d4f'
})

// 柱状图高度（基于最大值）
const barHeight = (count) => {
  const max = Math.max(
    gradeStats.value.excellent,
    gradeStats.value.good,
    gradeStats.value.medium,
    gradeStats.value.pass,
    gradeStats.value.fail,
    1
  )
  if (!count) return 0
  return Math.round((count / max) * 100)
}

// 从成绩统计接口数据中按分数段归类
const parseDistribution = (data) => {
  // 优先使用后端返回的分段数据
  const pick = (val) => Number(val) || 0
  const excellent = pick(data.excellent ?? data.excellentCount)
  const good = pick(data.good ?? data.goodCount)
  const medium = pick(data.medium ?? data.mediumCount ?? data.middle ?? data.middleCount)
  const pass = pick(data.pass ?? data.passCount)
  const fail = pick(data.fail ?? data.failCount)

  // 若后端已提供分段数据，直接使用
  if (excellent || good || medium || pass || fail || data.distribution) {
    return { excellent, good, medium, pass, fail }
  }

  // 否则根据 scores 列表自行计算
  const scores = Array.isArray(data.scores) ? data.scores
    : Array.isArray(data.scoreList) ? data.scoreList
    : []
  const dist = { excellent: 0, good: 0, medium: 0, pass: 0, fail: 0 }
  scores.forEach((s) => {
    const score = typeof s === 'object' ? Number(s.score) : Number(s)
    if (Number.isNaN(score)) return
    if (score >= 90) dist.excellent++
    else if (score >= 80) dist.good++
    else if (score >= 70) dist.medium++
    else if (score >= 60) dist.pass++
    else dist.fail++
  })
  return dist
}

// 加载统计数据
const loadData = async () => {
  const homeworkId = route.query.homeworkId
  if (!homeworkId) {
    ElMessage.warning('缺少作业参数')
    return
  }
  loading.value = true
  try {
    const res = await getHomeworkStatistics(homeworkId)
    stats.value = {
      totalStudents: res.data.totalStudents || 0,
      submittedCount: res.data.submittedCount || 0,
      unsubmittedCount: res.data.unsubmittedCount || 0
    }
    unsubmittedStudents.value = res.data.unsubmittedStudents || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }

  // 加载成绩统计
  gradeLoading.value = true
  try {
    const res = await getGradeStatistics(homeworkId)
    const data = res.data || {}
    const dist = parseDistribution(data)
    gradeStats.value = {
      gradedCount: data.gradedCount ?? data.graded ?? 0,
      averageScore: data.averageScore ?? data.avg ?? data.average ?? 0,
      maxScore: data.maxScore ?? data.max ?? 0,
      minScore: data.minScore ?? data.min ?? 0,
      excellent: dist.excellent,
      good: dist.good,
      medium: dist.medium,
      pass: dist.pass,
      fail: dist.fail
    }
  } catch (error) {
    console.error(error)
  } finally {
    gradeLoading.value = false
  }
}

// 组件挂载时加载统计数据
onMounted(() => { loadData() })
</script>

<style scoped>
/* 统计页面容器样式 */
.statistics-page {
  background: #f0f6ff;
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

/* 统计卡片样式 */
.stats-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
  margin-bottom: 20px;
}

/* 卡片头部渐变背景 */
.stats-card :deep(.el-card__header) {
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
  flex: 1;
}

/* 返回按钮样式 */
.btn-back {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  color: #ffffff;
  border-radius: 6px;
}

/* 返回按钮悬浮/聚焦样式 */
.btn-back:hover,
.btn-back:focus {
  background: rgba(255, 255, 255, 0.35);
  border-color: #ffffff;
  color: #ffffff;
}

/* 卡片内容区内边距 */
.stats-card :deep(.el-card__body) {
  padding: 24px 20px;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin-bottom: 24px;
}

/* 成绩统计卡片（四列） */
.grade-stat-cards {
  grid-template-columns: repeat(4, 1fr);
  margin-bottom: 24px;
}

/* 单个统计项样式 */
.stat-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(29, 78, 216, 0.1);
}

/* 统计图标样式 */
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  margin-right: 14px;
  background: rgba(255, 255, 255, 0.6);
}

/* 总人数卡片渐变 */
.stat-total {
  background: linear-gradient(135deg, #1d4ed8 0%, #3da675 100%);
}

/* 已提交卡片渐变 */
.stat-submitted {
  background: linear-gradient(135deg, #3b82f6 0%, #73d13d 100%);
}

/* 未提交卡片渐变 */
.stat-unsubmitted {
  background: linear-gradient(135deg, #ff7875 0%, #ffa39e 100%);
}

/* 平均分卡片渐变 */
.stat-avg {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
}

/* 已批改人数卡片渐变 */
.stat-graded {
  background: linear-gradient(135deg, #13c2c2 0%, #36cfc9 100%);
}

/* 最高分卡片渐变 */
.stat-max {
  background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%);
}

/* 最低分卡片渐变 */
.stat-min {
  background: linear-gradient(135deg, #722ed1 0%, #9254de 100%);
}

/* 统计信息文字颜色 */
.stat-info {
  color: #ffffff;
}

/* 统计数值样式 */
.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

/* 统计标签样式 */
.stat-label {
  font-size: 13px;
  opacity: 0.9;
  margin-top: 2px;
}

/* 进度条区域样式 */
.rate-section {
  background: #f8fcfa;
  border: 1px solid #d6e9dd;
  border-radius: 10px;
  padding: 18px 20px;
  margin-bottom: 24px;
}

/* 进度条头部布局 */
.rate-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

/* 提交率标题样式 */
.rate-title {
  color: #1d4ed8;
  font-size: 15px;
  font-weight: 600;
}

/* 提交率数值样式 */
.rate-value {
  color: #1d4ed8;
  font-size: 18px;
  font-weight: 700;
}

/* 进度条外框圆角 */
.rate-progress :deep(.el-progress-bar__outer) {
  border-radius: 10px;
}

/* 进度条内条圆角与过渡 */
.rate-progress :deep(.el-progress-bar__inner) {
  border-radius: 10px;
  transition: width 0.4s ease;
}

/* 分隔线文字样式 */
.hw-divider :deep(.el-divider__text) {
  background: #ffffff;
  color: #1d4ed8;
  font-weight: 600;
  padding: 0 12px;
}

/* 分隔线线条颜色 */
.hw-divider :deep(.el-divider__text::before),
.hw-divider :deep(.el-divider__text::after) {
  border-top-color: #1d4ed8;
}

/* 分隔线文字颜色 */
.divider-text {
  color: #1d4ed8;
}

/* 表格样式 */
.hw-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 表格表头背景色 */
.hw-table :deep(.el-table__header th) {
  background: #1d4ed8 !important;
  color: #ffffff !important;
  font-weight: 600;
}

/* 表格表头文字颜色 */
.hw-table :deep(.el-table__header th .cell) {
  color: #ffffff;
}

/* 表格行悬浮背景 */
.hw-table :deep(.el-table__body tr:hover > td) {
  background: #f0f6ff !important;
}

/* 表格单元格边框 */
.hw-table :deep(.el-table__body td) {
  border-color: #e0ebe5;
}

/* 成绩分布柱状图区域 */
.dist-section {
  background: #f8fcfa;
  border: 1px solid #d6e9dd;
  border-radius: 10px;
  padding: 20px;
}

/* 分布标题样式 */
.dist-title {
  color: #1d4ed8;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 18px;
}

/* 柱状图容器 */
.bar-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  gap: 12px;
  height: 240px;
  padding-top: 10px;
}

/* 单个柱状图列 */
.bar-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

/* 柱状图人数文字 */
.bar-count {
  color: #1d4ed8;
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 6px;
}

/* 柱状图外框 */
.bar-wrap {
  width: 100%;
  max-width: 70px;
  height: 160px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: #f0f6ff;
  border-radius: 6px 6px 0 0;
  overflow: hidden;
}

/* 柱状图填充条 */
.bar-fill {
  width: 100%;
  border-radius: 6px 6px 0 0;
  transition: height 0.5s ease;
  min-height: 2px;
}

/* 优秀段柱状颜色 */
.bar-excellent {
  background: linear-gradient(180deg, #3b82f6 0%, #389e0d 100%);
}

/* 良好段柱状颜色 */
.bar-good {
  background: linear-gradient(180deg, #73d13d 0%, #3b82f6 100%);
}

/* 中等段柱状颜色 */
.bar-medium {
  background: linear-gradient(180deg, #bae637 0%, #a0d911 100%);
}

/* 及格段柱状颜色 */
.bar-pass {
  background: linear-gradient(180deg, #faad14 0%, #d48806 100%);
}

/* 不及格段柱状颜色 */
.bar-fail {
  background: linear-gradient(180deg, #ff7875 0%, #ff4d4f 100%);
}

/* 柱状标签 */
.bar-label {
  margin-top: 10px;
  color: #1d4ed8;
  font-size: 14px;
  font-weight: 600;
}

/* 柱状分数范围 */
.bar-range {
  color: #8c9c93;
  font-size: 12px;
  margin-top: 2px;
}

/* 空数据提示 */
.empty-tip {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 30px 0;
}
</style>
