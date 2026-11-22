<template>
  <!-- 课程管理页面容器 -->
  <div class="course-manage">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">课程管理</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 课程名称搜索输入框 -->
        <el-input
          v-model="searchForm.courseName"
          placeholder="课程名称搜索"
          style="width: 200px"
          clearable
          @keyup.enter="loadData"
        />
        <!-- 学期筛选下拉框 -->
        <el-select v-model="searchForm.semesterId" placeholder="学期筛选" clearable filterable style="width: 180px">
          <el-option v-for="s in semesterOptions" :key="s.id" :label="s.semesterName" :value="s.id" />
        </el-select>
        <!-- 查询按钮 -->
        <el-button type="success" @click="loadData">查询</el-button>
        <!-- 新增课程按钮 -->
        <el-button type="success" class="btn-add" @click="openDialog(null)">新增课程</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border class="green-table" style="margin-top: 15px" v-loading="loading">
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
        <!-- 任课教师列，prop 对应数据字段 teacherName，自定义模板兼容多字段 -->
        <el-table-column prop="teacherName" label="任课教师" width="110" align="center">
          <template #default="{ row }">{{ row.teacherName || row.teacherRealName || '-' }}</template>
        </el-table-column>
        <!-- 学期列，prop 对应数据字段 semesterName -->
        <el-table-column prop="semesterName" label="学期" width="150" align="center">
          <template #default="{ row }">{{ row.semesterName || '-' }}</template>
        </el-table-column>
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
        <!-- 操作列：编辑、删除 -->
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" type="success" link @click="openDialog(row)">编辑</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑课程' : '新增课程'" width="560px" class="green-dialog">
      <!-- 表单 -->
      <el-form :model="editForm" label-width="90px">
        <!-- 课程名称输入项 -->
        <el-form-item label="课程名称" required>
          <el-input v-model="editForm.courseName" placeholder="请输入课程名称" maxlength="100" show-word-limit />
        </el-form-item>
        <!-- 课程代码输入项 -->
        <el-form-item label="课程代码" required>
          <el-input v-model="editForm.courseCode" placeholder="请输入课程代码" />
        </el-form-item>
        <!-- 学分与学时：两列布局 -->
        <el-row :gutter="10">
          <el-col :span="12">
            <!-- 学分数字输入项 -->
            <el-form-item label="学分" required>
              <el-input-number v-model="editForm.credit" :min="0.5" :max="10" :step="0.5" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 学时数字输入项 -->
            <el-form-item label="学时" required>
              <el-input-number v-model="editForm.hours" :min="1" :max="200" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 任课教师选择项 -->
        <el-form-item label="任课教师" required>
          <el-select v-model="editForm.teacherId" placeholder="选择任课教师" filterable style="width: 100%">
            <el-option v-for="t in teacherOptions" :key="t.userId" :label="t.realName" :value="t.userId" />
          </el-select>
        </el-form-item>
        <!-- 学期选择项 -->
        <el-form-item label="学期" required>
          <el-select v-model="editForm.semesterId" placeholder="选择学期" filterable style="width: 100%">
            <el-option v-for="s in semesterOptions" :key="s.id" :label="s.semesterName" :value="s.id" />
          </el-select>
        </el-form-item>
        <!-- 关联班级多选项 -->
        <el-form-item label="关联班级">
          <el-select
            v-model="editForm.classIds"
            placeholder="选择关联班级（可多选）"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            style="width: 100%"
          >
            <el-option v-for="c in classOptions" :key="c.classId" :label="c.className" :value="c.classId" />
          </el-select>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 从 vue 引入：ref、reactive、onMounted
import { ref, reactive, onMounted } from 'vue'
// 引入 Element Plus 消息提示与确认框组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入课程相关接口：获取列表、新增、修改、删除；以及教师、学期、班级列表接口
import {
  getCourseList, addCourse, updateCourse, deleteCourse,
  getUserList, getSemesterList, getClassList
} from '@/api'

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
// 弹窗显示状态
const dialogVisible = ref(false)

// 教师选项列表
const teacherOptions = ref([])
// 学期选项列表
const semesterOptions = ref([])
// 班级选项列表
const classOptions = ref([])

// 搜索表单数据
const searchForm = reactive({ courseName: '', semesterId: null })

// 编辑表单数据
const editForm = reactive({
  id: null,
  courseName: '',
  courseCode: '',
  credit: 2,
  hours: 32,
  teacherId: null,
  semesterId: null,
  classIds: []
})

