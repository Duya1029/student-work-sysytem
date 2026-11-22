import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      // 公共首页（所有角色可访问）
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // ==================== 管理员路由 ====================
      {
        path: 'admin/user',
        name: 'AdminUser',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', role: 'ADMIN' }
      },
      {
        path: 'admin/class',
        name: 'AdminClass',
        component: () => import('@/views/admin/ClassManage.vue'),
        meta: { title: '班级管理', role: 'ADMIN' }
      },
      {
        path: 'admin/course',
        name: 'AdminCourse',
        component: () => import('@/views/admin/CourseManage.vue'),
        meta: { title: '课程管理', role: 'ADMIN' }
      },
      {
        path: 'admin/semester',
        name: 'AdminSemester',
        component: () => import('@/views/admin/SemesterManage.vue'),
        meta: { title: '学期管理', role: 'ADMIN' }
      },
      {
        path: 'admin/homework',
        name: 'AdminHomework',
        component: () => import('@/views/admin/HomeworkList.vue'),
        meta: { title: '作业管理', role: 'ADMIN' }
      },
      {
        path: 'admin/announcement',
        name: 'AdminAnnouncement',
        component: () => import('@/views/admin/AnnouncementManage.vue'),
        meta: { title: '公告管理', role: 'ADMIN' }
      },
      {
        path: 'admin/log',
        name: 'AdminLog',
        component: () => import('@/views/admin/SystemLog.vue'),
        meta: { title: '系统日志', role: 'ADMIN' }
      },
      {
        path: 'admin/profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/Profile.vue'),
        meta: { title: '个人中心', role: 'ADMIN' }
      },
      // ==================== 教师路由 ====================
      {
        path: 'teacher/course',
        name: 'TeacherCourse',
        component: () => import('@/views/teacher/MyCourses.vue'),
        meta: { title: '我的课程', role: 'TEACHER' }
      },
      {
        path: 'teacher/homework',
        name: 'TeacherHomework',
        component: () => import('@/views/teacher/MyHomework.vue'),
        meta: { title: '作业管理', role: 'TEACHER' }
      },
      {
        path: 'teacher/publish',
        name: 'TeacherPublish',
        component: () => import('@/views/teacher/PublishHomework.vue'),
        meta: { title: '发布作业', role: 'TEACHER' }
      },
      {
        path: 'teacher/grade/:homeworkId?',
        name: 'TeacherGrade',
        component: () => import('@/views/teacher/GradeHomework.vue'),
        meta: { title: '作业批改', role: 'TEACHER' }
      },
      {
        path: 'teacher/statistics',
        name: 'TeacherStatistics',
        component: () => import('@/views/teacher/Statistics.vue'),
        meta: { title: '成绩统计', role: 'TEACHER' }
      },
      {
        path: 'teacher/announcement',
        name: 'TeacherAnnouncement',
        component: () => import('@/views/teacher/AnnouncementManage.vue'),
        meta: { title: '通知公告', role: 'TEACHER' }
      },
      {
        path: 'teacher/profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/Profile.vue'),
        meta: { title: '个人中心', role: 'TEACHER' }
      },
      // ==================== 学生路由 ====================
      {
        path: 'student/course',
        name: 'StudentCourse',
        component: () => import('@/views/student/MyCourses.vue'),
        meta: { title: '我的课程', role: 'STUDENT' }
      },
      {
        path: 'student/homework',
        name: 'StudentHomework',
        component: () => import('@/views/student/MyHomework.vue'),
        meta: { title: '我的作业', role: 'STUDENT' }
      },
      {
        path: 'student/grades',
        name: 'StudentGrades',
        component: () => import('@/views/student/MyGrades.vue'),
        meta: { title: '成绩查询', role: 'STUDENT' }
      },
      {
        path: 'student/announcement',
        name: 'StudentAnnouncement',
        component: () => import('@/views/student/Announcement.vue'),
        meta: { title: '通知公告', role: 'STUDENT' }
      },
      {
        path: 'student/profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人中心', role: 'STUDENT' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫：登录校验
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学生作业管理系统` : '学生作业管理系统'

  const token = localStorage.getItem('token')

  if (to.path === '/login' || to.path === '/register') {
    // 已登录则跳转首页，未登录允许访问登录/注册页
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    // 需要登录的页面
    if (!token) {
      next('/login')
    } else {
      // 角色权限校验
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (to.meta.role && userInfo.role !== to.meta.role) {
        next('/dashboard')
      } else {
        next()
      }
    }
  }
})

export default router
