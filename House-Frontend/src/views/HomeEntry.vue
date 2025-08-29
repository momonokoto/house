<template>
  <div style="max-width: 450px; margin: 80px auto;">
    <el-card>
      <h2 style="text-align: center;">欢迎来到小众租房平台</h2>

      <!-- 登录模块 -->
      <div v-if="activeTab === 'login'">
        <el-divider><span style="font-weight: bold">登录</span></el-divider>
        <el-form :model="loginForm" ref="loginForm" :rules="rules" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="loginForm.password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="login(false)">登录</el-button>
          </el-form-item>
        </el-form>
        <div style="text-align: right; margin-top: 10px;">
          <el-link type="primary" @click="activeTab = 'register'">点我注册！</el-link>
          <br>
          <el-link type="primary" style="margin-left: 10px;" @click="showResetEmailDialog = true">忘记密码？</el-link>
        </div>
      </div>

      <!-- 注册模块 -->
      <div v-else>
        <el-divider><span style="font-weight: bold">注册</span></el-divider>
        <el-form :model="registerForm" ref="registerForm" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="registerForm.username" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="registerForm.password" show-password />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="registerForm.email" />
          </el-form-item>
          <el-form-item label="验证码">
            <el-row :gutter="10">
              <el-col :span="14">
                <el-input v-model="registerForm.code" />
              </el-col>
              <el-col :span="5">
                <el-button type="primary" @click="sendCode">发送验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="register">注册</el-button>
          </el-form-item>
        </el-form>
        <div style="text-align: right; margin-top: 10px;">
          <el-link type="primary" @click="activeTab = 'login'">已有账号？点击登录</el-link>
        </div>
      </div>
    </el-card>

    <!-- 忘记密码弹窗 -->
    <el-dialog title="找回密码" :visible.sync="showResetEmailDialog" width="450px">
      <el-form :model="resetForm" label-width="100px">
        <el-form-item label="邮箱">
          <el-input v-model="resetForm.email" />
        </el-form-item>
        <el-form-item label="验证码">
          <el-row :gutter="10">
            <el-col :span="14">
              <el-input v-model="resetForm.code" />
            </el-col>
            <el-col :span="8">
              <el-button
                type="primary"
                :disabled="resetCountdown > 0"
                @click="sendResetCode"
              >
                {{ resetCountdown > 0 ? resetCountdown + 's后重发' : '发送验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="resetForm.password" show-password />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showResetEmailDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReset">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Entry',
  data() {
    return {
      activeTab: 'login',
      loginForm: { username: '', password: '' },
      registerForm: { username: '', password: '', email: '', code: '' },
      showResetEmailDialog: false,
      resetForm: { email: '', code: '', password: '' },
      resetCountdown: 0,
      resetTimer: null,
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async login(isAdmin) {
      try {
        const res = await request.post('/api/user/authenticate', this.loginForm)
        const token = res.data.accessToken
        const user = res.data.User
        if (!token || !user) throw new Error('响应格式不正确')

        localStorage.setItem('token', token)
        localStorage.setItem('userInfo', JSON.stringify(user))

        this.$message.success('登录成功')
        this.$router.push(isAdmin ? '/admin' : '/user')
      } catch (err) {
        this.$message.error(err.response?.data?.message || '登录失败，请检查用户名和密码')
      }
    },

    async register() {
      try {
        await request.post('/api/user/register', this.registerForm)
        this.$message.success('注册成功，请登录')
        this.activeTab = 'login'
      } catch (err) {
        this.$message.error(err.response?.data?.message || '注册失败')
      }
    },

    async sendCode() {
      try {
        await request.post('/api/user/resendemail', {
          username: this.registerForm.username,
          email: this.registerForm.email
        })
        this.$message.success('验证码已发送')
      } catch (err) {
        this.$message.error('验证码发送失败')
      }
    },

    // 忘记密码发送验证码
    async sendResetCode() {
      if (!this.resetForm.email) {
        this.$message.warning('请输入邮箱')
        return
      }
      try {
        await request.post('/api/user/repasswordemail', null, {
          params: { email: this.resetForm.email }
        })
        this.$message.success('验证码已发送')
        this.startCountdown()
      } catch (err) {
        this.$message.error(err.response?.data?.error || '发送失败，请检查邮箱是否注册')
      }
    },

    // 倒计时
    startCountdown() {
      this.resetCountdown = 60
      this.resetTimer = setInterval(() => {
        if (this.resetCountdown > 0) {
          this.resetCountdown--
        } else {
          clearInterval(this.resetTimer)
          this.resetTimer = null
        }
      }, 1000)
    },

    // 提交新密码
    async submitReset() {
      const { email, code, password } = this.resetForm
      if (!email || !code || !password) {
        this.$message.warning('请填写完整信息')
        return
      }
      try {
        await request.post('/api/user/repassword', this.resetForm)
        this.$message.success('密码重置成功，请登录')
        this.showResetEmailDialog = false
        this.activeTab = 'login'
      } catch (err) {
        const msg = err.response?.data?.error || err.message
        this.$message.error('重置失败：' + msg)
      }
    }
  },
  beforeDestroy() {
    if (this.resetTimer) clearInterval(this.resetTimer)
  }
}
</script>

<style scoped>
.el-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
