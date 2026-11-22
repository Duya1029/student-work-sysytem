<template>
  <!-- 我的成绩页面根容器 -->
  <div class="my-grades">
    <!-- 平均分卡片 -->
    <el-card class="avg-card" shadow="never" v-loading="loading">
      <div class="avg-content">
        <!-- 左侧：图标与说明 -->
        <div class="avg-left">
          <div class="avg-icon">📊</div>
          <div class="avg-info">
            <div class="avg-label">平均分</div>
            <div class="avg-desc">已批改作业综合表现</div>
          </div>
        </div>
        <!-- 中间：平均分数值 -->
        <div class="avg-score">
          <span class="avg-num">{{ averageScore }}</span>
          <span class="avg-unit">分</span>
        </div>
        <!-- 右侧：已批改/总提交统计 -->
        <div class="avg-stat">
          <!-- 已批改数量 -->
          <div class="stat-item">
            <div class="stat-num">{{ gradedCount }}</div>
            <div class="stat-label">已批改</div>
          </div>
          <div class="stat-divider"></div>
          <!-- 总提交数量 -->
          <div class="stat-item">
            <div class="stat-num">{{ totalCount }}</div>
            <div class="stat-label">总提交</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 历史成绩表格卡片 -->
    <el-card class="grades-card" shadow="never">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-title">历史成绩</span>
          <span class="header-sub">查看作业批改记录</span>
        </div>
      </template>

      <!-- 课程筛选栏 -->
      <div class="filter-bar">
        <!-- 课程筛选下拉 -->
        <el-select
          v-model="courseId"
          placeholder="全部课程"
          clearable
          class="filter-select"
        >
          <!-- 课程选项循环 -->
          <el-option
            v-for="c in courseList"
            :key="c.courseId ?? c.id"
            :label="c.courseName"
            :value="c.courseId ?? c.id"
          />
        </el-select>
        <!-- 重置筛选按钮 -->
        <el-button class="btn-reset" @click="courseId = null">重置</el-button>
      </div>

      <!-- 成绩列表表格 -->
      <el-table
        :data="displayList"
        border
        v-loading="loading"
        :header-cell-style="{ background: '#1d4ed8', color: '#fff', fontWeight: 600 }"
        class="grades-table"
      >
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 作业标题列：prop绑定title字段 -->
        <el-table-column prop="title" label="作业标题" min-width="150" />
        <!-- 所属课程列：prop绑定courseName字段 -->
        <el-table-column prop="courseName" label="所属课程" width="140">
          <template #default="{ row }">{{ row.courseName || '-' }}</template>
        </el-table-column>
        <!-- 提交时间列：prop绑定submitTime字段，格式化显示 -->
        <el-table-column prop="submitTime" label="提交时间" width="170">
          <template #default="{ row }">{{ formatDate(row.submitTime) }}</template>
        </el-table-column>
        <!-- 分数列：根据分数区间着色 -->
        <el-table-column label="分数" width="100" align="center">
          <template #default="{ row }">
            <span :class="getScoreClass(row)" class="score-cell">
              {{ row.score !== null && row.score !== undefined ? row.score + ' 分' : '未批改' }}
            </span>
          </template>
        </el-table-column>
        <!-- 状态标记列：打回/优秀 -->
        <el-table-column label="状态标记" width="140" align="center">
          <template #default="{ row }">
            <!-- 打回标签 -->
            <el-tag v-if="isReturned(row)" size="small" effect="dark" round class="return-tag">打回</el-tag>
            <!-- 优秀标签 -->
            <el-tag v-if="isExcellent(row)" size="small" effect="dark" round class="excellent-tag">优秀</el-tag>
            <!-- 无标记占位 -->
            <span v-if="!isReturned(row) && !isExcellent(row)" class="text-muted">-</span>
          </template>
        </el-table-column>
        <!-- 评语列：prop绑定comment字段 -->
        <el-table-column prop="comment" label="评语" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ row.comment || '暂无评语' }}</template>
        </el-table-column>
        <!-- 批改时间列：prop绑定gradeTime字段，格式化显示 -->
        <el-table-column prop="gradeTime" label="批改时间" width="170">
          <template #default="{ row }">{{ formatDate(row.gradeTime) }}</template>
        </el-table-column>
      </el-table>
      <!-- 暂无成绩记录 -->
      <el-empty v-if="!loading && displayList.length === 0" description="暂无成绩记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyGrades, getStudentCourses } from '@/api'

