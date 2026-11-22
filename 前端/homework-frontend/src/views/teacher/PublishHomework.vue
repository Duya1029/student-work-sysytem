<template>
  <!-- 发布作业页面根容器 -->
  <div class="publish-homework">
    <!-- 发布作业卡片 -->
    <el-card class="publish-card">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-bar"></span>
          <span class="header-title">发布作业</span>
        </div>
      </template>
      <!-- 发布作业表单 -->
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="publish-form">
        <!-- 作业标题输入项 -->
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入作业标题" />
        </el-form-item>
        <!-- 作业描述输入项 -->
        <el-form-item label="作业描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="6" placeholder="请输入作业要求/描述" />
        </el-form-item>
        <!-- 课程选择项 -->
        <el-form-item label="选择课程" prop="courseId">
          <el-select
            v-model="form.courseId"
            placeholder="请选择课程"
            filterable
            style="width: 100%"
            @change="handleCourseChange"
          >
            <!-- 课程选项循环 -->
            <el-option
              v-for="c in courseList"
              :key="c.courseId ?? c.id"
              :label="c.courseName"
              :value="c.courseId ?? c.id"
            />
          </el-select>
        </el-form-item>
        <!-- 班级选择项 -->
        <el-form-item label="选择班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" filterable style="width: 100%">
            <!-- 班级选项循环 -->
            <el-option v-for="c in filteredClassList" :key="c.classId" :label="c.className" :value="c.classId" />
          </el-select>
          <!-- 课程未关联班级提示 -->
          <div v-if="form.courseId && filteredClassList.length === 0" class="form-tip">该课程未关联班级</div>
        </el-form-item>
        <!-- 截止时间选择项 -->
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="form.deadline"
            type="datetime"
            placeholder="选择截止时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <!-- 附件上传项 -->
        <el-form-item label="附件上传">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :on-exceed="handleExceed"
            :file-list="fileList"
            class="attachment-upload"
          >
            <el-button class="btn-upload">选择文件</el-button>
            <template #tip>
              <div class="upload-tip">只能上传一个附件，附件文件名将随作业一起发布</div>
            </template>
          </el-upload>
        </el-form-item>
        <!-- 操作按钮区 -->
        <el-form-item>
          <el-button class="btn-publish" :loading="loading" @click="handleSubmit">发布</el-button>
          <el-button class="btn-reset" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { publishHomework, getMyClasses, getTeacherCourses } from '@/api'

// 表单引用
const formRef = ref()
// 上传组件引用
const uploadRef = ref()
// 提交加载状态
const loading = ref(false)
// 班级列表
const classList = ref([])
// 课程列表
const courseList = ref([])
// 附件文件列表
const fileList = ref([])

// 发布作业表单数据
const form = reactive({
  // 作业标题
  title: '',
  // 作业描述
  description: '',
  // 课程ID
  courseId: null,
  // 班级ID
  classId: null,
  // 截止时间
  deadline: '',
  // 附件文件名
  attachment: ''
})

// 表单校验规则
const rules = {
  // 标题校验
  title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
  // 描述校验
  description: [{ required: true, message: '请输入作业描述', trigger: 'blur' }],
  // 课程校验
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  // 班级校验
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  // 截止时间校验
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

// 提取课程关联的班级 ID 列表
const courseClassIds = (course) => {
  if (!course) return []
  const list = course.classIds || course.classIdList || []
  if (Array.isArray(list) && list.length) {
    return list.map(c => (typeof c === 'object' ? (c.classId ?? c.id) : c)).filter(Boolean)
  }
  if (course.classes && Array.isArray(course.classes)) {
    return course.classes.map(c => (typeof c === 'object' ? (c.classId ?? c.id) : c)).filter(Boolean)
  }
  return []
}

// 当前选中课程关联的班级选项
const filteredClassList = computed(() => {
  if (!form.courseId) return classList.value
  const course = courseList.value.find(c => (c.courseId ?? c.id) === form.courseId)
  if (!course) return classList.value
  const ids = courseClassIds(course)
  if (!ids.length) return []
  return classList.value.filter(c => ids.includes(c.classId))
})

// 课程选择变化处理
const handleCourseChange = () => {
  // 选择课程后自动关联班级
  form.classId = null
  const available = filteredClassList.value
  if (available.length === 1) {
    form.classId = available[0].classId
  }
}

// 附件文件变更处理
const handleFileChange = (file) => {
  form.attachment = file.name
}

// 附件文件移除处理
const handleFileRemove = () => {
  form.attachment = ''
  fileList.value = []
}

// 附件超出数量限制处理
const handleExceed = () => {
  ElMessage.warning('只能上传一个附件，请先删除已有附件')
}

// 加载班级列表
const loadClasses = async () => {
  try {
    const res = await getMyClasses()
    classList.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
  } catch (error) { console.error(error) }
}

// 加载课程列表
const loadCourses = async () => {
  try {
    const res = await getTeacherCourses()
    courseList.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
  } catch (error) { console.error(error) }
}

// 提交发布作业
const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await publishHomework(form)
      ElMessage.success('作业发布成功')
      resetForm()
    } catch (error) { console.error(error) } finally { loading.value = false }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  form.attachment = ''
  fileList.value = []
  uploadRef.value?.clearFiles()
}

// 组件挂载时加载班级和课程列表
onMounted(() => {
  loadClasses()
  loadCourses()
})
</script>

<style scoped>
/* 发布作业页面容器样式 */
.publish-homework {
  background: #f0f6ff;
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

/* 发布作业卡片样式 */
.publish-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部渐变背景 */
.publish-card :deep(.el-card__header) {
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
}

/* 卡片内容区内边距 */
.publish-card :deep(.el-card__body) {
  padding: 30px 24px;
}

/* 表单标签样式 */
.publish-form :deep(.el-form-item__label) {
  color: #1d4ed8;
  font-weight: 500;
}

/* 输入框圆角 */
.publish-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 输入框悬浮与聚焦阴影 */
.publish-form :deep(.el-input__wrapper:hover),
.publish-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
}

/* 文本域聚焦阴影 */
.publish-form :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
}

/* 附件上传列表项样式 */
.attachment-upload :deep(.el-upload-list__item) {
  border-radius: 6px;
  border-color: #d6e9dd;
  background: #f8fcfa;
}

/* 附件上传列表项悬浮背景 */
.attachment-upload :deep(.el-upload-list__item:hover) {
  background: #f0f6ff;
}

/* 附件上传图标颜色 */
.attachment-upload :deep(.el-upload-list__item .el-icon) {
  color: #1d4ed8;
}

/* 选择文件按钮样式 */
.btn-upload {
  background: #f0f6ff;
  border-color: #1d4ed8;
  color: #1d4ed8;
  border-radius: 6px;
  padding: 0 18px;
}

/* 选择文件按钮悬浮/聚焦样式 */
.btn-upload:hover,
.btn-upload:focus {
  background: #e0ebe5;
  border-color: #1d4ed8;
  color: #1d4ed8;
}

/* 上传提示文字 */
.upload-tip {
  color: #8c9c93;
  font-size: 12px;
  margin-top: 6px;
}

/* 表单提示文字 */
.form-tip {
  color: #faad14;
  font-size: 12px;
  margin-top: 4px;
}

/* 发布按钮样式 */
.btn-publish {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #ffffff;
  border-radius: 6px;
  padding: 0 28px;
}

/* 发布按钮悬浮/聚焦样式 */
.btn-publish:hover,
.btn-publish:focus {
  background: #73d13d;
  border-color: #73d13d;
  color: #ffffff;
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
</style>
