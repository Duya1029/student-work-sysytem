<template>
  <!-- 学生作业页面根容器 -->
  <div class="student-homework">
    <!-- 作业卡片 -->
    <el-card class="hw-card" shadow="never">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-title">我的作业列表</span>
          <span class="header-sub">查看教师发布的作业及详情</span>
        </div>
      </template>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <!-- 课程筛选下拉 -->
        <el-select
          v-model="courseId"
          placeholder="全部课程"
          clearable
          class="filter-select"
          @change="onCourseChange"
        >
          <!-- 课程选项循环 -->
          <el-option
            v-for="c in courseList"
            :key="c.courseId ?? c.id"
            :label="c.courseName"
            :value="c.courseId ?? c.id"
          />
        </el-select>
        <!-- 提交状态筛选下拉 -->
        <el-select
          v-model="statusFilter"
          placeholder="全部状态"
          clearable
          class="filter-select"
        >
          <el-option label="未提交" value="unsubmitted" />
          <el-option label="已提交" value="submitted" />
          <el-option label="已批改" value="graded" />
        </el-select>
        <!-- 重置筛选按钮 -->
        <el-button class="btn-reset" @click="resetFilter">重置</el-button>
      </div>

      <!-- 作业列表表格 -->
      <el-table
        :data="displayList"
        border
        v-loading="loading"
        :header-cell-style="{ background: '#1d4ed8', color: '#fff', fontWeight: 600 }"
        class="hw-table"
      >
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 作业标题列：prop绑定title字段 -->
        <el-table-column prop="title" label="作业标题" min-width="150" />
        <!-- 所属课程列：prop绑定courseName字段 -->
        <el-table-column prop="courseName" label="所属课程" width="140">
          <template #default="{ row }">{{ row.courseName || '-' }}</template>
        </el-table-column>
        <!-- 发布教师列：prop绑定teacherName字段 -->
        <el-table-column prop="teacherName" label="发布教师" width="100" />
        <!-- 截止时间列：prop绑定deadline字段，格式化显示 -->
        <el-table-column prop="deadline" label="截止时间" width="170">
          <template #default="{ row }">{{ formatDate(row.deadline) }}</template>
        </el-table-column>
        <!-- 发布时间列：prop绑定createTime字段，格式化显示 -->
        <el-table-column prop="createTime" label="发布时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <!-- 提交状态列 -->
        <el-table-column label="提交状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag
              :type="statusTagType(row)"
              effect="light"
              round
            >
              {{ statusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列：查看详情、提交作业、查看成绩 -->
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link class="detail-btn" @click="goDetail(row)">查看详情</el-button>
            <el-button size="small" type="success" link class="submit-btn" @click="openSubmitDialog(row)">提交作业</el-button>
            <el-button
              size="small"
              type="warning"
              link
              class="grade-btn"
              :disabled="!isSubmitted(row)"
              @click="openGradeDialog(row)"
            >查看成绩</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页器 -->
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

    <!-- 提交作业对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交作业"
      width="560px"
      class="submit-dialog"
      :close-on-click-modal="false"
    >
      <!-- 提交作业表单 -->
      <el-form :model="submitForm" label-width="90px" class="submit-form">
        <!-- 作业标题（只读显示） -->
        <el-form-item label="作业标题">
          <span class="dialog-hw-title">{{ currentHomework.title || '-' }}</span>
        </el-form-item>
        <!-- 截止时间（只读显示） -->
        <el-form-item label="截止时间">
          <span class="dialog-hw-deadline">{{ formatDate(currentHomework.deadline) }}</span>
        </el-form-item>
        <!-- 提交内容输入 -->
        <el-form-item label="提交内容" required>
          <el-input
            v-model="submitForm.content"
            type="textarea"
            :rows="6"
            :placeholder="hasSubmitted ? '重新提交作业内容...' : '请输入作业内容...'"
          />
        </el-form-item>
        <!-- 附件上传 -->
        <el-form-item label="附件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :on-exceed="handleExceed"
            class="attachment-upload"
          >
            <el-button class="upload-trigger">
              <el-icon><Upload /></el-icon>
              <span>选择附件</span>
            </el-button>
            <template #tip>
              <div class="upload-tip">支持单个附件，可上传文档、图片等文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button class="btn-cancel" @click="submitDialogVisible = false">取消</el-button>
        <el-button class="btn-confirm" :loading="submitting" @click="handleSubmit">确认提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看成绩对话框 -->
    <el-dialog
      v-model="gradeDialogVisible"
      title="查看成绩"
      width="500px"
      class="grade-dialog"
      :close-on-click-modal="false"
    >
      <!-- 成绩内容区 -->
      <div v-loading="gradeLoading" class="grade-content">
        <template v-if="!gradeLoading && currentSubmission">
          <!-- 分数显示块（已批改时） -->
          <div v-if="currentSubmission.score !== null && currentSubmission.score !== undefined" class="score-block">
            <div class="score-label">本次作业分数</div>
            <div class="score-value">
              <span class="score-num">{{ currentSubmission.score }}</span>
              <span class="score-unit">分</span>
            </div>
          </div>
          <!-- 未批改提示（未批改时） -->
          <div v-else class="not-graded">
            <el-icon class="not-graded-icon"><Clock /></el-icon>
            <div class="not-graded-text">教师暂未批改，请耐心等待</div>
          </div>
          <el-divider class="grade-divider" />
          <!-- 教师评语块 -->
          <div class="comment-block">
            <div class="comment-label">教师评语</div>
            <div class="comment-text">{{ currentSubmission.comment || '暂无评语' }}</div>
          </div>
          <!-- 批改时间 -->
          <div class="grade-time-row" v-if="currentSubmission.gradeTime">
            批改时间：{{ formatDate(currentSubmission.gradeTime) }}
          </div>
        </template>
        <!-- 暂无提交记录 -->
        <el-empty v-else-if="!gradeLoading" description="暂无提交记录" />
      </div>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button class="btn-confirm" @click="gradeDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Upload, Clock } from '@element-plus/icons-vue'
import { getStudentHomeworkList, submitHomework, getHomeworkDetail, getStudentCourses } from '@/api'

// 路由对象
const router = useRouter()
// 列表加载状态
const loading = ref(false)
// 作业列表数据
const tableData = ref([])
// 总记录数
const total = ref(0)
// 当前页码
const pageNum = ref(1)
// 每页条数
const pageSize = ref(10)

// 筛选条件
const courseList = ref([])
// 选中的课程ID
const courseId = ref(null)
// 提交状态筛选
const statusFilter = ref('')

// 格式化日期字符串
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 判断作业是否已提交
const isSubmitted = (row) => {
  if (!row) return false
  // 兼容多种后端返回字段
  if (row.submitted === true) return true
  if (row.submitTime) return true
  if (row.submission) return true
  if (row.submitContent) return true
  return false
}

// 判断作业是否已批改
const isGraded = (row) => {
  if (!row) return false
  if (row.graded === true || row.isGraded === true) return true
  if (row.score !== null && row.score !== undefined && row.score !== '') return true
  if (row.gradeTime) return true
  return false
}

// 状态文本：未提交 / 已提交 / 已批改
const statusText = (row) => {
  if (isGraded(row)) return '已批改'
  if (isSubmitted(row)) return '已提交'
  return '未提交'
}

// 状态标签颜色
const statusTagType = (row) => {
  if (isGraded(row)) return 'success'
  if (isSubmitted(row)) return 'warning'
  return 'info'
}

// 状态键值，用于筛选
const statusKey = (row) => {
  if (isGraded(row)) return 'graded'
  if (isSubmitted(row)) return 'submitted'
  return 'unsubmitted'
}

// 当前页数据 + 状态筛选（客户端）
const displayList = computed(() => {
  if (!statusFilter.value) return tableData.value
  return tableData.value.filter(row => statusKey(row) === statusFilter.value)
})

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

// 课程筛选变化处理
const onCourseChange = () => {
  pageNum.value = 1
  loadData()
}

// 重置筛选条件
const resetFilter = () => {
  courseId.value = null
  statusFilter.value = ''
  pageNum.value = 1
  loadData()
}

// 加载作业列表
const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (courseId.value) params.courseId = courseId.value
    const res = await getStudentHomeworkList(params)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) { console.error(error) } finally { loading.value = false }
}

