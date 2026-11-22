<template>
  <!-- 班级管理页面容器 -->
  <div class="class-manage">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">班级管理</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 班级名称搜索输入框 -->
        <el-input
          v-model="searchForm.className"
          placeholder="班级名称搜索"
          style="width: 200px"
          clearable
          @keyup.enter="loadData"
        />
        <!-- 查询按钮 -->
        <el-button type="success" @click="loadData">查询</el-button>
        <!-- 新增班级按钮 -->
        <el-button type="success" class="btn-add" @click="openDialog(null)">新增班级</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 班级名称列，prop 对应数据字段 className -->
        <el-table-column prop="className" label="班级名称" />
        <!-- 学生人数列，prop 对应数据字段 studentCount -->
        <el-table-column prop="studentCount" label="学生人数" width="100" align="center" />
        <!-- 创建时间列，prop 对应数据字段 createTime，自定义模板格式化 -->
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
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
    <el-dialog v-model="dialogVisible" :title="editForm.classId ? '编辑班级' : '新增班级'" width="450px" class="green-dialog">
      <!-- 表单 -->
      <el-form :model="editForm" label-width="80px">
        <!-- 班级名称输入项 -->
        <el-form-item label="班级名称" required>
          <el-input v-model="editForm.className" placeholder="请输入班级名称" />
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
// 引入班级相关接口：获取列表、新增、修改、删除
import { getClassList, addClass, updateClass, deleteClass } from '@/api'

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

// 搜索表单数据
const searchForm = reactive({ className: '' })
// 编辑表单数据
const editForm = reactive({ classId: null, className: '' })

// 格式化日期字符串，将 T 替换为空格并截取到秒
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

// 加载班级列表数据（带分页与搜索条件）
const loadData = async () => {
  loading.value = true
  try {
    const res = await getClassList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      className: searchForm.className || undefined
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 打开新增/编辑弹窗；传 row 为编辑，传 null 为新增
const openDialog = (row) => {
  if (row) {
    editForm.classId = row.classId
    editForm.className = row.className
  } else {
    editForm.classId = null
    editForm.className = ''
  }
  dialogVisible.value = true
}

// 保存：校验名称非空后，有 classId 则更新，否则新增
const handleSave = async () => {
  if (!editForm.className.trim()) {
    ElMessage.warning('班级名称不能为空')
    return
  }
  try {
    if (editForm.classId) {
      await updateClass({ classId: editForm.classId, className: editForm.className })
      ElMessage.success('修改成功')
    } else {
      await addClass({ className: editForm.className })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 删除班级：确认后调用删除接口
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除班级"${row.className}"吗？`, '提示', { type: 'warning' })
    await deleteClass(row.classId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

// 组件挂载时加载班级列表
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 页面容器 */
.class-manage {
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
