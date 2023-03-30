import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '../views/home.vue'
import Welcome from '../views/.vue'
import PreHome from '../views/prehome.vue'
import About from '../views/about.vue'
import Text from '../views/text.vue'
import Contribute from '../views/contribute.vue'
import Doc from '../views/doc.vue'
import AdminUser from '../views/admin/admin-user.vue'
import AdminTeacher from '../views/admin/admin-teacher.vue'
import AdminStudent from '../views/admin/admin-student.vue'
import StudentAdmin from '../views/admin/student-admin.vue'
import TeacherAdmin from '../views/admin/teacher-admin.vue'
import AdminEbook from '../views/admin/admin-ebook.vue'
import AdminProject from '../views/admin/admin-project.vue'
import AdminCategory from '../views/admin/admin-category.vue'
import AdminDoc from '../views/admin/admin-doc.vue'
import {Tool} from "@/util/tool";
import store from "@/store";
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'PreHome',
    component: PreHome
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  // {
  //   path: '/home',
  //   name: 'Home',
  //   component: Home
  // },
  {
    path: '/doc',
    name: 'Doc',
    component: Doc
  },
  {
    path: '/about',
    name: 'About',
    component: About
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
  },
  {
    path: '/text',
    name: 'Text',
    component: Text
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
  },
  {
    path: '/contribute',
    name: 'Contribute',
    component: Contribute
  },
  {
    path: '/admin_project',
    name: 'AdminProject',
    component: AdminProject
  },
  {
    path: '/admin/user',
    name: 'AdminUser',
    component: AdminUser,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/admin/teacher',
    name: 'AdminTeacher',
    component: AdminTeacher,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/admin/student',
    name: 'AdminStudent',
    component: AdminStudent,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/student/admin',
    name: 'StudentAdmin',
    component: StudentAdmin,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/teacher/admin',
    name: 'TeacherAdmin',
    component: TeacherAdmin,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/admin/ebook',
    name: 'AdminEbook',
    component: AdminEbook,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/admin/category',
    name: 'AdminCategory',
    component: AdminCategory,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/admin/doc',
    name: 'AdminDoc',
    component: AdminDoc,
    meta: {
      loginRequire: true
    }
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由登录拦截
router.beforeEach((to, from, next) => {
  // 要不要对meta.loginRequire属性做监控拦截
  if (to.matched.some(function (item) {
    console.log(item, "是否需要登录校验：", item.meta.loginRequire);
    return item.meta.loginRequire
  })) {
    const loginUser = store.state.user;
    if (Tool.isEmpty(loginUser)) {
      console.log("用户未登录！");
      next('/');
    } else {
      next();
    }
  } else {
    next();
  }
});
export default router;