// 提取班级名称列表，兼容数组与逗号分隔字符串两种格式
const classNames = (row) => {
  const list = row.classNames || row.classNameList || row.classes || []
  if (Array.isArray(list) && list.length) {
    return list.map(c => (typeof c === 'string' ? c : (c.className || c.name || ''))).filter(Boolean)
  }
  // 逗号分隔字符串
  if (typeof row.classNames === 'string' && row.classNames) {
    return row.classNames.split(',').map(s => s.trim()).filter(Boolean)
  }
  return []
}

// 加载课程列表数据（带分页与筛选条件）
const loadData = async () => {
  loading.value = true
  try {
    const res = await getCourseList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      courseName: searchForm.courseName || undefined,
      semesterId: searchForm.semesterId || undefined
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载教师选项列表（仅教师角色）
const loadTeachers = async () => {
  try {
    const res = await getUserList({ role: 'TEACHER', pageSize: 1000 })
    teacherOptions.value = res.data.list || res.data || []
  } catch (error) { console.error(error) }
}

// 加载学期选项列表
const loadSemesters = async () => {
  try {
    const res = await getSemesterList()
    semesterOptions.value = res.data || []
  } catch (error) { console.error(error) }
}

// 加载班级选项列表
const loadClasses = async () => {
  try {
    const res = await getClassList({ pageSize: 1000 })
    classOptions.value = res.data.list || res.data || []
  } catch (error) { console.error(error) }
}

// 打开新增/编辑弹窗；传 row 为编辑，传 null 为新增
const openDialog = (row) => {
  if (row) {
    editForm.id = row.id ?? row.courseId
    editForm.courseName = row.courseName || ''
    editForm.courseCode = row.courseCode || ''
    editForm.credit = row.credit ?? 2
    editForm.hours = row.hours ?? 32
    editForm.teacherId = row.teacherId ?? row.teacherUserId ?? null
    editForm.semesterId = row.semesterId ?? null
    // 提取班级 ID 列表
    const list = row.classIds || row.classIdList || []
    if (Array.isArray(list) && list.length) {
      editForm.classIds = list.map(c => (typeof c === 'object' ? (c.classId ?? c.id) : c))
    } else if (row.classes && Array.isArray(row.classes)) {
      editForm.classIds = row.classes.map(c => (typeof c === 'object' ? (c.classId ?? c.id) : c))
    } else {
      editForm.classIds = []
    }
  } else {
    Object.assign(editForm, {
      id: null, courseName: '', courseCode: '', credit: 2, hours: 32,
      teacherId: null, semesterId: null, classIds: []
    })
  }
  dialogVisible.value = true
}

// 保存：校验必填项后，有 id 则更新，否则新增
const handleSave = async () => {
  if (!editForm.courseName.trim()) {
    ElMessage.warning('课程名称不能为空')
    return
  }
  if (!editForm.courseCode.trim()) {
    ElMessage.warning('课程代码不能为空')
    return
  }
  if (!editForm.teacherId) {
    ElMessage.warning('请选择任课教师')
    return
  }
  if (!editForm.semesterId) {
    ElMessage.warning('请选择学期')
    return
  }
  try {
    const payload = {
      courseName: editForm.courseName,
      courseCode: editForm.courseCode,
      credit: editForm.credit,
      hours: editForm.hours,
      teacherId: editForm.teacherId,
      semesterId: editForm.semesterId,
      classIds: editForm.classIds
    }
    if (editForm.id) {
      await updateCourse({ id: editForm.id, ...payload })
      ElMessage.success('修改成功')
    } else {
      await addCourse(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 删除课程：确认后调用删除接口
const handleDelete = async (row) => {
  const id = row.id ?? row.courseId
  try {
    await ElMessageBox.confirm(`确定要删除课程"${row.courseName}"吗？`, '提示', { type: 'warning' })
    await deleteCourse(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

// 组件挂载时加载课程列表、教师、学期、班级选项
onMounted(() => {
  loadData()
  loadTeachers()
  loadSemesters()
  loadClasses()
})
</script>

<style scoped>
/* 页面容器 */
.course-manage {
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

/* 新增按钮：蓝色主题 */
.btn-add {
  background: #3b82f6 !important;
  border-color: #3b82f6 !important;
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

/* 弹窗内输入框聚焦/悬浮颜色 */
.green-dialog :deep(.el-input-number .el-input__wrapper.is-focus),
.green-dialog :deep(.el-input__wrapper.is-focus),
.green-dialog :deep(.el-select .el-input__wrapper.is-focus),
.green-dialog :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #1d4ed8 inset !important;
}
</style>
