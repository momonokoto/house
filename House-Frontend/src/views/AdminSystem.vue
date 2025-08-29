<template>
  <div class="admin-container">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="admin-header">
        <h2 style="font-family: '钉钉进步体';">小众租房后台管理系统</h2>
        <div class="admin-header-right">
          <el-button type="text" @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-container>
        <!-- 左侧菜单 -->
        <el-aside width="200px" class="admin-aside">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            @select="handleSelect"
            router
          >
            <el-menu-item index="/admin/house">房源管理</el-menu-item>
            <el-menu-item index="/admin/user">用户管理</el-menu-item>
            <el-menu-item index="/admin/order">订单管理</el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主内容区 -->
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<script>
import axios from 'axios'
export default {
  name: 'AdminSystem',
  data() {
    return {
      activeMenu: '/admin/house',
      isAdmin: false
    }
  },
  created() {
    this.fetchUserInfo()
  },
  methods: {
    
    async fetchUserInfo() {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/entry')
        return
      }

      try {
        const res = await axios.get('http://www.xzzf.xyz/api/get/getUser', {
          headers: {
            Authorization: 'Bearer ' + token
          }
        })

        if (res.data) {
          const user = res.data
          if (user.role === 'ADMIN') {
            this.isAdmin = true
            this.$message.success('欢迎管理员登录系统')
          } else {
            this.$message.warning('您无权访问管理系统，已跳转至个人中心')
            this.$router.push('/user')
          }
        } else {
          this.$message.error(res.data.message || '无法获取用户信息')
          this.$router.push('/user')
        }
      } catch (err) {
        this.$message.error('请求失败：' + err.message)
        this.$router.push('/user')
      }
    },
  
    handleSelect(key) {
      this.activeMenu = key
    },
    logout() {
      localStorage.removeItem('token')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.admin-container {
  height: 108vh;
}
.admin-header {
  background-color: #409EFF;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.admin-aside {
  background-color: #f5f5f5;
}
</style>
