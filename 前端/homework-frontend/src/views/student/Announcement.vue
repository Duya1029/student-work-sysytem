<template>
  <!-- 学生通知公告页面根容器 -->
  <div class="student-announcement">
    <!-- 通知公告卡片 -->
    <el-card class="ann-card" shadow="never">
      <template #header>
        <!-- 卡片标题栏 -->
        <div class="card-header">
          <span class="header-title">通知公告</span>
          <span class="header-sub">查看学校与教师发布的通知</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="filter-bar">
        <!-- 标题/内容搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="标题 / 内容搜索"
          clearable
          class="search-input"
        />
        <!-- 重置搜索按钮 -->
        <el-button class="btn-reset" @click="searchKeyword = ''">重置</el-button>
      </div>

      <!-- 公告列表 -->
      <div class="ann-list" v-loading="loading">
        <!-- 单个公告项 -->
        <div
          v-for="(item, idx) in sortedList"
          :key="idx"
          class="ann-item"
          :class="{ 'is-pinned': isPinned(item), 'is-expanded': expandedId === getKey(item) }"
        >
          <!-- 公告头部（点击展开/收起） -->
          <div class="ann-item-header" @click="toggleExpand(item)">
            <div class="ann-title-wrap">
              <!-- 置顶标签 -->
              <el-tag v-if="isPinned(item)" size="small" effect="dark" round class="pin-tag">置顶</el-tag>
              <!-- 公告标题 -->
              <span class="ann-title">{{ item.title || '无标题' }}</span>
              <!-- 展开/收起图标 -->
              <el-icon class="expand-icon">
                <ArrowDown v-if="expandedId !== getKey(item)" />
                <ArrowUp v-else />
              </el-icon>
            </div>
            <!-- 公告元信息：发布人、发布时间 -->
            <div class="ann-meta">
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ item.publisherName || item.publisher || '系统' }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDate(item.createTime) }}
              </span>
            </div>
          </div>

          <!-- 展开内容（带动画） -->
          <transition name="ann-slide">
            <div v-if="expandedId === getKey(item)" class="ann-content">
              <div class="content-text">{{ item.content || '暂无内容' }}</div>
            </div>
          </transition>
        </div>

        <!-- 暂无公告 -->
        <el-empty v-if="!loading && sortedList.length === 0" description="暂无公告" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowDown, ArrowUp, User, Clock } from '@element-plus/icons-vue'
import { getAnnouncementList } from '@/api'

// 列表加载状态
const loading = ref(false)
// 公告列表数据
const tableData = ref([])
// 搜索关键词
const searchKeyword = ref('')
// 当前展开的公告ID
const expandedId = ref(null)

// 格式化日期字符串
const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 19) : '-'

// 公告唯一标识，兼容 id / announcementId
const getKey = (item) => item.id ?? item.announcementId ?? (item.title + (item.createTime || ''))

// 判断是否置顶，兼容多种字段
const isPinned = (item) => {
  if (item.isTop === true || item.top === true || item.pinned === true) return true
  if (item.isTop === 1 || item.top === 1) return true
  return false
}

// 置顶在前，相同情况按时间倒序
const sortedList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  let list = tableData.value.slice()
  if (kw) {
    list = list.filter(item => {
      const title = (item.title || '').toLowerCase()
      const content = (item.content || '').toLowerCase()
      return title.includes(kw) || content.includes(kw)
    })
  }
  return list.sort((a, b) => {
    const aTop = isPinned(a) ? 1 : 0
    const bTop = isPinned(b) ? 1 : 0
    if (aTop !== bTop) return bTop - aTop
    const aTime = new Date(a.createTime || 0).getTime()
    const bTime = new Date(b.createTime || 0).getTime()
    return bTime - aTime
  })
})

// 切换公告展开/收起状态
const toggleExpand = (item) => {
  const key = getKey(item)
  if (expandedId.value === key) {
    expandedId.value = null
  } else {
    expandedId.value = key
  }
}

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

// 组件挂载时加载公告数据
onMounted(() => { loadData() })
</script>

<style scoped>
/* 学生通知公告页面容器样式 */
.student-announcement {
  padding: 20px;
  background: #f0f6ff;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 通知公告卡片样式 */
.ann-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.12);
  overflow: hidden;
}

/* 卡片头部渐变背景 */
.ann-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  padding: 16px 20px;
  border-bottom: none;
}

/* 卡片标题栏布局 */
.card-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

/* 标题文字样式 */
.header-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 副标题样式 */
.header-sub {
  color: rgba(255, 255, 255, 0.85);
  font-size: 13px;
}

/* 卡片内容区内边距 */
.ann-card :deep(.el-card__body) {
  padding: 20px;
}

/* 搜索栏 */
.filter-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

/* 搜索输入框宽度 */
.search-input {
  width: 260px;
}

/* 搜索输入框圆角 */
.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 搜索输入框悬浮/聚焦阴影 */
.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1d4ed8 inset;
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

/* 公告列表 */
.ann-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 公告项样式 */
.ann-item {
  background: #ffffff;
  border: 1px solid #e0ebe5;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.25s ease;
}

/* 公告项悬浮效果 */
.ann-item:hover {
  box-shadow: 0 4px 12px rgba(29, 78, 216, 0.12);
  border-color: #b7eb8f;
}

/* 置顶公告项样式 */
.ann-item.is-pinned {
  border-color: #3b82f6;
  background: linear-gradient(180deg, #f6ffed 0%, #ffffff 30%);
}

/* 展开公告项样式 */
.ann-item.is-expanded {
  border-color: #1d4ed8;
}

/* 公告项头部 */
.ann-item-header {
  padding: 14px 18px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 公告标题包装 */
.ann-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 置顶标签样式 */
.pin-tag {
  background: #f5222d !important;
  border-color: #f5222d !important;
  color: #fff !important;
  font-weight: 600;
  flex-shrink: 0;
}

/* 公告标题样式 */
.ann-title {
  flex: 1;
  font-size: 15px;
  font-weight: 600;
  color: #1d4ed8;
  word-break: break-all;
}

/* 置顶公告标题颜色 */
.ann-item.is-pinned .ann-title {
  color: #1d4ed8;
}

/* 展开/收起图标样式 */
.expand-icon {
  color: #8c9c93;
  font-size: 16px;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

/* 公告元信息布局 */
.ann-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
  font-size: 13px;
  color: #8c9c93;
}

/* 单个元信息项 */
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 元信息图标大小 */
.meta-item .el-icon {
  font-size: 14px;
}

/* 展开内容 */
.ann-content {
  padding: 0 18px 16px;
  border-top: 1px dashed #d6e9dd;
}

/* 内容文字样式 */
.content-text {
  margin-top: 12px;
  padding: 12px 14px;
  background: #f8fcfa;
  border: 1px solid #d6e9dd;
  border-radius: 8px;
  color: #303133;
  line-height: 1.8;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 展开过渡动画 */
.ann-slide-enter-active,
.ann-slide-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}

/* 展开动画起始/结束状态 */
.ann-slide-enter-from,
.ann-slide-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
