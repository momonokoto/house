<template>
  <div style="background-color: #000; color: white; padding: 40px;">
    <el-row :gutter="40">
      <el-col :span="8">
        <h3>关于我们</h3>
        <p>我们是来自贵州民族大学2022级软件工程1班的租房平台小组。产品经理：谢林晏，后端开发：谢茂驰、念博涵，前端开发：陈宇航，测试：郭晶、杨洁莹。本项目由前端使用vue+elementUI开发，后端用的什么我是前端我也不太清楚</p>
      </el-col>
      <el-col :span="8">
        <h3>联系方式</h3>
        <p>电话：666</p>
        <p>邮箱：暂无</p>
        <p>地址：贵州省贵阳市花溪区贵州民族大学南校区数信学院</p>
      </el-col>
      <el-col :span="8">
        <h3>帮助中心</h3>
        <p>常用问题</p>
        <p>使用指南</p>
        <p>隐私政策</p>
      </el-col>
    </el-row>
        <div class="footer">
      <p>© 2025 租房平台小组 版权所有 | 客服热线：400-123-4567</p>
    </div>
  </div>
</template>

<style scoped>
.footer {
  text-align: center;
  /* margin-top: 40px; */
  padding: 20px 0;
  color: #909399;
  font-size: 14px;
}
</style>