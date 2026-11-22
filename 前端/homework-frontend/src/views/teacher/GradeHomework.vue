<template>
  <!-- 批改作业页面根容器 -->
  <div class="grade-homework">
    <!-- 批改作业卡片 -->
    <el-card class="grade-card" v-loading="loading">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">批改作业</span>
          <!-- 返回按钮 -->
          <el-button class="btn-back" size="small" @click="$router.back()">返回</el-button>
        </div>
      </template>

      <!-- 作业基本信息描述列表 -->
      <el-descriptions :column="2" border class="hw-desc">
        <!-- 作业标题 -->
        <el-descriptions-item label="作业标题">{{ homework.title }}</el-descriptions-item>
        <!-- 班级 -->
        <el-descriptions-item label="班级">{{ homework.className }}</el-descriptions-item>
        <!-- 截止时间 -->
        <el-descriptions-item label="截止时间">{{ formatDate(homework.deadline) }}</el-descriptions-item>
        <!-- 发布时间 -->
        <el-descriptions-item label="发布时间">{{ formatDate(homework.createTime) }}</el-descriptions-item>
        <!-- 作业描述（跨两列） -->
        <el-descriptions-item label="作业描述" :span="2">{{ homework.description }}</el-descriptions-item>
      </el-descriptions>

      <!-- 学生提交列表分隔线 -->
      <el-divider content-position="left" class="hw-divider">
        <span class="divider-text">学生提交列表</span>
      </el-divider>

      <!-- 学生提交列表表格 -->
      <el-table :data="submissionList" border class="hw-table">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 姓名列：prop绑定studentName字段 -->
        <el-table-column prop="studentName" label="姓名" width="80" />
        <!-- 学号列：prop绑定studentNo字段 -->
        <el-table-column prop="studentNo" label="学号" width="90" />
        <!-- 提交内容列：prop绑定content字段，超出省略 -->
        <el-table-column prop="content" label="提交内容" min-width="200" show-overflow-tooltip />
        <!-- 提交时间列：prop绑定submitTime字段，格式化显示 -->
        <el-table-column prop="submitTime" label="提交时间" width="160">
          <template #default="{ row }">{{ formatDate(row.submitTime) }}</template>
        </el-table-column>
        <!-- 分数列：prop绑定score字段，显示分数标签 -->
        <el-table-column prop="score" label="分数" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score !== null ? 'success' : 'info'" class="score-tag">{{ row.score !== null ? row.score : '未批改' }}</el-tag>
          </template>
        </el-table-column>
        <!-- 状态列：显示打回/优秀标记 -->
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <!-- 已打回标签 -->
            <el-tag v-if="row.isReturned" type="warning" class="status-tag" size="small">已打回</el-tag>
            <!-- 优秀标签 -->
            <el-tag v-if="row.isExcellent" class="excellent-tag" size="small">优秀</el-tag>
            <!-- 无状态占位 -->
            <span v-if="!row.isReturned && !row.isExcellent" class="text-muted">-</span>
          </template>
        </el-table-column>
        <!-- 操作列：批改按钮 -->
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button size="small" class="btn-grade" link @click="openGradeDialog(row)">批改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 批改弹窗 -->
    <el-dialog v-model="gradeDialogVisible" title="批改作业" width="520px" class="grade-dialog">
      <!-- 批改表单 -->
      <el-form :model="gradeForm" label-width="80px" class="grade-form">
        <!-- 学生姓名（只读显示） -->
        <el-form-item label="学生">{{ gradeForm.studentName }}</el-form-item>
        <!-- 提交内容（只读） -->
        <el-form-item label="提交内容">
          <el-input type="textarea" :rows="4" v-model="gradeForm.content" readonly />
        </el-form-item>
        <!-- 附件信息（有附件时显示） -->
        <el-form-item label="附件" v-if="gradeForm.attachment">
          <div class="attachment-box">
            <!-- 附件图标 -->
            <el-icon class="att-icon"><Document /></el-icon>
            <!-- 附件文件名 -->
            <span class="att-name">{{ gradeForm.attachment }}</span>
            <!-- 下载按钮 -->
            <el-button size="small" class="btn-download" link @click="handleDownload(gradeForm.attachment)">下载</el-button>
          </div>
        </el-form-item>
        <!-- 分数输入 -->
        <el-form-item label="分数" required>
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" class="score-input" />
        </el-form-item>
        <!-- 评语输入 -->
        <el-form-item label="评语">
          <el-input type="textarea" :rows="3" v-model="gradeForm.comment" placeholder="请输入评语" class="comment-input" />
        </el-form-item>
        <!-- 标记区域：优秀/打回 -->
        <el-form-item label="标记">
          <div class="mark-box">
            <!-- 标记为优秀 -->
            <el-checkbox v-model="gradeForm.isExcellent" class="mark-excellent">
              <span class="mark-label">标记为优秀</span>
            </el-checkbox>
            <!-- 打回重交 -->
            <el-checkbox v-model="gradeForm.isReturned" class="mark-return">
              <span class="mark-label">打回重交</span>
            </el-checkbox>
          </div>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button class="btn-cancel" @click="gradeDialogVisible = false">取消</el-button>
        <el-button class="btn-confirm-grade" @click="handleGrade">确认批改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getTeacherSubmissions, gradeHomework } from '@/api'
