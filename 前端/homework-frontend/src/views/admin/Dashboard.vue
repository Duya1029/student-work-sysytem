<template>
  <div class="admin-dashboard">
    <!-- 顶部 Hero -->
    <div class="hero">
      <div class="hero-content">
        <div class="hero-avatar">
          <el-icon :size="42"><DataAnalysis /></el-icon>
        </div>
        <div class="hero-text">
          <h2>数据仪表盘</h2>
          <p>实时洞察系统运行概况，把握教学管理全局</p>
        </div>
      </div>
      <div class="hero-deco deco-1"></div>
      <div class="hero-deco deco-2"></div>
      <div class="hero-deco deco-3"></div>
    </div>

    <!-- KPI 卡片 -->
    <el-row :gutter="20" class="kpi-row" v-loading="loading">
      <el-col :xs="12" :sm="6" v-for="(item, idx) in kpiCards" :key="idx">
        <div class="kpi-card" :style="{ '--accent': item.accent }">
          <div class="kpi-icon" :style="{ background: item.bgColor }">
            <el-icon :size="26" color="#ffffff"><component :is="item.icon" /></el-icon>
          </div>
          <div class="kpi-body">
            <div class="kpi-title">{{ item.title }}</div>
            <div class="kpi-value">{{ item.value }}</div>
            <div class="kpi-foot">
              <span class="kpi-unit">{{ item.unit }}</span>
              <span class="kpi-bar"><span class="kpi-bar-fill"></span></span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 柱状图：班级学生分布 -->
      <el-col :xs="24" :lg="12">
        <div class="panel">
          <div class="panel-title"><span class="panel-bar"></span>各班级学生人数分布</div>
          <div class="bar-chart">
            <div v-if="classDist.length === 0" class="empty-tip">暂无数据</div>
            <div v-for="(item, idx) in classDist" :key="idx" class="bar-item">
              <div class="bar-label" :title="item.name">{{ item.name }}</div>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: item.percent + '%' }">
                  <span v-if="item.percent > 15" class="bar-fill-text">{{ item.count }}人</span>
                </div>
                <span v-if="item.percent <= 15" class="bar-value">{{ item.count }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 环形图：作业完成率 -->
      <el-col :xs="24" :lg="12">
        <div class="panel">
          <div class="panel-title"><span class="panel-bar"></span>作业完成率统计</div>
          <div class="donut-wrap">
            <div class="donut" :style="donutStyle">
              <div class="donut-hole">
                <div class="donut-rate">{{ submitRate }}<span class="donut-unit">%</span></div>
                <div class="donut-label">整体完成率</div>
              </div>
            </div>
            <div class="donut-legend">
              <div class="legend-item">
                <span class="dot submitted"></span>
                <span class="legend-label">已提交</span>
                <span class="legend-value">{{ submittedCount }}</span>
              </div>
              <div class="legend-item">
                <span class="dot unsubmitted"></span>
                <span class="legend-label">未提交</span>
                <span class="legend-value">{{ unsubmittedCount }}</span>
              </div>
              <div class="legend-item total">
                <span class="legend-label">作业总数</span>
                <span class="legend-value">{{ submittedCount + unsubmittedCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 班级作业完成情况 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <div class="panel">
          <div class="panel-title"><span class="panel-bar"></span>班级作业完成情况</div>
          <div class="progress-list">
            <div v-if="classCompletion.length === 0" class="empty-tip">暂无数据</div>
            <div v-for="(item, idx) in classCompletion" :key="idx" class="progress-item">
              <div class="progress-label" :title="item.name">{{ item.name }}</div>
              <div class="progress-track">
                <div class="progress-fill" :style="{ width: item.rate + '%' }"></div>
              </div>
              <div class="progress-value">{{ item.rate }}%</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { DataAnalysis, User, UserFilled, School, Document } from '@element-plus/icons-vue'
import { getStatistics } from '@/api'

const loading = ref(false)
const stats = ref({})

const num = (v) => {
  const n = Number(v)
  return Number.isFinite(n) ? n : 0
}

const kpiCards = computed(() => [
  { title: '学生总数', value: num(stats.value.studentCount), unit: '人', icon: User, accent: '#1d4ed8', bgColor: 'linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%)' },
  { title: '教师总数', value: num(stats.value.teacherCount), unit: '人', icon: UserFilled, accent: '#1e3a8a', bgColor: 'linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 100%)' },
  { title: '班级总数', value: num(stats.value.classCount), unit: '个', icon: School, accent: '#2563eb', bgColor: 'linear-gradient(135deg, #2563eb 0%, #3b82f6 100%)' },
  { title: '作业总数', value: num(stats.value.homeworkCount), unit: '份', icon: Document, accent: '#1d4ed8', bgColor: 'linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%)' }
])

const classDist = computed(() => {
  const list = stats.value.classDistribution || stats.value.classList || stats.value.classes || []
  const arr = Array.isArray(list) ? list : []
  const max = arr.reduce((m, it) => Math.max(m, num(it.studentCount ?? it.count ?? it.value)), 0) || 1
  return arr.map(it => ({
    name: it.className ?? it.name ?? '-',
    count: num(it.studentCount ?? it.count ?? it.value),
    percent: Math.round((num(it.studentCount ?? it.count ?? it.value) / max) * 100)
  }))
})

const submittedCount = computed(() => num(stats.value.submittedCount ?? stats.value.submitted))
const unsubmittedCount = computed(() => num(stats.value.unsubmittedCount ?? stats.value.unsubmitted))
const totalCount = computed(() => {
  const t = num(stats.value.totalCount)
  return t || (submittedCount.value + unsubmittedCount.value)
})
const submitRate = computed(() => {
  const total = totalCount.value
  if (!total) return 0
  return Math.round((submittedCount.value / total) * 100)
})

const donutStyle = computed(() => ({
  background: `conic-gradient(#3b82f6 0 ${submitRate.value}%, #e3eee7 ${submitRate.value}% 100%)`
}))

const classCompletion = computed(() => {
  const list = stats.value.classCompletion || stats.value.classSubmission || []
  const arr = Array.isArray(list) ? list : []
  return arr.map(it => ({
    name: it.className ?? it.name ?? '-',
    rate: Math.min(100, Math.max(0, num(it.completionRate ?? it.rate ?? it.submitRate)))
  }))
})

const loadStats = async () => {
  loading.value = true
  try {
    const res = await getStatistics()
    stats.value = res.data || {}
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 10px;
}

.hero {
  position: relative;
  border-radius: 18px;
  overflow: hidden;
  margin-bottom: 22px;
  background: linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 55%, #3b82f6 100%);
  padding: 36px 34px;
  box-shadow: 0 10px 28px rgba(29, 78, 216, 0.28);
}

.hero-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 22px;
}

.hero-avatar {
  width: 78px;
  height: 78px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.35);
  flex-shrink: 0;
}

.hero-text h2 {
  margin: 0 0 8px;
  color: #fff;
  font-size: 26px;
  font-weight: 700;
}

.hero-text p {
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.hero-deco {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  z-index: 1;
}

.deco-1 { width: 220px; height: 220px; right: -60px; top: -70px; }
.deco-2 { width: 140px; height: 140px; right: 110px; bottom: -70px; background: rgba(255, 255, 255, 0.07); }
.deco-3 { width: 70px; height: 70px; right: 40px; top: 30px; background: rgba(255, 255, 255, 0.12); }

.kpi-row { margin-bottom: 22px; }

.kpi-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 14px rgba(29, 78, 216, 0.1);
  border-top: 3px solid var(--accent);
  transition: all 0.3s ease;
}

.kpi-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 14px 26px rgba(29, 78, 216, 0.22);
}

.kpi-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 6px 14px rgba(29, 78, 216, 0.25);
}

