<template>
  <div class="my-published-houses">
    <el-card>
      <h2>我发布的房源</h2>
<el-table :data="houseList" style="width: 100%">
  <!-- 点击标题跳转 -->
  <el-table-column prop="title" label="标题">
    <template slot-scope="scope">
      <el-link
        type="primary"
        :underline="false"
        @click="goToDetail(scope.row.houseId)"
      >
        {{ scope.row.title }}
      </el-link>
    </template>
  </el-table-column>

  <el-table-column prop="region" label="地区" />
  <el-table-column prop="rent" label="租金(元/月)" />
  <el-table-column prop="createTime" label="发布时间" />
  
  <el-table-column prop="status" label="状态">
    <template slot-scope="scope">
      <el-tag :type="statusTagType(scope.row.status)">
        {{ statusText(scope.row.status) }}
      </el-tag>
    </template>
  </el-table-column>

  <el-table-column label="操作">
    <template slot-scope="scope">
      <el-button
        v-if="scope.row.status !== 2"
        type="danger"
        size="mini"
        @click="cancelHouse(scope.row.houseId)"
      >
        下架
      </el-button>
    </template>
  </el-table-column>
</el-table>


      <el-pagination
        layout="total, prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: center;"
      />
    </el-card>
  </div>
</template>
<script>
import axios from "axios";

export default {
  name: "MyPublishedHouses",
  data() {
    return {
      houseList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
    };
  },
  mounted() {
    this.fetchMyHouses();
  },
  methods: {
    goToDetail(houseId) {
    this.$router.push(`/house/${houseId}`);
  },
        handleRowClick(row) {
      // 跳转到对应房源详情页
      this.$router.push(`/house/${row.houseId}`)
    },
    // 获取我发布的房源
    async fetchMyHouses() {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get("http://www.xzzf.xyz/personal_center/my_house", {
          params: {
            currentPage: this.currentPage,
            pageSize: this.pageSize,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (res.data.code) {
          this.houseList = res.data.jsonData.records || [];
          this.total = res.data.jsonData.total || 0;
        } else {
          this.$message.error("获取失败：" + res.data.message);
        }
      } catch (error) {
        this.$message.error("请求出错：" + (error.response?.data?.message || error.message));
      }
    },

    // 分页切换
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchMyHouses();
    },

    // 下架房源
    async cancelHouse(houseId) {
      try {
        const token = localStorage.getItem("token");
        console.log('Bearer '+token)
        const res = await axios.put(
          `http://www.xzzf.xyz/personal_center/cancel_house/${houseId}`,
          {},
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (res.data.code) {
          this.$message.success("房源已下架");
          this.fetchMyHouses();
        } else {
          this.$message.error("下架失败：" + res.data.message);
        }
      } catch (error) {
        this.$message.error("请求失败：" + (error.response?.data?.message || error.message));
      }
    },

    // 状态文字
    statusText(status) {
      return {
        0: "待租",
        1: "已租",
        2: "已下架",
      }[status] || "未知";
    },

    // 状态颜色
    statusTagType(status) {
      return {
        0: "warning",
        1: "success",
        2: "info",
      }[status] || "";
    },
  },
};
</script>
