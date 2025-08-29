<template>
  <div class="house-detail" v-if="house">
    <el-card class="detail-card">
      <!-- 顶部操作 -->
      <div class="header">
        <h2>{{ house.title }}</h2>
        <div class="actions">
          <el-button
            :icon="isCollected ? 'el-icon-star-on' : 'el-icon-star-off'"
            circle
            type="warning"
            @click="toggleCollection"
          ></el-button>
          <el-button type="primary" @click="showAppointmentDialog = true">预约看房</el-button>
        </div>
      </div>

      <!-- 主体内容 -->
      <div class="content">
        <!-- 图片轮播 -->
        <div class="carousel-wrapper" v-if="house.img">
<el-carousel height="400px" v-if="house.img">
  <el-carousel-item>
    <img
      :src="imgUrl || fallbackImg"
      class="carousel-image"
      @click="openPreview(imgUrl || fallbackImg)"
      style="cursor: pointer;"
    />
  </el-carousel-item>
</el-carousel>

        </div>
        <!-- 大图预览对话框 -->
<el-dialog
  :visible.sync="showPreview"
  width="80%"
  top="5vh"
  :append-to-body="true"
  center
  custom-class="image-preview-dialog"
  :close-on-click-modal="true"
>
  <img :src="previewUrl" alt="预览图" style="width: 100%; max-height: 80vh; object-fit: contain;" />
</el-dialog>

        <!-- 房屋信息 -->
        <div class="info">
          <p><strong>地区：</strong>{{ house.region }} · {{ house.address }}</p>
          <p><strong>详细地址：</strong>{{ house.detailedAddress }}</p>
          <p><strong>租金：</strong><el-tag type="danger">{{ house.rent }} 元/月</el-tag></p>
          <p><strong>面积：</strong>{{ house.area }}㎡</p>
          <p><strong>电梯：</strong>
            <el-tag :type="house.elevator === 1 ? 'success' : 'info'">
              {{ house.elevator === 1 ? '有电梯' : '无电梯' }}
            </el-tag>
          </p>
          <p><strong>状态：</strong>
            <el-tag :type="house.status === 0 ? 'warning' : 'info'">
              {{ house.status === 0 ? '待租' : house.status === 1 ? '已租' : '已下架' }}
            </el-tag>
          </p>
          <p><strong>发布时间：</strong>{{ house.createTime }}</p>
          <p><strong>房源描述：</strong></p>
          <el-alert
            :title="house.description || '暂无描述'"
            type="info"
            :closable="false"
            show-icon
          />
        </div>
      </div>
      <h3>位置地图</h3>
<div ref="mapContainer" class="map-container"></div>

    </el-card>

    <!-- 预约对话框 -->
    <el-dialog title="预约看房" :visible.sync="showAppointmentDialog" width="500px">
      <el-form :model="appointmentForm" :rules="appointmentRules" ref="appointmentForm" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="appointmentForm.name" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="appointmentForm.phone" readonly />
        </el-form-item>
        <el-form-item label="预约时间" prop="date">
          <el-date-picker
            v-model="appointmentForm.date"
            type="datetime"
            placeholder="选择预约时间"
            format="yyyy-MM-dd HH:mm"
            value-format="yyyy-MM-dd HH:mm"
            :picker-options="datePickerOptions"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="留言" prop="message">
          <el-input type="textarea" v-model="appointmentForm.message" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAppointmentDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAppointment">提交预约</el-button>
      </div>
    </el-dialog>
    <!-- 地图展示区域 -->

  </div>
  
</template>



<script>
import axios from 'axios'
import defaultImg from '@/assets/户型示意图.png'
import moment from 'moment'