.kpi-body { flex: 1; min-width: 0; }
.kpi-title { font-size: 13px; color: #909399; margin-bottom: 4px; }
.kpi-value { font-size: 28px; font-weight: 700; color: #1d4ed8; line-height: 1.2; }
.kpi-foot { display: flex; align-items: center; gap: 8px; margin-top: 4px; }
.kpi-unit { font-size: 12px; color: #b0b8b5; }
.kpi-bar { flex: 1; height: 4px; background: #eaf3ec; border-radius: 2px; overflow: hidden; }
.kpi-bar-fill { display: block; width: 70%; height: 100%; background: linear-gradient(90deg, #1d4ed8, #3b82f6); border-radius: 2px; }

.panel {
  background: #fff;
  border-radius: 14px;
  padding: 22px;
  box-shadow: 0 4px 14px rgba(29, 78, 216, 0.1);
  margin-bottom: 20px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d4ed8;
  margin-bottom: 22px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-bar {
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 2px;
}

.chart-row { margin-bottom: 4px; }

.bar-chart { display: flex; flex-direction: column; gap: 14px; min-height: 200px; }
.bar-item { display: flex; align-items: center; gap: 12px; }
.bar-label { width: 90px; font-size: 13px; color: #5a6e6a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex-shrink: 0; }
.bar-track { flex: 1; height: 26px; background: #eaf3ec; border-radius: 13px; overflow: hidden; display: flex; align-items: center; position: relative; }
.bar-fill { height: 100%; background: linear-gradient(90deg, #1d4ed8 0%, #3b82f6 100%); border-radius: 13px; transition: width 0.7s ease; display: flex; align-items: center; justify-content: flex-end; padding-right: 10px; min-width: 4px; }
.bar-fill-text { color: #fff; font-size: 12px; font-weight: 600; }
.bar-value { position: absolute; right: 10px; font-size: 12px; font-weight: 600; color: #1d4ed8; }
.empty-tip { text-align: center; color: #909399; font-size: 13px; padding: 50px 0; }

.donut-wrap { display: flex; align-items: center; gap: 34px; flex-wrap: wrap; min-height: 220px; }
.donut { width: 200px; height: 200px; border-radius: 50%; flex-shrink: 0; display: flex; align-items: center; justify-content: center; transition: background 0.7s ease; box-shadow: 0 8px 22px rgba(29, 78, 216, 0.18); }
.donut-hole { width: 140px; height: 140px; border-radius: 50%; background: #fff; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.donut-rate { font-size: 34px; font-weight: 700; color: #1d4ed8; line-height: 1; }
.donut-unit { font-size: 16px; margin-left: 2px; }
.donut-label { font-size: 12px; color: #909399; margin-top: 6px; }
.donut-legend { display: flex; flex-direction: column; gap: 14px; min-width: 180px; }
.legend-item { display: flex; align-items: center; gap: 8px; font-size: 14px; }
.legend-item .dot { width: 12px; height: 12px; border-radius: 3px; flex-shrink: 0; }
.legend-item .dot.submitted { background: #3b82f6; }
.legend-item .dot.unsubmitted { background: #e3eee7; }
.legend-label { color: #5a6e6a; }
.legend-value { font-weight: 600; color: #1d4ed8; margin-left: auto; padding-left: 18px; }
.legend-item.total { margin-top: 6px; padding-top: 10px; border-top: 1px dashed #d6e8df; }

.progress-list { display: flex; flex-direction: column; gap: 12px; min-height: 80px; }
.progress-item { display: flex; align-items: center; gap: 12px; }
.progress-label { width: 100px; font-size: 13px; color: #5a6e6a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex-shrink: 0; }
.progress-track { flex: 1; height: 18px; background: #eaf3ec; border-radius: 9px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #1d4ed8 0%, #3b82f6 100%); border-radius: 9px; transition: width 0.7s ease; }
.progress-value { width: 48px; text-align: right; font-size: 13px; font-weight: 600; color: #1d4ed8; }
</style>
