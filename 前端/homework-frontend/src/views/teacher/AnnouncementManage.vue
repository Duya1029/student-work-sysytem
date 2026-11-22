<template>
  <!-- 通知公告管理页面根容器 -->
  <div class="announcement-manage">
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">通知公告</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 标题搜索输入框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="标题搜索"
          style="width: 220px"
          clearable
        />
        <!-- 发布通知按钮 -->
        <el-button type="success" class="btn-add" @click="openDialog">发布通知</el-button>
      </div>

      <!-- 通知公告表格 -->
      <el-table :data="filteredList" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 标题列：prop绑定title字段 -->
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <!-- 内容摘要列：prop绑定content字段，截取摘要显示 -->
        <el-table-column prop="content" label="内容摘要" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">{{ summarize(row.content) }}</template>
        </el-table-column>
        <!-- 发布时间列：prop绑定createTime字段，格式化显示 -->
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
      </el-table>

      <!-- 暂无数据提示 -->
      <div v-if="!loading && filteredList.length === 0" class="empty-tip">暂无通知数据</div>
    </el-card>

    <!-- 发布通知弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="发布通知"
      width="600px"
      class="green-dialog"
    >
      <!-- 发布通知表单 -->
      <el-form ref="formRef" :model="editForm" :rules="rules" label-width="80px">
        <!-- 标题输入项 -->
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入通知标题" maxlength="100" show-word-limit />
        </el-form-item>
        <!-- 内容输入项 -->
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入通知内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        <!-- 班级选择项 -->
        <el-form-item label="选择班级" prop="classId">
          <el-select v-model="editForm.classId" placeholder="请选择班级" filterable style="width: 100%">
            <!-- 班级选项循环 -->
            <el-option v-for="c in classList" :key="c.classId" :label="c.className" :value="c.classId" />
          </el-select>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="success" :loading="submitting" @click="handleSave">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAnnouncementList, addClassAnnouncement, getMyClasses } from '@/api'

// 列表加载状态
const loading = ref(false)
// 提交加载状态
const submitting = ref(false)
// 公告列表数据
const tableData = ref([])
// 弹窗显示状态
const dialogVisible = ref(false)
// 搜索关键词
const searchKeyword = ref('')
// 班级列表
const classList = ref([])
// 表单引用
const formRef = ref()

// 编辑表单数据
const editForm = reactive({
  // 标题
  title: '',
  // 内容
  content: '',
  // 班级ID
  classId: null
})

// 表单校验规则
const rules = {
  // 标题校验
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  // 内容校验
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
  // 班级校验
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

// 格式化日期字符串
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 内容摘要：截取前 40 个字符
const summarize = (content) => {
  if (!content) return '-'
  const text = String(content)
  return text.length > 40 ? text.substring(0, 40) + '...' : text
}

// 按搜索关键词过滤的公告列表
const filteredList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return tableData.value
  return tableData.value.filter(item => (item.title || '').toLowerCase().includes(kw))
})

// 加载公告列表
const loadData = async () => {
  loading.value = true
  try {
    const res = await getAnnouncementList()
    const list = Array.isArray(res.data) ? res.data : (res.data?.list || [])
    tableData.value = list
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载班级列表
const loadClasses = async () => {
  try {
    const res = await getMyClasses()
    classList.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
  } catch (error) { console.error(error) }
}

// 打开发布通知弹窗
const openDialog = () => {
  editForm.title = ''
  editForm.content = ''
  editForm.classId = null
  dialogVisible.value = true
}

// 保存并发布通知
const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await addClassAnnouncement({
        title: editForm.title,
        content: editForm.content,
        classId: editForm.classId
      })
      ElMessage.success('通知发布成功')
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

// 组件挂载时加载公告列表和班级列表
onMounted(() => {
  loadData()
  loadClasses()
})
</script>

<style scoped>
/* 页面容器 */
.announcement-manage {
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

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

/* 新增按钮 */
.btn-add {
  background: #3b82f6 !important;
  border-color: #3b82f6 !important;
}

/* 表格 - 绿色表头白字 */
.green-table :deep(.el-table__header th) {
  background-color: #1d4ed8 !important;
  color: #fff !important;
  font-weight: 600;
}

/* 表格表头文字颜色 */
.green-table :deep(.el-table__header th .cell) {
  color: #fff !important;
}

/* 表格行悬浮背景 */
.green-table :deep(.el-table__body tr:hover > td) {
  background: #f0f6ff !important;
}

/* 表格单元格边框 */
.green-table :deep(.el-table__body td) {
  border-color: #e0ebe5;
}

/* 空提示 */
.empty-tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 30px 0;
}

/* 弹窗标题绿色 */
.green-dialog :deep(.el-dialog__header) {
  background: #1d4ed8;
  border-radius: 8px 8px 0 0;
  padding: 16px 20px;
  margin-right: 0;
}

/* 弹窗标题文字颜色 */
.green-dialog :deep(.el-dialog__title) {
  color: #fff !important;
  font-weight: 600;
}

/* 弹窗关闭按钮颜色 */
.green-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff !important;
}

/* 弹窗关闭按钮悬浮颜色 */
.green-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #f0f6ff !important;
}

/* 弹窗圆角与溢出隐藏 */
.green-dialog :deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

/* 弹窗主要按钮颜色 */
.green-dialog :deep(.el-dialog__footer .el-button--primary) {
  background-color: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
}

/* 弹窗成功按钮颜色 */
.green-dialog :deep(.el-dialog__footer .el-button--success) {
  background-color: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
  color: #fff !important;
}

/* 弹窗成功按钮悬浮颜色 */
.green-dialog :deep(.el-dialog__footer .el-button--success:hover) {
  background-color: #3b82f6 !important;
  border-color: #3b82f6 !important;
}

/* 表单输入框聚焦色 */
.green-dialog :deep(.el-input__wrapper.is-focus),
.green-dialog :deep(.el-select .el-input__wrapper.is-focus),
.green-dialog :deep(.el-input__wrapper:hover),
.green-dialog :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset !important;
}

/* 弹窗表单标签样式 */
.green-dialog :deep(.el-form-item__label) {
  color: #1d4ed8;
  font-weight: 500;
}
</style>
