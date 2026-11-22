import request from '@/utils/request'

// ==================== 公共接口 ====================

// 登录
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

// 自助注册（学生/教师）
export function register(data) {
  return request({
    url: '/register',
    method: 'post',
    data
  })
}

// 注册页获取班级列表（无需登录）
export function getRegisterClasses() {
  return request({
    url: '/register/classes',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// ==================== 管理员接口 ====================

// 班级列表
export function getClassList(params) {
  return request({ url: '/admin/class/list', method: 'get', params })
}

// 所有班级（下拉用）
export function getAllClasses() {
  return request({ url: '/admin/class/all', method: 'get' })
}

// 新增班级
export function addClass(data) {
  return request({ url: '/admin/class/add', method: 'post', data })
}

// 修改班级
export function updateClass(data) {
  return request({ url: '/admin/class/update', method: 'put', data })
}

// 删除班级
export function deleteClass(classId) {
  return request({ url: `/admin/class/delete/${classId}`, method: 'delete' })
}

// 账号列表
export function getUserList(params) {
  return request({ url: '/admin/user/list', method: 'get', params })
}

// 新增账号
export function addUser(data) {
  return request({ url: '/admin/user/add', method: 'post', data })
}

// 修改账号
export function updateUser(data) {
  return request({ url: '/admin/user/update', method: 'put', data })
}

// 删除账号
export function deleteUser(userId) {
  return request({ url: `/admin/user/delete/${userId}`, method: 'delete' })
}

// 重置密码
export function resetPassword(data) {
  return request({ url: '/admin/user/resetPwd', method: 'put', data })
}

// 教师列表（下拉用）
export function getTeacherList() {
  return request({ url: '/admin/teacher/list', method: 'get' })
}

// 全部作业记录
export function getHomeworkList(params) {
  return request({ url: '/admin/homework/list', method: 'get', params })
}

// 作业提交详情
export function getHomeworkSubmissions(homeworkId) {
  return request({ url: `/admin/homework/submissions/${homeworkId}`, method: 'get' })
}

// ==================== 教师接口 ====================

// 发布作业
export function publishHomework(data) {
  return request({ url: '/teacher/homework/publish', method: 'post', data })
}

// 我的作业列表
export function getMyHomeworkList(params) {
  return request({ url: '/teacher/homework/list', method: 'get', params })
}

// 作业提交列表
export function getTeacherSubmissions(homeworkId) {
  return request({ url: `/teacher/homework/submissions/${homeworkId}`, method: 'get' })
}

// 批改作业
export function gradeHomework(data) {
  return request({ url: '/teacher/homework/grade', method: 'put', data })
}

// 我的班级
export function getMyClasses() {
  return request({ url: '/teacher/classes', method: 'get' })
}

// ==================== 学生接口 ====================

// 我的作业列表
export function getStudentHomeworkList(params) {
  return request({ url: '/student/homework/list', method: 'get', params })
}

// 提交作业
export function submitHomework(data) {
  return request({ url: '/student/homework/submit', method: 'post', data })
}

// 作业详情
export function getHomeworkDetail(homeworkId) {
  return request({ url: `/student/homework/detail/${homeworkId}`, method: 'get' })
}

// ==================== 公共接口（新增） ====================

// 修改密码
export function changePassword(data) {
  return request({ url: '/user/changePwd', method: 'put', data })
}

// 公告列表
export function getAnnouncementList() {
  return request({ url: '/announcement/list', method: 'get' })
}

// ==================== 管理员接口（新增） ====================

// 发布公告
export function addAnnouncement(data) {
  return request({ url: '/admin/announcement/add', method: 'post', data })
}

// 修改公告
export function updateAnnouncement(data) {
  return request({ url: '/admin/announcement/update', method: 'put', data })
}

// 删除公告
export function deleteAnnouncement(id) {
  return request({ url: `/admin/announcement/delete/${id}`, method: 'delete' })
}

// 删除作业
export function deleteHomework(homeworkId) {
  return request({ url: `/admin/homework/delete/${homeworkId}`, method: 'delete' })
}

// 统计数据
export function getStatistics() {
  return request({ url: '/admin/statistics', method: 'get' })
}

// ==================== 教师接口（新增） ====================

// 作业完成统计
export function getHomeworkStatistics(homeworkId) {
  return request({ url: `/teacher/homework/statistics/${homeworkId}`, method: 'get' })
}

// ==================== 学生接口（新增） ====================

// 历史成绩
export function getMyGrades() {
  return request({ url: '/student/homework/grades', method: 'get' })
}

// ==================== 课程接口 ====================
export function getCourseList(params) { return request({ url: '/admin/course/list', method: 'get', params }) }
export function addCourse(data) { return request({ url: '/admin/course/add', method: 'post', data }) }
export function updateCourse(data) { return request({ url: '/admin/course/update', method: 'put', data }) }
export function deleteCourse(id) { return request({ url: `/admin/course/delete/${id}`, method: 'delete' }) }
export function getTeacherCourses() { return request({ url: '/teacher/course/list', method: 'get' }) }
export function getStudentCourses() { return request({ url: '/student/course/list', method: 'get' }) }
export function getCourseStudents(courseId) { return request({ url: `/teacher/course/students/${courseId}`, method: 'get' }) }

// ==================== 学期接口 ====================
export function getSemesterList() { return request({ url: '/admin/semester/list', method: 'get' }) }
export function addSemester(data) { return request({ url: '/admin/semester/add', method: 'post', data }) }
export function updateSemester(data) { return request({ url: '/admin/semester/update', method: 'put', data }) }
export function deleteSemester(id) { return request({ url: `/admin/semester/delete/${id}`, method: 'delete' }) }
export function setCurrentSemester(id) { return request({ url: `/admin/semester/set-current/${id}`, method: 'put' }) }
export function getCurrentSemester() { return request({ url: '/semester/current', method: 'get' }) }

// ==================== 日志接口 ====================
export function getLogList(params) { return request({ url: '/admin/log/list', method: 'get', params }) }

// ==================== 扩展接口 ====================
export function getStudentDashboard() { return request({ url: '/student/dashboard', method: 'get' }) }
export function getGradeStatistics(homeworkId) { return request({ url: `/teacher/grade/statistics/${homeworkId}`, method: 'get' }) }
export function addClassAnnouncement(data) { return request({ url: '/teacher/announcement/add', method: 'post', data }) }
