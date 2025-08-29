<template>
  <div style="padding: 20px">
    <el-card>
      <div style="margin-bottom: 20px; font-size: 20px; font-weight: bold;">所有房源管理</div>

      <el-table :data="houseList" border stripe style="min-height: 400px">
        <el-table-column prop="houseId" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="region" label="地区" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="area" label="面积(m²)" width="100" />
        <el-table-column prop="roomType" label="户型" />
        <el-table-column prop="rent" label="租金(元)" />

        <!-- 出租状态选择框 -->
        <el-table-column label="状态" width="120">
          <template slot-scope="{ row }">
            <el-select
              v-model="row.status"
              placeholder="请选择状态"
              @change="onStatusChange(row)"
              size="small"
              style="width: 100%"
            >
              <el-option label="待租" :value="1" />
              <el-option label="已租" :value="2" />
            </el-select>
          </template>
        </el-table-column>

        <!-- 审核状态选择框 -->
        <el-table-column label="审核状态" width="120">
          <template slot-scope="{ row }">
            <el-select
              v-model="row.verify"
              placeholder="请选择审核状态"
              @change="onVerifyChange(row)"
              size="small"
              style="width: 100%"
            >
              <el-option label="待审核" :value="0" />
              <el-option label="已审核" :value="1" />
              <el-option label="未通过" :value="-1" />
            </el-select>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="发布时间" width="180" />
      </el-table>

      <el-pagination
        style="margin-top: 20px; text-align: right;"
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="fetchHouseList"
      />
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AdminHouse',
  data() {
    return {
      houseList: [],
      total: 0,
      pageSize: 7,
      currentPage: 1,
    }
  },
  methods: {
    backendStatusToFrontend(status) {
      return status === 0 ? 1 : status === 1 ? 2 : status
    },
    frontendStatusToBackend(status) {
      return status === 1 ? 0 : status === 2 ? 1 : status
    },

    async fetchHouseList(page = 1) {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.error('请先登录')
        this.$router.push('/entry')
        return
      }

      try {
        const res = await axios.get('http://www.xzzf.xyz/api/admin/findAllHouses', {
          params: {
            currentPage: page,
            pageSize: this.pageSize,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })

        if (
          res.data.code === 200 &&
          res.data.jsonData &&
          Array.isArray(res.data.jsonData.records)
        ) {
          const records = res.data.jsonData.records
          this.houseList = records.map(house => ({
            ...house,
            status: house.status != null ? this.backendStatusToFrontend(house.status) : 1,
            verify: house.verify != null ? house.verify : 0,
          }))
          this.total = res.data.jsonData.total || 0
          this.currentPage = res.data.jsonData.current || page
        } else {
          this.$message.warning(res.data.message || '查询失败')
        }
      } catch (err) {
        this.$message.error('请求出错: ' + (err.response?.data?.message || err.message))
      }
    },

    async onStatusChange(row) {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.error('请先登录')
        this.$router.push('/entry')
        return
      }

      const backendStatus = this.frontendStatusToBackend(row.status)

      try {
        await axios.post(
          'http://www.xzzf.xyz/api/admin/setHouseStatus',
          null,
          {
            params: {
              houseId: row.houseId,
              status: backendStatus,
            },
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        )
        this.$message.success('出租状态更新成功')
      } catch (error) {
        this.$message.error('更新出租状态失败')
        this.fetchHouseList(this.currentPage)
      }
    },

    async onVerifyChange(row) {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.error('请先登录')
        this.$router.push('/entry')
        return
      }

      try {
        await axios.post(
          'http://www.xzzf.xyz/api/admin/setHouseVerify',
          null,
          {
            params: {
              houseId: row.houseId,
              verify: row.verify,
            },
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        )
        this.$message.success('审核状态更新成功')
      } catch (error) {
        this.$message.error('更新审核状态失败')
        this.fetchHouseList(this.currentPage)
      }
    },
  },
  mounted() {
    this.fetchHouseList(this.currentPage)
  },
}
</script>

