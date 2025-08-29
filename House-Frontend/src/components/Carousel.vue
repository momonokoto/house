<template>
  <el-carousel :height="carouselHeight">
    <el-carousel-item v-for="(item, index) in images" :key="index">
      <img
        :src="item"
        class="carousel-image"
        alt="poster"
      />
    </el-carousel-item>
  </el-carousel>
</template>

<script>
export default {
  data() {
    return {
      images: [
        require('@/assets/poster/海报 (1).png'),
        require('@/assets/poster/海报 (2).png'),
        require('@/assets/poster/海报 (3).png')
      ],
      carouselHeight: '25vw' // 2:1 等比例缩小一倍
    }
  }
}
</script>

<style scoped>
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  margin: 0 auto;
}
</style>
