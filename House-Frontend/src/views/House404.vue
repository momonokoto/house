<template>
  <div class="house-404-page">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="logo">
        <i class="el-icon-s-home"></i>
        <span>房源之家</span>
      </div>
    </div>
    
    <!-- 主要内容区 -->
    <div class="main-content">
      <div class="error-container">
        <div class="error-code">404</div>
        <div class="error-message">
          <h2>抱歉，您访问的房源不见了</h2>
          <p class="reason">该房源可能已下架或尚未通过审核</p>
        </div>
        
        <!-- 房屋插图 -->
        <div class="house-illustration">
          <svg viewBox="0 0 300 200" class="house-svg">
            <rect x="50" y="50" width="200" height="100" fill="#f8f9fa" stroke="#dcdfe6" stroke-width="2"/>
            <polygon points="50,50 150,20 250,50" fill="#8492a6" stroke="#606266" stroke-width="2"/>
            <rect x="100" y="80" width="30" height="40" fill="#eef2f7" stroke="#dcdfe6" stroke-width="2"/>
            <rect x="170" y="80" width="30" height="40" fill="#eef2f7" stroke="#dcdfe6" stroke-width="2"/>
            <path d="M150,100 L150,150" stroke="#dcdfe6" stroke-width="2" stroke-dasharray="5,5"/>
            <circle cx="150" cy="30" r="15" fill="#ff9800"/>
          </svg>
        </div>
        
        <!-- 解决方案 -->
        <div class="solutions">
          <h3>您可以这样做：</h3>
          <ul>
            <li>
              <i class="el-icon-search"></i>
              <span>在搜索栏中输入关键词查找其他房源</span>
            </li>
            <li>
              <i class="el-icon-s-home"></i>
              <span><router-link to="/home">返回首页</router-link></span>
            </li>
            <li>
              <i class="el-icon-message"></i>
              <span>联系客服咨询房源详情</span>
            </li>
          </ul>
        </div>

      </div>
    </div>
    
    <!-- 页脚 -->
    <div class="footer">
      <p>© 2025 房源之家 版权所有 | 客服热线：400-123-4567</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'House404',
  data() {
    return {
      searchKey: ''
    }
  },
  methods: {

  }
}
</script>

<style scoped>
.house-404-page {
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, 'Microsoft YaHei', Arial, sans-serif;
  min-height: 100vh;
  background-color: #f5f7fa;
  color: #303133;
}

.header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.logo i {
  margin-right: 8px;
  font-size: 24px;
}

.main-content {
  max-width: 800px;
  margin: 60px auto;
  padding: 40px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.error-container {
  text-align: center;
}

.error-code {
  font-size: 100px;
  font-weight: bold;
  color: #909399;
  margin-bottom: 20px;
}

.error-message h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.reason {
  font-size: 16px;
  color: #909399;
  margin-bottom: 40px;
}

.house-illustration {
  margin: 40px 0;
  height: 200px;
  display: flex;
  justify-content: center;
}

.house-svg {
  max-width: 100%;
  max-height: 100%;
}

.solutions {
  text-align: left;
  margin: 30px 0;
  padding-left: 20px;
  border-left: 3px solid #409EFF;
}

.solutions h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #303133;
}

.solutions ul {
  list-style: none;
  padding: 0;
}

.solutions li {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.solutions li i {
  margin-right: 10px;
  color: #409EFF;
  font-size: 18px;
  margin-top: 2px;
}

.search-box {
  margin-top: 40px;
  width: 100%;
}

.input-with-button {
  width: 100%;
}

.search-button {
  background-color: #409EFF;
  border-color: #409EFF;
}

.search-button:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.footer {
  text-align: center;
  margin-top: 40px;
  padding: 20px 0;
  color: #909399;
  font-size: 14px;
}
</style>