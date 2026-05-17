<template>
  <div class="refund">
    <h2>退款处理</h2>
    <el-card>
      <el-table :data="refundList" border stripe>
        <el-table-column prop="refundId" label="退款单号" width="150"></el-table-column>
        <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
        <el-table-column prop="orderId" label="订单号" width="120"></el-table-column>
        <el-table-column prop="amount" label="退款金额" width="120">
          <template slot-scope="scope">¥{{ scope.row.amount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag type="warning">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="success" size="small" @click="confirmRefund(scope.row.refundId)">确认退款</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Refund',
  data() {
    return {
      refundList: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/refunds')
        .then(res => {
          this.refundList = res.data.filter(item => item.status === '待退款')
        })
    },
    confirmRefund(id) {
      this.$axios.post('/api/aftersale/refund/' + id)
        .then(res => {
          if (res.data.success) {
            this.$message.success(res.data.message)
            this.loadData()
          } else {
            this.$message.error(res.data.message)
          }
        })
    }
  }
}
</script>

<style scoped>
.refund h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
</style>
