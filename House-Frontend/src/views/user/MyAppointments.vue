<template>
  <div class="">
    <el-card>
      <h2>我预约的房源</h2>
      <el-table :data="appointments" stripe style="width: 100%">
        
        <el-table-column label="详细" width="100">
          <template slot-scope="scope">
            <el-button
              type="primary"
              plain
              size="small"
              @click="goToDetail(scope.row.houseId)"
            >
              详细
            </el-button>
          </template>
        </el-table-column>

        <!-- 原有字段列 -->
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="date" label="预约时间" />
        <el-table-column prop="message" label="留言" />

        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status !== 0"
              type="danger"
              size="mini"
              @click="cancelAppointment(scope.row.appointmentId)"
            >
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page.sync="currentPage"
        @current-change="fetchAppointments"
        style="margin-top: 20px; text-align: center"
      />
    </el-card>
  </div>
</template>


<script>
import axios from 'axios'

export default {
  data() {
    return {
      appointments: [],
      total: 0,
      currentPage: 1,
      pageSize: 10
    }
  },
  created() {
    this.fetchAppointments(this.currentPage)
  },
  methods: {
      goToDetail(houseId) {
    this.$router.push(`/house/${houseId}`)
  },
      getStatusText(status) {
    switch (status) {
      case 0: return '已取消'
      case 1: return '已确认'
      case 2: return '待处理'
      default: return '未知'
    }
  },
  getStatusTagType(status) {
    switch (status) {
      case 0: return 'info'
      case 1: return 'success'
      case 2: return 'warning'
      default: return ''
    }
  },
      async cancelAppointment(id) {
    const token = localStorage.getItem('token') || ''
            console.log(id)
    try {
      const res = await axios.put(
        `http://www.xzzf.xyz/personal_center/cancel_appointment/${id}`,
        {},
        {
          headers: {
            Authorization: 'Bearer ' + token
          }
        }
      )
      if (res.data.code) {
        this.$message.success('已取消预约')

        this.fetchAppointments(this.currentPage) // 刷新列表
      } else {
        this.$message.error(res.data.message || '取消失败')
      }
    } catch (err) {
      this.$message.error('网络错误：' + err.message)
    }
  },
    async fetchAppointments(page = 1) {
      const token = localStorage.getItem('token') || ''
      try {
        const res = await axios.get('http://www.xzzf.xyz/personal_center/my_appointment', {
          params: {
            currentPage: page,
            pageSize: this.pageSize
          },
          headers: {
            Authorization: 'Bearer ' + token
          }
        })
        if (res.data.code === 200) {
          this.appointments = res.data.jsonData.records
          this.total = res.data.jsonData.total
        } else {
          this.$message.error(res.data.message || '获取预约列表失败')
        }
      } catch (err) {
        this.$message.error('网络错误：' + err.message)
      }
    },
    statusTagType(status) {
      switch (status) {
        case 0:
          return 'danger'  // 已取消
        case 1:
          return 'success' // 已确认
        case 2:
          return 'info'    // 待处理
        default:
          return 'default'
      }
    },
    statusText(status) {
      switch (status) {
        case 0:
          return '已取消'
        case 1:
          return '已确认'
        case 2:
          return '待处理'
        default:
          return '未知'
      }
    }
  }
}
</script>
