<!-- src/views/Home.vue -->
<template>
  <div class="home-page">
    <Carousel />
    <!-- <HouseList /> -->
     <h1 style="font-family:'钉钉进步体'; text-align: center; margin-top: 100px;">热门房源推荐</h1>
       <div class="find-house">
    <el-row :gutter="20">
      <el-col :span="6" v-for="house in displayedHouses" :key="house.houseId">
        <HouseCard :house="house" @click.native="goToDetail(house.houseId)" />
      </el-col>
    </el-row>
  </div>
  </div>
</template>

<script>
import Carousel from '../components/Carousel.vue'
import HouseList from '../components/HouseList.vue'
import axios from 'axios'
import HouseCard from '@/components/HouseCard.vue'
export default {
  name: 'Home',
  components: {
    Carousel,
    HouseList,
    HouseCard
  },
  data() {
    return {
      houses: []
    }
  },
  computed: {
    displayedHouses() {
      return this.houses.slice(0, 8)
    }
  },
  methods: {
    async fetchAllHouses() {
      const token = localStorage.getItem('token') || ''
      try {
        const res = await axios.get('http://www.xzzf.xyz/house/find', {
          headers: {
            Authorization: 'Bearer ' + token
          },
          params: {
            currentPage: 1,
            pageSize: 100,
            house: {} // 不带任何筛选条件
          }
        })
        if (res.data.code === 200) {
          this.houses = res.data.jsonData.records || []
        } else {
          this.$message.error(res.data.message || '获取房源失败')
        }
      } catch (e) {
        this.$message.error('接口请求失败: ' + e.message)
      }
    },
    goToDetail(houseId) {
      this.$router.push(`/house/${houseId}`)
    }
  },
  mounted() {
    this.fetchAllHouses()
  }
}
</script>

<style scoped>
.home-page {
  /* padding: 20px; */
}

</style>