<template>
  <!-- 学生课程页面根容器 -->
  <div class="student-courses">
    <!-- 课程卡片 -->
    <el-card class="course-card" shadow="never">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-title">我的课程</span>
          <span class="header-sub">查看本学期所选课程信息</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="filter-bar">
        <!-- 课程名称/代码搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="课程名称 / 代码搜索"
          clearable
          class="search-input"
        />
        <!-- 重置搜索按钮 -->
        <el-button class="btn-reset" @click="searchKeyword = ''">重置</el-button>
      </div>

      <!-- 课程列表表格 -->
      <el-table
        :data="filteredList"
        border
        v-loading="loading"
        :header-cell-style="{ background: '#1d4ed8', color: '#fff', fontWeight: 600 }"
        class="course-table"
      >
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 课程名称列：prop绑定courseName字段 -->
        <el-table-column prop="courseName" label="课程名称" min-width="160" show-overflow-tooltip />
        <!-- 课程代码列：prop绑定courseCode字段 -->
        <el-table-column prop="courseCode" label="课程代码" width="130" align="center">
          <template #default="{ row }">{{ row.courseCode || '-' }}</template>
        </el-table-column>
        <!-- 学分列：prop绑定credit字段 -->
        <el-table-column prop="credit" label="学分" width="90" align="center">
          <template #default="{ row }">{{ row.credit ?? '-' }}</template>
        </el-table-column>
        <!-- 学时列：prop绑定hours字段 -->
        <el-table-column prop="hours" label="学时" width="90" align="center">
          <template #default="{ row }">{{ row.hours ?? '-' }}</template>
        </el-table-column>
        <!-- 任课教师列 -->
        <el-table-column label="任课教师" width="120" align="center">
          <template #default="{ row }">{{ row.teacherName || row.teacher || '-' }}</template>
        </el-table-column>
        <!-- 学期列 -->
        <el-table-column label="学期" width="160" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="light" round class="semester-tag">
              {{ row.semesterName || row.semester || '-' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 暂无课程数据 -->
      <el-empty v-if="!loading && filteredList.length === 0" description="暂无课程数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStudentCourses } from '@/api'

// 列表加载状态
const loading = ref(false)
// 课程列表数据
const tableData = ref([])
// 搜索关键词
const searchKeyword = ref('')

// 按搜索关键词过滤的课程列表
const filteredList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return tableData.value
  return tableData.value.filter(item => {
    const name = (item.courseName || '').toLowerCase()
    const code = (item.courseCode || '').toLowerCase()
    return name.includes(kw) || code.includes(kw)
  })
})

// 加载课程列表
const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudentCourses()
    const list = Array.isArray(res.data) ? res.data : (res.data?.list || [])
    tableData.value = list
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载课程数据
onMounted(() => { loadData() })
</script>

<style scoped>
/* 学生课程页面容器样式 */
.student-courses {
  padding: 20px;
  background: #f0f6ff;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 课程卡片样式 */
.course-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部渐变背景 */
.course-card :deep(.el-card__header) {
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

/* 卡片内容区内边距 */
.course-card :deep(.el-card__body) {
  padding: 20px;
}

/* 搜索栏 */
.filter-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 4px;
}

/* 搜索输入框宽度 */
.search-input {
  width: 260px;
}

/* 搜索输入框圆角 */
.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 搜索输入框悬浮/聚焦阴影 */
.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
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

/* 表格样式 */
.course-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 表格表头边框颜色 */
.course-table :deep(.el-table__header th) {
  border-color: #1d4ed8;
}

/* 表格行悬浮背景 */
.course-table :deep(.el-table__body tr:hover > td) {
  background: #f0f6ff !important;
}

/* 表格单元格边框 */
.course-table :deep(.el-table__body td) {
  border-color: #e0ebe5;
}

/* 学期标签样式 */
.semester-tag {
  background: #f6ffed !important;
  border-color: #b7eb8f !important;
  color: #1d4ed8 !important;
  font-weight: 600;
}
</style>