import request from '@/utils/request'

// 路由对象
const route = useRoute()
// 数据加载状态
const loading = ref(false)
// 作业信息
const homework = ref({})
// 学生提交列表
const submissionList = ref([])
// 批改弹窗显示状态
const gradeDialogVisible = ref(false)

// 批改表单数据
const gradeForm = reactive({
  // 提交记录ID
  submissionId: null,
  // 学生姓名
  studentName: '',
  // 提交内容
  content: '',
  // 附件文件名
  attachment: '',
  // 分数
  score: 0,
  // 评语
  comment: '',
  // 是否打回
  isReturned: false,
  // 是否优秀
  isExcellent: false
})

// 格式化日期字符串
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 加载作业信息与提交列表
const loadData = async () => {
  loading.value = true
  try {
    // 获取作业信息（通过管理员接口复用）
    const hwRes = await request.get('/admin/homework/list', { params: { pageNum: 1, pageSize: 100 } })
    const found = hwRes.data.list.find(h => h.homeworkId == route.params.homeworkId)
    if (found) homework.value = found

    // 获取提交列表
    const res = await getTeacherSubmissions(route.params.homeworkId)
    submissionList.value = res.data
  } catch (error) { console.error(error) } finally { loading.value = false }
}

// 打开批改弹窗，填充学生提交数据
const openGradeDialog = (row) => {
  gradeForm.submissionId = row.submissionId
  gradeForm.studentName = row.studentName
  gradeForm.content = row.content
  gradeForm.attachment = row.attachment || ''
  gradeForm.score = row.score !== null ? row.score : 0
  gradeForm.comment = row.comment || ''
  gradeForm.isReturned = !!row.isReturned
  gradeForm.isExcellent = !!row.isExcellent
  gradeDialogVisible.value = true
}

// 下载附件
const handleDownload = async (filename) => {
  if (!filename) return
  try {
    const res = await request.get('/file/download', {
      params: { filename },
      responseType: 'blob'
    })
    const blob = new Blob([res])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error(error)
    ElMessage.error('附件下载失败')
  }
}

