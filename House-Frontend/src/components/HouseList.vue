<template>
  <div>
    <!-- 房源表格 -->
    <el-table
      :data="houses"
      border
      style="width: 100%; margin-top: 20px;"
      v-loading="loading"
      @row-click="goToDetail"
    >
      <!-- 缩略图 -->
      <el-table-column label="图片" width="140">
        <template slot-scope="{ row }">
          <img
            :src="row.img || defaultImg"
            @error="e => e.target.src = defaultImg"
            alt="房源图片"
            style="width: 120px; height: 90px; object-fit: cover; border-radius: 4px;"
          />
        </template>
      </el-table-column>

      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="region" label="地区" width="120" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="roomNumber" label="房间数" width="80" />
      <el-table-column prop="roomType" label="户型" width="120" />
      <el-table-column prop="rent" label="租金(元/月)" width="120" />
      <el-table-column prop="area" label="面积(m²)" width="100" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <span v-if="row.status === 0" style="color: green;">待租</span>
          <span v-else-if="row.status === 1" style="color: red;">已租</span>
          <span v-else>未知</span>
        </template>
      </el-table-column>
      <el-table-column label="电梯" width="80">
        <template slot-scope="{ row }">
          {{ row.elevator === 1 ? '有' : '无' }}
        </template>
      </el-table-column>
      <el-table-column label="审核" width="100">
        <template slot-scope="{ row }">
          {{ row.verify === 1 ? '已审核' : '待审核' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180" />
    </el-table>

    <!-- 分页 -->
    <el-pagination
      style="margin-top: 20px; text-align: right;"
      background
      layout="prev, pager, next, jumper, ->, total"
      :total="total"
      :page-size="pageSize"
      :current-page="currentPage"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script>
import axios from 'axios'
import qs from 'qs'
import defaultImg from '@/assets/户型示意图.png'

export default {
  data() {
    return {
      filters: {
        region: '',
        minPrice: null,
        maxPrice: null,
      },
      houses: [],
      loading: false,
      total: 0,
      pageSize: 10,
      currentPage: 1,
      defaultImg,
    }
  },
  methods: {
    async fetchHouses() {
      this.loading = true
      try {
        const token = localStorage.getItem('token') || ''
        const queryHouse = {}
        if (this.filters.region) queryHouse.region = this.filters.region
        if (this.filters.minPrice != null) queryHouse.minPrice = this.filters.minPrice
        if (this.filters.maxPrice != null) queryHouse.maxPrice = this.filters.maxPrice

        const response = await axios.get('http://www.xzzf.xyz/house/find', {
          headers: {
            Authorization: 'Bearer ' + token,
          },
          params: {
            currentPage: this.currentPage,
            pageSize: this.pageSize,
            house: queryHouse,
          },
          paramsSerializer: params => qs.stringify(params, { allowDots: true }),
        })

        if (response.data && response.data.code === 200) {
          const jsonData = response.data.jsonData
          this.houses = jsonData.records.map(item => ({
            ...item,
            id: item.houseId // 确保每行数据带有 id
          }))
          this.total = jsonData.total
          this.pageSize = jsonData.size
          this.currentPage = jsonData.current
        } else {
          this.$message.error(response.data.message || '获取房源失败')
        }
      } catch (error) {
        // this.$message.error('请求出错：' + error.message)
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.fetchHouses()
    },
    goToDetail(row) {
      if (row.id) {
        this.$router.push({ name: 'HouseDetail', params: { id: row.id } })
      } else {
        this.$message.error('该房源缺少ID，无法跳转详情')
      }
    },
  },
  mounted() {
    this.fetchHouses()
  },
}
</script>

<style scoped>
.el-table img {
  border: 1px solid #ddd;
}
</style>
