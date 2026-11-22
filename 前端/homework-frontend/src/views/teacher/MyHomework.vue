<template>
  <!-- 我发布的作业页面容器（教师端） -->
  <div class="my-homework">
    <!-- 作业卡片容器 -->
    <el-card class="hw-card">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">我发布的作业</span>
        </div>
      </template>
      <!-- 作业列表表格 -->
      <el-table :data="tableData" border v-loading="loading" class="hw-table">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 作业标题列，prop 对应数据字段 title -->
        <el-table-column prop="title" label="作业标题" min-width="150" />
        <!-- 班级列，prop 对应数据字段 className -->
        <el-table-column prop="className" label="班级" width="180" />
        <!-- 截止时间列，prop 对应数据字段 deadline，自定义模板格式化 -->
        <el-table-column prop="deadline" label="截止时间" width="170">
          <template #default="{ row }">{{ formatDate(row.deadline) }}</template>
        </el-table-column>
        <!-- 发布时间列，prop 对应数据字段 createTime，自定义模板格式化 -->
        <el-table-column prop="createTime" label="发布时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <!-- 操作列：批改、完成统计 -->
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button size="small" class="btn-grade" link @click="goGrade(row)">批改</el-button>
            <el-button size="small" class="btn-stat" link @click="goStatistics(row)">完成统计</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="hw-pagination"
        @change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
// 从 vue 引入：ref、onMounted
import { ref, onMounted } from 'vue'
// 引入路由钩子，用于页面跳转
import { useRouter } from 'vue-router'
// 引入获取我发布作业列表的接口
import { getMyHomeworkList } from '@/api'

// 路由实例，用于跳转批改与统计页面
const router = useRouter()
// 表格加载状态
const loading = ref(false)
// 作业列表数据
const tableData = ref([])
// 总记录数
const total = ref(0)
// 当前页码
const pageNum = ref(1)
// 每页条数
const pageSize = ref(10)

// 格式化日期字符串，将 T 替换为空格并截取到秒
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 加载我发布的作业列表数据（带分页）
const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyHomeworkList({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) { console.error(error) } finally { loading.value = false }
}

// 跳转到作业批改页面
const goGrade = (row) => {
  router.push(`/teacher/grade/${row.homeworkId}`)
}

// 跳转到作业完成统计页面
const goStatistics = (row) => {
  router.push(`/teacher/statistics?homeworkId=${row.homeworkId}`)
}

// 组件挂载时加载作业列表
onMounted(() => { loadData() })
</script>

<style scoped>
/* 页面容器：浅蓝背景、内边距 */
.my-homework {
  background: #f0f6ff;
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

/* 作业卡片：圆角、阴影、溢出隐藏 */
.hw-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部：蓝色渐变背景 */
.hw-card :deep(.el-card__header) {
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

/* 头部标题文字：白色 */
.header-title {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
}

/* 表格表头：蓝色背景白字 */
.hw-table :deep(.el-table__header th) {
  background: #1d4ed8 !important;
  color: #ffffff !important;
  font-weight: 600;
}

/* 表格表头单元格文字颜色 */
.hw-table :deep(.el-table__header th .cell) {
  color: #ffffff;
}

/* 表格行悬浮背景色 */
.hw-table :deep(.el-table__body tr:hover > td) {
  background: #f0f6ff !important;
}

/* 表格单元格边框色 */
.hw-table :deep(.el-table__body td) {
  border-color: #e0ebe5;
}

/* 批改按钮文字颜色 */
.btn-grade {
  color: #1d4ed8;
  font-weight: 500;
}

/* 批改按钮悬浮色 */
.btn-grade:hover,
.btn-grade:focus {
  color: #3b82f6;
}

/* 完成统计按钮文字颜色 */
.btn-stat {
  color: #3b82f6;
  font-weight: 500;
}

/* 完成统计按钮悬浮色 */
.btn-stat:hover,
.btn-stat:focus {
  color: #1d4ed8;
}

/* 分页器：右对齐、上外边距 */
.hw-pagination {
  margin-top: 18px;
  justify-content: flex-end;
}

/* 分页器：当前页蓝色背景 */
.hw-pagination :deep(.el-pagination .el-pager li.is-active) {
  background-color: #1d4ed8;
  color: #ffffff;
}

/* 分页器：页码悬浮颜色 */
.hw-pagination :deep(.el-pagination .el-pager li:hover) {
  color: #1d4ed8;
}

/* 分页器：当前页悬浮保持白色文字 */
.hw-pagination :deep(.el-pagination .el-pager li.is-active:hover) {
  color: #ffffff;
}

/* 分页器：按钮悬浮颜色 */
.hw-pagination :deep(.el-pagination button:hover) {
  color: #1d4ed8;
}

/* 分页器：每页条数选择框聚焦颜色 */
.hw-pagination :deep(.el-pagination .el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
}
</style>
