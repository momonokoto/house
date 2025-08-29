<template>
  <el-row style="height: 600px;">
    <!-- 地图 -->
    <el-col :span="16" style="height: 100%;">
      <div id="mapContainer" style="width: 100%; height: 100%;"></div>
    </el-col>

    <!-- 房源列表 -->
    <el-col :span="8" style="height: 100%; overflow-y: auto; padding: 10px;">
      <el-select v-model="selectedCity" placeholder="选择城市" @change="onCityChange" style="width: 100%; margin-bottom: 12px;">
        <el-option
          v-for="city in cityList"
          :key="city.name"
          :label="city.name"
          :value="city.name"
        ></el-option>
      </el-select>

      <el-scrollbar style="height: calc(100% - 50px);">
        <div v-if="allHouses.length">
        <HouseCard
            v-for="item in allHouses.filter(h => h.region === selectedCity)"
            :key="item.houseId"
            :house="item"
            @click.native="goToDetail(item.houseId)"
        />
        </div>
        <div v-else style="color: #999; text-align: center; margin-top: 100px;">
          暂无房源
        </div>
      </el-scrollbar>
    </el-col>
  </el-row>
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
      map: null,
      markers: [],
      selectedCity: '北京市',
      cityList: [
        { name: '北京市', center: [116.407395, 39.904211] },
        { name: '上海市', center: [121.473701, 31.230416] },
        { name: '天津市', center: [117.200983, 39.084158] },
        { name: '重庆市', center: [106.551556, 29.563009] },
        { name: '广州市', center: [113.264435, 23.129163] },
        { name: '深圳市', center: [114.057939, 22.543527] },
        { name: '南京市', center: [118.796877, 32.060255] },
        { name: '杭州市', center: [120.15507, 30.274085] },
        { name: '成都市', center: [104.066541, 30.572269] },
        { name: '武汉市', center: [114.305215, 30.592935] },
        { name: '西安市', center: [108.940175, 34.341568] },
        { name: '长沙市', center: [112.938814, 28.228209] },
        { name: '青岛市', center: [120.38264, 36.067082] },
        { name: '厦门市', center: [118.089425, 24.479834] },
        { name: '苏州市', center: [120.619585, 31.299379] }
      ],
      allHouses: [],
      selectedHouse: null,
      infoWindow: null
    }
  },
computed: {
filteredHouses() {
    if (!this.selectedCity) return this.houses
    return this.houses.filter(house => house.region === this.selectedCity)
}
},
  methods: {
    async initMap() {
      this.map = new AMap.Map('mapContainer', {
        zoom: 11,
        center: [120.15507, 30.27415]
      })

      this.infoWindow = new AMap.InfoWindow({
        offset: new AMap.Pixel(0, -30)
      })

      await this.fetchAllHouses() // 获取所有房源
      this.updateCityMarkers(this.selectedCity)
    },
    async fetchAllHouses() {
      const token = localStorage.getItem('token') || ''
      try {
        const res = await axios.get('http://www.xzzf.xyz/house/find', {
          headers: { Authorization: 'Bearer ' + token },
          params: {
            currentPage: 1,
            pageSize: 9999, // 拉取全部
            house: {}
          }
        })
        if (res.data.code === 200) {
          this.allHouses = res.data.jsonData.records || []
        } else {
          this.$message.error(res.data.message || '获取房源失败')
        }
      } catch (e) {
        this.$message.error('接口请求失败: ' + e.message)
      }
    },
    updateCityMarkers(cityName) {
      const cityObj = this.cityList.find(c => c.name === cityName)
      if (cityObj) {
        this.map.setCenter(cityObj.center)
        this.map.setZoom(12)
      }

      // 清除旧的
      this.clearMarkers()

      // 根据城市筛选房源进行打点（不影响右侧展示）
      const cityHouses = this.allHouses.filter(h => h.region === cityName)
      cityHouses.forEach(house => {
        if (!house.latitude || !house.longitude) return
        const marker = new AMap.Marker({
          position: [house.longitude, house.latitude],
          map: this.map,
          title: house.title
        })
        marker.on('click', () => {
          this.selectedHouse = house
          this.showInfoWindow(marker, house)
        })
        this.markers.push(marker)
      })

      // 如果有房源，把地图中心设置为第一个点
      if (cityHouses.length > 0) {
        const first = cityHouses[0]
        this.map.setCenter([first.longitude, first.latitude])
      }
    },
    clearMarkers() {
      this.markers.forEach(marker => marker.setMap(null))
      this.markers = []
      this.selectedHouse = null
    },
    showInfoWindow(marker, house) {
      const content = `
        <div>
          <h4 style="margin:0 0 5px 0;">${house.title}</h4>
          <p style="margin:0 0 5px 0;">${house.address}</p>
          <p style="margin:0;">租金: ¥${house.rent}</p>
          <a href="javascript:void(0)" id="detailLink">查看详情</a>
        </div>
      `
      this.infoWindow.setContent(content)
      this.infoWindow.open(this.map, marker.getPosition())

      this.$nextTick(() => {
        const detailLink = document.getElementById('detailLink')
        if (detailLink) {
          detailLink.onclick = () => this.goToDetail(house.houseId)
        }
      })
    },
    onCityChange(city) {
      this.updateCityMarkers(city)
    },
    goToDetail(houseId) {
      this.$router.push(`/house/${houseId}`)
    }
  },
  mounted() {
    if (typeof AMap === 'undefined') {
      this.$message.error('高德地图API未加载，请检查 script 引入')
      return
    }
    this.initMap()
  },

}
</script>

<style scoped>
#mapContainer {
  width: 100%;
  height: 100%;
}
</style>
