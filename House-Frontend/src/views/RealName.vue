<template>
  <div style="max-width: 500px; margin: 50px auto;">
    <el-card>
      <template v-if="realNameStatus === 1">
        <div style="text-align: center;">
          <el-result
            icon="success"
            title="您已通过实名认证"
            sub-title="正在跳转到发布房源页面..."
          />
        </div>
      </template>

      <template v-else>
        <h3>实名认证</h3>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" style="margin-top: 20px;">
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" />
          </el-form-item>
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="form.mobile" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitAuth">提交认证</el-button>
          </el-form-item>
        </el-form>
      </template>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'RealName',
  data() {
    return {
      realNameStatus: 0, // 0未实名，1已实名
      form: {
        realName: '',
        idCard: '',
        mobile: ''
      },
      rules: {
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { pattern: /^[1-9]\d{16}[\dXx]$/, message: '身份证格式错误', trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.checkRealNameStatus()
  },
  methods: {
    async checkRealNameStatus() {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/entry')
        return
      }

      try {
        const res = await axios.get('http://www.xzzf.xyz/api/get/getUser', {
          headers: {
            Authorization: 'Bearer ' + token
          }
        })
        if (res.data) {
          const user = res.data
          this.realNameStatus = user.real_name_status
          if (this.realNameStatus === 1) {
            setTimeout(() => {
              this.$router.push('/publish')
            }, 2000)
          }
        } else {
          this.$message.error('获取实名认证状态失败')
        }
      } catch (err) {
        this.$message.error('请求失败：' + err.message)
      }
    },

    async submitAuth() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return

        const token = localStorage.getItem('token')
        if (!token) {
          this.$message.warning('请先登录')
          this.$router.push('/entry')
          return
        }

        try {
          const res = await axios.post('http://www.xzzf.xyz/realNameAuthentication', this.form, {
            headers: {
              Authorization: 'Bearer ' + token
            }
          })

          if (res.data.code === 200) {
            this.$message.success('实名认证成功，正在跳转...')
            setTimeout(() => {
              this.$router.push('/publish')
            }, 1000)
          } else {
            this.$message.error(res.data.message || '实名认证失败')
          }
        } catch (err) {
          this.$message.error('认证请求失败：' + err.message)
        }
      })
    }
  }
}
</script>

<style scoped>
h3 {
  text-align: center;
  margin-bottom: 20px;
}
</style>
