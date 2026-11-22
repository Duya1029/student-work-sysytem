<template>
  <!-- 404 页面容器 -->
  <div class="not-found">
    <!-- 结果展示组件：显示 404 提示与返回首页按钮 -->
    <el-result icon="warning" title="404" sub-title="抱歉，您访问的页面不存在">
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
// 404 页面
</script>

<style scoped>
/* 404 页面容器：全屏居中 */
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