// 列表加载状态
const loading = ref(false)
// 成绩列表数据
const tableData = ref([])

// 课程筛选
const courseList = ref([])
// 选中的课程ID
const courseId = ref(null)

// 格式化日期字符串
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 分数颜色：>=90绿色，>=60橙色，<60红色，未批改灰色
const getScoreClass = (row) => {
  if (row.score === null || row.score === undefined) return 'score-pending'
  if (row.score >= 90) return 'score-excellent'
  if (row.score >= 60) return 'score-pass'
  return 'score-fail'
}

// 是否打回，兼容 isReturned / is_returned
const isReturned = (row) => {
  if (!row) return false
  if (row.isReturned === true || row.is_returned === true) return true
  if (row.isReturned === 1 || row.is_returned === 1) return true
  if (row.returned === true || row.returned === 1) return true
  return false
}

// 是否优秀，兼容 isExcellent / is_excellent
const isExcellent = (row) => {
  if (!row) return false
  if (row.isExcellent === true || row.is_excellent === true) return true
  if (row.isExcellent === 1 || row.is_excellent === 1) return true
  if (row.excellent === true || row.excellent === 1) return true
  return false
}

// 选中课程的名称（用于按名称匹配成绩记录）
const selectedCourseName = computed(() => {
  if (!courseId.value) return null
  const c = courseList.value.find(item => (item.courseId ?? item.id) === courseId.value)
  return c ? c.courseName : null
})

// 当前展示列表：按课程筛选
const displayList = computed(() => {
  if (!courseId.value) return tableData.value
  const name = selectedCourseName.value
  return tableData.value.filter(row => {
    if (row.courseId !== undefined && row.courseId !== null) {
      return row.courseId === courseId.value
    }
    return name && (row.courseName === name)
  })
})

// 总提交数量
const totalCount = computed(() => tableData.value.length)

// 已批改数量
const gradedCount = computed(() =>
  tableData.value.filter(item => item.score !== null && item.score !== undefined).length
)

// 平均分（保留一位小数）
const averageScore = computed(() => {
  const graded = tableData.value.filter(item => item.score !== null && item.score !== undefined)
  if (graded.length === 0) return '0.0'
  const sum = graded.reduce((acc, item) => acc + Number(item.score || 0), 0)
  return (sum / graded.length).toFixed(1)
})

// 加载成绩列表
const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyGrades()
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载课程下拉
const loadCourses = async () => {
  try {
    const res = await getStudentCourses()
    const list = Array.isArray(res.data) ? res.data : (res.data?.list || [])
    courseList.value = list
  } catch (error) {
    console.error(error)
  }
}

// 组件挂载时加载课程和成绩数据
onMounted(() => { loadCourses(); loadData() })
</script>

<style scoped>
/* 我的成绩页面容器样式 */
.my-grades {
  padding: 20px;
  background: #f0f6ff;
  min-height: 100vh;
  box-sizing: border-box;
}

/* ============ 平均分卡片 ============ */
/* 平均分卡片样式 */
.avg-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  margin-bottom: 20px;
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  overflow: hidden;
}

/* 平均分卡片内容区内边距 */
.avg-card :deep(.el-card__body) {
  padding: 24px 28px;
}

/* 平均分内容布局 */
.avg-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

/* 平均分左侧区域 */
.avg-left {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
}

