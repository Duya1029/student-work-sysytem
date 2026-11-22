<template>
  <!-- 作业列表页面容器 -->
  <div class="homework-list">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">作业列表</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 班级名称搜索输入框 -->
        <el-input v-model="searchForm.className" placeholder="班级名称搜索" style="width: 180px" clearable @keyup.enter="loadData" />
        <!-- 教师姓名搜索输入框 -->
        <el-input v-model="searchForm.teacherName" placeholder="教师姓名搜索" style="width: 160px" clearable @keyup.enter="loadData" />
        <!-- 查询按钮 -->
        <el-button type="success" @click="loadData">查询</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 作业标题列，prop 对应数据字段 title -->
        <el-table-column prop="title" label="作业标题" min-width="150" />
        <!-- 发布教师列，prop 对应数据字段 teacherName -->
        <el-table-column prop="teacherName" label="发布教师" width="100" />
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
        <!-- 操作列：查看提交、删除 -->
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" type="success" link @click="viewSubmissions(row)">查看提交</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
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
        class="green-pagination"
        style="margin-top: 15px; justify-content: flex-end"
        @change="loadData"
      />
    </el-card>

    <!-- 提交详情弹窗 -->
    <el-dialog v-model="submissionVisible" title="作业提交详情" width="800px" class="green-dialog">
      <!-- 提交详情表格 -->
      <el-table :data="submissionList" border class="green-table" v-loading="subLoading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 学生姓名列，prop 对应数据字段 studentName -->
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <!-- 学号列，prop 对应数据字段 studentNo -->
        <el-table-column prop="studentNo" label="学号" width="100" />
        <!-- 班级列，prop 对应数据字段 className -->
        <el-table-column prop="className" label="班级" width="150" />
        <!-- 提交时间列，prop 对应数据字段 submitTime，自定义模板格式化 -->
        <el-table-column prop="submitTime" label="提交时间" width="160">
          <template #default="{ row }">{{ formatDate(row.submitTime) }}</template>
        </el-table-column>
        <!-- 分数列，prop 对应数据字段 score，自定义模板显示分数或未批改 -->
        <el-table-column prop="score" label="分数" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score !== null ? 'success' : 'info'">{{ row.score !== null ? row.score : '未批改' }}</el-tag>
          </template>
        </el-table-column>
        <!-- 评语列，prop 对应数据字段 comment，超出显示 tooltip -->
        <el-table-column prop="comment" label="评语" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
// 从 vue 引入：ref、reactive、onMounted
import { ref, reactive, onMounted } from 'vue'
// 引入 Element Plus 消息提示与确认框组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入作业相关接口：获取作业列表、获取提交详情、删除作业
import { getHomeworkList, getHomeworkSubmissions, deleteHomework } from '@/api'

// 表格加载状态
const loading = ref(false)
// 表格数据
const tableData = ref([])
// 总记录数
const total = ref(0)
// 当前页码
const pageNum = ref(1)
// 每页条数
const pageSize = ref(10)
// 提交详情弹窗显示状态
const submissionVisible = ref(false)
// 提交详情列表数据
const submissionList = ref([])
// 提交详情加载状态
const subLoading = ref(false)

// 搜索表单数据
const searchForm = reactive({ className: '', teacherName: '' })

// 格式化日期字符串，将 T 替换为空格并截取到秒
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 加载作业列表数据（带分页与筛选条件）
const loadData = async () => {
  loading.value = true
  try {
    const res = await getHomeworkList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      className: searchForm.className || undefined,
      teacherName: searchForm.teacherName || undefined
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) { console.error(error) } finally { loading.value = false }
}

// 查看作业提交详情
const viewSubmissions = async (row) => {
  submissionVisible.value = true
  subLoading.value = true
  try {
    const res = await getHomeworkSubmissions(row.homeworkId)
    submissionList.value = res.data
  } catch (error) { console.error(error) } finally { subLoading.value = false }
}

// 删除作业：确认后调用删除接口，若当前页只剩一条则回退一页
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除作业"${row.title}"吗？该操作将同时清除相关提交记录，不可恢复。`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await deleteHomework(row.homeworkId)
    ElMessage.success('删除成功')
    // 删除后若当前页空了，回退一页
    if (tableData.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1
    }
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

// 组件挂载时加载作业列表
onMounted(() => { loadData() })
</script>

<style scoped>
/* 页面容器 */
.homework-list {
  padding: 10px;
}

/* 卡片样式 - 圆角+阴影 */
.page-card {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  border: none;
  background: #f0f6ff;
}

/* 页面标题 - 绿色左边框装饰 */
.page-title {
  border-left: 5px solid #1d4ed8;
  padding-left: 12px;
  margin-bottom: 18px;
}

/* 标题文字样式 */
.page-title .title-text {
  font-size: 18px;
  font-weight: 600;
  color: #1d4ed8;
}

/* 搜索栏：水平布局、可换行 */
.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

/* 表格表头：蓝色背景白字 */
.green-table :deep(.el-table__header th) {
  background-color: #1d4ed8 !important;
  color: #fff !important;
  font-weight: 600;
}

/* 表格表头单元格文字颜色 */
.green-table :deep(.el-table__header th .cell) {
  color: #fff !important;
}

/* 分页器：当前页蓝色背景 */
.green-pagination :deep(.el-pagination .el-pager li.is-active) {
  background-color: #1d4ed8 !important;
  color: #fff !important;
}

/* 分页器：页码悬浮颜色 */
.green-pagination :deep(.el-pagination .el-pager li:hover) {
  color: #1d4ed8 !important;
}

/* 分页器：按钮悬浮颜色 */
.green-pagination :deep(.el-pagination button:hover) {
  color: #1d4ed8 !important;
}

/* 分页器：每页条数选择框聚焦颜色 */
.green-pagination :deep(.el-pagination .el-select .el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px #1d4ed8 inset !important;
}

/* 弹窗头部：蓝色背景 */
.green-dialog :deep(.el-dialog__header) {
  background: #1d4ed8;
  border-radius: 8px 8px 0 0;
  padding: 16px 20px;
  margin-right: 0;
}

/* 弹窗标题文字：白色 */
.green-dialog :deep(.el-dialog__title) {
  color: #fff !important;
  font-weight: 600;
}

/* 弹窗关闭按钮：白色 */
.green-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff !important;
}

/* 弹窗关闭按钮悬浮颜色 */
.green-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #f0f6ff !important;
}

/* 弹窗整体圆角与溢出隐藏 */
.green-dialog :deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

/* 弹窗底部主按钮：蓝色主题 */
.green-dialog :deep(.el-dialog__footer .el-button--primary) {
  background-color: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
}
</style>
