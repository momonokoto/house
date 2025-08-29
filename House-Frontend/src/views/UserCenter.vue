<template>
  <el-row :gutter="20" style="padding: 20px">
    <!-- 左侧个人信息栏 -->
    <el-col :span="6">
      <el-card>
        <div style="text-align: center">
<el-upload
  class="avatar-uploader"
  :show-file-list="false"
  :action="uploadUrl"
  :headers="uploadHeaders"
  :on-success="handleAvatarSuccess"
  :before-upload="beforeAvatarUpload"
  name="file"
>
<el-avatar
  :size="80"
  :src="this.img || defaultAvatar"
  @error="onAvatarError"
  style="cursor: pointer"
/>

</el-upload>


          <h3 style="margin-top: 10px">{{ user.username || '未登录用户' }}</h3>
          <p>
            {{ user.userNickname || '这个人很懒，还没有设置名字' }}
            <el-tag
              :type="user.realNameStatus === 1 ? 'success' : 'info'"
              size="mini"
              style="margin-left: 6px"
            >
              {{ user.realNameStatus === 1 ? '已实名' : '未实名' }}
            </el-tag>
            <el-tag size="mini" type="warning" style="margin-left: 8px">
              {{ user.role === 'ADMIN' ? '管理员' : '注册用户' }}
            </el-tag>
          </p>
        </div>

        <el-divider></el-divider>

        <div>
          <p>邮箱：{{ user.email || '未填写' }}</p>
          <p>电话：{{ user.phone || '未填写' }}</p>
          <p>简介：{{ user.introduction || '无简介' }}</p>
        </div>

        <el-divider></el-divider>

        <el-row :gutter="10">
          <el-col :span="8">
            <div style="text-align: center">
              <h4>{{ listingsCount }}</h4>
              <p>已发布</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div style="text-align: center">
              <h4>{{ appointmentCount }}</h4>
              <p>已预约</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div style="text-align: center">
              <h4>{{ favoriteCount }}</h4>
              <p>已收藏</p>
            </div>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <div style="text-align: center">
          <el-button type="success" size="small" @click="showProfileDialog = true">修改资料</el-button>
          <el-button type="primary" size="small" @click="showEditDialog = true">修改密码</el-button>
        </div>
      </el-card>
    </el-col>

    <!-- 右侧功能菜单 -->
    <el-col :span="18">
      <el-card>
        <el-menu :default-active="activeMenu" @select="handleMenuSelect" mode="horizontal">
          <el-menu-item index="1">我发布的房源</el-menu-item>
          <el-menu-item index="2">预约看房</el-menu-item>
          <el-menu-item index="3">我的收藏</el-menu-item>
          <el-menu-item index="4">消息通知</el-menu-item>
        </el-menu>

        <div style="margin-top: 20px">
          <component :is="currentView"></component>
        </div>
      </el-card>
    </el-col>

    <!-- 修改资料弹窗 -->
    <el-dialog title="修改资料" :visible.sync="showProfileDialog" width="500px">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="80px">
        <el-form-item label="昵称" prop="userNickname">
          <el-input v-model="editForm.userNickname" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input type="textarea" :rows="3" v-model="editForm.introduction" />
        </el-form-item>
        <p>修改用户名和邮箱等敏感信息请联系客服：scacyh@163.com</p>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showProfileDialog = false">取消</el-button>
        <el-button type="primary" @click="submitProfileUpdate">保存</el-button>
      </div>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" :visible.sync="showEditDialog" width="400px">
      <el-form :model="passwordForm" :rules="rules" ref="passwordForm" label-width="90px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmNewPassword">
          <el-input type="password" v-model="passwordForm.confirmNewPassword" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPasswordChange">确认修改</el-button>
      </div>
    </el-dialog>
  </el-row>
</template>

<script>
import axios from 'axios'
import MyListings from './user/MyListings.vue'
import MyAppointments from './user/MyAppointments.vue'
import MyFavorites from './user/MyFavorites.vue'
import Notifications from './user/Notifications.vue'
import defaultAvatar from '@/assets/default-avatar.png'

