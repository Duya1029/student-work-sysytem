<template>
  <!-- 学期管理页面容器 -->
  <div class="semester-manage">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">学期管理</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 学期名称搜索输入框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="学期名称搜索"
          style="width: 220px"
          clearable
        />
        <!-- 查询按钮 -->
        <el-button type="success" @click="searchKeyword = searchKeyword">查询</el-button>
        <!-- 新增学期按钮 -->
        <el-button type="success" class="btn-add" @click="openDialog(null)">新增学期</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="filteredList" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 学期名称列，prop 对应数据字段 semesterName -->
        <el-table-column prop="semesterName" label="学期名称" min-width="180" show-overflow-tooltip />
        <!-- 开始日期列，prop 对应数据字段 startDate，自定义模板格式化 -->
        <el-table-column prop="startDate" label="开始日期" width="130" align="center">
          <template #default="{ row }">{{ formatDate(row.startDate) }}</template>
        </el-table-column>
        <!-- 结束日期列，prop 对应数据字段 endDate，自定义模板格式化 -->
        <el-table-column prop="endDate" label="结束日期" width="130" align="center">
          <template #default="{ row }">{{ formatDate(row.endDate) }}</template>
        </el-table-column>
        <!-- 状态列，prop 对应数据字段 status，自定义模板显示标签 -->
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" effect="dark" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <!-- 当前学期标记列 -->
        <el-table-column label="当前学期" width="110" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isCurrent" type="success" effect="dark" round size="small">当前</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <!-- 操作列：编辑、设为当前、删除 -->
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <el-button size="small" type="success" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" link @click="handleSetCurrent(row)" :disabled="row.isCurrent">设为当前</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <div v-if="!loading && filteredList.length === 0" class="empty-tip">暂无学期数据</div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑学期' : '新增学期'" width="500px" class="green-dialog">
      <!-- 表单 -->
      <el-form :model="editForm" label-width="90px">
        <!-- 学期名称输入项 -->
        <el-form-item label="学期名称" required>
          <el-input v-model="editForm.semesterName" placeholder="如：2025-2026学年第一学期" maxlength="50" show-word-limit />
        </el-form-item>
        <!-- 开始日期选择项 -->
        <el-form-item label="开始日期" required>
          <el-date-picker
            v-model="editForm.startDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <!-- 结束日期选择项 -->
        <el-form-item label="结束日期" required>
          <el-date-picker
            v-model="editForm.endDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <!-- 状态选择项 -->
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="选择状态" style="width: 100%">
            <el-option label="未开始" value="NOT_STARTED" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已结束" value="ENDED" />
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
// 从 vue 引入：ref、reactive、computed、onMounted
import { ref, reactive, computed, onMounted } from 'vue'
// 引入 Element Plus 消息提示与确认框组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入学期相关接口：获取列表、新增、修改、删除、设为当前学期
import {
  getSemesterList, addSemester, updateSemester, deleteSemester, setCurrentSemester
} from '@/api'

// 表格加载状态
const loading = ref(false)
// 表格数据
const tableData = ref([])
// 弹窗显示状态
const dialogVisible = ref(false)
// 搜索关键字
const searchKeyword = ref('')

// 编辑表单数据
const editForm = reactive({
  id: null,
  semesterName: '',
  startDate: '',
  endDate: '',
  status: 'NOT_STARTED'
})

// 格式化日期字符串，截取到日（YYYY-MM-DD）
const formatDate = (dateStr) => dateStr ? String(dateStr).replace('T', ' ').substring(0, 10) : '-'

// 状态编码转中文文本
const statusText = (status) => ({
  NOT_STARTED: '未开始',
  IN_PROGRESS: '进行中',
  ENDED: '已结束'
}[status] || (status || '-'))

// 状态编码转标签类型（info/success/warning）
const statusTagType = (status) => ({
  NOT_STARTED: 'info',
  IN_PROGRESS: 'success',
  ENDED: 'warning'
}[status] || 'info')

// 按学期名称关键字过滤列表
const filteredList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return tableData.value
  return tableData.value.filter(item => (item.semesterName || '').toLowerCase().includes(kw))
})

// 加载学期列表数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getSemesterList()
    const list = Array.isArray(res.data) ? res.data : (res.data?.list || [])
    tableData.value = list
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 打开新增/编辑弹窗；传 row 为编辑，传 null 为新增
const openDialog = (row) => {
  if (row) {
    editForm.id = row.id ?? row.semesterId
    editForm.semesterName = row.semesterName || ''
    editForm.startDate = row.startDate ? String(row.startDate).substring(0, 10) : ''
    editForm.endDate = row.endDate ? String(row.endDate).substring(0, 10) : ''
    editForm.status = row.status || 'NOT_STARTED'
  } else {
    Object.assign(editForm, {
      id: null, semesterName: '', startDate: '', endDate: '', status: 'NOT_STARTED'
    })
  }
  dialogVisible.value = true
}

// 保存：校验名称与日期，且开始日期不晚于结束日期；有 id 则更新，否则新增
const handleSave = async () => {
  if (!editForm.semesterName.trim()) {
    ElMessage.warning('学期名称不能为空')
    return
  }
  if (!editForm.startDate) {
    ElMessage.warning('请选择开始日期')
    return
  }
  if (!editForm.endDate) {
    ElMessage.warning('请选择结束日期')
    return
  }
  if (editForm.startDate > editForm.endDate) {
    ElMessage.warning('开始日期不能晚于结束日期')
    return
  }
  try {
    const payload = {
      semesterName: editForm.semesterName,
      startDate: editForm.startDate,
      endDate: editForm.endDate,
      status: editForm.status
    }
    if (editForm.id) {
      await updateSemester({ id: editForm.id, ...payload })
      ElMessage.success('修改成功')
    } else {
      await addSemester(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 删除学期：确认后调用删除接口
const handleDelete = async (row) => {
  const id = row.id ?? row.semesterId
  try {
    await ElMessageBox.confirm(`确定要删除学期"${row.semesterName}"吗？`, '提示', { type: 'warning' })
    await deleteSemester(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

// 设为当前学期：确认后调用设置接口
const handleSetCurrent = async (row) => {
  const id = row.id ?? row.semesterId
  try {
    await ElMessageBox.confirm(`确定要将"${row.semesterName}"设为当前学期吗？`, '提示', { type: 'warning' })
    await setCurrentSemester(id)
    ElMessage.success('设置成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

// 组件挂载时加载学期列表
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 页面容器 */
.semester-manage {
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

/* 分页器：当前页蓝色背景（备用） */
.green-pagination :deep(.el-pagination .el-pager li.is-active) {
  background-color: #1d4ed8 !important;
  color: #fff !important;
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

/* 弹窗内日期/输入框聚焦/悬浮颜色 */
.green-dialog :deep(.el-input__wrapper.is-focus),
.green-dialog :deep(.el-select .el-input__wrapper.is-focus),
.green-dialog :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #1d4ed8 inset !important;
}
</style>
