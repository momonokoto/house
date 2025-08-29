import Vue from 'vue'
import Router from 'vue-router'

// 引入页面组件
import Home from '../views/Home.vue'
import UserCenter from '../views/UserCenter.vue'
import FindHouse from '../views/FindHouse.vue'
import PublishHouse from '../views/PublishHouse.vue'
import HouseDetail from '../views/HouseDetail.vue'
import Map from '@/views/Map.vue'
import HomeEntry from '@/views/HomeEntry.vue'
import DeepSeek from '@/views/DeepSeek.vue'
import AdminUser from '@/views/admin/AdminUser.vue'
import AdminOrder from '@/views/admin/AdminOrder.vue'
import AdminHouse from '@/views/admin/AdminHouse.vue'
import RealName from '@/views/RealName.vue'
import Test from '@/views/Pub.vue'
import House404 from '@/views/House404.vue'
Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/entry'  //启动时重定向到 /entry
    },
        { path: '/test', component: Test },
        { path: '/deepseek', component: DeepSeek },
        { path: '/entry', component: HomeEntry },
    { path: '/user', component: UserCenter },
        { path: '/home', component: Home },
    { path: '/find', component: FindHouse },
        { path: '/house404', component: House404 },
    { path: '/publish', component: PublishHouse },
        { path: '/realname', component: RealName },
        { path: '/map', component: Map },
      {
        path: '/house/:id',
        name: 'HouseDetail',
        component: () => import('@/views/HouseDetail.vue')
      },
      {
  path: '/admin',
  component: () => import('@/views/AdminSystem.vue'),
  children: [
    {
      path: 'house',
      name: 'AdminHouse',
      component: () => import('@/views/admin/AdminHouse.vue')
    },
    {
      path: 'user',
      name: 'AdminUser',
      component: () => import('@/views/admin/AdminUser.vue')
    },
    {
      path: 'order',
      name: 'AdminOrder',
      component: () => import('@/views/admin/AdminOrder.vue')
    }
  ]
}


    
  ]
})

// 👉 实时检测：全局导航守卫
// router.beforeEach((to, from, next) => {
//   const token = localStorage.getItem('token')

//   // 如果没有 token 且访问的不是 /entry，就强制跳转到 /entry
//   if (!token) {
//     // this.$router.push('/')
//   }
// })