// 跳转作业详情页
const goDetail = (row) => {
  router.push(`/student/homework/detail/${row.homeworkId}`)
}

// ============ 提交作业 ============
// 提交弹窗显示状态
const submitDialogVisible = ref(false)
// 提交加载状态
const submitting = ref(false)
// 上传组件引用
const uploadRef = ref()
// 当前操作的作业
const currentHomework = ref({})
// 提交表单数据
const submitForm = reactive({
  // 作业ID
  homeworkId: null,
  // 提交内容
  content: '',
  // 附件文件名
  attachment: ''
})
// 选中的附件文件
const selectedFile = ref(null)

// 当前作业是否已提交
const hasSubmitted = computed(() => isSubmitted(currentHomework.value))

// 打开提交作业弹窗
const openSubmitDialog = (row) => {
  currentHomework.value = row
  submitForm.homeworkId = row.homeworkId
  submitForm.content = ''
  submitForm.attachment = ''
  selectedFile.value = null
  submitDialogVisible.value = true
}

// 附件文件变更处理
const handleFileChange = (file) => {
  selectedFile.value = file
  submitForm.attachment = file.name
}

// 附件文件移除处理
const handleFileRemove = () => {
  selectedFile.value = null
  submitForm.attachment = ''
}

// 附件超出数量限制处理
const handleExceed = () => {
  ElMessage.warning('最多只能上传一个附件，请先移除已有附件')
}

