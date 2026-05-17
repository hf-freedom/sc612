<template>
  <div class="statistics">
    <h2>统计报表</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div slot="header">售后总数</div>
          <div class="stat-value">{{ stats.totalAfterSales || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header">平均处理时长</div>
          <div class="stat-value">{{ stats.avgProcessingHours || 0 }} 小时</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header">退款/换货比例</div>
          <div class="stat-value">退款: {{ stats.refundRate || 0 }}%</div>
          <div class="stat-value">换货: {{ stats.exchangeRate || 0 }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <div slot="header">售后原因统计</div>
      <el-table :data="reasonList" border stripe>
        <el-table-column prop="reason" label="原因" width="300"></el-table-column>
        <el-table-column prop="count" label="数量" width="200"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Statistics',
  data() {
    return {
      stats: {},
      reasonList: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/statistics')
        .then(res => {
          this.stats = res.data
          this.reasonList = []
          for (let key in res.data.reasonStatistics) {
            this.reasonList.push({
              reason: key,
              count: res.data.reasonStatistics[key]
            })
          }
        })
    }
  }
}
</script>

<style scoped>
.statistics h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #67C23A;
  text-align: center;
  padding: 10px 0;
}
</style>
