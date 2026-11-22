<template>
  <!-- 用户管理页面容器 -->
  <div class="user-manage">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">用户管理</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 角色筛选下拉框 -->
        <el-select v-model="searchForm.role" placeholder="角色筛选" clearable style="width: 120px">
          <el-option label="管理员" value="ADMIN" />
          <el-option label="教师" value="TEACHER" />
          <el-option label="学生" value="STUDENT" />
        </el-select>
        <!-- 姓名搜索输入框 -->
        <el-input v-model="searchForm.realName" placeholder="姓名搜索" style="width: 160px" clearable @keyup.enter="loadData" />
        <!-- 班级筛选下拉框 -->
        <el-select v-model="searchForm.classId" placeholder="班级筛选" clearable filterable style="width: 180px">
          <el-option v-for="c in classOptions" :key="c.classId" :label="c.className" :value="c.classId" />
        </el-select>
        <!-- 查询按钮 -->
        <el-button type="success" @click="loadData">查询</el-button>
        <!-- 新增账号按钮 -->
        <el-button type="success" class="btn-add" @click="openDialog(null)">新增账号</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 用户名列，prop 对应数据字段 username -->
        <el-table-column prop="username" label="用户名" width="120" />
        <!-- 姓名列，prop 对应数据字段 realName -->
        <el-table-column prop="realName" label="姓名" width="100" />
        <!-- 角色列，prop 对应数据字段 role，使用自定义模板显示标签 -->
        <el-table-column prop="role" label="角色" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)" :class="'role-tag-' + row.role.toLowerCase()">{{ roleText(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <!-- 班级列，prop 对应数据字段 className -->
        <el-table-column prop="className" label="班级" width="180" />
        <!-- 学号列，prop 对应数据字段 studentNo -->
        <el-table-column prop="studentNo" label="学号" width="120" />
        <!-- 创建时间列，prop 对应数据字段 createTime，自定义模板格式化日期 -->
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <!-- 操作列：编辑、重置密码、删除 -->
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" type="success" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)" :disabled="row.username === 'admin'">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="editForm.userId ? '编辑账号' : '新增账号'" width="500px" class="green-dialog">
      <!-- 表单 -->
      <el-form :model="editForm" label-width="80px">
        <!-- 用户名输入项，编辑时禁用 -->
        <el-form-item label="用户名" required>
          <el-input v-model="editForm.username" placeholder="登录用户名" :disabled="!!editForm.userId" />
        </el-form-item>
        <!-- 密码输入项，新增时必填，编辑时留空表示不修改 -->
        <el-form-item label="密码" :required="!editForm.userId">
          <el-input v-model="editForm.password" type="password" show-password :placeholder="editForm.userId ? '不修改请留空' : '请输入密码'" />
        </el-form-item>
        <!-- 姓名输入项 -->
        <el-form-item label="姓名" required>
          <el-input v-model="editForm.realName" placeholder="真实姓名" />
        </el-form-item>
        <!-- 角色选择项 -->
        <el-form-item label="角色" required>
          <el-select v-model="editForm.role" placeholder="选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <!-- 班级选择项，仅学生角色显示 -->
        <el-form-item label="班级" v-if="editForm.role === 'STUDENT'">
          <el-select v-model="editForm.classId" placeholder="选择班级" filterable style="width: 100%">
            <el-option v-for="c in classOptions" :key="c.classId" :label="c.className" :value="c.classId" />
          </el-select>
        </el-form-item>
        <!-- 学号输入项，仅学生角色显示 -->
        <el-form-item label="学号" v-if="editForm.role === 'STUDENT'">
          <el-input v-model="editForm.studentNo" placeholder="学号" />
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
// 引入用户相关接口：获取用户列表、新增、修改、删除、重置密码、获取班级列表
import { getUserList, addUser, updateUser, deleteUser, resetPassword, getAllClasses } from '@/api'

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
// 班级选项列表
const classOptions = ref([])

// 搜索表单数据
const searchForm = reactive({ role: '', realName: '', classId: null })
// 编辑表单数据
const editForm = reactive({
  userId: null, username: '', password: '', realName: '',
  role: '', classId: null, studentNo: ''
})

// 格式化日期字符串，将 T 替换为空格并截取到秒
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 角色编码转中文文本
const roleText = (role) => ({ ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }[role] || '')
// 角色编码转标签类型（danger/warning/success）
const roleTagType = (role) => ({ ADMIN: 'danger', TEACHER: 'warning', STUDENT: 'success' }[role] || '')

// 加载用户列表数据（带分页与筛选条件）
const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      role: searchForm.role || undefined,
      realName: searchForm.realName || undefined,
      classId: searchForm.classId || undefined
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载班级选项列表
const loadClasses = async () => {
  try {
    const res = await getAllClasses()
    classOptions.value = res.data
  } catch (error) { console.error(error) }
}

// 打开新增/编辑弹窗；传 row 为编辑，传 null 为新增
const openDialog = (row) => {
  if (row) {
    Object.assign(editForm, { ...row, password: '' })
  } else {
    Object.assign(editForm, { userId: null, username: '', password: '', realName: '', role: '', classId: null, studentNo: '' })
  }
  dialogVisible.value = true
}

// 保存：有 userId 则更新，否则新增
const handleSave = async () => {
  try {
    if (editForm.userId) {
      await updateUser(editForm)
      ElMessage.success('修改成功')
    } else {
      await addUser(editForm)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

// 删除用户：确认后调用删除接口
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除账号"${row.realName}"吗？`, '提示', { type: 'warning' })
    await deleteUser(row.userId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) { if (error !== 'cancel') console.error(error) }
}

// 重置密码：弹出输入框输入新密码后调用接口
const handleResetPwd = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt(`重置"${row.realName}"的密码`, '重置密码', {
      inputPlaceholder: '请输入新密码',
      inputValidator: (val) => val && val.trim() ? true : '密码不能为空'
    })
    await resetPassword({ userId: row.userId, newPassword: value })
    ElMessage.success('重置成功')
  } catch (error) { if (error !== 'cancel') console.error(error) }
}

// 组件挂载时加载用户列表和班级列表
onMounted(() => {
  loadData()
  loadClasses()
})
</script>

<style scoped>
/* 页面容器 */
.user-manage {
  padding: 10px;
}

/* 卡片样式 */
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

/* 表格边框色调 */
.green-table :deep(.el-table border) {
  border-color: #d6e8df;
}

/* 管理员角色标签颜色 */
.role-tag-admin {
  background: #f56c6c !important;
  border-color: #f56c6c !important;
  color: #fff !important;
}

/* 教师角色标签颜色 */
.role-tag-teacher {
  background: #409eff !important;
  border-color: #409eff !important;
  color: #fff !important;
}

/* 学生角色标签颜色 */
.role-tag-student {
  background: #3b82f6 !important;
  border-color: #3b82f6 !important;
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
