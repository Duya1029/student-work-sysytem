<template>
  <!-- 我的课程页面容器（教师端） -->
  <div class="my-courses">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">我的课程</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 课程名称搜索输入框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="课程名称搜索"
          style="width: 220px"
          clearable
        />
        <!-- 重置按钮 -->
        <el-button type="success" @click="searchKeyword = ''">重置</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="filteredList" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 课程名称列，prop 对应数据字段 courseName -->
        <el-table-column prop="courseName" label="课程名称" min-width="160" show-overflow-tooltip />
        <!-- 课程代码列，prop 对应数据字段 courseCode -->
        <el-table-column prop="courseCode" label="课程代码" width="120" align="center" />
        <!-- 学分列，prop 对应数据字段 credit -->
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <!-- 学时列，prop 对应数据字段 hours -->
        <el-table-column prop="hours" label="学时" width="80" align="center" />
        <!-- 关联班级列：以标签形式展示多个班级 -->
        <el-table-column label="关联班级" min-width="180">
          <template #default="{ row }">
            <template v-if="classNames(row).length">
              <!-- 单个班级标签 -->
              <el-tag
                v-for="(name, i) in classNames(row)"
                :key="i"
                size="small"
                class="class-tag"
              >{{ name }}</el-tag>
            </template>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <!-- 学期列，prop 对应数据字段 semesterName -->
        <el-table-column prop="semesterName" label="学期" width="150" align="center">
          <template #default="{ row }">{{ row.semesterName || '-' }}</template>
        </el-table-column>
        <!-- 操作列：查看学生名单 -->
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" link @click="openStudentDialog(row)">查看学生名单</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <div v-if="!loading && filteredList.length === 0" class="empty-tip">暂无课程数据</div>
    </el-card>

    <!-- 学生名单弹窗 -->
    <el-dialog
      v-model="studentDialogVisible"
      :title="`学生名单 - ${currentCourse.courseName || ''}`"
      width="640px"
      class="green-dialog"
    >
      <!-- 学生名单表格 -->
      <el-table :data="studentList" border class="green-table" v-loading="studentLoading" max-height="420">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 姓名列，prop 对应数据字段 realName，自定义模板兼容多字段 -->
        <el-table-column prop="realName" label="姓名" min-width="120">
          <template #default="{ row }">{{ row.realName || row.studentName || '-' }}</template>
        </el-table-column>
        <!-- 学号列，prop 对应数据字段 username，自定义模板兼容多字段 -->
        <el-table-column prop="username" label="学号" min-width="130">
          <template #default="{ row }">{{ row.username || row.studentNo || '-' }}</template>
        </el-table-column>
        <!-- 班级列，prop 对应数据字段 className -->
        <el-table-column prop="className" label="班级" min-width="140">
          <template #default="{ row }">{{ row.className || '-' }}</template>
        </el-table-column>
      </el-table>
      <!-- 空数据提示 -->
      <div v-if="!studentLoading && studentList.length === 0" class="empty-tip">该课程暂无学生</div>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button type="success" @click="studentDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 从 vue 引入：ref、reactive、computed、onMounted
import { ref, reactive, computed, onMounted } from 'vue'
// 引入 Element Plus 消息提示组件
import { ElMessage } from 'element-plus'
// 引入教师课程相关接口：获取我的课程、获取课程学生名单
import { getTeacherCourses, getCourseStudents } from '@/api'

// 表格加载状态
const loading = ref(false)
// 课程表格数据
const tableData = ref([])
// 搜索关键字
const searchKeyword = ref('')

// 学生名单弹窗显示状态
const studentDialogVisible = ref(false)
// 学生名单加载状态
const studentLoading = ref(false)
// 学生名单数据
const studentList = ref([])
// 当前查看的课程信息
const currentCourse = reactive({ courseId: null, courseName: '' })

// 提取班级名称列表，兼容数组与逗号分隔字符串两种格式
const classNames = (row) => {
  const list = row.classNames || row.classNameList || row.classes || []
  if (Array.isArray(list) && list.length) {
    return list.map(c => (typeof c === 'string' ? c : (c.className || c.name || ''))).filter(Boolean)
  }
  if (typeof row.classNames === 'string' && row.classNames) {
    return row.classNames.split(',').map(s => s.trim()).filter(Boolean)
  }
  return []
}

// 按课程名称关键字过滤列表
const filteredList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return tableData.value
  return tableData.value.filter(item => (item.courseName || '').toLowerCase().includes(kw))
})

// 加载我的课程列表数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getTeacherCourses()
    const list = Array.isArray(res.data) ? res.data : (res.data?.list || [])
    tableData.value = list
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 打开学生名单弹窗并加载该课程的学生列表
const openStudentDialog = async (row) => {
  const id = row.courseId ?? row.id
  if (!id) {
    ElMessage.warning('课程信息缺失')
    return
  }
  currentCourse.courseId = id
  currentCourse.courseName = row.courseName || ''
  studentDialogVisible.value = true
  studentLoading.value = true
  studentList.value = []
  try {
    const res = await getCourseStudents(id)
    studentList.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
  } catch (error) {
    console.error(error)
  } finally {
    studentLoading.value = false
  }
}

// 组件挂载时加载我的课程
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 页面容器 */
.my-courses {
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

/* 表格行悬浮背景色 */
.green-table :deep(.el-table__body tr:hover > td) {
  background: #f0f6ff !important;
}

/* 表格单元格边框色 */
.green-table :deep(.el-table__body td) {
  border-color: #e0ebe5;
}

/* 班级标签：浅绿背景 */
.class-tag {
  margin: 2px 4px 2px 0;
  background: #f6ffed !important;
  border-color: #b7eb8f !important;
  color: #1d4ed8 !important;
}

/* 次要文字颜色 */
.text-muted {
  color: #c0c4cc;
}

/* 空数据提示 */
.empty-tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 30px 0;
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

/* 弹窗底部成功按钮：蓝色主题 */
.green-dialog :deep(.el-dialog__footer .el-button--success) {
  background-color: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
  color: #fff !important;
}

/* 弹窗底部成功按钮悬浮色 */
.green-dialog :deep(.el-dialog__footer .el-button--success:hover) {
  background-color: #3b82f6 !important;
  border-color: #3b82f6 !important;
}
</style>
