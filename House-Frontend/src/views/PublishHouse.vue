<template>
  <div style="max-width: 800px; margin: 40px auto;">
    <el-card>
      <h2 style="text-align: center;">发布房源</h2>

      <el-form :model="houseForm" label-width="100px" ref="houseForm" :rules="rules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="houseForm.title" />
        </el-form-item>

        <el-form-item label="地区" prop="region">
          <el-select v-model="houseForm.region" placeholder="请选择城市">
            <el-option
              v-for="city in cityList"
              :key="city"
              :label="city"
              :value="city"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="房屋地址" prop="address">
          <el-input v-model="houseForm.address" />
        </el-form-item>

        <el-form-item label="详细地址" prop="detailedAddress">
          <el-input v-model="houseForm.detailedAddress" placeholder="如：贵州省贵阳市花溪区贵州民族大学南校区学生活动中心" />
        </el-form-item>

        <el-form-item label="坐标识别">
          <el-button type="primary" @click="identifyCoords">识别并显示地图</el-button>
          <div v-if="coordsIdentified" style="margin-top: 10px;">
            <p>经度: {{ houseForm.longitude }}, 纬度: {{ houseForm.latitude }}</p>
            <div ref="mapContainer" class="map-container"></div>
          </div>
        </el-form-item>

        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number v-model="houseForm.area" :min="5" />
        </el-form-item>

        <el-form-item label="房间数" prop="roomNumber">
          <el-input-number v-model="houseForm.roomNumber" :min="1" />
        </el-form-item>

<el-form-item label="户型" prop="roomType" >
  <el-select v-model="houseForm.roomType" placeholder="请选择户型">
    <el-option label="一室" value="1" />
    <el-option label="二室" value="2" />
    <el-option label="三室" value="3" />
    <el-option label="三室以上" value="4" />
  </el-select>
</el-form-item>



<el-form-item label="租赁方式" prop="rentType">
  <el-select v-model="houseForm.rentType" placeholder="请选择租赁方式">
    <el-option label="合租" value="1"></el-option>
    <el-option label="整租" value="2"></el-option>
    <el-option label="公寓" value="3"></el-option>
  </el-select>
</el-form-item>

        <el-form-item label="租金(元)" prop="rent">
          <el-input-number v-model="houseForm.rent" :min="100" />
        </el-form-item>

        <el-form-item label="电梯" prop="elevator">
          <el-radio-group v-model="houseForm.elevator">
            <el-radio :label="1">有</el-radio>
            <el-radio :label="0">无</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="houseForm.description" />
        </el-form-item>

        <el-form-item label="图片">
          <el-upload
            action=""
            :auto-upload="false"
            :file-list="imageList"
            :on-change="handleImageChange"
            list-type="picture"
          >
            <el-button>上传图片</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="视频">
          <el-upload
            action=""
            :auto-upload="false"
            :file-list="videoList"
            :on-change="handleVideoChange"
            list-type="text"
          >
            <el-button>上传视频</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitHouse">提交发布</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'PublishHouse',
  data() {
    return {
houseForm: {
  title: '',
  region: '',
  address: '',
  detailedAddress: '',
  area: null,
  roomNumber: 1,
  roomType: '',
  rentType: '',  // 新增
  rent: null,
  elevator: 1,
  description: '',
  status: 0,
  verify: 0,
  isDeleted: 0,
  latitude: 39.9087,
  longitude: 66.6666,
},

      coordsIdentified: false,
      imageList: [],
      videoList: [],
      cityList: [
        '北京市', '上海市', '天津市', '重庆市', '广州市', '深圳市', '南京市',
        '杭州市', '成都市', '武汉市', '西安市', '长沙市', '青岛市',
        '厦门市', '苏州市'
      ],
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        region: [{ required: true, message: '请选择地区', trigger: 'change' }],
        address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
        area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
        rent: [{ required: true, message: '请输入租金', trigger: 'blur' }],
        rentType: [{ required: true, message: '请选择租赁方式', trigger: 'change' }],
        roomType: [{ required: true, message: '请选择户型', trigger: 'change' }]

      }

    }
  },
  methods: {
    handleImageChange(file, fileList) {
      this.imageList = fileList.slice(-1) // 限制上传一张图
    },
    handleVideoChange(file, fileList) {
      this.videoList = fileList.slice(-1) // 限制上传一个视频
    },
        async geocodeAddress() {
      const key = "db71ee4d4daf5ef750efc6b7587540ab";
      const address = this.houseForm.region + this.houseForm.detailedAddress;
      const res = await axios.get(
        `https://restapi.amap.com/v3/geocode/geo?key=${key}&address=${encodeURIComponent(address)}`
      );
      const geo = res.data.geocodes?.[0];
      if (geo) {
        [this.houseForm.longitude, this.houseForm.latitude] = geo.location
          .split(",")
          .map(Number);
        return true;
      }
      return false;
    },
    async identifyCoords() {
      if (!this.houseForm.region || !this.houseForm.detailedAddress) {
        return this.$message.warning("先填写地区和详细地址");
      }
      const ok = await this.geocodeAddress();
      if (ok) {
        this.coordsIdentified = true;
        this.$nextTick(this.initMap);
        this.$message.success("坐标识别成功");
      } else {
        this.$message.error("识别失败，请检查地址");
      }
    },
    initMap() {
      if (!window.AMap) {
        const script = document.createElement("script");
        script.src = `https://webapi.amap.com/maps?v=2.0&key=db71ee4d4daf5ef750efc6b7587540ab`;
        script.onload = this.renderMap;
        document.head.appendChild(script);
      } else {
        this.renderMap();
      }
    },
    renderMap() {
      this.map = new window.AMap.Map(this.$refs.mapContainer, {
        center: [this.houseForm.longitude, this.houseForm.latitude],
        zoom: 16,
      });
      new window.AMap.Marker({
        position: [this.houseForm.longitude, this.houseForm.latitude],
        map: this.map,
      });
    },
    async submitHouse() {
      this.$refs.houseForm.validate(async valid => {
        if (!valid) return

        const token = localStorage.getItem('token')
        if (!token) {
          this.$message.error('请先登录')
          this.$router.push('/entry')
          return
        }
this.houseForm.latitude = Number(this.houseForm.latitude);
this.houseForm.longitude = Number(this.houseForm.longitude);
console.log(this.houseForm.latitude + " " + this.houseForm.longitude)
        const formData = new FormData()
        formData.append('house', new Blob([JSON.stringify(this.houseForm)], { type: 'application/json' }))
        if (this.imageList.length) {
          formData.append('image', this.imageList[0].raw)
        }
        if (this.videoList.length) {
          formData.append('video', this.videoList[0].raw)
        }

        try {
          const res = await axios.post('http://www.xzzf.xyz/api/file/upload', formData, {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'multipart/form-data'
            }
          })

          if (res.data.imageUrl || res.data.videoUrl) {
            this.$message.success('房源发布成功')
            this.$router.push('/user')
          } else {
            this.$message.warning('上传成功，房源发布成功，最好添加视频哦')
            this.$router.push('/user')
            function getCurrentTime() {
                return new Date().toISOString().slice(0, 19).replace('T', ' ');
              }

              // 调用示例
              console.log(getCurrentTime());

          }
        } catch (err) {
          console.error(err)
          this.$message.error('发布失败，请检查表单或网络')
        }
      })
    }
  }
}
</script>

<style scoped>
.el-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.map-container {
  width: 100%;
  height: 300px;
  margin-top: 10px;
}
</style>
