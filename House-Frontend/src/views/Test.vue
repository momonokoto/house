<template>
  <div style="padding: 20px; max-width: 600px;">
    <h2>高德地址解析测试</h2>
    <el-input
      v-model="address"
      placeholder="请输入详细地址"
      clearable
      style="margin-bottom: 12px;"
    />
    <el-button type="primary" @click="geocode">识别坐标</el-button>

    <div v-if="location" style="margin-top: 20px;">
      <p><strong>经度:</strong> {{ location.lng }}</p>
      <p><strong>纬度:</strong> {{ location.lat }}</p>
      <div id="map" style="width: 100%; height: 400px; border: 1px solid #ccc;"></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      address: '',
      location: null,
      map: null,
      geocoder: null,
    }
  },
  methods: {
    geocode() {
      if (!this.address.trim()) {
        this.$message.warning('请输入详细地址')
        return
      }

      if (!window.AMap) {
        this.$message.error('高德地图 SDK 未加载，请检查script引入')
        return
      }

      if (!this.geocoder) {
        this.geocoder = new window.AMap.Geocoder({
          city: "全国",  // 全国范围搜索，精度最高
          radius: 1000,  // 搜索半径
        })
      }

      this.geocoder.getLocation(this.address, (status, result) => {
        if (status === 'complete' && result.geocodes && result.geocodes.length > 0) {
          this.location = result.geocodes[0].location

          // 初始化地图或者更新地图中心
          if (!this.map) {
            this.map = new window.AMap.Map('map', {
              center: this.location,
              zoom: 16,
              viewMode: '3D'
            })
            // 添加标记
            new window.AMap.Marker({
              position: this.location,
              map: this.map
            })
          } else {
            this.map.setCenter(this.location)
            this.map.clearMap()
            new window.AMap.Marker({
              position: this.location,
              map: this.map
            })
          }
        } else {
          this.$message.error('未找到该地址坐标，请检查地址是否准确')
          this.location = null
          if (this.map) {
            this.map.clearMap()
          }
        }
      })
    }
  }
}
</script>

<style scoped>
</style>