export default {
  components: {
    MyListings,
    MyAppointments,
    MyFavorites,
    Notifications
  },
  data() {
    return {

    uploadUrl: 'http://www.xzzf.xyz/personal_center/upload_avatar',
    uploadHeaders: {
      Authorization: 'Bearer ' + (localStorage.getItem('token') || '')
    },

    defaultAvatar,
      user: {
        username: '',
        userNickname: '',
        email: '',
        phone: '',
        introduction: '',
        realNameStatus: 0,
        avatar: '',
        role: 'USER',
        status: ''
      },
      img:'',
      editForm: {
        userNickname: '',
        phone: '',
        introduction: ''
      },
      editRules: {
        userNickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        phone: [{ pattern: /^[0-9\-+]{7,20}$/, message: '请输入合法手机号', trigger: 'blur' }]
      },
      showProfileDialog: false,
      showEditDialog: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      },
      rules: {
        oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
        confirmNewPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      activeMenu: '1',
      currentView: 'MyListings',
      favoriteCount: 0,
      listingsCount: 0,
      appointmentCount: 0
    }
  },
  mounted() {
    this.fetchUserInfo()
    this.fetchFavoriteCount()
    this.fetchListingsCount()
    this.fetchAppointmentCount()
    

  },
  methods: {
     // 上传成功后更新头像
handleAvatarSuccess(res) {
  if (res.code && res.jsonData) {
    const fileName = res.jsonData;
    const token = localStorage.getItem('token') || '';

    axios.get('http://www.xzzf.xyz/api/file/get-url', {
      headers: {
        Authorization: 'Bearer ' + token
      },
      params: {
        fileName
      }
    })
    .then(resp => {
      if (resp.status&& resp.data && resp.data.url) {
        const avatarUrl = resp.data.url;

        this.user.avatar = avatarUrl;

        // 同步更新 localStorage
        const localUser = JSON.parse(localStorage.getItem('user') || '{}');
        localUser.avatar = avatarUrl;
        this.img = localUser.avatar;
        console.log("img："+this.img);
        localStorage.setItem('user', JSON.stringify(localUser));

        this.$message.success('头像上传成功');
      } else {
        this.$message.error('获取头像地址失败');
      }
    })
    .catch(() => {
      this.$message.error('获取头像失败');
    });
  } else {
    this.$message.error(res.message || '上传失败');
  }
}
,

  // 上传前校验
  beforeAvatarUpload(file) {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isImage) {
      this.$message.error('只能上传图片格式')
    }
    if (!isLt2M) {
      this.$message.error('上传头像大小不能超过 2MB')
    }
    return isImage && isLt2M
  },

  // 默认头像出错处理
  onAvatarError(e) {
    e.target.src = this.defaultAvatar
  },

    async fetchAppointmentCount() {
    const token = localStorage.getItem('token') || ''
    try {
      const res = await axios.get('http://www.xzzf.xyz/personal_center/my_appointment', {
        headers: {
          Authorization: 'Bearer ' + token
        },
        params: {
          currentPage: 1,
          pageSize: 1
        }
      })
      if (res.data.code) {
        this.appointmentCount = res.data.jsonData.total || 0
      } else {
        this.$message.error('获取预约数失败')
      }
    } catch (err) {
      this.$message.error('请求失败：' + err.message)
    }
  },
    async fetchListingsCount() {
      try {
        const res = await axios.get("http://www.xzzf.xyz/personal_center/my_house", {
          params: {
            currentPage: 1,
            pageSize: 1
          },
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
          }
        });
        if (res.data.code) {
          this.listingsCount = res.data.jsonData.total || 0
        } else {
          this.$message.error("获取房源总数失败：" + res.data.message)
        }
      } catch (error) {
        this.$message.error("网络错误：" + error.message)
      }
    },
