<template>
  <div id="app">
    <!-- 判断当前路由不是 /entry 时显示导航栏 -->
    <NavBar v-if="!isPage" />

    <!-- 路由视图 -->
    <router-view />

    <!-- 可选：底部 Footer（如有） -->
    <Footer/>
  </div>
</template>

<script>
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'

export default {
  name: 'App',
  components: {
    NavBar,
    Footer
  },
  computed: {
    isPage() {
      return this.$route.path === '/entry'
    },
  }
}
</script>

<style>
/* 样式根据需要自行保留 */
</style>
