<template>
  <div class="find-house">
    <el-form :inline="true" :model="filters" class="filter-form" @submit.native.prevent>
      <el-form-item label="城市">
        <el-select v-model="filters.region" placeholder="--请选择--" clearable style="width: 160px;">
          <el-option label="--请选择--" value="" />
          <el-option v-for="city in cityList" :key="city" :label="city" :value="city" />
        </el-select>
      </el-form-item>

      <el-form-item label="租金">
        <el-input-number v-model="filters.minPrice" :min="0" placeholder="最低" style="width: 100px" />
        -
        <el-input-number v-model="filters.maxPrice" :min="0" placeholder="最高" style="width: 100px" />
      </el-form-item>

      <el-form-item label="类型">
        <el-select v-model="filters.rentType" placeholder="请选择" style="width: 100px">
          <el-option label="全部" value="" />
          <el-option label="合租" value="1" />
          <el-option label="整租" value="2" />
          <el-option label="公寓" value="3" />
        </el-select>
      </el-form-item>

      <el-form-item label="户型">
        <el-select v-model="filters.roomType" placeholder="请选择" style="width: 100px">
          <el-option label="全部" value="" />
          <el-option label="一室" value="1" />
          <el-option label="二室" value="2" />
          <el-option label="三室" value="3" />
          <el-option label="三室以上" value="4" />
        </el-select>
      </el-form-item>

      <el-form-item label="排序">
        <el-select v-model="filters.sortBy" placeholder="请选择" style="width: 120px">
          <el-option label="默认" value="" />
          <el-option label="租金升序" value="rentAsc" />
          <el-option label="租金降序" value="rentDesc" />
          <el-option label="面积升序" value="areaAsc" />
          <el-option label="面积降序" value="areaDesc" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="applyFilters">筛选</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table
      :data="pagedHouses"
      border
      v-loading="loading"
      style="width: 100%"
      @row-click="goToDetail"
    >
      <el-table-column label="图片" width="140">
        <template slot-scope="{ row }">
          <img
            :src="row.imgUrl || defaultImg"
            style="width: 120px; height: 90px; object-fit: cover"
            @error="e => e.target.src = defaultImg"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="region" label="城市" width="100" />
      <el-table-column prop="address" label="地址" />
      <el-table-column label="户型" width="100">
        <template slot-scope="{ row }">{{ formatRoomType(row.roomType) }}</template>
      </el-table-column>
      <el-table-column label="类型" width="100">
        <template slot-scope="{ row }">{{ formatRentType(row.rentType) }}</template>
      </el-table-column>
      <el-table-column prop="rent" label="租金(元/月)" width="120" />
      <el-table-column prop="area" label="面积(m²)" width="120" />
    </el-table>

    <el-pagination
      :total="filteredHouses.length"
      :page-size="pageSize"
      :current-page.sync="currentPage"
      layout="prev, pager, next, jumper, ->, total"
      background
      style="margin-top: 20px; text-align: right"
    />
  </div>
</template>

<script>
import axios from 'axios'
import qs from 'qs'
import defaultImg from '@/assets/户型示意图.png'

export default {
  name: 'FindHouse',
  data() {
    return {
      cityList: [
        '北京市', '上海市', '天津市', '重庆市', '广州市', '深圳市', '南京市',
        '杭州市', '成都市', '武汉市', '西安市', '长沙市', '青岛市',
        '厦门市', '苏州市'
      ],
      filters: {
        region: '',
        minPrice: null,
        maxPrice: null,
        rentType: '',
        roomType: '',
        sortBy: ''
      },
      allHouses: [],
      filteredHouses: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      defaultImg
    }
  },
  computed: {
    pagedHouses() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = this.currentPage * this.pageSize
      return this.filteredHouses.slice(start, end)
    }
  },
  methods: {
    formatRoomType(val) {
      const map = { '1': '一室', '2': '二室', '3': '三室', '4': '三室以上' }
      return map[String(val)] || '未知'
    },
    formatRentType(val) {
      const map = { '1': '合租', '2': '整租', '3': '公寓' }
      return map[String(val)] || '未知'
    },
    async fetchAllHouses() {
      this.loading = true
      try {
        const token = localStorage.getItem('token') || ''
        const res = await axios.get('http://www.xzzf.xyz/house/find', {
          headers: { Authorization: 'Bearer ' + token },
          params: { currentPage: 1, pageSize: 1000 },
          paramsSerializer: params => qs.stringify(params, { allowDots: true })
        })

        if (res.data.code === 200) {
          const list = res.data.jsonData.records
            .filter(h => h.status === 0 && h.verify === 1)

          for (const house of list) {
            house.imgUrl = ''
            if (house.img) {
              try {
                const imgRes = await axios.get('http://www.xzzf.xyz/api/file/get-url', {
                  headers: { Authorization: 'Bearer ' + token },
                  params: { fileName: house.img }
                })
                house.imgUrl = imgRes.data.url || ''
              } catch {}
            }
          }

          this.allHouses = list
          this.applyFilters()
        } else {
          this.$message.error(res.data.message || '获取房源失败')
        }
      } catch (err) {
        this.$message.error('请求失败：' + err.message)
      } finally {
        this.loading = false
      }
    },
    applyFilters() {
      this.currentPage = 1
      let result = [...this.allHouses]

      if (this.filters.region) {
        result = result.filter(h => h.region === this.filters.region)
      }
      if (this.filters.rentType) {
        result = result.filter(h => String(h.rentType) === String(this.filters.rentType))
      }
      if (this.filters.roomType) {
        result = result.filter(h => String(h.roomType) === String(this.filters.roomType))
      }
      if (this.filters.minPrice != null) {
        result = result.filter(h => h.rent >= this.filters.minPrice)
      }
      if (this.filters.maxPrice != null) {
        result = result.filter(h => h.rent <= this.filters.maxPrice)
      }

      switch (this.filters.sortBy) {
        case 'rentAsc': result.sort((a, b) => a.rent - b.rent); break
        case 'rentDesc': result.sort((a, b) => b.rent - a.rent); break
        case 'areaAsc': result.sort((a, b) => a.area - b.area); break
        case 'areaDesc': result.sort((a, b) => b.area - a.area); break
      }

      this.filteredHouses = result
    },
    resetFilters() {
      this.filters = {
        region: '',
        minPrice: null,
        maxPrice: null,
        rentType: '',
        roomType: '',
        sortBy: ''
      }
      this.applyFilters()
    },
    goToDetail(row) {
      this.$router.push(`/house/${row.houseId}`)
    }
  },
  mounted() {
    this.fetchAllHouses()
  }
}
</script>

<style scoped>
.find-house {
  padding: 40px;
}
.filter-form {
  margin-bottom: 20px;
}
</style>