async fetchUserInfo() {
  try {
    const token = localStorage.getItem('token') || '';
    const res = await axios.post('http://www.xzzf.xyz/personal_center/get_user_info', null, {
      headers: {
        Authorization: 'Bearer ' + token
      }
    });
    console.log(res.data);

    if (res.data && res.data.code === 200) {
      const userData = res.data.jsonData;

      // 状态检查
      if (userData.status === 0) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this.$message.warning('您的账号已被封禁，若有疑问请联系管理员');
        if (this.$route.path !== '/entry') {
          this.$router.push('/entry');
        }
        return;
      }

      // 设置用户基本信息
      this.user = userData;

      // 获取头像URL（如果有头像字段）
      if (userData.avatar) {
        const fileName = userData.avatar;
        const avatarRes = await axios.get('http://www.xzzf.xyz/api/file/get-url', {
          headers: {
            Authorization: 'Bearer ' + token
          },
          params: {
            fileName
          }
        });

        if (avatarRes.status === 200 && avatarRes.data && avatarRes.data.url) {
          const avatarUrl = avatarRes.data.url;
          this.user.avatar = avatarUrl;

          // 同步 localStorage
          const localUser = { ...this.user };
          localUser.avatar = avatarUrl;
          localStorage.setItem('user', JSON.stringify(localUser));
          this.img = avatarUrl; // 如你有 this.img
        } else {
          this.$message.error('获取头像地址失败');
        }
      }

    } else {
      this.$message.warning('获取用户信息失败：' + (res.data.message || '未知错误'));
    }
  } catch (err) {
    this.$message.error('请求失败：' + err.message);
  }
}
,

    async fetchFavoriteCount() {
      try {
        const token = localStorage.getItem('token') || ''
        const res = await axios.get('http://www.xzzf.xyz/personal_center/my_collection', {
          params: { currentPage: 1, pageSize: 1 },
          headers: { Authorization: 'Bearer ' + token }
        })
        if (res.data.code && res.data.jsonData) {
          this.favoriteCount = res.data.jsonData.total || 0
        }
      } catch {
        this.favoriteCount = 0
      }
    },

    handleMenuSelect(index) {
      const viewMap = {
        '1': 'MyListings',
        '2': 'MyAppointments',
        '3': 'MyFavorites',
        '4': 'Notifications'
      }
      this.currentView = viewMap[index]
    },
    onAvatarError(e) {
      e.target.src = defaultAvatar
    },
    async submitProfileUpdate() {
      this.$refs.editForm.validate(async valid => {
        if (!valid) return
        try {
          const token = localStorage.getItem('token') || ''
          const res = await axios.put('http://www.xzzf.xyz/personal_center/update', this.editForm, {
            headers: { Authorization: 'Bearer ' + token }
          })
          if (res.data.code) {
            this.$message.success('资料修改成功')
            this.showProfileDialog = false
            this.fetchUserInfo()
          } else {
            this.$message.error(res.data.message || '修改失败')
          }
        } catch (err) {
          this.$message.error('请求失败：' + err.message)
        }
      })
    },
    async submitPasswordChange() {
      this.$refs.passwordForm.validate(async valid => {
        if (!valid) return
        try {
          const token = localStorage.getItem('token') || ''
          const res = await axios.put('http://www.xzzf.xyz/personal_center/change_password', this.passwordForm, {
            headers: { Authorization: 'Bearer ' + token }
          })
          if (res.data.code) {
            this.$message.success('密码修改成功')
            this.showEditDialog = false
            this.passwordForm = { oldPassword: '', newPassword: '', confirmNewPassword: '' }
            location.reload()
          } else {
            this.$message.error(res.data.message || '密码修改失败')
            location.reload()
          }
        } catch (err) {
          this.$message.error('修改失败：' + err.message)
          location.reload()
        }
      })
    }
  }
}
</script>

<style scoped>
.el-avatar {
  background-color: #f2f2f2;
}
.avatar-uploader {
  display: inline-block;
  cursor: pointer;
}

</style>