// 提交作业
const handleSubmit = async () => {
  if (!submitForm.content.trim()) {
    ElMessage.warning('作业内容不能为空')
    return
  }
  submitting.value = true
  try {
    await submitHomework({
      homeworkId: submitForm.homeworkId,
      content: submitForm.content,
      attachment: submitForm.attachment
    })
    ElMessage.success('作业提交成功')
    submitDialogVisible.value = false
    loadData() // 刷新列表
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

// ============ 查看成绩 ============
// 成绩弹窗显示状态
const gradeDialogVisible = ref(false)
// 成绩加载状态
const gradeLoading = ref(false)
// 当前提交记录
const currentSubmission = ref(null)

// 打开查看成绩弹窗
const openGradeDialog = async (row) => {
  currentHomework.value = row
  currentSubmission.value = null
  gradeDialogVisible.value = true
  gradeLoading.value = true
  try {
    const res = await getHomeworkDetail(row.homeworkId)
    currentSubmission.value = res.data || null
  } catch (error) {
    console.error(error)
  } finally {
    gradeLoading.value = false
  }
}

import { onMounted } from 'vue'
// 组件挂载时加载课程和作业列表
onMounted(() => { loadCourses(); loadData() })
</script>

<style scoped>
/* 学生作业页面容器样式 */
.student-homework {
  padding: 20px;
  background: #f0f6ff;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 作业卡片样式 */
.hw-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部渐变背景 */
.hw-card :deep(.el-card__header) {
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
.hw-card :deep(.el-card__body) {
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
  width: 200px;
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

/* 表格样式 */
.hw-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 表格表头边框颜色 */
.hw-table :deep(.el-table__header th) {
  border-color: #1d4ed8;
}

/* 表格行悬浮背景 */
.hw-table :deep(.el-table__body tr:hover > td) {
  background: #f0f6ff !important;
}

/* 查看详情按钮颜色 */
.detail-btn {
  color: #1d4ed8 !important;
  font-weight: 600;
}

/* 查看详情按钮悬浮颜色 */
.detail-btn:hover {
  color: #3b82f6 !important;
}

/* 提交作业按钮颜色 */
.submit-btn {
  color: #3b82f6 !important;
  font-weight: 600;
}

/* 提交作业按钮悬浮颜色 */
.submit-btn:hover {
  color: #1d4ed8 !important;
}

/* 查看成绩按钮颜色 */
.grade-btn {
  color: #fa8c16 !important;
  font-weight: 600;
}

/* 查看成绩按钮悬浮/禁用颜色 */
.grade-btn:hover,
.grade-btn.is-disabled,
.grade-btn.is-disabled:hover {
  color: #bfa88c !important;
}

/* 分页器样式 */
.hw-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

/* 分页器总数文字 */
.hw-pagination :deep(.el-pagination__total) {
  color: #1d4ed8;
  font-weight: 600;
}

/* 分页器按钮样式 */
.hw-pagination :deep(.el-pagination .btn-prev),
.hw-pagination :deep(.el-pagination .btn-next),
.hw-pagination :deep(.el-pager li) {
  border-radius: 6px;
  margin: 0 3px;
  color: #1d4ed8;
}

/* 分页器页码悬浮颜色 */
.hw-pagination :deep(.el-pager li:hover) {
  color: #3b82f6;
}

/* 分页器当前页码样式 */
.hw-pagination :deep(.el-pager li.is-active) {
  background: #1d4ed8;
  color: #fff;
  font-weight: 600;
}

/* 分页器每页条数输入框 */
.hw-pagination :deep(.el-pagination__sizes .el-input__wrapper) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
}

/* 分页器每页条数输入框悬浮 */
.hw-pagination :deep(.el-pagination__sizes .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #3b82f6 inset;
}

/* 分页器跳转文字颜色 */
.hw-pagination :deep(.el-pagination__jump) {
  color: #1d4ed8;
}

/* ============ 提交作业对话框 ============ */
/* 提交弹窗头部渐变背景 */
.submit-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  margin-right: 0;
  border-radius: 12px 12px 0 0;
  padding: 16px 20px;
}

/* 提交弹窗标题样式 */
.submit-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 17px;
  font-weight: 600;
}

/* 提交弹窗关闭按钮颜色 */
.submit-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
}

/* 提交弹窗内容区内边距 */
.submit-dialog :deep(.el-dialog__body) {
  padding: 24px 20px 4px;
}

/* 提交弹窗底部内边距 */
.submit-dialog :deep(.el-dialog__footer) {
  padding: 10px 20px 20px;
}

/* 弹窗作业标题样式 */
.dialog-hw-title {
  font-weight: 600;
  color: #1d4ed8;
}

/* 弹窗截止时间样式 */
.dialog-hw-deadline {
  color: #8c9c93;
  font-size: 13px;
}

/* 提交表单标签样式 */
.submit-form :deep(.el-form-item__label) {
  color: #1d4ed8;
  font-weight: 600;
}

/* 提交内容文本域样式 */
.submit-form :deep(.el-textarea__inner) {
  border-color: #c8e6d0;
  border-radius: 8px;
}

/* 提交内容文本域聚焦样式 */
.submit-form :deep(.el-textarea__inner:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

/* 附件上传列表项样式 */
.attachment-upload :deep(.el-upload-list__item) {
  border-color: #d6e9dd;
  border-radius: 6px;
}

/* 上传触发按钮样式 */
.upload-trigger {
  background: #f6ffed;
  border: 1px dashed #3b82f6;
  color: #1d4ed8;
  border-radius: 6px;
}

/* 上传触发按钮悬浮/聚焦样式 */
.upload-trigger:hover,
.upload-trigger:focus {
  background: #f0f6ff;
  border-color: #1d4ed8;
  color: #1d4ed8;
}

/* 上传提示文字 */
.upload-tip {
  color: #8c9c93;
  font-size: 12px;
  margin-top: 6px;
}

/* 取消按钮样式 */
.btn-cancel {
  border-color: #1d4ed8;
  color: #1d4ed8;
  border-radius: 6px;
  padding: 0 22px;
}

/* 取消按钮悬浮/聚焦样式 */
.btn-cancel:hover,
.btn-cancel:focus {
  background: #f0f6ff;
  border-color: #1d4ed8;
  color: #1d4ed8;
}

/* 确认按钮样式 */
.btn-confirm {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
  border-radius: 6px;
  padding: 0 28px;
  font-weight: 600;
}

/* 确认按钮悬浮/聚焦/加载样式 */
.btn-confirm:hover,
.btn-confirm:focus,
.btn-confirm.is-loading {
  background: #73d13d;
  border-color: #73d13d;
  color: #fff;
}

/* ============ 查看成绩对话框 ============ */
/* 成绩弹窗头部渐变背景 */
.grade-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  margin-right: 0;
  border-radius: 12px 12px 0 0;
  padding: 16px 20px;
}

/* 成绩弹窗标题样式 */
.grade-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 17px;
  font-weight: 600;
}

