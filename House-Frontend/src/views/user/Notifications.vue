<template>
  <el-card>
    <h2>预约通知</h2>

    <el-empty description="暂无预约通知" v-if="notifications.length === 0 && !loading" />

    <el-table v-else :data="notifications" stripe style="width: 100%">
      <el-table-column prop="name" label="预约人" width="120" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="date" label="预约时间" width="180" />
      <el-table-column prop="message" label="留言" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
<el-table-column label="操作" width="300">
  <template slot-scope="scope">
    <el-button
      type="success"
      size="mini"
      @click="confirmAppointment(scope.row)"
      :disabled="scope.row.status === 0"
    >
      同意预约
    </el-button>

    <el-button
      type="danger"
      size="mini"
      @click="cancelAppointment(scope.row.appointmentId)"
      :disabled="scope.row.status === 0"
    >
      取消预约
    </el-button>
  </template>
</el-table-column>

    </el-table>
  </el-card>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Notifications',
  data() {
    return {
      notifications: [],
      loading: false
    }
  },
  mounted() {
    this.fetchNotifications()
  },
  methods: {
    async confirmAppointment(row) {
  try {
    const token = localStorage.getItem('token');
    const res = await axios.post('http://www.xzzf.xyz/api/admin/setHouseStatus', null, {
      params: {
        houseId: row.houseId,
        status: 1
      },
      headers: {
        Authorization: 'Bearer ' + token
      }
    });

    if (res.data && res.data.code === 200) {
      this.$message.success('已同意预约，该房源设置为“已出租”');
      this.fetchNotifications(); // 刷新列表
    } else {
      this.$message.error(res.data.message || '操作失败');
    }
  } catch (err) {
    this.$message.error('请求失败：' + err.message);
  }
}
,
    async fetchNotifications() {
      this.loading = true
      try {
        const token = localStorage.getItem('token')
        const res = await axios.get('http://www.xzzf.xyz/personal_center/appointment_my', {
          headers: {
            Authorization: 'Bearer ' + token
          }
        })
        if (res.data.code === 200 && res.data.jsonData) {
          this.notifications = res.data.jsonData
        } else {
          this.$message.warning(res.data.message || '获取预约通知失败')
        }
      } catch (err) {
        this.$message.error('加载失败：' + err.message)
      } finally {
        this.loading = false
      }
    },
    async cancelAppointment(appointmentId) {
      try {
        const token = localStorage.getItem('token')
        const res = await axios.put(
          `http://www.xzzf.xyz/personal_center/cancel_appointment/${appointmentId}`,
          {},
          {
            headers: {
              Authorization: 'Bearer ' + token
            }
          }
        )
        if (res.data.code === 200) {
          this.$message.success('取消成功')
          this.fetchNotifications()
        } else {
          this.$message.error(res.data.message || '取消失败')
        }
      } catch (err) {
        this.$message.error('取消失败：' + err.message)
      }
    },
    statusText(status) {
      switch (status) {
        case 0:
          return '已取消'
        case 1:
          return '已同意'
        case 2:
          return '待同意'
        default:
          return '未知状态'
      }
    },
    statusTagType(status) {
      switch (status) {
        case 0:
          return 'info'
        case 1:
          return 'success'
        case 2:
          return 'warning'
        default:
          return ''
      }
    }
  }
}
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}
</style>
