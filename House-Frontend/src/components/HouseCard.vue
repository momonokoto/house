<template>
  <el-card class="house-card" :body-style="{ padding: '0' }">
    <img
      :src="imgUrl || defaultImg"
      alt="房源图"
      class="house-image"
      @error="loadDefaultImage"
    />
    <div class="info">
      <h3 class="title">{{ house.title }}</h3>
      <div class="price">{{ house.rent }}元/月</div>
      <div class="detail">面积：{{ house.area }}㎡</div>
      <div class="detail">位置：{{ house.region }}</div>
    </div>
  </el-card>
</template>

<script>
import axios from 'axios'
import defaultImg from '@/assets/户型示意图.png'

export default {
  name: 'HouseCard',
  props: {
    house: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      imgUrl: '' // 实际图片URL
    }
  },
  mounted() {
    this.fetchImageUrl()
  },
  watch: {
    house: {
      handler() {
        this.fetchImageUrl()
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    async fetchImageUrl() {
      if (!this.house.img) return
      const token = localStorage.getItem('token') || ''
      try {
        const res = await axios.get('http://www.xzzf.xyz/api/file/get-url', {
          headers: {
            Authorization: 'Bearer ' + token
          },
          params: {
            fileName: this.house.img
          }
        })
        if (res.status === 200 && res.data && res.data.url) {
          this.imgUrl = res.data.url
        } else {
          this.imgUrl = ''
        }
      } catch (err) {
        console.error('获取图片失败', err)
        this.imgUrl = ''
      }
    },
    loadDefaultImage(e) {
      e.target.src = defaultImg
    }
  }
}
</script>

<style scoped>
.house-card {
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s;
  cursor: pointer;
  margin-bottom: 15px;
  margin-top: 15px;
}
.house-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}
.house-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
}
.info {
  padding: 12px;
}
.title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 6px;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 6px;
}
.detail {
  font-size: 13px;
  color: #888;
  line-height: 1.5;
}
</style>
