<template>
  <div class="publish-house">
    <el-card style="max-width: 800px; margin: 40px auto; padding: 20px;">
      <h2>发布房源</h2>
      <el-form :model="form" ref="form" :rules="rules" label-width="120px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-select v-model="form.region" placeholder="选择地区">
            <el-option v-for="item in regions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailedAddress">
          <el-input v-model="form.detailedAddress" placeholder="如：朝阳区高碑店..." />
        </el-form-item>

        <el-form-item label="坐标识别">
          <el-button type="primary" @click="identifyCoords">识别并显示地图</el-button>
          <div v-if="coordsIdentified" style="margin-top: 10px;">
            <p>经度: {{ form.longitude }}, 纬度: {{ form.latitude }}</p>
            <div ref="mapContainer" class="map-container"></div>
          </div>
        </el-form-item>

        <el-form-item label="面积(m²)" prop="area">
          <el-input-number v-model="form.area" :min="0" />
        </el-form-item>
        <el-form-item label="户型" prop="roomType">
          <el-input v-model="form.roomType" />
        </el-form-item>
        <el-form-item label="房间数" prop="roomNumber">
          <el-input-number v-model="form.roomNumber" :min="1" />
        </el-form-item>
        <el-form-item label="租金(元/月)" prop="rent">
          <el-input-number v-model="form.rent" :min="0" />
        </el-form-item>
        <el-form-item label="是否有电梯" prop="elevator">
          <el-switch v-model="form.elevator" active-text="有" inactive-text="无" />
        </el-form-item>
        <el-form-item label="房源描述" prop="description">
          <el-input type="textarea" v-model="form.description" rows="4" />
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
          <el-button type="primary" @click="submit">发布</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "PublishHouse",
  data() {
    return {
      regions: ["北京市", "上海市", "天津市", "重庆市", "广州市", "深圳市", "南京市", "杭州市", "成都市", "武汉市", "西安市", "长沙市", "青岛市", "厦门市", "苏州市"],
      form: {
        title: "",
        region: "",
        detailedAddress: "",
        latitude: null,
        longitude: null,
        area: null,
        roomType: "",
        roomNumber: 1,
        rent: null,
        elevator: false,
        description: "",
        img: "1",
        video: "",
        ownerId: null,
        createTime: "",
      },
      coordsIdentified: false,
      map: null,
      rules: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        region: [{ required: true, message: "请选择地区", trigger: "change" }],
        detailedAddress: [{ required: true, message: "请输入详细地址", trigger: "blur" }],
        area: [{ required: true, message: "请输入面积", trigger: "blur" }],
        roomType: [{ required: true, message: "请输入户型", trigger: "blur" }],
        rent: [{ required: true, message: "请输入租金", trigger: "blur" }],
      },
      uploadHeaders: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    };
  },
  methods: {
    handleImageUpload(res) {
      if (res.imageUrl) {
        this.form.img = res.imageUrl;
      } else {
        this.$message.error("图片上传失败，请重试");
      }
    },
    handleVideoUpload(res) {
      if (res.videoUrl) {
        this.form.video = res.videoUrl;
      } else {
        this.$message.error("视频上传失败，请重试");
      }
    },
    async geocodeAddress() {
      const key = "db71ee4d4daf5ef750efc6b7587540ab";
      const address = this.form.region + this.form.detailedAddress;
      const res = await axios.get(
        `https://restapi.amap.com/v3/geocode/geo?key=${key}&address=${encodeURIComponent(address)}`
      );
      const geo = res.data.geocodes?.[0];
      if (geo) {
        [this.form.longitude, this.form.latitude] = geo.location
          .split(",")
          .map(Number);
        return true;
      }
      return false;
    },
    async identifyCoords() {
      if (!this.form.region || !this.form.detailedAddress) {
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
        center: [this.form.longitude, this.form.latitude],
        zoom: 16,
      });
      new window.AMap.Marker({
        position: [this.form.longitude, this.form.latitude],
        map: this.map,
      });
    },
async submit() {
  this.$refs.form.validate(async (valid) => {
    if (!valid) return;

    if (!this.coordsIdentified) {
      const ok = await this.geocodeAddress();
      if (!ok) {
        return this.$message.error("请先识别坐标");
      }
      this.coordsIdentified = true;
    }

    const user = JSON.parse(localStorage.getItem("userInfo") || "{}");
    this.form.ownerId = user.id;
    this.form.createTime = new Date()
      .toISOString()
      .slice(0, 19)
      .replace("T", " ");

    // 创建FormData
    const formData = new FormData();
    // house 字段必须是JSON字符串
    formData.append("house", new Blob([JSON.stringify(this.form)], { type: "application/json" }));
    
    // 图片上传
    if (this.imageList && this.imageList.length > 0) {
      formData.append("image", this.imageList[0].raw);
    }
    // 视频上传
    if (this.videoList && this.videoList.length > 0) {
      formData.append("video", this.videoList[0].raw);
    }

    try {
      await axios.post("http://www.xzzf.xyz/api/file/upload", formData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "multipart/form-data"
        }
      });
      this.$message.success("发布成功");
      setTimeout(() => this.$router.push("/user"), 1000);
    } catch (err) {
      this.$message.error("发布失败：" + (err.response?.data?.message || err.message));
    }
  });
}

  },
};
</script>

<style scoped>
.publish-house .map-container {
  width: 100%;
  height: 300px;
  margin-top: 10px;
}
.preview-img {
  display: block;
  margin-top: 10px;
  max-width: 200px;
}
.preview-video {
  display: block;
  margin-top: 10px;
  max-width: 300px;
}
</style>
