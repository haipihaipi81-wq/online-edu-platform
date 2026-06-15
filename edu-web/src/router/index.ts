import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/RegisterView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/components/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue')
      },
      {
        path: 'courses',
        name: 'CourseList',
        component: () => import('@/views/course/CourseList.vue')
      },
      {
        path: 'courses/:id',
        name: 'CourseDetail',
        component: () => import('@/views/course/CourseDetail.vue')
      },
      {
        path: 'teacher/courses',
        name: 'CourseManage',
        component: () => import('@/views/course/CourseEdit.vue'),
        meta: { roles: ['TEACHER', 'ADMIN'] }
      },
      {
        path: 'admin/users',
        name: 'UserManage',
        component: () => import('@/views/user/UserManageView.vue'),
        meta: { roles: ['ADMIN'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/ProfileView.vue')
      },
      {
        path: 'live',
        name: 'LiveList',
        component: () => import('@/views/live/LiveList.vue')
      },
      {
        path: 'live/:id',
        name: 'LiveRoom',
        component: () => import('@/views/live/LiveRoom.vue')
      },
      {
        path: 'exams',
        name: 'ExamList',
        component: () => import('@/views/exam/ExamList.vue')
      },
      {
        path: 'exams/:id',
        name: 'ExamTake',
        component: () => import('@/views/exam/ExamTake.vue')
      },
      {
        path: 'homework',
        name: 'HomeworkList',
        component: () => import('@/views/exam/HomeworkList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('accessToken')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  if (to.meta.public) {
    if (token && (to.path === '/login' || to.path === '/register')) {
      return next('/dashboard')
    }
    return next()
  }

  if (!token) {
    return next('/login')
  }

  if (to.meta.roles && userInfo) {
    const required = to.meta.roles as string[]
    if (!required.includes(userInfo.role)) {
      return next('/dashboard')
    }
  }

  next()
})

export default router
