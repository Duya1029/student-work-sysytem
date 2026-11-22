<template>
  <!-- 作业详情页面根容器 -->
  <div class="homework-detail">
    <!-- 作业详情卡片 -->
    <el-card v-loading="loading" class="detail-card" shadow="never">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-title">作业详情</span>
          <!-- 返回按钮 -->
          <el-button class="back-btn" @click="$router.back()">返回</el-button>
        </div>
      </template>

      <!-- 作业信息区块 -->
      <div class="info-block">
        <div class="info-block-title">
          <span class="dot"></span>
          <span>作业信息</span>
        </div>
        <!-- 作业信息描述列表 -->
        <el-descriptions :column="2" border class="hw-descriptions">
          <!-- 作业标题 -->
          <el-descriptions-item label="作业标题">{{ homework.title }}</el-descriptions-item>
          <!-- 发布教师 -->
          <el-descriptions-item label="发布教师">{{ homework.teacherName }}</el-descriptions-item>
          <!-- 截止时间 -->
          <el-descriptions-item label="截止时间">{{ formatDate(homework.deadline) }}</el-descriptions-item>
          <!-- 发布时间 -->
          <el-descriptions-item label="发布时间">{{ formatDate(homework.createTime) }}</el-descriptions-item>
          <!-- 作业描述（跨两列） -->
          <el-descriptions-item label="作业描述" :span="2">{{ homework.description }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 我的提交分隔线 -->
      <el-divider content-position="left" class="my-divider">
        <span class="divider-text">我的提交</span>
      </el-divider>

      <!-- 分数评语展示（已批改时显示） -->
      <div v-if="submission && submission.score !== null" class="score-section">
        <!-- 分数提示 -->
        <el-alert type="success" :closable="false" class="score-alert">
          <template #title>
            <span class="score-text">
              分数：<b class="score-num">{{ submission.score }}</b> 分
            </span>
          </template>
        </el-alert>
        <!-- 教师评语卡片 -->
        <el-card shadow="never" class="comment-card">
          <template #header>
            <span class="comment-title">教师评语</span>
          </template>
          <!-- 评语内容 -->
          <div class="comment-content">{{ submission.comment || '暂无评语' }}</div>
          <!-- 批改时间 -->
          <p class="grade-time">批改时间：{{ formatDate(submission.gradeTime) }}</p>
        </el-card>
      </div>

      <!-- 提交作业表单区块 -->
      <div class="submit-block">
        <el-form :model="submitForm" label-width="80px" class="submit-form">
          <!-- 提交内容输入 -->
          <el-form-item label="提交内容" required>
            <el-input
              v-model="submitForm.content"
              type="textarea"
              :rows="8"
              :placeholder="submission ? '重新提交作业内容...' : '请输入作业内容...'"
            />
          </el-form-item>
          <!-- 提交按钮 -->
          <el-form-item>
            <el-button type="primary" :loading="submitting" class="submit-btn" @click="handleSubmit">
              {{ submission ? '重新提交' : '提交作业' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHomeworkDetail, submitHomework } from '@/api'
import request from '@/utils/request'

// 路由对象
const route = useRoute()
// 数据加载状态
const loading = ref(false)
// 提交加载状态
const submitting = ref(false)
// 作业信息
const homework = ref({})
// 提交记录
const submission = ref(null)

// 提交表单数据
const submitForm = reactive({
  // 作业ID
  homeworkId: null,
  // 提交内容
  content: ''
})

// 格式化日期字符串
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 加载作业详情与提交记录
const loadData = async () => {
  loading.value = true
  try {
    // 获取作业信息（通过学生作业列表获取）
    const listRes = await request.get('/student/homework/list', { params: { pageNum: 1, pageSize: 100 } })
    const found = listRes.data.list.find(h => h.homeworkId == route.params.homeworkId)
    if (found) homework.value = found

    // 获取提交详情
    const res = await getHomeworkDetail(route.params.homeworkId)
    submission.value = res.data
    if (submission.value) {
      submitForm.content = submission.value.content || ''
    }
    submitForm.homeworkId = parseInt(route.params.homeworkId)
  } catch (error) { console.error(error) } finally { loading.value = false }
}

// 提交作业
const handleSubmit = async () => {
  if (!submitForm.content.trim()) {
    ElMessage.warning('作业内容不能为空')
    return
  }
  submitting.value = true
  try {
    await submitHomework(submitForm)
    ElMessage.success('提交成功')
    loadData() // 刷新数据
  } catch (error) { console.error(error) } finally { submitting.value = false }
}

// 组件挂载时加载作业详情
onMounted(() => { loadData() })
</script>

<style scoped>
/* 作业详情页面容器样式 */
.homework-detail {
  padding: 20px;
  background: #f0f6ff;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 作业详情卡片样式 */
.detail-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部渐变背景 */
.detail-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  padding: 16px 20px;
  border-bottom: none;
}

/* 卡片标题栏布局 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 标题文字样式 */
.header-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 返回按钮样式 */
.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: #fff;
  border-radius: 6px;
}

/* 返回按钮悬浮样式 */
.back-btn:hover {
  background: rgba(255, 255, 255, 0.35);
  border-color: #fff;
  color: #fff;
}

/* 卡片内容区内边距 */
.detail-card :deep(.el-card__body) {
  padding: 24px 20px;
}

/* 信息区块 */
.info-block {
  margin-bottom: 24px;
}

/* 信息区块标题样式 */
.info-block-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1d4ed8;
  margin-bottom: 14px;
}