/* 平均分图标样式 */
.avg-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}

/* 平均分标签样式 */
.avg-label {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 平均分描述样式 */
.avg-desc {
  color: rgba(255, 255, 255, 0.85);
  font-size: 12px;
  margin-top: 4px;
}

/* 平均分数值布局 */
.avg-score {
  display: flex;
  align-items: baseline;
  gap: 4px;
  color: #ffffff;
}

/* 平均分数字样式 */
.avg-num {
  font-size: 44px;
  font-weight: 700;
  line-height: 1;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 平均分单位样式 */
.avg-unit {
  font-size: 16px;
  font-weight: 600;
  opacity: 0.9;
}

/* 统计区域布局 */
.avg-stat {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-left: 24px;
  border-left: 1px solid rgba(255, 255, 255, 0.3);
}

/* 统计项样式 */
.stat-item {
  text-align: center;
  color: #ffffff;
}

/* 统计数值样式 */
.stat-num {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.1;
}

/* 统计标签样式 */
.stat-label {
  font-size: 12px;
  opacity: 0.85;
  margin-top: 2px;
}

/* 统计分隔线 */
.stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.3);
}

/* ============ 成绩卡片 ============ */
/* 成绩卡片样式 */
.grades-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 成绩卡片头部渐变背景 */
.grades-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  padding: 16px 20px;
  border-bottom: none;
}

/* 卡片标题栏布局 */
.card-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

/* 标题文字样式 */
.header-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 副标题样式 */
.header-sub {
  color: rgba(255, 255, 255, 0.85);
  font-size: 13px;
}

/* 成绩卡片内容区内边距 */
.grades-card :deep(.el-card__body) {
  padding: 20px;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

/* 筛选下拉宽度 */
.filter-select {
  width: 220px;
}

/* 筛选下拉圆角 */
.filter-select :deep(.el-select__wrapper) {
  border-radius: 8px;
}

/* 筛选下拉悬浮/聚焦阴影 */
.filter-select :deep(.el-select__wrapper:hover),
.filter-select :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
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

/* 成绩表格样式 */
.grades-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 成绩表格表头边框颜色 */
.grades-table :deep(.el-table__header th) {
  border-color: #1d4ed8;
}

/* 成绩表格行悬浮背景 */
.grades-table :deep(.el-table__body tr:hover > td) {
  background: #f0f6ff !important;
}

/* 分数颜色 */
.score-cell {
  font-weight: 600;
  font-size: 14px;
  display: inline-block;
  padding: 2px 8px;
  border-radius: 6px;
}

/* 优秀分数颜色 */
.score-excellent {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
}

/* 及格分数颜色 */
.score-pass {
  color: #fa8c16;
  background: rgba(250, 140, 22, 0.1);
}

/* 不及格分数颜色 */
.score-fail {
  color: #f5222d;
  background: rgba(245, 34, 45, 0.1);
}

/* 未批改分数颜色 */
.score-pending {
  color: #8c9c93;
  background: rgba(140, 156, 147, 0.1);
}

/* 状态标记标签 */
/* 打回标签样式 */
.return-tag {
  background: #f5222d !important;
  border-color: #f5222d !important;
  color: #fff !important;
  font-weight: 600;
  margin: 2px;
}

/* 优秀标签样式 */
.excellent-tag {
  background: #faad14 !important;
  border-color: #faad14 !important;
  color: #fff !important;
  font-weight: 600;
  margin: 2px;
}

/* 无标记占位文字颜色 */
.text-muted {
  color: #c0c4cc;
}

/* 响应式：窄屏平均分卡片堆叠 */
@media (max-width: 768px) {
  .avg-content {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  .avg-stat {
    padding-left: 0;
    border-left: none;
    border-top: 1px solid rgba(255, 255, 255, 0.3);
    padding-top: 16px;
    justify-content: space-around;
  }
}
</style>
