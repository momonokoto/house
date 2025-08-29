<template>
  <div>
    <el-card>
      <h2>我的收藏</h2>
      <el-empty description="暂无收藏" v-if="favorites.length === 0 && !loading" />

      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="house in favorites" :key="house.houseId">
          <HouseCard :house="house" @click.native="goToDetail(house.houseId)" />
        </el-col>
      </el-row>

      <div v-if="total > pageSize" style="text-align: center; margin-top: 20px;">
        <el-pagination
          layout="prev, pager, next"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import HouseCard from '@/components/HouseCard.vue'

export default {
  components: {
    HouseCard
  },
  data() {
    return {
      favorites: [],
      currentPage: 1,
      pageSize: 6,
      total: 0,
      loading: false
    }
  },
  mounted() {
    this.fetchFavorites()
  },
  methods: {
    async fetchFavorites() {
      this.loading = true
      try {
        const token = localStorage.getItem('token')

        // 获取收藏的房源ID
        const res = await axios.get('http://www.xzzf.xyz/personal_center/my_collection', {
          params: {
            currentPage: this.currentPage,
            pageSize: this.pageSize
          },
          headers: {
            Authorization: 'Bearer ' + token
          }
        })

        if (res.data.code !== 200 || !res.data.jsonData) {
          this.$message.error(res.data.message || '获取收藏失败')
          this.loading = false
          return
        }

        const records = res.data.jsonData.records || []
        this.total = res.data.jsonData.total || 0

        // 获取每个房源的详情
        const houseDetailPromises = records.map(item =>
          axios.get(`http://www.xzzf.xyz/house/detail/${item.houseId}`, {
            headers: { Authorization: 'Bearer ' + token }
          }).then(resp => resp.data.jsonData)
        )

        this.favorites = await Promise.all(houseDetailPromises)

      } catch (err) {
        console.error(err)
        this.$message.error('加载收藏详情失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.fetchFavorites()
    },
    goToDetail(houseId) {
      this.$router.push(`/house/${houseId}`)
    }
  
  }
}
</script>