/* 信息区块标题装饰点 */
.info-block-title .dot {
  width: 8px;
  height: 18px;
  background: linear-gradient(180deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 3px;
}

/* 描述列表标签样式 */
.hw-descriptions :deep(.el-descriptions__label) {
  background: #f0f6ff;
  color: #1d4ed8;
  font-weight: 600;
  border-color: #d6e9dd;
}

/* 描述列表内容边框 */
.hw-descriptions :deep(.el-descriptions__content) {
  border-color: #d6e9dd;
}

/* 描述列表单元格边框 */
.hw-descriptions :deep(.el-descriptions__border .el-descriptions__cell) {
  border-color: #d6e9dd;
}

/* 分割线文字背景 */
.my-divider :deep(.el-divider__text) {
  background: #fff;
}

/* 分割线文字颜色 */
.divider-text {
  color: #1d4ed8;
  font-weight: 600;
}

/* 分割线文字左对齐 */
.my-divider :deep(.el-divider__text.is-left) {
  left: 0;
  transform: translateX(0);
  padding-left: 0;
}

/* 分割线边框颜色 */
.my-divider :deep(.el-divider) {
  border-color: #d6e9dd;
}

/* 分数评语区 */
.score-section {
  margin-bottom: 24px;
}

/* 分数提示样式 */
.score-alert {
  margin-bottom: 15px;
  border-radius: 8px;
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  border: none;
}

/* 分数提示内容内边距 */
.score-alert :deep(.el-alert__content) {
  padding: 6px 0;
}

/* 分数文字样式 */
.score-text {
  font-size: 16px;
  color: #fff;
}

/* 分数数字样式 */
.score-num {
  font-size: 24px;
  color: #fff;
  font-weight: 700;
}

/* 评语卡片样式 */
.comment-card {
  border-radius: 8px;
  border: 1px solid #d6e9dd;
  background: #f8fcfa;
  box-shadow: 0 2px 8px rgba(29, 78, 216, 0.08);
}

/* 评语卡片头部样式 */
.comment-card :deep(.el-card__header) {
  background: #f0f6ff;
  padding: 12px 16px;
  border-bottom: 1px solid #d6e9dd;
}

/* 评语标题样式 */
.comment-title {
  color: #1d4ed8;
  font-weight: 600;
}

/* 评语内容样式 */
.comment-content {
  color: #303133;
  line-height: 1.7;
  white-space: pre-wrap;
  padding: 4px 0;
}

/* 批改时间样式 */
.grade-time {
  margin-top: 12px;
  color: #8c9c93;
  font-size: 12px;
}

/* 提交表单区 */
.submit-block {
  background: #f8fcfa;
  border: 1.5px solid #3b82f6;
  border-radius: 10px;
  padding: 20px 20px 4px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
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

/* 提交按钮样式 */
.submit-btn {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
  border-radius: 6px;
  font-weight: 600;
  padding: 0 28px;
}

/* 提交按钮悬浮样式 */
.submit-btn:hover {
  background: #3da618;
  border-color: #3da618;
  color: #fff;
}

/* 提交按钮加载样式 */
.submit-btn.is-loading {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
}
</style>