export default {
  data() {
    return {
      house: {
        img: '',
        latitude: 0,
        longitude: 0
      },
      imgUrl: '',
      fallbackImg: require('@/assets/户型示意图.png'),
      isCollected: false,
      showPreview: false,
      previewUrl: '',
      showAppointmentDialog: false,
      appointmentForm: {
        name: '',
        phone: '',
        date: '',
        message: ''
      },
      appointmentRules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入电话', trigger: 'blur' },
          { pattern: /^[0-9\-+]{7,20}$/, message: '电话格式不正确', trigger: 'blur' }
        ],
        date: [{ required: true, message: '请选择时间', trigger: 'change' }],
        message: [{ required: true, message: '请输入留言', trigger: 'blur' }]
      },
      datePickerOptions: {
        disabledDate(time) {
          const now = new Date()
          const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1)
          return time.getTime() < tomorrow.getTime()
        }
      }
    }
  },
  created() {
    this.fetchHouseDetail()
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      this.appointmentForm.phone = user.phone || ''
      this.appointmentForm.name = user.userNickname || ''
    }
  },
  methods: {
async fetchHouseDetail() {
  const id = this.$route.params.id
  try {
    const token = localStorage.getItem('token') || ''
    const res = await axios.get(`http://www.xzzf.xyz/house/detail/${id}`, {
      headers: { Authorization: 'Bearer ' + token }
    })
    if (res.data.code === 200) {
      const houseData = res.data.jsonData

      // 🌟 如果为待审核或已下架，跳转到 404 页面
      if (houseData.verify !== 1 || houseData.status === 2) {
        this.$router.replace('/house404')
        return
      }

      this.house = houseData
      this.checkCollection()
      this.fetchImageUrl()
      await this.loadAMapScript()
      this.initMap()
    } else {
      this.$message.error(res.data.message || '获取详情失败')
    }
  } catch (err) {
    this.$message.error('请求失败: ' + err.message)
  }
}
,
    async fetchImageUrl() {
      const token = localStorage.getItem('token') || ''
      try {
        const res = await axios.get('http://www.xzzf.xyz/api/file/get-url', {
          headers: { Authorization: 'Bearer ' + token },
          params: { fileName: this.house.img }
        })
        this.imgUrl = res.data?.url || ''
      } catch (err) {
        console.error('获取图片失败', err)
        this.imgUrl = ''
      }
    },
    async checkCollection() {
      const collectedList = JSON.parse(localStorage.getItem('collectedHouses') || '[]')
      this.isCollected = collectedList.includes(this.house.houseId)
    },
    async toggleCollection() {
      const token = localStorage.getItem('token') || ''
      const id = this.house.houseId
      try {
        if (this.isCollected) {
          await axios.put(`http://www.xzzf.xyz/collection/del/${id}`, {}, {
            headers: { Authorization: 'Bearer ' + token }
          })
          this.$message.success('已取消收藏')
        } else {
          await axios.post(`http://www.xzzf.xyz/collection/add/${id}`, {}, {
            headers: { Authorization: 'Bearer ' + token }
          })
          this.$message.success('收藏成功')
        }
        this.isCollected = !this.isCollected
      } catch (err) {
        this.$message.error('收藏操作失败: ' + err.message)
      }
    },
    openPreview(url) {
      this.previewUrl = url
      this.showPreview = true
    },
    async submitAppointment() {
      this.$refs.appointmentForm.validate(async valid => {
        if (!valid) return
        const token = localStorage.getItem('token') || ''
        const houseId = this.house.houseId
        try {
          const res = await axios.post(
            `http://www.xzzf.xyz/appointment/${houseId}`,
            this.appointmentForm,
            {
              headers: { Authorization: 'Bearer ' + token }
            }
          )
          if (res.data.code) {
            this.$message.success('预约成功！')
            this.showAppointmentDialog = false
            this.appointmentForm = { name: '', phone: '', date: '', message: '' }
          } else {
            this.$message.error(res.data.message || '预约失败')
          }
        } catch (err) {
          this.$message.error('预约失败：' + err.message)
        }
      })
    },
    loadAMapScript() {
      return new Promise((resolve, reject) => {
        if (window.AMap) return resolve()
        const script = document.createElement('script')
        script.src = 'https://webapi.amap.com/maps?v=2.0&key=db71ee4d4daf5ef750efc6b7587540ab'
        script.onload = resolve
        script.onerror = reject
        document.head.appendChild(script)
      })
    },
    initMap() {
      const lng = Number(this.house.longitude)
      const lat = Number(this.house.latitude)
      if (!lng || !lat) {
        console.warn('无效坐标，地图未加载')
        return
      }
      this.map = new AMap.Map(this.$refs.mapContainer, {
        center: [lng, lat],
        zoom: 16
      })
      new AMap.Marker({
        position: [lng, lat],
        map: this.map
      })
    }
  }
}
</script>


<style scoped>
.detail-card {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions > * {
  margin-left: 10px;
}

.content {
  display: flex;
  margin-top: 20px;
  flex-wrap: wrap;
}

.carousel-wrapper {
  flex: 1 1 400px;
  margin-right: 20px;
}

.carousel-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 6px;
}

.info {
  flex: 1 1 300px;
  min-width: 280px;
  line-height: 1.8;
}
.map-container {
  width: 100%;
  height: 300px;
  margin-top: 10px;
  border-radius: 8px;
  overflow: hidden;
}

</style>
