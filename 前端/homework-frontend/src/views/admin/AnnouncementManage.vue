<template>
  <!-- 公告管理页面容器 -->
  <div class="announcement-manage">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">公告管理</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 标题搜索输入框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="标题搜索"
          style="width: 220px"
          clearable
          @keyup.enter="loadData"
          @clear="loadData"
        />
        <!-- 查询按钮 -->
        <el-button type="success" @click="loadData">查询</el-button>
        <!-- 发布公告按钮 -->
        <el-button type="success" class="btn-add" @click="openDialog(null)">发布公告</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="filteredList" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 公告标题列，prop 对应数据字段 title -->
        <el-table-column prop="title" label="公告标题" min-width="180" show-overflow-tooltip />
        <!-- 公告内容列，prop 对应数据字段 content -->
        <el-table-column prop="content" label="公告内容" min-width="260" show-overflow-tooltip />
        <!-- 发布人列，prop 对应数据字段 publisherName，自定义模板兼容多字段 -->
        <el-table-column prop="publisherName" label="发布人" width="110" align="center">
          <template #default="{ row }">{{ row.publisherName || row.publisher || '-' }}</template>
        </el-table-column>
        <!-- 发布时间列，prop 对应数据字段 createTime，自定义模板格式化 -->
        <el-table-column prop="createTime" label="发布时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <!-- 操作列：编辑、删除 -->
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" type="success" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <div v-if="!loading && filteredList.length === 0" class="empty-tip">暂无公告数据</div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editForm.id ? '编辑公告' : '发布公告'"
      width="600px"
      class="green-dialog"
    >
      <!-- 表单 -->
      <el-form :model="editForm" label-width="80px">
        <!-- 标题输入项 -->
        <el-form-item label="标题" required>
          <el-input v-model="editForm.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <!-- 内容多行文本输入项 -->
        <el-form-item label="内容" required>
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
            maxlength="1000"
            show-word-limit
          />
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
// 引入公告相关接口：获取列表、新增、修改、删除
import { getAnnouncementList, addAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api'

// 表格加载状态
const loading = ref(false)
// 表格数据
const tableData = ref([])
// 弹窗显示状态
const dialogVisible = ref(false)
// 搜索关键字
const searchKeyword = ref('')

// 编辑表单数据
const editForm = reactive({ id: null, title: '', content: '' })

// 格式化日期字符串，将 T 替换为空格并截取到秒
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 按标题关键字过滤公告列表
const filteredList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return tableData.value
  return tableData.value.filter(item => (item.title || '').toLowerCase().includes(kw))
})

// 加载公告列表数据
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

// 打开新增/编辑弹窗；传 row 为编辑，传 null 为新增
const openDialog = (row) => {
  if (row) {
    editForm.id = row.id ?? row.announcementId
    editForm.title = row.title || ''
    editForm.content = row.content || ''
  } else {
    editForm.id = null
    editForm.title = ''
    editForm.content = ''
  }
  dialogVisible.value = true
}

// 保存：校验标题与内容非空后，有 id 则更新，否则新增
const handleSave = async () => {
  if (!editForm.title.trim()) {
    ElMessage.warning('公告标题不能为空')
    return
  }
  if (!editForm.content.trim()) {
    ElMessage.warning('公告内容不能为空')
    return
  }
  try {
    if (editForm.id) {
      await updateAnnouncement({ id: editForm.id, title: editForm.title, content: editForm.content })
      ElMessage.success('修改成功')
    } else {
      await addAnnouncement({ title: editForm.title, content: editForm.content })
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 删除公告：确认后调用删除接口
const handleDelete = async (row) => {
  const id = row.id ?? row.announcementId
  try {
    await ElMessageBox.confirm(`确定要删除公告"${row.title}"吗？`, '提示', { type: 'warning' })
    await deleteAnnouncement(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

// 组件挂载时加载公告列表
onMounted(() => {
  loadData()
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
</style>