// 提交批改结果
const handleGrade = async () => {
  try {
    await gradeHomework({
      submissionId: gradeForm.submissionId,
      score: gradeForm.score,
      comment: gradeForm.comment,
      isReturned: gradeForm.isReturned,
      isExcellent: gradeForm.isExcellent
    })
    ElMessage.success('批改成功')
    gradeDialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

// 组件挂载时加载数据
onMounted(() => { loadData() })
</script>

<style scoped>
/* 批改作业页面容器样式 */
.grade-homework {
  background: #f0f6ff;
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

/* 批改作业卡片样式 */
.grade-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部渐变背景 */
.grade-card :deep(.el-card__header) {
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

/* 描述列表标签样式 */
.hw-desc :deep(.el-descriptions__label) {
  background: #f0f6ff !important;
  color: #1d4ed8 !important;
  font-weight: 600;
}

/* 描述列表单元格边框 */
.hw-desc :deep(.el-descriptions__cell) {
  border-color: #e0ebe5 !important;
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

/* 分数标签样式 */
.score-tag.el-tag--success {
  background: #f6ffed;
  border-color: #3b82f6;
  color: #1d4ed8;
}

/* 打回状态标签样式 */
.status-tag.el-tag--warning {
  background: #fffbe6;
  border-color: #faad14;
  color: #d48806;
  margin-right: 4px;
}

/* 优秀标签样式 */
.excellent-tag {
  background: #f6ffed;
  border-color: #3b82f6;
  color: #1d4ed8;
  margin-right: 4px;
}

/* 无状态占位文字颜色 */
.text-muted {
  color: #c0c4cc;
}

/* 批改按钮样式 */
.btn-grade {
  color: #1d4ed8;
  font-weight: 500;
}

/* 批改按钮悬浮/聚焦样式 */
.btn-grade:hover,
.btn-grade:focus {
  color: #3b82f6;
}

/* 批改弹窗圆角 */
.grade-dialog :deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

/* 批改弹窗头部渐变背景 */
.grade-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  margin-right: 0;
  padding: 16px 20px;
}

/* 批改弹窗标题颜色 */
.grade-dialog :deep(.el-dialog__title) {
  color: #ffffff;
  font-weight: 600;
}

/* 批改弹窗关闭按钮颜色 */
.grade-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #ffffff;
}

/* 批改弹窗关闭按钮悬浮颜色 */
.grade-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #f0f6ff;
}

/* 批改弹窗内容区内边距 */
.grade-dialog :deep(.el-dialog__body) {
  padding: 24px 20px;
}

/* 批改表单标签样式 */
.grade-form :deep(.el-form-item__label) {
  color: #1d4ed8;
  font-weight: 500;
}

/* 附件信息框样式 */
.attachment-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f8fcfa;
  border: 1px solid #d6e9dd;
  border-radius: 8px;
  padding: 8px 12px;
  width: 100%;
  box-sizing: border-box;
}

/* 附件图标颜色 */
.att-icon {
  color: #3b82f6;
  font-size: 16px;
}

/* 附件文件名样式 */
.att-name {
  flex: 1;
  color: #1d4ed8;
  font-size: 14px;
  word-break: break-all;
}

/* 下载按钮样式 */
.btn-download {
  color: #3b82f6;
  font-weight: 500;
}

/* 下载按钮悬浮/聚焦样式 */
.btn-download:hover,
.btn-download:focus {
  color: #1d4ed8;
}

/* 分数输入框样式 */
.score-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
  border-radius: 8px;
}

/* 分数输入框悬浮/聚焦样式 */
.score-input :deep(.el-input__wrapper:hover),
.score-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #3b82f6 inset;
}

/* 评语文本域样式 */
.comment-input :deep(.el-textarea__inner) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
  border-radius: 8px;
}

/* 评语文本域聚焦样式 */
.comment-input :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #3b82f6 inset;
}

/* 批改表单输入框圆角 */
.grade-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 批改表单输入框悬浮/聚焦阴影 */
.grade-form :deep(.el-input__wrapper:hover),
.grade-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
}

/* 标记区域 */
.mark-box {
  display: flex;
  gap: 24px;
  align-items: center;
}

/* 优秀复选框选中状态样式 */
.mark-excellent :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
.mark-excellent :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  background-color: transparent;
}

/* 优秀复选框选中内框样式 */
.mark-excellent :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #3b82f6;
  border-color: #3b82f6;
}

/* 优秀复选框选中标签颜色 */
.mark-excellent :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #1d4ed8;
}

/* 优秀复选框悬浮边框颜色 */
.mark-excellent :deep(.el-checkbox__inner:hover) {
  border-color: #3b82f6;
}

/* 打回复选框选中内框样式 */
.mark-return :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #faad14;
  border-color: #faad14;
}

/* 打回复选框选中标签颜色 */
.mark-return :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #d48806;
}

/* 打回复选框悬浮边框颜色 */
.mark-return :deep(.el-checkbox__inner:hover) {
  border-color: #faad14;
}

/* 标记标签文字字重 */
.mark-label {
  font-weight: 500;
}

/* 取消按钮样式 */
.btn-cancel {
  border-color: #1d4ed8;
  color: #1d4ed8;
  border-radius: 6px;
}

/* 取消按钮悬浮/聚焦样式 */
.btn-cancel:hover,
.btn-cancel:focus {
  background: #f0f6ff;
  border-color: #1d4ed8;
  color: #1d4ed8;
}

/* 确认批改按钮样式 */
.btn-confirm-grade {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #ffffff;
  border-radius: 6px;
}

/* 确认批改按钮悬浮/聚焦样式 */
.btn-confirm-grade:hover,
.btn-confirm-grade:focus {
  background: #73d13d;
  border-color: #73d13d;
  color: #ffffff;
}
</style>
