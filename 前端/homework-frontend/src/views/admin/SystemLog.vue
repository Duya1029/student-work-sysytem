<template>
  <!-- 系统日志页面容器 -->
  <div class="system-log">
    <!-- 页面卡片容器 -->
    <el-card class="page-card">
      <!-- 页面标题 -->
      <div class="page-title">
        <span class="title-text">系统日志</span>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <!-- 用户名筛选输入框 -->
        <el-input
          v-model="searchForm.username"
          placeholder="用户名筛选"
          style="width: 180px"
          clearable
          @keyup.enter="loadData"
        />
        <!-- 日志类型筛选下拉框 -->
        <el-select v-model="searchForm.type" placeholder="日志类型筛选" clearable style="width: 160px">
          <el-option label="登录" value="LOGIN" />
          <el-option label="登出" value="LOGOUT" />
          <el-option label="新增" value="INSERT" />
          <el-option label="修改" value="UPDATE" />
          <el-option label="删除" value="DELETE" />
          <el-option label="查询" value="SELECT" />
          <el-option label="其他" value="OTHER" />
        </el-select>
        <!-- 查询按钮 -->
        <el-button type="success" @click="loadData">查询</el-button>
        <!-- 重置按钮 -->
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border class="green-table" style="margin-top: 15px" v-loading="loading">
        <!-- 序号列 -->
        <el-table-column type="index" label="序号" width="60" align="center" />
        <!-- 时间列，prop 对应数据字段 operateTime，自定义模板兼容多字段 -->
        <el-table-column prop="operateTime" label="时间" width="170">
          <template #default="{ row }">{{ formatDate(row.operateTime || row.createTime || row.time) }}</template>
        </el-table-column>
        <!-- 用户列，prop 对应数据字段 username，自定义模板兼容多字段 -->
        <el-table-column prop="username" label="用户" width="120">
          <template #default="{ row }">{{ row.username || row.userName || row.realName || '-' }}</template>
        </el-table-column>
        <!-- 操作列，prop 对应数据字段 operation，超出显示 tooltip -->
        <el-table-column prop="operation" label="操作" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ row.operation || row.description || row.action || '-' }}</template>
        </el-table-column>
        <!-- 方法列，prop 对应数据字段 method，超出显示 tooltip -->
        <el-table-column prop="method" label="方法" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">{{ row.method || row.requestMethod || '-' }}</template>
        </el-table-column>
        <!-- IP 列，prop 对应数据字段 ip，自定义模板兼容多字段 -->
        <el-table-column prop="ip" label="IP" width="140">
          <template #default="{ row }">{{ row.ip || row.ipAddress || '-' }}</template>
        </el-table-column>
        <!-- 类型列，prop 对应数据字段 type，自定义模板显示标签 -->
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)" effect="dark" size="small">{{ typeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <div v-if="!loading && tableData.length === 0" class="empty-tip">暂无日志数据</div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        class="green-pagination"
        style="margin-top: 15px; justify-content: flex-end"
        @change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
// 从 vue 引入：ref、reactive、onMounted
import { ref, reactive, onMounted } from 'vue'
// 引入日志列表接口
import { getLogList } from '@/api'

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

// 搜索表单数据
const searchForm = reactive({ username: '', type: '' })

// 格式化日期字符串，将 T 替换为空格并截取到秒
const formatDate = (dateStr) => dateStr ? String(dateStr).replace('T', ' ').substring(0, 19) : '-'

// 日志类型编码转中文文本
const typeText = (type) => ({
  LOGIN: '登录',
  LOGOUT: '登出',
  INSERT: '新增',
  UPDATE: '修改',
  DELETE: '删除',
  SELECT: '查询',
  OTHER: '其他'
}[type] || (type || '-'))

// 日志类型编码转标签类型（success/info/warning/danger）
const typeTagType = (type) => ({
  LOGIN: 'success',
  LOGOUT: 'info',
  INSERT: 'success',
  UPDATE: 'warning',
  DELETE: 'danger',
  SELECT: 'info',
  OTHER: 'info'
}[type] || 'info')

// 加载日志列表数据（带分页与筛选条件），兼容数组与分页对象两种返回结构
const loadData = async () => {
  loading.value = true
  try {
    const res = await getLogList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      username: searchForm.username || undefined,
      type: searchForm.type || undefined
    })
    // 兼容 list/total 或直接数组两种返回结构
    if (Array.isArray(res.data)) {
      tableData.value = res.data
      total.value = res.data.length
    } else {
      tableData.value = res.data?.list || res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 重置搜索条件并回到第一页
const handleReset = () => {
  searchForm.username = ''
  searchForm.type = ''
  pageNum.value = 1
  loadData()
}

// 组件挂载时加载日志列表
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 页面容器 */
.system-log {
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

/* 空数据提示 */
.empty-tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 30px 0;
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
</style>
