<template>
  <div style="padding: 20px">
    <el-card>
      <div style="margin-bottom: 20px; font-size: 20px; font-weight: bold;">用户信息管理</div>

      <el-table :data="userList" border stripe style="min-height: 400px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="userNickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" width="115"/>
        <el-table-column prop="email" label="邮箱" width="180"/>
<el-table-column prop="role" label="角色" width="100">
  <template slot-scope="{ row }">
    <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">
      {{ row.role === 'ADMIN' ? '管理员' : '注册用户' }}
    </el-tag>
  </template>
</el-table-column>


        <!-- 用户状态 -->
<el-table-column label="状态" width="120">
  <template slot-scope="{ row }">
    <el-select
      v-model="row.status"
      :disabled="row.role === 'ADMIN'"
      @change="onStatusChange(row)"
      size="small"
      style="width: 100%"
    >
      <el-option label="正常" :value="1" />
      <el-option label="禁用" :value="0" />
    </el-select>
  </template>
</el-table-column>


        <!-- 实名状态 -->
        <el-table-column prop="realNameStatus" label="实名认证" width="90"> 
          <template slot-scope="{ row }">
            <el-tag :type="row.realNameStatus === 1 ? 'success' : 'danger'">
              {{ row.realNameStatus === 1 ? '已实名' : '未实名' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="introduction" label="简介" />
      </el-table>

      <el-pagination
        style="margin-top: 20px; text-align: right;"
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="fetchUserList"
      />
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AdminUser',
  data() {
    return {
      userList: [],
      total: 0,
      pageSize: 7,
      currentPage: 1,
    }
  },
  methods: {
    // 获取用户列表
    async fetchUserList(page = 1) {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.error('请先登录')
        this.$router.push('/entry')
        return
      }

      try {
        const res = await axios.get('http://www.xzzf.xyz/api/admin/findAllUsers', {
          params: {
            currentPage: page,
            pageSize: this.pageSize,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })

        if (res.data.code === 200 && Array.isArray(res.data.jsonData)) {
          this.userList = res.data.jsonData
          this.total = res.data.jsonData.length < this.pageSize ? page * this.pageSize : (page + 1) * this.pageSize
          this.currentPage = page
        } else {
          this.$message.warning(res.data.message || '查询失败')
        }
      } catch (err) {
        this.$message.error('请求出错: ' + (err.response?.data?.message || err.message))
      }
    },

    // 更新用户状态
    async onStatusChange(row) {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.error('请先登录')
        this.$router.push('/entry')
        return
      }

      try {
        await axios.post(
          'http://www.xzzf.xyz/api/admin/setUserStatus',
          null,
          {
            params: {
              username: row.username,
              status: row.status,
            },
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        )
        this.$message.success('用户状态更新成功')
      } catch (err) {
        this.$message.error('更新失败：' + (err.response?.data?.message || err.message))
        // 恢复旧值（假设你记录旧值）
        row.status = row.status === 1 ? 0 : 1
      }
    },
  },
  mounted() {
    this.fetchUserList(this.currentPage)
  },
}
</script>