/* 成绩弹窗关闭按钮颜色 */
.grade-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
}

/* 成绩弹窗内容区内边距 */
.grade-dialog :deep(.el-dialog__body) {
  padding: 24px 20px 4px;
}

/* 成绩弹窗底部内边距 */
.grade-dialog :deep(.el-dialog__footer) {
  padding: 10px 20px 20px;
}

/* 成绩内容区最小高度 */
.grade-content {
  min-height: 160px;
}

/* 分数显示块居中 */
.score-block {
  text-align: center;
  padding: 20px 0 8px;
}

/* 分数标签样式 */
.score-label {
  color: #8c9c93;
  font-size: 13px;
  margin-bottom: 8px;
}

/* 分数数值行布局 */
.score-value {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
}

/* 分数数字样式 */
.score-num {
  font-size: 56px;
  font-weight: 700;
  color: #1d4ed8;
  line-height: 1;
}

/* 分数单位样式 */
.score-unit {
  font-size: 18px;
  color: #3b82f6;
  font-weight: 600;
}

/* 未批改提示居中 */
.not-graded {
  text-align: center;
  padding: 28px 0 12px;
}

/* 未批改图标样式 */
.not-graded-icon {
  font-size: 40px;
  color: #bfa88c;
  margin-bottom: 8px;
}

/* 未批改文字样式 */
.not-graded-text {
  color: #8c9c93;
  font-size: 14px;
}

/* 成绩分隔线文字背景 */
.grade-divider :deep(.el-divider__text) {
  background: #fff;
}

/* 成绩分隔线边框颜色 */
.grade-divider :deep(.el-divider) {
  border-color: #d6e9dd;
}

/* 评语块内边距 */
.comment-block {
  padding: 4px 4px 12px;
}

/* 评语标签样式 */
.comment-label {
  color: #1d4ed8;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 8px;
}

/* 评语内容样式 */
.comment-text {
  background: #f8fcfa;
  border: 1px solid #d6e9dd;
  border-radius: 8px;
  padding: 12px 14px;
  color: #303133;
  line-height: 1.7;
  white-space: pre-wrap;
  min-height: 48px;
}

/* 批改时间行样式 */
.grade-time-row {
  color: #8c9c93;
  font-size: 12px;
  text-align: right;
  margin-top: 8px;
}
</style>
