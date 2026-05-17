<template>
  <div class="list">
    <h2>售后列表</h2>
    <el-card>
      <el-table :data="afterSales" border stripe>
        <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
        <el-table-column prop="orderId" label="订单号" width="120"></el-table-column>
        <el-table-column prop="productName" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusColor(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentNode" label="当前节点" width="120"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'List',
  data() {
    return {
      afterSales: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/list')
        .then(res => {
          this.afterSales = res.data
        })
    },
    getTypeColor(type) {
      const colors = { '退款': 'danger', '换货': 'warning', '维修': 'info' }
      return colors[type] || ''
    },
    getStatusColor(status) {
      const colors = { '待审核': 'warning', '审核通过': 'success', '已拒绝': 'danger', '已完成': 'info' }
      return colors[status] || ''
    }
  }
}
</script>

<style scoped>
.list h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
</style>
