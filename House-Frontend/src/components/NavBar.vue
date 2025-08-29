<template>
  <el-menu mode="horizontal" background-color="#ffffff" text-color="#333" active-text-color="#409EFF">
    <el-menu-item 
    active-text-color="#409EFF"
    style="background-color: #409EFF;
    color: #fff;
    font-size: 20px;font-family: '钉钉进步体';pointer-events: none;"
    >租房信息平台</el-menu-item>   
    
    <el-menu-item @click="$router.push('/home')"><i class="el-icon-s-home"></i>首页</el-menu-item>
    <el-menu-item @click="$router.push('/find')"><i class="el-icon-office-building" ></i>立即找房</el-menu-item>
    <el-menu-item @click="$router.push('/realname')"><i class="el-icon-upload2"></i>发布房源</el-menu-item>
    <el-menu-item @click="$router.push('/user')"><i class="el-icon-user-solid"></i>个人中心</el-menu-item>
    <!-- <el-menu-item> -->
      <!-- <el-select v-model="region" placeholder="选择地区" size="small">
        <el-option label="北京市" value="beijing"></el-option>
        <el-option label="上海市" value="shanghai"></el-option>
      </el-select> -->
    <!-- </el-menu-item> -->

    <el-menu-item style="float: right;">
      <template v-if="1">
        <el-button @click="logout" type="danger" size="small">
          <i class="el-icon-user"  style="color: #fff;"></i>退出登录</el-button>
      </template>
      <template v-else>
        <el-button @click="$router.push('/auth')" type="text" size="small">登录</el-button>
        <el-button @click="$router.push('/auth')" type="primary" size="small">注册</el-button>
      </template>
    </el-menu-item>
        <el-menu-item style="float: right;">
      <el-button @click="$router.push('/map')" type="warning" size="small">
        <i class="el-icon-map-location" style="color: #fff;"></i>地图找房</el-button>
    </el-menu-item>
      <el-menu-item style="float: right;">
      <el-button @click="$router.push('/deepseek')" type="success" size="small">
        <i class="el-icon-service" style="color: #fff;"></i>智能客服</el-button>
    </el-menu-item>
        <!-- ✅ 管理员才显示 -->
    <el-menu-item   v-if="isAdmin" index="admin" style="float: right;">
      <el-button @click="$router.push('/admin/house')" type="primary" size="small">
        <i class="el-icon-s-check" style="color: #fff;"></i>管理系统
      </el-button>
    </el-menu-item>
  </el-menu>
</template>

<script>
import axios from 'axios'
export default {
  data() {
    return {
      region: '',
      isAdmin: false,
    }
  },
    mounted() {
    this.fetchUserInfo()
  },

  computed: {
    isLogin() {
      // location.reload()
      return !!localStorage.getItem('token') // 有 token 即已登录
    }
  },
  methods: {
async fetchUserInfo() {
  const token = localStorage.getItem('token')
  if (!token) {
    this.$message.warning('尚未登录，请先登录')
    this.$router.push('/entry')
    return
  }

  // this.$message.info('正在获取用户信息...')

  try {
    const res = await axios.get('http://www.xzzf.xyz/api/get/getUser', {
      headers: {
        Authorization: 'Bearer ' + token
      }
    })

    if (res.data) {
      const user = res.data
      this.user = user

      if (user.role === 'ADMIN') {
        this.isAdmin = true
        this.$message.success('管理员登录成功')
      } else {
        this.$message.success('用户信息获取成功')
      }
    } else {
      // this.$message.error(`获取用户信息失败：${res.data.message || '未知错误'}`)
    }
  } catch (err) {
    // console.error('获取用户信息失败', err)
    // this.$message.error('请求失败：' + err.message)
  }
}
,
    logout() {
      localStorage.removeItem('token')
      this.$message.success('已退出登录')
      if (this.$route.path !== '/entry') {
        this.$router.push('/entry')
      }
      // location.reload()
    }

  }
}
</script>
